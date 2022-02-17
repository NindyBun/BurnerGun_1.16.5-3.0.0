package com.nindybun.burnergun.common.items;

import com.nindybun.burnergun.common.BurnerGun;
import net.minecraft.item.Item;

public class MegaBlazeSummon extends Item {
    public MegaBlazeSummon() {
        super(new Properties().tab(BurnerGun.itemGroup).fireResistant().stacksTo(64));
    }
}
