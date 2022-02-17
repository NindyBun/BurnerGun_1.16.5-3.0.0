package com.nindybun.burnergun.common.capabilities.burnergunmk1;

import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;

public class BurnerGunMK1InfoProvider implements BurnerGunMK1Info, ICapabilitySerializable<INBT> {
    @CapabilityInject(BurnerGunMK1Info.class)
    public static Capability<BurnerGunMK1Info> burnerGunInfoMK1Capability = null;

    private LazyOptional<BurnerGunMK1Info> instance = LazyOptional.of(burnerGunInfoMK1Capability::getDefaultInstance);

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
        return cap == burnerGunInfoMK1Capability ? instance.cast() : LazyOptional.empty();
    }

    @Override
    public INBT serializeNBT() {
        return burnerGunInfoMK1Capability.getStorage().writeNBT(burnerGunInfoMK1Capability, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null);
    }

    @Override
    public void deserializeNBT(INBT nbt) {
        burnerGunInfoMK1Capability.getStorage().readNBT(burnerGunInfoMK1Capability, this.instance.orElseThrow(() -> new IllegalArgumentException("LazyOptional must not be empty!")), null, nbt);

    }

    public float volume = 1.0f;
    public double fuel;
    public int vertical, maxVertical;
    public int horizontal, maxHorizontal;
    public int raycast = 5, maxRaycast = 5;
    public boolean trash = true;
    public boolean smelt = true;
    public ListNBT upgrades = new ListNBT();
    public ListNBT trashFilter = new ListNBT();
    public ListNBT smeltFilter = new ListNBT();
    public ListNBT color = new ListNBT();

    @Override
    public void setFuelValue(double value) {
        fuel = value;
    }

    @Override
    public double getFuelValue() {
        return fuel;
    }

    @Override
    public void setVolume(float value) {
        volume = value;
    }

    @Override
    public float getVolume() {
        return volume;
    }

    @Override
    public void setVertical(int value) {
        vertical = value;
    }

    @Override
    public int getVertical() {
        return vertical;
    }

    @Override
    public void setMaxVertical(int value) {
        maxVertical = value;
    }

    @Override
    public int getMaxVertical() {
        return maxVertical;
    }

    @Override
    public void setHorizontal(int value) {
        horizontal = value;
    }

    @Override
    public int getHorizontal() {
        return horizontal;
    }

    @Override
    public void setMaxHorizontal(int value) {
        maxHorizontal = value;
    }

    @Override
    public int getMaxHorizontal() {
        return maxHorizontal;
    }

    @Override
    public void setTrashIsWhitelist(boolean value) {
        trash = value;
    }

    @Override
    public boolean getTrashIsWhitelist() {
        return trash;
    }

    @Override
    public void setSmeltIsWhitelist(boolean value) {
        smelt = value;
    }

    @Override
    public boolean getSmeltIsWhitelist() {
        return smelt;
    }

    @Override
    public void setRaycastRange(int value) {
        raycast = value;
    }

    @Override
    public int getRaycastRange() {
        return raycast;
    }

    @Override
    public void setMaxRaycastRange(int value) {
        maxRaycast = value;
    }

    @Override
    public int getMaxRaycastRange() {
        return maxRaycast;
    }

    @Override
    public void setUpgradeNBTList(ListNBT upgrades) {
        this.upgrades = upgrades;
    }

    @Override
    public ListNBT getUpgradeNBTList() {
        return upgrades;
    }

    @Override
    public void setTrashNBTFilter(ListNBT items) {
        trashFilter = items;
    }

    @Override
    public ListNBT getTrashNBTFilter() {
        return trashFilter;
    }

    @Override
    public void setSmeltNBTFilter(ListNBT items) {
        smeltFilter = items;
    }

    @Override
    public ListNBT getSmeltNBTFilter() {
        return smeltFilter;
    }

    @Override
    public void setColor(ListNBT color) {
        this.color = color;
    }

    @Override
    public ListNBT getColor() {
        return color;
    }
}
