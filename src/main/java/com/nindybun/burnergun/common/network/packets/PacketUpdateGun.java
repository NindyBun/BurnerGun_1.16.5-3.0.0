package com.nindybun.burnergun.common.network.packets;

import com.nindybun.burnergun.client.screens.ModScreens;
import com.nindybun.burnergun.common.capabilities.burnergunmk1.BurnerGunMK1Info;
import com.nindybun.burnergun.common.capabilities.burnergunmk2.BurnerGunMK2Info;
import com.nindybun.burnergun.common.containers.ModContainers;
import com.nindybun.burnergun.common.items.burnergunmk1.BurnerGunMK1;
import com.nindybun.burnergun.common.items.burnergunmk2.BurnerGunMK2;
import com.nindybun.burnergun.common.items.upgrades.Auto_Smelt.AutoSmelt;
import com.nindybun.burnergun.common.items.upgrades.Trash.Trash;
import com.nindybun.burnergun.common.items.upgrades.Trash.TrashHandler;
import com.nindybun.burnergun.common.items.upgrades.Upgrade;
import com.nindybun.burnergun.common.items.upgrades.UpgradeCard;
import com.nindybun.burnergun.util.UpgradeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.AirItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tags.Tag;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.thread.SidedThreadGroup;
import net.minecraftforge.fml.common.thread.SidedThreadGroups;
import net.minecraftforge.fml.network.FMLConnectionData;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

public class PacketUpdateGun {
    private static final Logger LOGGER = LogManager.getLogger();
    private static boolean open;
    public PacketUpdateGun(boolean open) {
        this.open = open;
    }

    public static void encode(PacketUpdateGun msg, PacketBuffer buffer) {
        buffer.writeBoolean(msg.open);
    }

    public static PacketUpdateGun decode(PacketBuffer buffer) {
        return new PacketUpdateGun(buffer.readBoolean());
    }

    public static class Handler {
        public static void handle(PacketUpdateGun msg, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                ServerPlayerEntity player = ctx.get().getSender();
                if (player == null)
                    return;

                ItemStack gun = !BurnerGunMK2.getGun(player).isEmpty() ? BurnerGunMK2.getGun(player) : BurnerGunMK1.getGun(player);
                if (gun.isEmpty())
                    return;
                BurnerGunMK1Info infoMK1 = BurnerGunMK1.getInfo(gun);
                BurnerGunMK2Info infoMK2 = BurnerGunMK2.getInfo(gun);

                IItemHandler handler = gun.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).orElse(null);
                List<Upgrade> currentUpgrades = new ArrayList<>();
                IItemHandler trashHandler = null;
                IItemHandler smeltHandler = null;

                int type = gun.getItem() instanceof BurnerGunMK1 ? 1 : 0;
                for (int i = type; i < handler.getSlots(); i++) {
                    if (!handler.getStackInSlot(i).getItem().equals(Items.AIR)){
                        if (UpgradeUtil.getStackByUpgrade(gun, Upgrade.TRASH) != null)
                            trashHandler = Trash.getHandler(handler.getStackInSlot(i));
                        if (UpgradeUtil.getStackByUpgrade(gun, Upgrade.AUTO_SMELT) != null)
                            smeltHandler = AutoSmelt.getHandler(handler.getStackInSlot(i));
                        currentUpgrades.add(((UpgradeCard)handler.getStackInSlot(i).getItem()).getUpgrade());
                    }
                }

                if (UpgradeUtil.containsUpgradeFromList(currentUpgrades, Upgrade.TRASH)){
                    List<ItemStack> trashFilter = new ArrayList<>();
                    for (int i = 0; i < trashHandler.getSlots(); i++){
                        if (!trashHandler.getStackInSlot(i).getItem().equals(Items.AIR))
                            trashFilter.add(trashHandler.getStackInSlot(i));
                    }
                    if (infoMK1 != null)
                        infoMK1.setTrashNBTFilter(UpgradeUtil.setFiltersNBT(trashFilter));
                    else
                        infoMK2.setTrashNBTFilter(UpgradeUtil.setFiltersNBT(trashFilter));
                }else if (!UpgradeUtil.containsUpgradeFromList(currentUpgrades, Upgrade.TRASH)){
                    if (infoMK1 != null)
                        infoMK1.setTrashNBTFilter(new ListNBT());
                    else
                        infoMK2.setTrashNBTFilter(new ListNBT());
                }

                if (UpgradeUtil.containsUpgradeFromList(currentUpgrades, Upgrade.AUTO_SMELT)){
                    List<ItemStack> smeltFilter = new ArrayList<>();
                    for (int i = 0; i < smeltHandler.getSlots(); i++){
                        if (!smeltHandler.getStackInSlot(i).getItem().equals(Items.AIR))
                            smeltFilter.add(smeltHandler.getStackInSlot(i));
                    }
                    if (infoMK1 != null)
                        infoMK1.setSmeltNBTFilter(UpgradeUtil.setFiltersNBT(smeltFilter));
                    else
                        infoMK2.setSmeltNBTFilter(UpgradeUtil.setFiltersNBT(smeltFilter));
                }else if (!UpgradeUtil.containsUpgradeFromList(currentUpgrades, Upgrade.AUTO_SMELT)){
                    if (infoMK1 != null)
                        infoMK1.setSmeltNBTFilter(new ListNBT());
                    else
                        infoMK2.setSmeltNBTFilter(new ListNBT());
                }

