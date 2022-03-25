package com.nindybun.burnergun.client.screens;


import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.nindybun.burnergun.common.BurnerGun;
import com.nindybun.burnergun.common.items.BurnerGunNBT;
import com.nindybun.burnergun.common.items.burnergunmk1.BurnerGunMK1;
import com.nindybun.burnergun.common.items.burnergunmk2.BurnerGunMK2;
import com.nindybun.burnergun.common.items.upgrades.Upgrade;
import com.nindybun.burnergun.common.network.PacketHandler;
import com.nindybun.burnergun.common.network.packets.*;
import com.nindybun.burnergun.util.StringUtil;
import com.nindybun.burnergun.util.UpgradeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.*;
import net.minecraftforge.fml.client.gui.widget.Slider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.Color;
import java.util.*;
import java.util.List;

public class burnergunSettingsScreen extends Screen implements Slider.ISlider {
    private ItemStack gun;
    private static final Logger LOGGER = LogManager.getLogger();
    private List<Upgrade> toggleableList = new ArrayList<>();
    private HashMap<Upgrade, ToggleButton> upgradeButtons = new HashMap<>();
    private int raycastRange,
                maxRaycastRange,
                vertical,
                maxVertical,
                horizontal,
                maxHorizontal,
                collectedBlocks,
                maxCollectedBlocks;
    private float volume;
    private boolean trashFilterWhitelist, containsTrash;
    private boolean smeltFilterWhitelist, containsSmelt;
    private Slider  raycastSlider,
                    volumeSlider,
                    verticalSlider,
                    horizontalSlider,
                    collectedBlocksSlider;
    private Button colorButton;

    protected burnergunSettingsScreen(ItemStack gun) {
        super(new StringTextComponent("Title"));
        this.gun = gun;
        this.volume = BurnerGunNBT.getVolume(gun);
        this.vertical = BurnerGunNBT.getVertical(gun);
        this.maxVertical = BurnerGunNBT.getMaxVertical(gun);
        this.horizontal = BurnerGunNBT.getHorizontal(gun);
        this.maxHorizontal = BurnerGunNBT.getMaxHorizontal(gun);
        this.raycastRange = BurnerGunNBT.getRaycast(gun);
        this.maxRaycastRange = BurnerGunNBT.getMaxRaycast(gun);
        this.trashFilterWhitelist = BurnerGunNBT.getTrashWhitelist(gun);
        this.smeltFilterWhitelist = BurnerGunNBT.getSmeltWhitelist(gun);
        this.collectedBlocks = BurnerGunNBT.getCollectedBlocks(gun);
        this.maxCollectedBlocks = BurnerGunNBT.getMaxCollectedBlocks(gun);

        toggleableList.clear();
        toggleableList = UpgradeUtil.getToggleableUpgrades(gun);
        containsTrash = UpgradeUtil.containsUpgradeFromList(toggleableList, Upgrade.TRASH);
        containsSmelt = UpgradeUtil.containsUpgradeFromList(toggleableList, Upgrade.AUTO_SMELT);

    }

    private void updateButtons(Upgrade upgrade) {
        for (Map.Entry<Upgrade, ToggleButton> btn : this.upgradeButtons.entrySet()) {
            Upgrade btnUpgrade = btn.getKey();
            if( (btnUpgrade.lazyIs(Upgrade.FORTUNE_1) && btn.getValue().isEnabled() && upgrade.lazyIs(Upgrade.SILK_TOUCH) )
                    || ((btnUpgrade.lazyIs(Upgrade.SILK_TOUCH)) && btn.getValue().isEnabled() && upgrade.lazyIs(Upgrade.FORTUNE_1)) ) {
                this.upgradeButtons.get(btn.getKey()).setEnabled(false);
            }
        }
    }

    private void toggleUpgrade(Upgrade upgrade) {
        this.updateButtons(upgrade);
        PacketHandler.sendToServer(new PacketUpdateUpgrade(upgrade.getName()));
    }

