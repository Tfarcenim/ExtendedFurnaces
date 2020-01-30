package com.tfar.extendedfurnace.network;

import com.tfar.extendedfurnace.ExtendedFurnanceContainer;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SXpDumpPacket {
  public void handle(Supplier<NetworkEvent.Context> ctx) {
    PlayerEntity player = ctx.get().getSender();

    if (player == null) return;

    ctx.get().enqueueWork(() -> {
      Container container = player.openContainer;
      if (container instanceof ExtendedFurnanceContainer) {
        ExtendedFurnanceContainer extendedFurnanceContainer = (ExtendedFurnanceContainer) container;
        BlockPos pos = player.getPosition();
        int xpAmount = extendedFurnanceContainer.te.fluidStorage.getFluidAmount() / 10;
        if (xpAmount > 0) {
          int orbSize = Math.min(xpAmount, Short.MAX_VALUE);
          ExperienceOrbEntity xp = new ExperienceOrbEntity(player.world, pos.getX(), pos.getY(), pos.getZ(),
                  orbSize);
          player.world.addEntity(xp);
          ((ExtendedFurnanceContainer) container).te.fluidStorage.drain(10 * orbSize, IFluidHandler.FluidAction.EXECUTE);
        }
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
