package com.tfar.extendedfurnace.network;

import com.tfar.extendedfurnace.ExtendedFurnaces;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketHandler {
  public static SimpleChannel INSTANCE;

  public static void registerMessages(String channelName) {
    int id = 0;

    INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(ExtendedFurnaces.MODID, channelName), () -> "1.0", s -> true, s -> true);

    INSTANCE.registerMessage(id++, C2SRecipeModePacket.class,
            (message, buffer) -> {},
            buffer -> new C2SRecipeModePacket(),
            C2SRecipeModePacket::handle);

    INSTANCE.registerMessage(id++, C2SXpDumpPacket.class,
            (msg,buffer) -> {},
            buffer -> new C2SXpDumpPacket(),
            C2SXpDumpPacket::handle);
  }
}
