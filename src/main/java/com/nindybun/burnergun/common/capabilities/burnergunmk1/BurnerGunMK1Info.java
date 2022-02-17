package com.nindybun.burnergun.common.capabilities.burnergunmk1;

import net.minecraft.nbt.ListNBT;

public interface BurnerGunMK1Info {
    void setFuelValue(double value);
    double getFuelValue();

    void setVolume(float value);
    float getVolume();

    void setVertical(int value);
    int getVertical();

    void setMaxVertical(int value);
    int getMaxVertical();

    void setHorizontal(int value);
    int getHorizontal();

    void setMaxHorizontal(int value);
    int getMaxHorizontal();

    void setTrashIsWhitelist(boolean value);
    boolean getTrashIsWhitelist();

    void setSmeltIsWhitelist(boolean value);
    boolean getSmeltIsWhitelist();

    void setRaycastRange(int value);
    int getRaycastRange();

    void setMaxRaycastRange(int value);
    int getMaxRaycastRange();

    void setUpgradeNBTList(ListNBT upgrades);
    ListNBT getUpgradeNBTList();

    void setTrashNBTFilter(ListNBT items);
    ListNBT getTrashNBTFilter();

    void setSmeltNBTFilter(ListNBT items);
    ListNBT getSmeltNBTFilter();

    void setColor(ListNBT color);
    ListNBT getColor();
}