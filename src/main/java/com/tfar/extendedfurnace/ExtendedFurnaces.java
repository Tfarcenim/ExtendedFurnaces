package com.tfar.extendedfurnace;

import com.tfar.extendedfurnace.network.PacketHandler;
import net.minecraft.block.Block;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.extensions.IForgeContainerType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.ForgeFlowingFluid;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(ExtendedFurnaces.MODID)
public class ExtendedFurnaces
{
  // Directly reference a log4j logger.

  public static final String MODID = "extendedfurnace";

  public static final Tag<Item> BLACKLISTED_ITEMS = new ItemTags.Wrapper(new ResourceLocation(MODID,"multiplication_blacklist"));


  public static final ResourceLocation FLUID_STILL = new ResourceLocation("minecraft:block/water_still");
  public static final ResourceLocation FLUID_FLOWING = new ResourceLocation("minecraft:block/water_flow");

  public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, MODID);
  public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, MODID);
  public static final DeferredRegister<Fluid> FLUIDS = new DeferredRegister<>(ForgeRegistries.FLUIDS, MODID);


  public static RegistryObject<FlowingFluid> xp = FLUIDS.register("xp", () ->
          new ForgeFlowingFluid.Source(ExtendedFurnaces.xpfluid_properties)
  );
  public static RegistryObject<FlowingFluid> xp_flowing = FLUIDS.register("xp_flowing", () ->
          new ForgeFlowingFluid.Flowing(ExtendedFurnaces.xpfluid_properties)
  );

  public static RegistryObject<FlowingFluidBlock> xpfluid_block = BLOCKS.register("xpfluid_block", () ->
          new FlowingFluidBlock(xp, Block.Properties.create(Material.WATER).doesNotBlockMovement().hardnessAndResistance(100.0F).noDrops())
  );
  public static RegistryObject<Item> xp_bucket = ITEMS.register("xp_bucket", () ->
          new BucketItem(xp, new Item.Properties().containerItem(Items.BUCKET).maxStackSize(1).group(ItemGroup.MISC))
  );

  public static final ForgeFlowingFluid.Properties xpfluid_properties =
          new ForgeFlowingFluid.Properties(xp, xp_flowing, FluidAttributes.builder(FLUID_STILL, FLUID_FLOWING).color(0xff00ff00))
                  .bucket(xp_bucket).block(xpfluid_block);

  public ExtendedFurnaces() {
    // Register the setup method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
    // Register the doClientStuff method for modloading
    FMLJavaModLoadingContext.get().getModEventBus().addListener(this::doClientStuff);
    IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

    BLOCKS.register(modEventBus);
    ITEMS.register(modEventBus);
    FLUIDS.register(modEventBus);
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
      Item.Properties properties = new Item.Properties().group(ItemGroup.DECORATIONS);
      register(new Item(properties),"speed_upgrade",event.getRegistry());
      register(new Item(properties),"efficiency_upgrade",event.getRegistry());
      register(new Item(properties),"xp_upgrade",event.getRegistry());
      register(new Item(properties),"multiplying_upgrade",event.getRegistry());
      register(new BlockItem(RegistryObjects.extended_furnace,properties),"extended_furnace",event.getRegistry());
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
    public static final Item speed_upgrade = null;
    public static final Item efficiency_upgrade = null;
    public static final Item xp_upgrade = null;
    public static final Item multiplying_upgrade = null;


  }
}
