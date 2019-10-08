package com.tfar.examplemod.network;

import com.tfar.examplemod.ExtendedFurnanceContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class C2SRecipeModePacket {
  public void handle(Supplier<NetworkEvent.Context> ctx) {
    PlayerEntity player = ctx.get().getSender();

    if (player == null) return;

    ctx.get().enqueueWork(() -> {
      Container container = player.openContainer;
      if (container instanceof ExtendedFurnanceContainer) {
        ((ExtendedFurnanceContainer) container).te.changeRecipeType();
      }
    });
    ctx.get().setPacketHandled(true);
  }
}
