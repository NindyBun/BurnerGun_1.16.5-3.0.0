package com.nindybun.burnergun.client.screens;


import com.mojang.blaze3d.matrix.MatrixStack;
import com.nindybun.burnergun.client.screens.buttons.ToggleButton;
import com.nindybun.burnergun.common.BurnerGun;
import com.nindybun.burnergun.common.capabilities.burnergunmk1.BurnerGunMK1Info;
import com.nindybun.burnergun.common.capabilities.burnergunmk2.BurnerGunMK2Info;
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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.Slider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                maxHorizontal;
    private float volume;
    private boolean trashFilterWhitelist, containsTrash;
    private boolean smeltFilterWhitelist, containsSmelt;
    private Slider  raycastSlider,
                    volumeSlider,
                    verticalSlider,
                    horizontalSlider;
    private Button colorButton;

    protected burnergunSettingsScreen(ItemStack gun) {
        super(new StringTextComponent("Title"));
        this.gun = gun;
        BurnerGunMK1Info infoMK1 = gun.getItem() instanceof BurnerGunMK1 ? BurnerGunMK1.getInfo(gun) : null;
        BurnerGunMK2Info infoMK2 = gun.getItem() instanceof BurnerGunMK2 ? BurnerGunMK2.getInfo(gun) : null;
        this.volume = infoMK1 != null ? infoMK1.getVolume() : infoMK2.getVolume();
        this.vertical = infoMK1 != null ? infoMK1.getVertical() : infoMK2.getVertical();
        this.maxVertical = infoMK1 != null ? infoMK1.getMaxVertical() : infoMK2.getMaxVertical();
        this.horizontal = infoMK1 != null ? infoMK1.getHorizontal() : infoMK2.getHorizontal();
        this.maxHorizontal = infoMK1 != null ? infoMK1.getMaxHorizontal() : infoMK2.getMaxHorizontal();
        this.raycastRange = infoMK1 != null ? infoMK1.getRaycastRange() : infoMK2.getRaycastRange();
        this.maxRaycastRange = infoMK1 != null ? infoMK1.getMaxRaycastRange() : infoMK2.getMaxRaycastRange();
        this.trashFilterWhitelist = infoMK1 != null ? infoMK1.getTrashIsWhitelist() : infoMK2.getTrashIsWhitelist();
        this.smeltFilterWhitelist = infoMK1 != null ? infoMK1.getSmeltIsWhitelist() : infoMK2.getSmeltIsWhitelist();

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

    private boolean toggleUpgrade(Upgrade upgrade, boolean update) {
        if (update){
            this.updateButtons(upgrade);
            PacketHandler.sendToServer(new PacketUpdateUpgrade(upgrade.getName()));
        }
        return upgrade.isActive();
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
            ToggleButton btn = new ToggleButton(x, y, new StringTextComponent(Upgrade.TRASH.getName()), new ResourceLocation(BurnerGun.MOD_ID, "textures/items/" + Upgrade.TRASH.getName() + "_upgrade.png"), send -> this.toggleUpgrade(UpgradeUtil.getUpgradeFromListByUpgrade(toggleableList, Upgrade.TRASH), send));
            addButton(btn);
            upgradeButtons.put(UpgradeUtil.getUpgradeFromListByUpgrade(toggleableList, Upgrade.TRASH), btn);
            addButton(new Button(x+25, y, 95, 20, new TranslationTextComponent("tooltip." + BurnerGun.MOD_ID + ".screen.edit_filter"), (button) -> {
                PacketHandler.sendToServer(new PacketOpenTrashGui());
            }));
            addButton(new WhitelistButton(x+125, y, 20, 20, trashFilterWhitelist, (button) -> {
                trashFilterWhitelist = !trashFilterWhitelist;
                ((WhitelistButton) button).setWhitelist(trashFilterWhitelist);
                PacketHandler.sendToServer(new PacketToggleTrashFilter());
            }));
        }

        if (containsSmelt){
            ToggleButton btn = new ToggleButton(x, y+(containsTrash?25:0), new StringTextComponent(Upgrade.AUTO_SMELT.getName()), new ResourceLocation(BurnerGun.MOD_ID, "textures/items/" + Upgrade.AUTO_SMELT.getName() + "_upgrade.png"), send -> this.toggleUpgrade(UpgradeUtil.getUpgradeFromListByUpgrade(toggleableList, Upgrade.AUTO_SMELT), send));
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
                ToggleButton btn = new ToggleButton(x + (index*25), y+(containsTrash?25:0)+(containsSmelt?25:0), new StringTextComponent(upgrade.getName()), new ResourceLocation(BurnerGun.MOD_ID, "textures/items/" + upgrade.getName() + "_upgrade.png"), send -> this.toggleUpgrade(upgrade, send));
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
        PacketHandler.sendToServer(new PacketChangeSettings(nbt));
        super.removed();
    }

    @Override
    public boolean mouseReleased(double p_231048_1_, double p_231048_3_, int p_231048_5_) {
        volumeSlider.dragging = false;
        raycastSlider.dragging = false;
        verticalSlider.dragging = false;
        horizontalSlider.dragging = false;
        return false;
    }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if( volumeSlider.isMouseOver(mouseX, mouseY) ) {
            volumeSlider.sliderValue += (.01f * (delta > 0 ? 1 : -1));
            volumeSlider.updateSlider();
        }
        if( raycastSlider.isMouseOver(mouseX, mouseY) ) {
            raycastSlider.sliderValue += (delta > 0 ? 1 : -1);
            raycastSlider.updateSlider();
        }
        if( verticalSlider.isMouseOver(mouseX, mouseY) ) {
            verticalSlider.sliderValue += (delta > 0 ? 1 : -1);
            verticalSlider.updateSlider();
        }
        if( horizontalSlider.isMouseOver(mouseX, mouseY) ) {
            horizontalSlider.sliderValue += (delta > 0 ? 1 : -1);
            horizontalSlider.updateSlider();
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
            isWhitelist = whitelist;
        }
    }
}
