package com.nindybun.burnergun.client.events;

import com.nindybun.burnergun.common.BurnerGun;
import com.nindybun.burnergun.common.entities.MegaBlazeEntity;
import com.nindybun.burnergun.common.entities.MegaBlazeSummonEntity;
import com.nindybun.burnergun.common.entities.ModEntities;
import com.nindybun.burnergun.common.items.ModSpawnEggs;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BurnerGun.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void addEntityAttributes(EntityAttributeCreationEvent event) {
        //event.put(ModEntities.MEGA_BLAZE.get(), MegaBlazeEntity.createAttributes().build());
    }

    @SubscribeEvent
    public static void onRegisterEntities(RegistryEvent.Register<EntityType<?>> event) {
        //ModSpawnEggs.initSpawnEggs();
    }
}