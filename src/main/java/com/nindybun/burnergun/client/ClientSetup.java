package com.nindybun.burnergun.client;

import com.nindybun.burnergun.common.BurnerGun;
import com.nindybun.burnergun.common.containers.ModContainers;
import com.nindybun.burnergun.common.entities.ModEntities;
import com.nindybun.burnergun.common.items.burnergunmk1.BurnerGunMK1Screen;
import com.nindybun.burnergun.common.items.burnergunmk2.BurnerGunMK2Screen;
import com.nindybun.burnergun.common.items.upgrades.Auto_Fuel.AutoFuelScreen;
import com.nindybun.burnergun.common.items.upgrades.Auto_Smelt.AutoSmeltScreen;
import com.nindybun.burnergun.common.items.upgrades.Trash.TrashScreen;
import com.nindybun.burnergun.common.items.upgrades.Upgrade_Bag.UpgradeBagScreen;
import net.minecraft.client.gui.ScreenManager;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;

public class ClientSetup {
    public static void setup(){
        ScreenManager.register(ModContainers.BURNERGUNMK1_CONTAINER.get(), BurnerGunMK1Screen::new);
        ScreenManager.register(ModContainers.BURNERGUNMK2_CONTAINER.get(), BurnerGunMK2Screen::new);
        ScreenManager.register(ModContainers.AUTOFUEL_CONTAINER.get(), AutoFuelScreen::new);
        ScreenManager.register(ModContainers.TRASH_CONTAINER.get(), TrashScreen::new);
        ScreenManager.register(ModContainers.AUTO_SMELT_CONTAINER.get(), AutoSmeltScreen::new);
        ScreenManager.register(ModContainers.UPGRADE_BAG_CONTAINER.get(), UpgradeBagScreen::new);
    }

}
