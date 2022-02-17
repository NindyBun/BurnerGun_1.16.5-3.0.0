package com.nindybun.burnergun.common;

import com.nindybun.burnergun.client.ClientSetup;
import com.nindybun.burnergun.client.KeyInputHandler;
import com.nindybun.burnergun.client.Keybinds;
//import com.nindybun.burnergun.client.particles.ModParticles;
import com.nindybun.burnergun.client.particles.ModParticles;
import com.nindybun.burnergun.common.blocks.ModBlocks;
import com.nindybun.burnergun.common.capabilities.*;
import com.nindybun.burnergun.common.capabilities.burnergunmk1.BurnerGunMK1Info;
import com.nindybun.burnergun.common.capabilities.burnergunmk1.BurnerGunMK1InfoProvider;
import com.nindybun.burnergun.common.capabilities.burnergunmk1.BurnerGunMK1InfoStorage;
import com.nindybun.burnergun.common.capabilities.burnergunmk2.BurnerGunMK2Info;
import com.nindybun.burnergun.common.capabilities.burnergunmk2.BurnerGunMK2InfoProvider;
import com.nindybun.burnergun.common.capabilities.burnergunmk2.BurnerGunMK2InfoStorage;
import com.nindybun.burnergun.common.containers.ModContainers;
import com.nindybun.burnergun.common.entities.ModEntities;
import com.nindybun.burnergun.common.entities.renders.MegaBlazeProjectileRenderer;
import com.nindybun.burnergun.common.entities.renders.MegaBlazeRenderer;
import com.nindybun.burnergun.common.items.ModItems;
import com.nindybun.burnergun.common.network.PacketHandler;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.SpriteRenderer;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BurnerGun.MOD_ID)
public class BurnerGun{

    private static final Logger LOGGER = LogManager.getLogger();
    public static final String MOD_ID = "burnergun";

    public BurnerGun()
    {
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModItems.UPGRADE_ITEMS.register(modEventBus);
        ModContainers.CONTAINERS.register(modEventBus);
        ModParticles.PARTICLE.register(modEventBus);
        //ModEntities.ENTITIES.register(modEventBus);

        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::setupClient);
        MinecraftForge.EVENT_BUS.register(new FMLEvents());

        MinecraftForge.EVENT_BUS.register(this);

    }

    private void setup(final FMLCommonSetupEvent event)
    {
        PacketHandler.register();
        CapabilityManager.INSTANCE.register(BurnerGunMK1Info.class, new BurnerGunMK1InfoStorage(), BurnerGunMK1InfoProvider::new);
        CapabilityManager.INSTANCE.register(BurnerGunMK2Info.class, new BurnerGunMK2InfoStorage(), BurnerGunMK2InfoProvider::new);
    }

    private void setupClient(final FMLClientSetupEvent event)
    {
        ClientSetup.setup();
        Keybinds.register();
        MinecraftForge.EVENT_BUS.register(new KeyInputHandler());
        //RenderingRegistry.registerEntityRenderingHandler(ModEntities.MEGA_BLAZE_PROJECTILE.get(), MegaBlazeProjectileRenderer::new);
        //RenderingRegistry.registerEntityRenderingHandler(ModEntities.MEGA_BLAZE.get(), MegaBlazeRenderer::new);
    }


    public static ItemGroup itemGroup = new ItemGroup(BurnerGun.MOD_ID) {
        @Override
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.BURNER_GUN_MK1.get());
        }
    };
}
