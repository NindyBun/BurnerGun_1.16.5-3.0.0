package com.nindybun.burnergun.common.items.upgrades.Auto_Smelt;

import com.nindybun.burnergun.common.items.burnergunmk1.BurnerGunMK1;
import com.nindybun.burnergun.common.items.burnergunmk2.BurnerGunMK2;
import com.nindybun.burnergun.common.items.upgrades.UpgradeCard;
import net.minecraft.client.Minecraft;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.world.World;
import net.minecraftforge.items.ItemStackHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.Optional;

public class AutoSmeltHandler extends ItemStackHandler {
    public static final Logger LOGGER = LogManager.getLogger();
    private static final IRecipeType<? extends AbstractCookingRecipe> RECIPE_TYPE = IRecipeType.SMELTING;

    public AutoSmeltHandler(int numberOfSlots){
        super(numberOfSlots);
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        this.setStackInSlot(slot, Items.AIR.getDefaultInstance());
        return ItemStack.EMPTY;
    }

    public boolean hasSmeltOption(ItemStack stack){
        IInventory inv = new Inventory(1);
        inv.setItem(0, stack);
        World world = Minecraft.getInstance().level;
        Optional<? extends AbstractCookingRecipe> recipe = world.getRecipeManager().getRecipeFor(RECIPE_TYPE, inv, world);
        if (!recipe.isPresent())
            return false;
        return true;
    }


    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        if (stack.getItem() instanceof BurnerGunMK1 || stack.getItem() instanceof BurnerGunMK2 || stack.getItem() instanceof UpgradeCard)
            return false;
        if (hasSmeltOption(stack))
            return false;
        if (this.getStackInSlot(slot).getItem() == Items.AIR){
            this.setStackInSlot(slot, stack.getItem().getDefaultInstance());
        }else if (stack.getItem() == Items.AIR){
            this.setStackInSlot(slot, Items.AIR.getDefaultInstance());
        }else if (stack.getItem() != Items.AIR){
            this.setStackInSlot(slot, Items.AIR.getDefaultInstance());
            this.setStackInSlot(slot, stack.getItem().getDefaultInstance());
        }
        return false;
    }

    protected void onContentsChanged(int slot) {
        this.validateSlotIndex(slot);
    }


}
