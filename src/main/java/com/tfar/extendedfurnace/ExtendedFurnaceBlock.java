package com.tfar.extendedfurnace;

import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;
import java.util.Random;
import java.util.stream.IntStream;

public class ExtendedFurnaceBlock extends AbstractFurnaceBlock {
  public ExtendedFurnaceBlock(Properties properties) {
    super(properties);
  }

  private static final Random RANDOM = new Random();

  @Override
  public ActionResultType func_225533_a_(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
    if (!world.isRemote) {
      TileEntity tileEntity = world.getTileEntity(pos);
      if (tileEntity instanceof INamedContainerProvider) {
        NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getPos());
      }
    }
    return ActionResultType.SUCCESS;
  }

  @Override
  public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
    if (state.getBlock() != newState.getBlock()) {
      TileEntity tileentity = world.getTileEntity(pos);
      if (tileentity instanceof ExtendedFurnaceBlockEntity) {
        ExtendedFurnaceBlockEntity extendedFurnaceBlockEntity = (ExtendedFurnaceBlockEntity)tileentity;
        dropItems((ExtendedFurnaceBlockEntity) tileentity, world, pos);
        ExperienceOrbEntity xp = new ExperienceOrbEntity(world,pos.getX(),pos.getY(),pos.getZ(),
                extendedFurnaceBlockEntity.fluidStorage.getFluidAmount()/10);
        if (xp.xpValue != 0) {
          world.addEntity(xp);
        }
      }

      if (state.hasTileEntity() && state.getBlock() != newState.getBlock()) {
        world.removeTileEntity(pos);
      }
    }
  }

  public static void dropItems(ExtendedFurnaceBlockEntity furnace, World world, BlockPos pos) {
    IntStream.range(0, furnace.inv.getSlots()).mapToObj(i -> furnace.inv.getStackInSlot(i)).filter(stack -> !stack.isEmpty()).forEach(stack -> spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack));
    IntStream.range(0, furnace.upgrades.getSlots()).mapToObj(i -> furnace.upgrades.getStackInSlot(i)).filter(stack -> !stack.isEmpty()).forEach(stack -> spawnItemStack(world, pos.getX(), pos.getY(), pos.getZ(), stack));

  }

  public static void spawnItemStack(World worldIn, double x, double y, double z, ItemStack stack) {
    double d0 = EntityType.ITEM.getWidth();
    double d1 = 1 - d0;
    double d2 = d0 / 2;
    double d3 = Math.floor(x) + RANDOM.nextDouble() * d1 + d2;
    double d4 = Math.floor(y) + RANDOM.nextDouble() * d1;
    double d5 = Math.floor(z) + RANDOM.nextDouble() * d1 + d2;

    while (!stack.isEmpty()) {
      ItemEntity itementity = new ItemEntity(worldIn, d3, d4, d5, stack.split(RANDOM.nextInt(21) + 10));
      float f = 0.05F;
      itementity.setMotion(RANDOM.nextGaussian() * f, RANDOM.nextGaussian() * f + 0.2, RANDOM.nextGaussian() * f);
      worldIn.addEntity(itementity);
    }
  }

  /**
   * Interface for handling interaction with blocks that impliment AbstractFurnaceBlock. Called in onBlockActivated
   * inside AbstractFurnaceBlock.
   *
   * @param worldIn
   * @param pos
   * @param player
   */
  @Override
  protected void interactWith(World worldIn, BlockPos pos, PlayerEntity player) {}

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new ExtendedFurnaceBlockEntity();
  }

  @Nullable
  @Override
  public TileEntity createNewTileEntity(IBlockReader worldIn) {
    return null;
  }
}
