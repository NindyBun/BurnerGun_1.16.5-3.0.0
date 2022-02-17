package com.nindybun.burnergun.client.particles;

import com.nindybun.burnergun.client.particles.lightParticle.LightParticle;
import com.nindybun.burnergun.client.particles.lightParticle.LightParticleType;
import com.nindybun.burnergun.client.particles.megaBlazeFireballParticle.MegaBlazeFireballParticleType;
import com.nindybun.burnergun.common.BurnerGun;
import net.minecraft.client.particle.Particle;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.ParticleType;
import net.minecraft.particles.ParticleTypes;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, BurnerGun.MOD_ID);

    public static final RegistryObject<BasicParticleType> LIGHT_PARTICLE = PARTICLE.register("light_particle", LightParticleType::new);
    public static final RegistryObject<BasicParticleType> MEGA_BLAZE_FIREBALL_PARTICLE = PARTICLE.register("mega_blaze_fireball_particle", MegaBlazeFireballParticleType::new);

}
