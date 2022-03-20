package com.nindybun.burnergun.common.containers;

import com.nindybun.burnergun.common.items.burnergunmk1.BurnerGunMK1;
import com.nindybun.burnergun.common.items.burnergunmk2.BurnerGunMK2;
import com.nindybun.burnergun.common.items.upgrades.UpgradeCard;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.world.World;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.SlotItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class AutoSmeltGhostSlot extends SlotItemHandler {
    private static final Logger LOGGER = LogManager.getLogger();
    private final IRecipeType<? extends AbstractCookingRecipe> RECIPE_TYPE = IRecipeType.SMELTING;
    private World world;
    public AutoSmeltGhostSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition, World world) {
        super(itemHandler, index, xPosition, yPosition);
        this.world = world;
    }

    @Override
    public boolean mayPickup(PlayerEntity player) {
        ((IItemHandlerModifiable)this.getItemHandler()).setStackInSlot(getSlotIndex(), ItemStack.EMPTY);
        return false;
    }

    public boolean hasSmeltOption(ItemStack stack){
        IInventory inv = new Inventory(1);
        inv.setItem(0, stack);
        Optional<? extends AbstractCookingRecipe> recipe = world.getRecipeManager().getRecipeFor(RECIPE_TYPE, inv, world);
        return recipe.isPresent();
    }

    @Override
    public boolean mayPlace(ItemStack stack) {
        if (stack.getItem() instanceof BurnerGunMK1 || stack.getItem() instanceof BurnerGunMK2 || stack.getItem() instanceof UpgradeCard)
            return false;
        if (!hasSmeltOption(stack))
            return false;
        ((IItemHandlerModifiable)this.getItemHandler()).setStackInSlot(getSlotIndex(), stack.copy().getItem().getDefaultInstance());
        return false;
    }

}
