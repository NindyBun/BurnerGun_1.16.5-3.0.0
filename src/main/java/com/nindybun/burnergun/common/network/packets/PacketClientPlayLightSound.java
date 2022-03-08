package com.nindybun.burnergun.common.network.packets;

import com.nindybun.burnergun.common.items.BurnerGunNBT;
import com.nindybun.burnergun.common.items.burnergunmk1.BurnerGunMK1;
import com.nindybun.burnergun.common.items.burnergunmk2.BurnerGunMK2;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.SoundEvents;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class PacketClientPlayLightSound {
    private static final Logger LOGGER = LogManager.getLogger();
    public PacketClientPlayLightSound() {

    }

    public static void encode(PacketClientPlayLightSound msg, PacketBuffer buffer) {
    }

    public static PacketClientPlayLightSound decode(PacketBuffer buffer) {
        return new PacketClientPlayLightSound();
    }

    public static class Handler {
        public static void handle(PacketClientPlayLightSound msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                    PlayerEntity player = Minecraft.getInstance().player;
                    if (player == null)
                        return;
                    ItemStack gun = !BurnerGunMK2.getGun(player).isEmpty() ? BurnerGunMK2.getGun(player) : BurnerGunMK1.getGun(player);
                    if (gun.isEmpty())
                        return;
                    player.playSound(SoundEvents.WOOL_PLACE, BurnerGunNBT.getVolume(gun) *0.5f, 1.0f);
                });
            });
            ctx.get().setPacketHandled(true);
        }
    }
}