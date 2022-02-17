package com.nindybun.burnergun.common.network.packets;

import com.nindybun.burnergun.common.capabilities.burnergunmk1.BurnerGunMK1Info;
import com.nindybun.burnergun.common.capabilities.burnergunmk2.BurnerGunMK2Info;
import com.nindybun.burnergun.common.items.burnergunmk1.BurnerGunMK1;
import com.nindybun.burnergun.common.items.burnergunmk2.BurnerGunMK2;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
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
                BurnerGunMK1Info infoMK1 = BurnerGunMK1.getInfo(gun);
                BurnerGunMK2Info infoMK2 = BurnerGunMK2.getInfo(gun);

                ListNBT color = new ListNBT();
                color.add(msg.nbt);

                if (infoMK1 != null){
                    infoMK1.setColor(color);
                }else{
                    infoMK2.setColor(color);
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
