package com.nindybun.burnergun.common.entities;

import com.nindybun.burnergun.common.BurnerGun;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITIES, BurnerGun.MOD_ID);

    public static final RegistryObject<EntityType<MegaBlazeEntity>> MEGA_BLAZE = ENTITIES.register("mega_blaze", () -> EntityType.Builder.<MegaBlazeEntity>of(MegaBlazeEntity::new,
            EntityClassification.MONSTER).sized(0.6f, 1.8f).fireImmune().clientTrackingRange(12).build(new ResourceLocation(BurnerGun.MOD_ID, "textures/entities/megablaze/mega_blaze.png").toString()));
    public static final RegistryObject<EntityType<MegaBlazeFireballEntity>> MEGA_BLAZE_FIREBALL = ENTITIES.register("mega_blaze_fireball", () -> EntityType.Builder.<MegaBlazeFireballEntity>of(MegaBlazeFireballEntity::new,
            EntityClassification.MISC).sized(1.0F, 1.0F).clientTrackingRange(4).updateInterval(10).build(new ResourceLocation(BurnerGun.MOD_ID, "textures/entities/megablaze/mega_blaze_fireball.png").toString()));
    public static final RegistryObject<EntityType<MegaBlazeSummonEntity>> MEGA_BLAZE_SUMMON = ENTITIES.register("mega_blaze_summon", () -> EntityType.Builder.<MegaBlazeSummonEntity>of(MegaBlazeSummonEntity::new,
            EntityClassification.MISC).fireImmune().build(new ResourceLocation(BurnerGun.MOD_ID, "textures/entities/mega_blaze_summon.png").toString()));
    public static final RegistryObject<EntityType<MegaBlazeProjectileEntity>> MEGA_BLAZE_PROJECTILE = ENTITIES.register("mega_blaze_projectile", () -> EntityType.Builder.<MegaBlazeProjectileEntity>of(MegaBlazeProjectileEntity::new,
            EntityClassification.MISC).sized(1f, 1f).clientTrackingRange(4).updateInterval(10).build(new ResourceLocation(BurnerGun.MOD_ID, "textures/entities/megablaze/mega_blaze_projectile.png").toString()));
}
