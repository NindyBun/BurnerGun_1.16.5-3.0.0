package com.nindybun.burnergun.common.capabilities;

import com.nindybun.burnergun.common.capabilities.burnergunmk1.BurnerGunMK1InfoProvider;
import com.nindybun.burnergun.common.capabilities.burnergunmk2.BurnerGunMK2InfoProvider;
import com.nindybun.burnergun.common.items.burnergunmk1.BurnerGunMK1;
import com.nindybun.burnergun.common.items.burnergunmk2.BurnerGunMK2;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class FMLEvents {

    @SubscribeEvent
    public static void onAttatchedCapabilities(AttachCapabilitiesEvent<ItemStack> event){
        if (event.getObject().getItem() instanceof BurnerGunMK1){
            event.addCapability(new ResourceLocation(com.nindybun.burnergun.common.BurnerGun.MOD_ID, "burnergun_mk1"), new BurnerGunMK1InfoProvider());
        }
        if (event.getObject().getItem() instanceof BurnerGunMK2){
            event.addCapability(new ResourceLocation(com.nindybun.burnergun.common.BurnerGun.MOD_ID, "burnergun_mk2"), new BurnerGunMK2InfoProvider());
        }
    }

}
