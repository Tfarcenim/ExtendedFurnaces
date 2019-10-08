package com.tfar.examplemod;

import com.tfar.examplemod.network.PacketHandler;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExtendedFurnaces.MODID)
public class ExtendedFurnaces
{
  // Directly reference a log4j logger.

  public static final String MODID = "extendedfurnace";

  private static final Logger LOGGER = LogManager.getLogger();

  public ExtendedFurnaces() {
    // Register the setup method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    // Register the doClientStuff method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
  }

  private void setup(final FMLCommonSetupEvent event) {
    PacketHandler.registerMessages(MODID);
  }

  private void doClientStuff(final FMLClientSetupEvent event) {
    ScreenManager.registerFactory(RegistryObjects.container_type, ExtendedFurnaceScreen::new);
  }

  // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
  // Event bus for receiving Registry Events)
  @Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
  public static class RegistryEvents {
    @SubscribeEvent
    public static void onBlocksRegistry(final RegistryEvent.Register<Block> event) {
      // register a new block here
      register(new ExtendedFurnaceBlock(Block.Properties.create(Material.IRON).hardnessAndResistance(1)),"extended_furnace",event.getRegistry());
    }

    @SubscribeEvent
    public static void item(final RegistryEvent.Register<Item> event) {
      // register a new block here
      register(new BlockItem(RegistryObjects.extended_furnace,new Item.Properties().group(ItemGroup.DECORATIONS)),"extended_furnace",event.getRegistry());
    }

    @SubscribeEvent
    public static void container(final RegistryEvent.Register<ContainerType<?>> event) {
      // register a new block here
      register(IForgeContainerType.create((windowId, inv, data) -> new ExtendedFurnanceContainer(windowId, inv.player.world, data.readBlockPos(), inv)),"container_type",event.getRegistry());
    }

    @SubscribeEvent
    public static void tile(final RegistryEvent.Register<TileEntityType<?>> event) {
      // register a new block here
      register(TileEntityType.Builder.create(ExtendedFurnaceBlockEntity::new, RegistryObjects.extended_furnace).build(null),"tile_type",event.getRegistry());
    }

    public static <T extends IForgeRegistryEntry<T>> void register(T obj, String name, IForgeRegistry<T> registry) {
      registry.register(obj.setRegistryName(new ResourceLocation(MODID, name)));
    }
  }

  @ObjectHolder(MODID)
  public static class RegistryObjects {
    public static final ContainerType<ExtendedFurnanceContainer> container_type = null;
    public static final TileEntityType<ExtendedFurnaceBlockEntity> tile_type = null;
    public static final Block extended_furnace = null;

  }
}
