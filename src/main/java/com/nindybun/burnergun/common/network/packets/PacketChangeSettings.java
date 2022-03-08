package com.nindybun.burnergun.common.network.packets;

import com.nindybun.burnergun.common.items.BurnerGunNBT;
import com.nindybun.burnergun.common.items.burnergunmk1.BurnerGunMK1;
import com.nindybun.burnergun.common.items.burnergunmk2.BurnerGunMK2;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class PacketChangeSettings {
    private CompoundNBT nbt;
    private static final Logger LOGGER = LogManager.getLogger();

    public PacketChangeSettings(CompoundNBT nbt){
        this.nbt = nbt;
    }

    public static void encode(PacketChangeSettings msg, PacketBuffer buffer){
        buffer.writeNbt(msg.nbt);
    }

    public static PacketChangeSettings decode(PacketBuffer buffer){
        return new PacketChangeSettings(buffer.readNbt());
    }

    public static class Handler {
        public static void handle(PacketChangeSettings msg, Supplier<NetworkEvent.Context> ctx){
            ctx.get().enqueueWork( ()-> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null)
                    return;
                ItemStack gun = !BurnerGunMK2.getGun(player).isEmpty() ? BurnerGunMK2.getGun(player) : BurnerGunMK1.getGun(player);
                if (gun.isEmpty())
                    return;
                BurnerGunNBT.setVolume(gun, msg.nbt.getFloat("Volume"));
                BurnerGunNBT.setRaycast(gun, msg.nbt.getInt("Raycast"));
                BurnerGunNBT.setVertical(gun, msg.nbt.getInt("Vertical"));
                BurnerGunNBT.setHorizontal(gun, msg.nbt.getInt("Horizontal"));
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
