package com.nindybun.burnergun.client.screens;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.nindybun.burnergun.common.BurnerGun;
import com.nindybun.burnergun.common.capabilities.burnergunmk1.BurnerGunMK1Info;
import com.nindybun.burnergun.common.capabilities.burnergunmk2.BurnerGunMK2Info;
import com.nindybun.burnergun.common.items.burnergunmk1.BurnerGunMK1;
import com.nindybun.burnergun.common.items.burnergunmk2.BurnerGunMK2;
import com.nindybun.burnergun.common.network.PacketHandler;
import com.nindybun.burnergun.common.network.packets.PacketChangeColor;
import com.nindybun.burnergun.util.StringUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.util.InputMappings;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.Slider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class colorScreen extends Screen implements Slider.ISlider {
    private static final Logger LOGGER = LogManager.getLogger();
    private Slider  redSlider,
                    greenSlider,
                    blueSlider;
    private float[] color = new float[3];
    protected colorScreen(ItemStack gun) {
        super(new StringTextComponent("title"));
        BurnerGunMK1Info infoMK1 = BurnerGunMK1.getInfo(gun);
        BurnerGunMK2Info infoMK2 = BurnerGunMK2.getInfo(gun);
        color[0] = infoMK1 != null ? infoMK1.getColor().getCompound(0).getFloat("Red") : infoMK2.getColor().getCompound(0).getFloat("Red");
        color[1] = infoMK1 != null ? infoMK1.getColor().getCompound(0).getFloat("Green") : infoMK2.getColor().getCompound(0).getFloat("Green");
        color[2] = infoMK1 != null ? infoMK1.getColor().getCompound(0).getFloat("Blue") : infoMK2.getColor().getCompound(0).getFloat("Blue");
    }

    @Override
    protected void init() {
        List<Widget> settings = new ArrayList<>();
        int midX = width/2;
        int midY = height/2;

        //Left Side
        settings.add(redSlider = new Slider(midX-140, 0, 125, 20, new TranslationTextComponent("tooltip." + BurnerGun.MOD_ID + ".screen.red"), new StringTextComponent(""), 0, 100,  Math.min(100, color[0] * 100), false, true, slider -> {}, this));
        settings.add(greenSlider = new Slider(midX-140, 0, 125, 20, new TranslationTextComponent("tooltip." + BurnerGun.MOD_ID + ".screen.green"), new StringTextComponent(""), 0, 100, Math.min(100, color[1] * 100), false, true, slider -> {}, this));
        settings.add(blueSlider = new Slider(midX-140, 0, 125, 20, new TranslationTextComponent("tooltip." + BurnerGun.MOD_ID + ".screen.blue"), new StringTextComponent(""), 0, 100, Math.min(100, color[2] * 100), false, true, slider -> {}, this));

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
        nbt.putFloat("Red", color[0]);
        nbt.putFloat("Green", color[1]);
        nbt.putFloat("Blue", color[2]);
        PacketHandler.sendToServer(new PacketChangeColor(nbt));
        super.removed();
    }

    @Override
    public boolean mouseReleased(double p_231048_1_, double p_231048_3_, int p_231048_5_) {
        redSlider.dragging = false;
        greenSlider.dragging = false;
        blueSlider.dragging = false;
        return false;
    }
    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if( redSlider.isMouseOver(mouseX, mouseY) ) {
            redSlider.sliderValue += (delta > 0 ? 1 : -1);
            redSlider.updateSlider();
        }
        if( greenSlider.isMouseOver(mouseX, mouseY) ) {
            greenSlider.sliderValue += (delta > 0 ? 1 : -1);
            greenSlider.updateSlider();
        }
        if( blueSlider.isMouseOver(mouseX, mouseY) ) {
            blueSlider.sliderValue += (delta > 0 ? 1 : -1);
            blueSlider.updateSlider();
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
        TranslationTextComponent string = new TranslationTextComponent("tooltip." + BurnerGun.MOD_ID + ".screen.color");
        drawString(matrixStack, Minecraft.getInstance().font, string, (width/2)-StringUtil.getStringPixelLength(string.getString())/2, 20, Color.WHITE.getRGB());
        fill(matrixStack, width/2+15, height/2-50, width/2+115, height/2+50, new Color(color[0], color[1], color[2]).hashCode());
        super.render(matrixStack, mouseX, mouseY, ticks_);
    }

    @Override
    public void onChangeSliderValue(Slider slider) {
        if (slider.equals(redSlider)){
            color[0] = (float) slider.getValue()/100;
        }
        if (slider.equals(greenSlider)){
            color[1] = (float) slider.getValue()/100;
        }
        if (slider.equals(blueSlider)){
            color[2] = (float) slider.getValue()/100;
        }
    }
}
