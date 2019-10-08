package com.tfar.examplemod;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class ExtendedFurnaceBlock extends Block {
  public ExtendedFurnaceBlock(Properties properties) {
    super(properties);
  }

  @Override
  public boolean onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
    if (!world.isRemote) {
      TileEntity tileEntity = world.getTileEntity(pos);
      if (tileEntity instanceof INamedContainerProvider) {
        NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) tileEntity, tileEntity.getPos());
        player.addStat(Stats.OPEN_BARREL);
      } else throw new IllegalStateException("Our named container provider is missing!");
    }
    return true;
  }

  @Override
  public boolean hasTileEntity(BlockState state) {
    return true;
  }

  @Nullable
  @Override
  public TileEntity createTileEntity(BlockState state, IBlockReader world) {
    return new ExtendedFurnaceBlockEntity();
  }
}
