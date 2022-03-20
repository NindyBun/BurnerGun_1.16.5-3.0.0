package com.nindybun.burnergun.common.network.packets;

import com.nindybun.burnergun.client.screens.ModScreens;
import com.nindybun.burnergun.common.items.burnergunmk1.BurnerGunMK1;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.IItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.invoke.SerializedLambda;
import java.util.function.Supplier;

public class PacketClientRefuel {
    private static final Logger LOGGER = LogManager.getLogger();
    private static ItemStack gun, container;
    public PacketClientRefuel(ItemStack gun, ItemStack container) {
        this.gun = gun;
        this.container = container;
    }

    public static void encode(PacketClientRefuel msg, PacketBuffer buffer) {
        buffer.writeItemStack(msg.gun, false);
        buffer.writeItemStack(msg.container, true);
    }

    public static PacketClientRefuel decode(PacketBuffer buffer) {
        return new PacketClientRefuel(gun, container);
    }

    public static class Handler {
        public static void handle(PacketClientRefuel msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                    ClientPlayerEntity player = Minecraft.getInstance().player;
                    if (player == null)
                        return;
                    IItemHandler handler = BurnerGunMK1.getHandler(gun);
                    handler.insertItem(0, container, false);
                });
            });
            ctx.get().setPacketHandled(true);
        }
    }
}