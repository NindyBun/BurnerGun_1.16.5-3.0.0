package com.nindybun.burnergun.common.containers;

import com.mojang.realmsclient.client.Request;
import com.nindybun.burnergun.common.BurnerGun;
import net.minecraft.inventory.container.ContainerType;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModContainers {
    public static final DeferredRegister<ContainerType<?>> CONTAINERS = DeferredRegister.create(ForgeRegistries.CONTAINERS, BurnerGun.MOD_ID);

    public static final RegistryObject<ContainerType<BurnerGunMK1Container>> BURNERGUNMK1_CONTAINER = CONTAINERS.register("burnergunmk1_container", () -> IForgeContainerType.create(BurnerGunMK1Container::new));
    public static final RegistryObject<ContainerType<AutoFuelContainer>> AUTOFUEL_CONTAINER = CONTAINERS.register("auto_fuel_container", () -> IForgeContainerType.create(AutoFuelContainer::new));
    public static final RegistryObject<ContainerType<TrashContainer>> TRASH_CONTAINER = CONTAINERS.register("trash_container", () -> IForgeContainerType.create(TrashContainer::new));
    public static final RegistryObject<ContainerType<AutoSmeltContainer>> AUTO_SMELT_CONTAINER = CONTAINERS.register("auto_smelt_container", () -> IForgeContainerType.create(AutoSmeltContainer::new));
    public static final RegistryObject<ContainerType<UpgradeBagContainer>> UPGRADE_BAG_CONTAINER = CONTAINERS.register("upgrade_bag_container", () -> IForgeContainerType.create(UpgradeBagContainer::new));
    public static final RegistryObject<ContainerType<BurnerGunMK2Container>> BURNERGUNMK2_CONTAINER = CONTAINERS.register("burnergunmk2_container", () -> IForgeContainerType.create(BurnerGunMK2Container::new));
}
