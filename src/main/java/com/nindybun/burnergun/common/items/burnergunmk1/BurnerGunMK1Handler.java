package com.nindybun.burnergun.common.items.burnergunmk1;

import com.nindybun.burnergun.common.containers.BurnerGunMK1Container;
import com.nindybun.burnergun.common.items.upgrades.Upgrade;
import com.nindybun.burnergun.common.items.upgrades.UpgradeCard;
import com.nindybun.burnergun.common.items.upgrades.Upgrade_Bag.UpgradeBag;
import com.nindybun.burnergun.common.network.PacketHandler;
import com.nindybun.burnergun.common.network.packets.PacketRefuel;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class BurnerGunMK1Handler extends ItemStackHandler {
    public static final int MAX_SLOTS = BurnerGunMK1Container.MAX_EXPECTED_GUN_SLOT_COUNT;

    public BurnerGunMK1Handler(int numberOfSlots){
        super(numberOfSlots);
    }

    public static boolean isFuel(ItemStack stack) {
        return net.minecraftforge.common.ForgeHooks.getBurnTime(stack) > 0;
    }

    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        if (slot < 0 || slot >= MAX_SLOTS) {
            throw new IllegalArgumentException("Invalid slot number: " + slot);
        }
        if ((isFuel(stack) || stack.getItem() == Items.BUCKET
                || stack.getItem().equals(Upgrade.AMBIENCE_1.getCard().getItem())
                || stack.getItem().equals(Upgrade.AMBIENCE_2.getCard().getItem())
                || stack.getItem().equals(Upgrade.AMBIENCE_3.getCard().getItem())
                || stack.getItem().equals(Upgrade.AMBIENCE_4.getCard().getItem())
                || stack.getItem().equals(Upgrade.AMBIENCE_5.getCard().getItem())) && slot == 0 ) {
            PacketHandler.sendToServer(new PacketRefuel());
            return true;
        }
        if (slot != 0 && stack.getItem() instanceof UpgradeCard && !(stack.getItem() instanceof UpgradeBag)
                && !stack.getItem().equals(Upgrade.AMBIENCE_1.getCard().getItem())
                && !stack.getItem().equals(Upgrade.AMBIENCE_2.getCard().getItem())
                && !stack.getItem().equals(Upgrade.AMBIENCE_3.getCard().getItem())
                && !stack.getItem().equals(Upgrade.AMBIENCE_4.getCard().getItem())
                && !stack.getItem().equals(Upgrade.AMBIENCE_5.getCard().getItem())){
            if (getUpgradeByUpgrade(((UpgradeCard) stack.getItem()).getUpgrade()) != null){
                return false;
            }
            return canInsert(stack);
        }
        return false;
    }

    public List<UpgradeCard> getUpgrades(){
        List<UpgradeCard> upgrades = new ArrayList<>();
        for (int index  = 1; index < MAX_SLOTS; index++){
            if (this.getStackInSlot(index).getItem() != Items.AIR){
                upgrades.add((UpgradeCard)this.getStackInSlot(index).getItem());
            }
        }
        return upgrades;
    }

    public Upgrade getUpgradeByUpgrade(Upgrade upgrade){
        List<UpgradeCard> upgrades = getUpgrades();
        for (UpgradeCard upgradeCard : upgrades) {
            if (upgradeCard.getUpgrade().getBaseName().equals(upgrade.getBaseName())){
                return upgradeCard.getUpgrade();
            }
        }
        return null;
    }


    public boolean canInsert(ItemStack item){
        List<UpgradeCard> upgradeCards = getUpgrades();

        if (!upgradeCards.isEmpty()){
            for (UpgradeCard upgrade : upgradeCards) {
                UpgradeCard upgradeItem = (UpgradeCard) item.getItem();
                //Checks if the holding upgrade is Silk and if there is a fortune upgrade
                //Checks if the holding upgrade is fortune and if there is a silk upgrade
                if ((upgradeItem.getUpgrade().equals(Upgrade.SILK_TOUCH) && upgrade.getUpgrade().getBaseName().equals(Upgrade.FORTUNE_1.getBaseName())) ||
                        (upgradeItem.getUpgrade().getBaseName().equals(Upgrade.FORTUNE_1.getBaseName()) && upgrade.getUpgrade().equals(Upgrade.SILK_TOUCH)) ){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onContentsChanged(int slot) {
        this.validateSlotIndex(slot);
    }

    private final Logger LOGGER = LogManager.getLogger();



}
