package com.nindybun.burnergun.common.network.packets;

import com.nindybun.burnergun.client.screens.ModScreens;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class PacketClientUpdateGun {
    private static final Logger LOGGER = LogManager.getLogger();
    private ItemStack gun;
    public PacketClientUpdateGun(ItemStack stack) {
        this.gun = stack;
    }

    public static void encode(PacketClientUpdateGun msg, PacketBuffer buffer) {
    buffer.writeItemStack(msg.gun, false);
    }

    public static PacketClientUpdateGun decode(PacketBuffer buffer) {
        return new PacketClientUpdateGun(buffer.readItem());
    }

    public static class Handler {
        public static void handle(PacketClientUpdateGun msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                    ModScreens.openGunSettingsScreen(msg.gun);
                });
            });
            ctx.get().setPacketHandled(true);
        }
    }
}