package com.nindybun.burnergun.common.network.packets;

import com.nindybun.burnergun.common.capabilities.burnergunmk1.BurnerGunMK1Info;
import com.nindybun.burnergun.common.capabilities.burnergunmk1.BurnerGunMK1InfoProvider;
import com.nindybun.burnergun.common.capabilities.burnergunmk2.BurnerGunMK2Info;
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
                BurnerGunMK1Info infoMK1 = BurnerGunMK1.getInfo(gun);
                BurnerGunMK2Info infoMK2 = BurnerGunMK2.getInfo(gun);
                if (infoMK1 != null){
                    infoMK1.setVolume(msg.nbt.getFloat("Volume"));
                    infoMK1.setRaycastRange(msg.nbt.getInt("Raycast"));
                    infoMK1.setVertical(msg.nbt.getInt("Vertical"));
                    infoMK1.setHorizontal(msg.nbt.getInt("Horizontal"));
                }else{
                    infoMK2.setVolume(msg.nbt.getFloat("Volume"));
                    infoMK2.setRaycastRange(msg.nbt.getInt("Raycast"));
                    infoMK2.setVertical(msg.nbt.getInt("Vertical"));
                    infoMK2.setHorizontal(msg.nbt.getInt("Horizontal"));
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
