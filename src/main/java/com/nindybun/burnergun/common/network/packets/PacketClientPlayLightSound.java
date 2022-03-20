package com.nindybun.burnergun.common.network.packets;

import com.nindybun.burnergun.common.items.BurnerGunNBT;
import com.nindybun.burnergun.common.items.burnergunmk1.BurnerGunMK1;
import com.nindybun.burnergun.common.items.burnergunmk2.BurnerGunMK2;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
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
    private float volume;
    public PacketClientPlayLightSound(float volume) {
        this.volume = volume;
    }

    public static void encode(PacketClientPlayLightSound msg, PacketBuffer buffer) {
        buffer.writeFloat(msg.volume);
    }

    public static PacketClientPlayLightSound decode(PacketBuffer buffer) {
        return new PacketClientPlayLightSound(buffer.readFloat());
    }

    public static class Handler {
        public static void handle(PacketClientPlayLightSound msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                    if (Minecraft.getInstance().player == null)
                        return;
                    Minecraft.getInstance().player.playSound(SoundEvents.WOOL_PLACE, msg.volume*0.5f, 1.0f);
                });
            });
            ctx.get().setPacketHandled(true);
        }
    }
}