    @Override
    protected void init() {
        List<Widget> settings = new ArrayList<>();
        int midX = width/2;
        int midY = height/2;

        int listSize = toggleableList.size() + (containsSmelt?3:0) + (containsTrash?3:0);
        int yy = (int)Math.ceil((double)listSize/4);

        //Right Side
        int index = 0, x = midX+15, y = midY-((yy*20)+((yy-1)*5))/2;

        if (containsTrash){
            ToggleButton btn = new ToggleButton(x, y, 20, 20, new ResourceLocation(BurnerGun.MOD_ID, "textures/items/" + Upgrade.TRASH.getName() + "_upgrade.png"), UpgradeUtil.getUpgradeFromListByUpgrade(toggleableList, Upgrade.TRASH).isActive(),
                    (button) -> {
                        ((ToggleButton)button).setEnabled(!((ToggleButton)button).isEnabled());
                        this.toggleUpgrade(UpgradeUtil.getUpgradeFromListByUpgrade(toggleableList, Upgrade.TRASH));
                    });
            addButton(btn);
            upgradeButtons.put(UpgradeUtil.getUpgradeFromListByUpgrade(toggleableList, Upgrade.TRASH), btn);
            addButton(new Button(x+25, y, 95, 20, new TranslationTextComponent("tooltip." + BurnerGun.MOD_ID + ".screen.edit_filter"),
                    (button) -> {
                PacketHandler.sendToServer(new PacketOpenTrashGui());
            }));
            addButton(new WhitelistButton(x+125, y, 20, 20, trashFilterWhitelist,
                    (button) -> {
                trashFilterWhitelist = !trashFilterWhitelist;
                ((WhitelistButton) button).setWhitelist(trashFilterWhitelist);
                PacketHandler.sendToServer(new PacketToggleTrashFilter());
            }));
        }

        if (containsSmelt){
            ToggleButton btn = new ToggleButton(x, y+(containsTrash?25:0), 20, 20, new ResourceLocation(BurnerGun.MOD_ID, "textures/items/" + Upgrade.AUTO_SMELT.getName() + "_upgrade.png"), UpgradeUtil.getUpgradeFromListByUpgrade(toggleableList, Upgrade.AUTO_SMELT).isActive(),
                    (button) -> {
                ((ToggleButton)button).setEnabled(!((ToggleButton)button).isEnabled());
                this.toggleUpgrade(UpgradeUtil.getUpgradeFromListByUpgrade(toggleableList, Upgrade.AUTO_SMELT));
            });
            addButton(btn);
            upgradeButtons.put(UpgradeUtil.getUpgradeFromListByUpgrade(toggleableList, Upgrade.AUTO_SMELT), btn);
            addButton(new Button(x+25, y+(containsTrash?25:0), 95, 20, new TranslationTextComponent("tooltip." + BurnerGun.MOD_ID + ".screen.edit_filter"), (button) -> {
                PacketHandler.sendToServer(new PacketOpenAutoSmeltGui());
            }));
            addButton(new WhitelistButton(x+125, y+(containsTrash?25:0), 20, 20, smeltFilterWhitelist, (button) -> {
                smeltFilterWhitelist = !smeltFilterWhitelist;
                ((WhitelistButton) button).setWhitelist(smeltFilterWhitelist);
                PacketHandler.sendToServer(new PacketToggleSmeltFilter());
            }));
        }

        for (Upgrade upgrade : toggleableList){
            if (!upgrade.equals(Upgrade.AUTO_SMELT) && !upgrade.equals(Upgrade.TRASH)){
                ToggleButton btn = new ToggleButton(x + (index*25), y+(containsTrash?25:0)+(containsSmelt?25:0), 20, 20, new ResourceLocation(BurnerGun.MOD_ID, "textures/items/" + upgrade.getName() + "_upgrade.png"), upgrade.isActive(),
                        (button) -> {
                            ((ToggleButton)button).setEnabled(!((ToggleButton)button).isEnabled());
                            this.toggleUpgrade(upgrade);
                        });
                addButton(btn);
                upgradeButtons.put(upgrade, btn);
                index++;
                if (index % 4 == 0) {
                    index = 0;
                    y += 25;
                }
            }
        }

        //Left Side
        settings.add(volumeSlider = new Slider(midX-140, 0, 125, 20, new TranslationTextComponent("tooltip." + BurnerGun.MOD_ID + ".screen.volume"), new StringTextComponent("%"), 0, 100,  Math.min(100, volume * 100), false, true, slider -> {}, this));
        settings.add(raycastSlider = new Slider(midX-140, 0, 125, 20, new TranslationTextComponent("tooltip." + BurnerGun.MOD_ID + ".screen.raycast"), new StringTextComponent(""), 1, maxRaycastRange, raycastRange, false, true, slider -> {}, this));
        settings.add(verticalSlider = new Slider(midX-140, 0, 125, 20, new TranslationTextComponent("tooltip." + BurnerGun.MOD_ID + ".screen.vertical"), new StringTextComponent(""), 0, maxVertical, vertical, false, true, slider -> {}, this));
        settings.add(horizontalSlider = new Slider(midX-140, 0, 125, 20, new TranslationTextComponent("tooltip." + BurnerGun.MOD_ID + ".screen.horizontal"), new StringTextComponent(""), 0, maxHorizontal, horizontal, false, true, slider -> {}, this));
        settings.add(collectedBlocksSlider = new Slider(midX - 140, 0, 125, 20, new TranslationTextComponent("tooltip." + BurnerGun.MOD_ID + ".screen.collectedBlocks"), new StringTextComponent(""), 0, maxCollectedBlocks, collectedBlocks, false, true, slider -> {}, this));
        settings.add(colorButton = new Button(midX-140, 0, 125, 20, new TranslationTextComponent("tooltip." + BurnerGun.MOD_ID + ".screen.color"), button -> {
            ModScreens.openColorScreen(gun);
        }));

        int top = midY-(((settings.size()*20)+(settings.size()-1)*5)/2);
        for (int i = 0; i < settings.size(); i++) {
            settings.get(i).y = (top)+(i*25);
            addButton(settings.get(i));
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void removed() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putFloat("Volume", volume);
        nbt.putInt("Raycast", raycastRange);
        nbt.putInt("Vertical", vertical);
        nbt.putInt("Horizontal", horizontal);
        nbt.putInt("Collected_Blocks", collectedBlocks);
        PacketHandler.sendToServer(new PacketChangeSettings(nbt));
        super.removed();
    }

    @Override
    public boolean mouseReleased(double p_231048_1_, double p_231048_3_, int p_231048_5_) {
        volumeSlider.dragging = false;
        raycastSlider.dragging = false;
        verticalSlider.dragging = false;
        horizontalSlider.dragging = false;
        collectedBlocksSlider.dragging = false;
        return false;
    }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (volumeSlider.isMouseOver(mouseX, mouseY)) {
            volumeSlider.sliderValue += (1f/100 * (delta > 0 ? 1 : -1));
            volumeSlider.updateSlider();
        }
        if (raycastSlider.isMouseOver(mouseX, mouseY)) {
            raycastSlider.sliderValue += (1f/(maxRaycastRange-1) * (delta > 0 ? 1 : -1));
            raycastSlider.updateSlider();
        }
        if (verticalSlider.isMouseOver(mouseX, mouseY)) {
            verticalSlider.sliderValue += (1f/maxVertical * (delta > 0 ? 1 : -1));
            verticalSlider.updateSlider();
        }
        if (horizontalSlider.isMouseOver(mouseX, mouseY)) {
            horizontalSlider.sliderValue += (1f/maxHorizontal * (delta > 0 ? 1 : -1));
            horizontalSlider.updateSlider();
        }
        if (collectedBlocksSlider.isMouseOver(mouseX, mouseY)) {
            collectedBlocksSlider.sliderValue += (1f/maxCollectedBlocks * (delta > 0 ? 1 : -1));
            collectedBlocksSlider.updateSlider();
        }
        return false;
    }