                if (UpgradeUtil.containsUpgradeFromList(currentUpgrades, Upgrade.FOCAL_POINT_1)){
                    Upgrade upgrade = UpgradeUtil.getUpgradeFromListByUpgrade(currentUpgrades, Upgrade.FOCAL_POINT_1);
                    if (infoMK1 != null){
                        if (infoMK1.getRaycastRange() > (int)upgrade.getExtraValue())
                            infoMK1.setRaycastRange((int)upgrade.getExtraValue());
                        infoMK1.setMaxRaycastRange((int)upgrade.getExtraValue());
                    }else{
                        if (infoMK2.getRaycastRange() > (int)upgrade.getExtraValue())
                            infoMK2.setRaycastRange((int)upgrade.getExtraValue());
                        infoMK2.setMaxRaycastRange((int)upgrade.getExtraValue());
                    }
                }else if (!UpgradeUtil.containsUpgradeFromList(currentUpgrades, Upgrade.FOCAL_POINT_1)){
                    if (infoMK1 != null){
                        if (infoMK1.getRaycastRange() > 5)
                            infoMK1.setRaycastRange(5);
                        if (infoMK1.getMaxRaycastRange() > 5)
                            infoMK1.setMaxRaycastRange(5);
                    }else{
                        if (infoMK2.getRaycastRange() > 5)
                            infoMK2.setRaycastRange(5);
                        if (infoMK2.getMaxRaycastRange() > 5)
                            infoMK2.setMaxRaycastRange(5);
                    }

                }

                if (UpgradeUtil.containsUpgradeFromList(currentUpgrades, Upgrade.VERTICAL_EXPANSION_1)){
                    Upgrade upgrade = UpgradeUtil.getUpgradeFromListByUpgrade(currentUpgrades, Upgrade.VERTICAL_EXPANSION_1);
                    if (infoMK1 != null){
                        if (infoMK1.getVertical() > upgrade.getTier())
                            infoMK1.setVertical(upgrade.getTier());
                        infoMK1.setMaxVertical(upgrade.getTier());
                    }else{
                        if (infoMK2.getVertical() > upgrade.getTier())
                            infoMK2.setVertical(upgrade.getTier());
                        infoMK2.setMaxVertical(upgrade.getTier());
                    }
                }else if (!UpgradeUtil.containsUpgradeFromList(currentUpgrades, Upgrade.VERTICAL_EXPANSION_1)){
                    if (infoMK1 != null){
                        infoMK1.setVertical(0);
                        infoMK1.setMaxVertical(0);
                    }else{
                        infoMK2.setVertical(0);
                        infoMK2.setMaxVertical(0);
                    }
                }

                if (UpgradeUtil.containsUpgradeFromList(currentUpgrades, Upgrade.HORIZONTAL_EXPANSION_1)){
                    Upgrade upgrade = UpgradeUtil.getUpgradeFromListByUpgrade(currentUpgrades, Upgrade.HORIZONTAL_EXPANSION_1);
                    if (infoMK1 != null){
                        if (infoMK1.getHorizontal() > upgrade.getTier())
                            infoMK1.setHorizontal(upgrade.getTier());
                        infoMK1.setHorizontal(upgrade.getTier());
                    }else{
                        if (infoMK2.getHorizontal() > upgrade.getTier())
                            infoMK2.setHorizontal(upgrade.getTier());
                        infoMK2.setHorizontal(upgrade.getTier());
                    }
                }else if (!UpgradeUtil.containsUpgradeFromList(currentUpgrades, Upgrade.HORIZONTAL_EXPANSION_1)){
                    if (infoMK1 != null){
                        infoMK1.setHorizontal(0);
                        infoMK1.setMaxHorizontal(0);
                    }else{
                        infoMK2.setHorizontal(0);
                        infoMK2.setMaxHorizontal(0);
                    }
                }

                currentUpgrades.forEach(upgrade -> {
                    if ((upgrade.lazyIs(Upgrade.FORTUNE_1) && upgrade.isActive() && currentUpgrades.contains(Upgrade.SILK_TOUCH))
                            || (upgrade.lazyIs(Upgrade.SILK_TOUCH) && upgrade.isActive() && UpgradeUtil.containsUpgradeFromList(currentUpgrades, Upgrade.FORTUNE_1))){
                        upgrade.setActive(!upgrade.isActive());
                    }
                });
                if (infoMK1 != null)
                    infoMK1.setUpgradeNBTList(UpgradeUtil.setUpgradesNBT(currentUpgrades));
                else
                    infoMK2.setUpgradeNBTList(UpgradeUtil.setUpgradesNBT(currentUpgrades));
                if (Thread.currentThread().getThreadGroup() == SidedThreadGroups.SERVER && open)
                    ModScreens.openGunSettingsScreen(gun);
            });
            ctx.get().setPacketHandled(true);
        }
    }
}