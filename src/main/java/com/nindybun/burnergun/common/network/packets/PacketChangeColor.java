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

public class PacketChangeColor {
    private CompoundNBT nbt;
    private static final Logger LOGGER = LogManager.getLogger();

    public PacketChangeColor(CompoundNBT nbt){
        this.nbt = nbt;
    }

    public static void encode(PacketChangeColor msg, PacketBuffer buffer){
        buffer.writeNbt(msg.nbt);
    }

    public static PacketChangeColor decode(PacketBuffer buffer){
        return new PacketChangeColor(buffer.readNbt());
    }

    public static class Handler {
        public static void handle(PacketChangeColor msg, Supplier<NetworkEvent.Context> ctx){
            ctx.get().enqueueWork( ()-> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null)
                    return;
                ItemStack gun = !BurnerGunMK2.getGun(player).isEmpty() ? BurnerGunMK2.getGun(player) : BurnerGunMK1.getGun(player);
                if (gun.isEmpty())
                    return;
                BurnerGunNBT.setColor(gun, new float[]{
                        msg.nbt.getFloat("Red"),
                        msg.nbt.getFloat("Green"),
                        msg.nbt.getFloat("Blue")
                });
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
