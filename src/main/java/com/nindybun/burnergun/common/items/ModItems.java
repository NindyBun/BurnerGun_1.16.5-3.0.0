package com.nindybun.burnergun.common.items;

import com.nindybun.burnergun.common.BurnerGun;
import com.nindybun.burnergun.common.blocks.ModBlocks;
import com.nindybun.burnergun.common.items.burnergunmk1.BurnerGunMK1;
import com.nindybun.burnergun.common.items.burnergunmk2.BurnerGunMK2;
import com.nindybun.burnergun.common.items.upgrades.Auto_Smelt.AutoSmelt;
import com.nindybun.burnergun.common.items.upgrades.Trash.Trash;
import com.nindybun.burnergun.common.items.upgrades.Upgrade;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
    public static final Item.Properties ITEM_GROUP = new Item.Properties().tab(BurnerGun.itemGroup);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BurnerGun.MOD_ID);

    // We have a separate register just to contain all of the upgrades for quick reference
    public static final DeferredRegister<Item> UPGRADE_ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BurnerGun.MOD_ID);

    /* public static final RegistryObject<ModSpawnEggs> MEGA_BLAZE_SPAWN_EGG = ITEMS.register("mega_blaze_spawn_egg",
            () -> new ModSpawnEggs(ModEntities.MEGA_BLAZE, 0x000000, 0xffa200, ITEM_GROUP.stacksTo(64)));*/

    //public static final RegistryObject<Item> MEGA_BLAZE_SUMMON_ITEM = ITEMS.register("mega_blaze_summon", () -> new MegaBlazeSummon());

    public static final RegistryObject<Item> BLAZE_CAGE = ITEMS.register("blaze_cage", () -> new BlazeCage());
    public static final RegistryObject<Item> CAGED_BLAZE= ITEMS.register("caged_blaze", () -> new Item(ITEM_GROUP.stacksTo(1)));
    public static final RegistryObject<Item> BURNER_GUN_MK1 = ITEMS.register("burnergun_mk1", () ->  new BurnerGunMK1());
    public static final RegistryObject<Item> BURNER_GUN_MK2 = ITEMS.register("burnergun_mk2", () -> new BurnerGunMK2());
    public static final RegistryObject<Item> LIGHT_ITEM = ITEMS.register("light", () -> new BlockItem(ModBlocks.LIGHT.get(), ITEM_GROUP.stacksTo(1)));
    //public static final RegistryObject<Item> GLITTERING_DIAMOND = ITEMS.register("glittering_diamond", () -> new Item(new Item.Properties().stacksTo(64).tab(BurnerGun.itemGroup)));

    public static final RegistryObject<Item> CONDENSED_BLAZE_1 = ITEMS.register("condensed_blaze_1", () -> new Item(ITEM_GROUP.stacksTo(64)));
    public static final RegistryObject<Item> CONDENSED_BLAZE_2 = ITEMS.register("condensed_blaze_2", () -> new Item(ITEM_GROUP.stacksTo(64)));
    public static final RegistryObject<Item> CONDENSED_BLAZE_3 = ITEMS.register("condensed_blaze_3", () -> new Item(ITEM_GROUP.stacksTo(64)));
    public static final RegistryObject<Item> CONDENSED_BLAZE_4 = ITEMS.register("condensed_blaze_4", () -> new Item(ITEM_GROUP.stacksTo(64)));
    public static final RegistryObject<Item> CONDENSED_BLAZE_5 = ITEMS.register("condensed_blaze_5", () -> new Item(ITEM_GROUP.stacksTo(64)));

    public static final RegistryObject<Item> BASE_TIER_1 = UPGRADE_ITEMS.register("base_tier_1", Upgrade.TIER_1::getCard);
    public static final RegistryObject<Item> BASE_TIER_2 = UPGRADE_ITEMS.register("base_tier_2", Upgrade.TIER_2::getCard);
    public static final RegistryObject<Item> BASE_TIER_3 = UPGRADE_ITEMS.register("base_tier_3", Upgrade.TIER_3::getCard);
    public static final RegistryObject<Item> BASE_TIER_4 = UPGRADE_ITEMS.register("base_tier_4", Upgrade.TIER_4::getCard);
    public static final RegistryObject<Item> BASE_TIER_5 = UPGRADE_ITEMS.register("base_tier_5", Upgrade.TIER_5::getCard);

    public static final RegistryObject<Item> MAGNET = UPGRADE_ITEMS.register("magnet_upgrade", Upgrade.MAGNET::getCard);
    public static final RegistryObject<Item> SILK_TOUCH = UPGRADE_ITEMS.register("silk_touch_upgrade", Upgrade.SILK_TOUCH::getCard);
    public static final RegistryObject<Item> LIGHT = UPGRADE_ITEMS.register("light_upgrade", Upgrade.LIGHT::getCard);
    public static final RegistryObject<Item> AUTO_SMELT = UPGRADE_ITEMS.register("auto_smelt_upgrade", () -> new AutoSmelt(Upgrade.AUTO_SMELT));
    public static final RegistryObject<Item> TRASH = UPGRADE_ITEMS.register("trash_upgrade", () -> new Trash(Upgrade.TRASH));
    //public static final RegistryObject<Item> UPGRADE_BAG = UPGRADE_ITEMS.register("upgrade_bag", () -> new UpgradeBag(Upgrade.UPGRADE_BAG));
    //public static final RegistryObject<Item> UNIFUEL = UPGRADE_ITEMS.register("unifuel", Upgrade.UNIFUEL::getCard);
    public static final RegistryObject<Item> AMBIENCE_1 = UPGRADE_ITEMS.register("ambience_1", Upgrade.AMBIENCE_1::getCard);
    public static final RegistryObject<Item> AMBIENCE_2 = UPGRADE_ITEMS.register("ambience_2", Upgrade.AMBIENCE_2::getCard);
    public static final RegistryObject<Item> AMBIENCE_3 = UPGRADE_ITEMS.register("ambience_3", Upgrade.AMBIENCE_3::getCard);
    public static final RegistryObject<Item> AMBIENCE_4 = UPGRADE_ITEMS.register("ambience_4", Upgrade.AMBIENCE_4::getCard);
    public static final RegistryObject<Item> AMBIENCE_5 = UPGRADE_ITEMS.register("ambience_5", Upgrade.AMBIENCE_5::getCard);

    //public static final RegistryObject<Item> REACTOR = UPGRADE_ITEMS.register("reactor", Upgrade.REACTOR::getCard);

    public static final RegistryObject<Item> FORTUNE_1 = UPGRADE_ITEMS.register("fortune_1_upgrade", Upgrade.FORTUNE_1::getCard);
    public static final RegistryObject<Item> FORTUNE_2 = UPGRADE_ITEMS.register("fortune_2_upgrade", Upgrade.FORTUNE_2::getCard);
    public static final RegistryObject<Item> FORTUNE_3 = UPGRADE_ITEMS.register("fortune_3_upgrade", Upgrade.FORTUNE_3::getCard);

    public static final RegistryObject<Item> HORIZONTAL_EXPANSION_1 = UPGRADE_ITEMS.register("horizontal_expansion_1_upgrade", Upgrade.HORIZONTAL_EXPANSION_1::getCard);
    public static final RegistryObject<Item> HORIZONTAL_EXPANSION_2 = UPGRADE_ITEMS.register("horizontal_expansion_2_upgrade", Upgrade.HORIZONTAL_EXPANSION_2::getCard);
    public static final RegistryObject<Item> HORIZONTAL_EXPANSION_3 = UPGRADE_ITEMS.register("horizontal_expansion_3_upgrade", Upgrade.HORIZONTAL_EXPANSION_3::getCard);
    public static final RegistryObject<Item> HORIZONTAL_EXPANSION_4 = UPGRADE_ITEMS.register("horizontal_expansion_4_upgrade", Upgrade.HORIZONTAL_EXPANSION_4::getCard);

    public static final RegistryObject<Item> VERTICAL_EXPANSION_1 = UPGRADE_ITEMS.register("vertical_expansion_1_upgrade", Upgrade.VERTICAL_EXPANSION_1::getCard);
    public static final RegistryObject<Item> VERTICAL_EXPANSION_2 = UPGRADE_ITEMS.register("vertical_expansion_2_upgrade", Upgrade.VERTICAL_EXPANSION_2::getCard);
    public static final RegistryObject<Item> VERTICAL_EXPANSION_3 = UPGRADE_ITEMS.register("vertical_expansion_3_upgrade", Upgrade.VERTICAL_EXPANSION_3::getCard);
    public static final RegistryObject<Item> VERTICAL_EXPANSION_4 = UPGRADE_ITEMS.register("vertical_expansion_4_upgrade", Upgrade.VERTICAL_EXPANSION_4::getCard);

    public static final RegistryObject<Item> FOCAL_POINT_1 = UPGRADE_ITEMS.register("focal_point_1_upgrade", Upgrade.FOCAL_POINT_1::getCard);
    public static final RegistryObject<Item> FOCAL_POINT_2 = UPGRADE_ITEMS.register("focal_point_2_upgrade", Upgrade.FOCAL_POINT_2::getCard);
    public static final RegistryObject<Item> FOCAL_POINT_3 = UPGRADE_ITEMS.register("focal_point_3_upgrade", Upgrade.FOCAL_POINT_3::getCard);

    public static final RegistryObject<Item> FUEL_EFFICIENCY_1 = UPGRADE_ITEMS.register("fuel_efficiency_1_upgrade", Upgrade.FUEL_EFFICIENCY_1::getCard);
    public static final RegistryObject<Item> FUEL_EFFICIENCY_2 = UPGRADE_ITEMS.register("fuel_efficiency_2_upgrade", Upgrade.FUEL_EFFICIENCY_2::getCard);
    public static final RegistryObject<Item> FUEL_EFFICIENCY_3 = UPGRADE_ITEMS.register("fuel_efficiency_3_upgrade", Upgrade.FUEL_EFFICIENCY_3::getCard);
    public static final RegistryObject<Item> FUEL_EFFICIENCY_4 = UPGRADE_ITEMS.register("fuel_efficiency_4_upgrade", Upgrade.FUEL_EFFICIENCY_4::getCard);
    public static final RegistryObject<Item> FUEL_EFFICIENCY_5 = UPGRADE_ITEMS.register("fuel_efficiency_5_upgrade", Upgrade.FUEL_EFFICIENCY_5::getCard);

    /*public static final RegistryObject<Item> HEAT_EFFICIENCY_1 = UPGRADE_ITEMS.register("heat_efficiency_1_upgrade", Upgrade.HEAT_EFFICIENCY_1::getCard);
    public static final RegistryObject<Item> HEAT_EFFICIENCY_2 = UPGRADE_ITEMS.register("heat_efficiency_2_upgrade", Upgrade.HEAT_EFFICIENCY_2::getCard);
    public static final RegistryObject<Item> HEAT_EFFICIENCY_3 = UPGRADE_ITEMS.register("heat_efficiency_3_upgrade", Upgrade.HEAT_EFFICIENCY_3::getCard);
    public static final RegistryObject<Item> HEAT_EFFICIENCY_4 = UPGRADE_ITEMS.register("heat_efficiency_4_upgrade", Upgrade.HEAT_EFFICIENCY_4::getCard);
    public static final RegistryObject<Item> HEAT_EFFICIENCY_5 = UPGRADE_ITEMS.register("heat_efficiency_5_upgrade", Upgrade.HEAT_EFFICIENCY_5::getCard);

    public static final RegistryObject<Item> COOLDOWN_MULTIPLIER_1 = UPGRADE_ITEMS.register("cooldown_multiplier_1_upgrade", Upgrade.COOLDOWN_MULTIPLIER_1::getCard);
    public static final RegistryObject<Item> COOLDOWN_MULTIPLIER_2 = UPGRADE_ITEMS.register("cooldown_multiplier_2_upgrade", Upgrade.COOLDOWN_MULTIPLIER_2::getCard);
    public static final RegistryObject<Item> COOLDOWN_MULTIPLIER_3 = UPGRADE_ITEMS.register("cooldown_multiplier_3_upgrade", Upgrade.COOLDOWN_MULTIPLIER_3::getCard);*/
}
