package com.nindybun.burnergun.common.items.upgrades.Auto_Smelt;

import com.nindybun.burnergun.common.containers.AutoSmeltContainer;
import com.nindybun.burnergun.common.containers.TrashContainer;
import com.nindybun.burnergun.common.items.upgrades.Upgrade;
import com.nindybun.burnergun.common.items.upgrades.UpgradeCard;
import com.nindybun.burnergun.common.network.PacketHandler;
import com.nindybun.burnergun.common.network.packets.PacketOpenAutoSmeltGui;
import com.nindybun.burnergun.common.network.packets.PacketOpenTrashGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AutoSmelt extends UpgradeCard {
    Upgrade upgrade;
    public static final Logger LOGGER = LogManager.getLogger();

    public AutoSmelt(Upgrade upgrade) {
        super(upgrade);
        this.upgrade = upgrade;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!world.isClientSide) {
            PacketHandler.sendToServer(new PacketOpenAutoSmeltGui());
        }
        return ActionResult.success(stack);
    }

    public Upgrade getUpgrade(){
        return upgrade;
    }

    @Nonnull
    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, CompoundNBT oldCapNbt) {
        return new AutoSmeltProvider();
    }

    public static IItemHandler getHandler(ItemStack itemStack) {
        return itemStack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).orElse(null);
    }

    private final String HANDLER_NBT_TAG = "autosmeltHandlerNBT";

    @Override
    public CompoundNBT getShareTag(ItemStack stack) {
        CompoundNBT infoTag = new CompoundNBT();
        stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent((cap)->{
            infoTag.put(HANDLER_NBT_TAG, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.writeNBT(cap, null));
        });
        return infoTag;
    }

    @Override
    public void readShareTag(ItemStack stack, @Nullable CompoundNBT nbt) {
        if (nbt != null){
            stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null).ifPresent((cap)->{
                CapabilityItemHandler.ITEM_HANDLER_CAPABILITY.readNBT(cap, null, nbt.get(HANDLER_NBT_TAG));
            });
        }
    }
}