    @Override
    public boolean keyPressed(int p_231046_1_, int p_231046_2_, int p_231046_3_) {
        InputMappings.Input key = InputMappings.getKey(p_231046_1_, p_231046_2_);
        if (p_231046_1_ == 256 || minecraft.options.keyInventory.isActiveAndMatches(key)){
            onClose();
            return true;
        }
        return super.keyPressed(p_231046_1_, p_231046_2_, p_231046_3_);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float ticks_) {
        //Gives us the darkened background
        this.renderBackground(matrixStack);
        TranslationTextComponent string  = new TranslationTextComponent("tooltip." + BurnerGun.MOD_ID + ".screen.settings");
        drawString(matrixStack, Minecraft.getInstance().font, string , (width/2)-StringUtil.getStringPixelLength(string.getString())/2, 20, Color.WHITE.getRGB());
        super.render(matrixStack, mouseX, mouseY, ticks_);
    }

    @Override
    public void onChangeSliderValue(Slider slider) {
        if (slider.equals(volumeSlider)){
            this.volume = slider.getValueInt()/100f;
        }
        if (slider.equals(raycastSlider)){
            this.raycastRange = slider.getValueInt();
        }
        if (slider.equals(verticalSlider)){
            this.vertical = slider.getValueInt();
        }
        if (slider.equals(horizontalSlider)){
            this.horizontal = slider.getValueInt();
        }
        if (slider.equals(collectedBlocksSlider)) {
            this.collectedBlocks = slider.getValueInt();
        }
    }

