package com.nindybun.burnergun.common.network.packets;

import com.nindybun.burnergun.common.items.BurnerGunNBT;
import com.nindybun.burnergun.common.items.burnergunmk1.BurnerGunMK1;
import com.nindybun.burnergun.common.items.upgrades.Upgrade;
import com.nindybun.burnergun.common.items.upgrades.UpgradeCard;
import com.nindybun.burnergun.common.network.PacketHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.IItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class PacketRefuel {
    private static final Logger LOGGER = LogManager.getLogger();

    public PacketRefuel(){

    }

    public static void encode(PacketRefuel msg, PacketBuffer buffer){

    }

    public static PacketRefuel decode(PacketBuffer buffer){
        return new PacketRefuel();
    }

    public static class Handler {
        public static void handle(PacketRefuel msg, Supplier<NetworkEvent.Context> ctx){
            ctx.get().enqueueWork( ()-> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null)
                    return;
                ItemStack gun = BurnerGunMK1.getGun(player);
                if (gun.isEmpty())
                    return;
                IItemHandler handler = BurnerGunMK1.getHandler(gun);
                ItemStack stack = handler.getStackInSlot(0);
                if (stack == ItemStack.EMPTY
                        || stack.getItem() == Items.BUCKET
                        || handler.getStackInSlot(0).getItem() instanceof UpgradeCard)
                    return;
                while (handler.getStackInSlot(0).getCount() > 0 && ForgeHooks.getBurnTime(handler.getStackInSlot(0)) > 0){
                    if (BurnerGunNBT.getFuelValue(gun) + net.minecraftforge.common.ForgeHooks.getBurnTime(handler.getStackInSlot(0)) > BurnerGunMK1.base_use_buffer)
                        break;
                    BurnerGunNBT.setFuelValue(gun, BurnerGunNBT.getFuelValue(gun) + net.minecraftforge.common.ForgeHooks.getBurnTime(handler.getStackInSlot(0)));
                    ItemStack containerItem = handler.getStackInSlot(0).getContainerItem();
                    handler.getStackInSlot(0).shrink(1);
                    if (!containerItem.isEmpty())
                        handler.insertItem(0, containerItem, false);
                }
            });

            ctx.get().setPacketHandled(true);
        }
    }
}
