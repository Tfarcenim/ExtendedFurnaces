package com.tfar.examplemod.network;

import com.tfar.examplemod.ExtendedFurnanceContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class S2CExtendedFurnaceSyncPacket {

  private String name;

  public S2CExtendedFurnaceSyncPacket(String newName) {
    this.name = newName;
  }

  public S2CExtendedFurnaceSyncPacket(PacketBuffer buf) {
    int length = buf.readInt();
    name = buf.readString(length);
  }

  public void encode(PacketBuffer buf) {
    buf.writeInt(name.length());
    buf.writeString(name);
  }

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
