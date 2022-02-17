package com.nindybun.burnergun.client.screens;


import com.mojang.blaze3d.matrix.MatrixStack;
import com.nindybun.burnergun.common.BurnerGun;
import com.nindybun.burnergun.common.capabilities.burnergunmk1.BurnerGunMK1Info;
import com.nindybun.burnergun.common.capabilities.burnergunmk1.BurnerGunMK1InfoProvider;
import com.nindybun.burnergun.common.network.PacketHandler;
import com.nindybun.burnergun.common.network.packets.PacketChangeVolume;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.Slider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;

public class mk1SettingsScreen extends Screen implements Slider.ISlider {
    private static BurnerGunMK1Info info;
    private static final Logger LOGGER = LogManager.getLogger();
    private float volume;
    private Slider  volumeSlider;

    protected mk1SettingsScreen(ItemStack gun) {
        super(new StringTextComponent("Title"));
        this.info = gun.getCapability(BurnerGunMK1InfoProvider.burnerGunInfoMK1Capability, null).orElseThrow(()->new IllegalArgumentException("No capability found!"));
        this.volume = info.getVolume();
    }

    @Override
    protected void init() {
        int midX = width/2;
        int midY = height/2;
        this.addButton(volumeSlider = new Slider(midX-75, midY-10, 125, 20, new TranslationTextComponent("tooltip." + BurnerGun.MOD_ID + ".screen.volume"), new StringTextComponent("%"), 0, 100, Math.min(100, volume*100), false, true, slider -> {}, this));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    @Override
    public void removed() {
        PacketHandler.sendToServer(new PacketChangeVolume(this.volume));
        super.removed();
    }

    @Override
    public boolean mouseReleased(double p_231048_1_, double p_231048_3_, int p_231048_5_) {
        volumeSlider.dragging = false;
        return false;
    }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if( volumeSlider.isMouseOver(mouseX, mouseY) ) {
            volumeSlider.sliderValue += (.01f * (delta > 0 ? 1 : -1));
            volumeSlider.updateSlider();
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
        this.renderBackground(matrixStack);
        drawString(matrixStack, Minecraft.getInstance().font, new TranslationTextComponent("tooltip." + BurnerGun.MOD_ID + ".screen.mk1Settings"), (width/2)-75, (height/2)-50, Color.WHITE.getRGB());
        super.render(matrixStack, mouseX, mouseY, ticks_);
    }

    @Override
    public void onChangeSliderValue(Slider slider) {
        if (slider.equals(volumeSlider)){
            this.volume = slider.getValueInt()/100f;
        }
    }
}
