package com.nindybun.burnergun.common.items.burnergunmk1;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.nindybun.burnergun.client.screens.ModScreens;
import com.nindybun.burnergun.common.BurnerGun;
import com.nindybun.burnergun.common.capabilities.burnergunmk1.BurnerGunMK1Info;
import com.nindybun.burnergun.common.containers.BurnerGunMK1Container;
import com.nindybun.burnergun.common.network.PacketHandler;
import com.nindybun.burnergun.common.network.packets.PacketOpenTrashGui;
import com.nindybun.burnergun.common.network.packets.PacketUpdateGun;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fml.client.gui.widget.Slider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.CallbackI;

import java.awt.*;
import java.security.SecureRandom;

public class BurnerGunMK1Screen extends ContainerScreen<BurnerGunMK1Container> {
    private static ItemStack gun;
    public BurnerGunMK1Screen(BurnerGunMK1Container container, PlayerInventory playerInv, ITextComponent title) {
        super(container, playerInv, title);
        this.gun = BurnerGunMK1.getGun(playerInv.player);
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        this.renderTooltip(matrixStack, mouseX, mouseY);
    }

    @Override
    protected void renderBg(MatrixStack matrixStack, float partialTicks, int x, int y) {
        RenderSystem.blendColor(1.0F, 1.0F, 1.0F, 1.0F);
        //this.minecraft
        this.minecraft.getTextureManager().bind(DEFAULT_TEXTURE);

        // width and height are the size provided to the window when initialised after creation.
        // xSize, ySize are the expected size of the texture-? usually seems to be left as a default.
        // The code below is typical for vanilla containers, so I've just copied that- it appears to centre the texture within
        //  the available window
        int edgeSpacingX = (this.width - this.getXSize()) / 2;
        int edgeSpacingY = (this.height - this.getYSize()) / 2;
        this.blit(matrixStack, edgeSpacingX, edgeSpacingY, 0, 0, this.getXSize(), this.getYSize());
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int x, int y) {
        this.font.draw(matrixStack, new TranslationTextComponent("tooltip." + BurnerGun.MOD_ID + ".screen.burnergunmk1"), 19, -8, Color.WHITE.getRGB());
    }

    private static final Logger LOGGER = LogManager.getLogger();
    // This is the resource location for the background image
    private static final ResourceLocation DEFAULT_TEXTURE = new ResourceLocation(BurnerGun.MOD_ID, "textures/gui/burnergunmk1_gui.png");

    @Override
    public void removed() {
        PacketHandler.sendToServer(new PacketUpdateGun(false));
        super.removed();
    }

    @Override
    public void init(Minecraft p_231158_1_, int p_231158_2_, int p_231158_3_) {
        super.init(p_231158_1_, p_231158_2_, p_231158_3_);
        buttons.clear();
        int x = this.width / 2;
        int y = this.height / 2;
        addButton(new Button(x-45, y+(130/2), 90, 20,
                new TranslationTextComponent("tooltip." + BurnerGun.MOD_ID + ".screen.openSettings"), (button) -> {
            //ModScreens.openGunSettingsScreen(gun);
            PacketHandler.sendToServer(new PacketUpdateGun(true));
        }));
    }

}
