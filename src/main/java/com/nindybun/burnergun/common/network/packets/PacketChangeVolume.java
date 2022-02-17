package com.nindybun.burnergun.common.network.packets;

import com.nindybun.burnergun.common.capabilities.burnergunmk1.BurnerGunMK1Info;
import com.nindybun.burnergun.common.capabilities.burnergunmk1.BurnerGunMK1InfoProvider;
import com.nindybun.burnergun.common.items.burnergunmk1.BurnerGunMK1;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class PacketChangeVolume {
    private float volume;
    private static final Logger LOGGER = LogManager.getLogger();

    public PacketChangeVolume(float volume){
        this.volume = volume;
    }

    public static void encode(PacketChangeVolume msg, PacketBuffer buffer){
        buffer.writeFloat(msg.volume);
    }

    public static PacketChangeVolume decode(PacketBuffer buffer){
        return new PacketChangeVolume(buffer.readFloat());
    }

    public static class Handler {
        public static void handle(PacketChangeVolume msg, Supplier<NetworkEvent.Context> ctx){
            ctx.get().enqueueWork( ()-> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null)
                    return;
                ItemStack gun = BurnerGunMK1.getGun(player);
                if (gun.isEmpty())
                    return;
                BurnerGunMK1Info info = gun.getCapability(BurnerGunMK1InfoProvider.burnerGunInfoMK1Capability, null).orElseThrow(()->new IllegalArgumentException("No capability found!"));
                info.setVolume(msg.volume);
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
