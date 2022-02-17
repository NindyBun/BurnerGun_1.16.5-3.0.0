package com.nindybun.burnergun.client.renderer;

import com.nindybun.burnergun.common.BurnerGun;
import com.nindybun.burnergun.common.capabilities.burnergunmk1.BurnerGunMK1Info;
import com.nindybun.burnergun.common.capabilities.burnergunmk1.BurnerGunMK1InfoProvider;
import com.nindybun.burnergun.common.items.burnergunmk1.BurnerGunMK1;
import com.nindybun.burnergun.common.items.upgrades.Upgrade;
import com.nindybun.burnergun.common.items.upgrades.UpgradeCard;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@Mod.EventBusSubscriber(modid = BurnerGun.MOD_ID, value = Dist.CLIENT)
public class FuelValueRenderer {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final double base_buffer = BurnerGunMK1.base_use_buffer;

    @SubscribeEvent
    public static void renderOverlay(@Nonnull RenderGameOverlayEvent.Post event){
        if (event.getType() == RenderGameOverlayEvent.ElementType.ALL){
            ItemStack stack = ItemStack.EMPTY;
            Minecraft mc = Minecraft.getInstance();
            ClientPlayerEntity player = mc.player;
            if (player.getMainHandItem().getItem() instanceof BurnerGunMK1)
                stack = player.getMainHandItem();
            else if (player.getOffhandItem().getItem() instanceof BurnerGunMK1)
                stack = player.getOffhandItem();
            if (stack.getItem() instanceof BurnerGunMK1)
                renderFuel(event, stack);

        }
    }

    public static void renderFuel(RenderGameOverlayEvent.Post event, ItemStack stack){
        BurnerGunMK1Info info = stack.getCapability(BurnerGunMK1InfoProvider.burnerGunInfoMK1Capability, null).orElseThrow(()->new IllegalArgumentException("No capability found!"));
        FontRenderer fontRenderer = Minecraft.getInstance().font;
        int level = (int)info.getFuelValue();
        Color color;
        if (level > base_buffer*3/4)
                color = Color.GREEN;
        else if (level > base_buffer*1/4 && level <= base_buffer*3/4)
            color = Color.ORANGE;
        else
            color = Color.RED;
        fontRenderer.draw(event.getMatrixStack(), "Fuel level: ", 6, event.getWindow().getGuiScaledHeight()-12, Color.WHITE.getRGB());
        fontRenderer.draw(event.getMatrixStack(), level+"", 61, event.getWindow().getGuiScaledHeight()-12, color.getRGB());
    }
}
