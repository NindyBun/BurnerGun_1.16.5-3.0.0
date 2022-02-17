package com.nindybun.burnergun.common.items.burnergunmk2;

import com.nindybun.burnergun.common.containers.BurnerGunMK1Container;
import com.nindybun.burnergun.common.containers.BurnerGunMK2Container;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BurnerGunMK2Provider implements ICapabilitySerializable<INBT> {
    private final Direction NO_SPECIFIC_SIDE = null;

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (CapabilityItemHandler.ITEM_HANDLER_CAPABILITY == cap) return (LazyOptional<T>)(lazyInitialisionSupplier).cast();
        return LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        return CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(getCachedInventory(), NO_SPECIFIC_SIDE);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(getCachedInventory(), NO_SPECIFIC_SIDE, nbt);
    }

    /**
     * Return a lazily-initialised inventory
     * i.e. After the class instance has been created, but before the first call to this function, the inventory hasn't been created yet.
     * At the time of the first call, we create the inventory
     * For all subsequent calls, we return the previously-created instance.
     * To be honest, unless your initialisation is very expensive in memory or time, it's probably not worth the effort, i.e. you
     *   could just allocate the itemStackHandlerFlowerBag in your constructor and your lazyInitialisationSupplier could just
     *   return that without a dedicated method to perform a cache check.
     * @return the ItemStackHandlerFlowerBag which stores the flowers.
     */
    private BurnerGunMK2Handler getCachedInventory() {
        if (handler == null) {
            handler = new BurnerGunMK2Handler(BurnerGunMK2Container.MAX_EXPECTED_GUN_SLOT_COUNT);
        }
        return handler;
    }

    private BurnerGunMK2Handler handler;  // initially null until our first call to getCachedInventory


    //  a supplier: when called, returns the result of getCachedInventory()
    private final LazyOptional<IItemHandler> lazyInitialisionSupplier = LazyOptional.of(this::getCachedInventory);

    
}
