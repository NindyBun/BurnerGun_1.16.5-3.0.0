package com.nindybun.burnergun.client.particles;

import com.nindybun.burnergun.client.particles.ModParticles;
import com.nindybun.burnergun.client.particles.lightParticle.LightParticleType;
import com.nindybun.burnergun.client.particles.megaBlazeFireballParticle.MegaBlazeFireballParticleType;
import com.nindybun.burnergun.common.BurnerGun;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = BurnerGun.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ParticleRenderDispatcher {
    @SubscribeEvent
    public static void registerFactories(ParticleFactoryRegisterEvent evt) {
        Minecraft.getInstance().particleEngine.register(ModParticles.LIGHT_PARTICLE.get(), LightParticleType.LightParticleFactory::new);
        Minecraft.getInstance().particleEngine.register(ModParticles.MEGA_BLAZE_FIREBALL_PARTICLE.get(), MegaBlazeFireballParticleType.MegaBlazeFireballParticleFactory::new);
    }
}