    public static final class WhitelistButton extends Button {
        private boolean isWhitelist;

        public WhitelistButton(int widthIn, int heightIn, int width, int height, boolean isWhitelist, IPressable onPress) {
            super(widthIn, heightIn, width, height, new StringTextComponent(""), onPress);
            this.isWhitelist = isWhitelist;
        }

        @Override
        public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
            fill(stack, this.x, this.y, this.x + this.width, this.y + this.height, 0xFFa8a8a8);
            fill(stack, this.x + 2, this.y + 2, this.x + this.width - 2, this.y + this.height - 2, this.isWhitelist ? 0xFFFFFFFF : 0xFF000000);
        }

        public void setWhitelist(boolean whitelist) {
            this.isWhitelist = whitelist;
        }
    }

    public static final class ToggleButton extends Button {
        private boolean isEnabled;
        private ResourceLocation texture;

        public ToggleButton(int widthIn, int heightIn, int width, int height, ResourceLocation texture, boolean isEnabled, IPressable onPress) {
            super(widthIn, heightIn, width, height, new StringTextComponent(""), onPress);
            this.isEnabled = isEnabled;
            this.texture = texture;
        }

        @Override
        public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
            Color activeColor = this.isEnabled ? Color.GREEN : Color.RED;

            RenderSystem.enableBlend();
            RenderSystem.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value, GlStateManager.SourceFactor.ONE.value, GlStateManager.DestFactor.ZERO.value);
            RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA.value, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA.value);

            RenderSystem.disableTexture();
            RenderSystem.color4f(activeColor.getRed() / 255f, activeColor.getGreen() / 255f, activeColor.getBlue() / 255f, this.isEnabled ? .4f : .6f);
            blit(stack, this.x, this.y, 0, 0, this.width, this.height);
            RenderSystem.enableTexture();

            RenderSystem.color4f(1f, 1f, 1f, 1f);
            Minecraft.getInstance().getTextureManager().bind(this.texture);
            blit(stack, this.x+2, this.y+2, 0, 0, 16, 16, 16, 16);
        }

        public List<IReorderingProcessor> getTooltip() {
            return LanguageMap.getInstance().getVisualOrder(Arrays.asList(this.getMessage(), new StringTextComponent("Enabled: " + this.isEnabled).withStyle(this.isEnabled ? TextFormatting.GREEN : TextFormatting.RED)));
        }

        public void setEnabled(boolean enable) { this.isEnabled = enable; }
        public boolean isEnabled() {return this.isEnabled; }
    }
}
