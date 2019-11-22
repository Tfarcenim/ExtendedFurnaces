package com.tfar.extendedfurnace.inventory;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;

import javax.annotation.Nonnull;

public class ExtendedFurnaceEnergyStorage extends EnergyStorage implements INBTSerializable<CompoundNBT> {

  public TileEntity blockEntity;

  public ExtendedFurnaceEnergyStorage(int capacity, int maxReceive, TileEntity blockEntity) {
    super(capacity, maxReceive, Integer.MAX_VALUE);
    this.blockEntity = blockEntity;
  }

  @Override
  public CompoundNBT serializeNBT() {
    CompoundNBT compound = new CompoundNBT();
    compound.putInt("capacity", capacity);
    compound.putInt("energy", energy);
    return compound;
  }

  @Override
  public void deserializeNBT(@Nonnull CompoundNBT compound) {
    capacity = compound.getInt("capacity");
    energy = compound.getInt("energy");
  }

  @Override
  public int receiveEnergy(int maxReceive, boolean simulate) {
    int received = super.receiveEnergy(maxReceive, simulate);
    if (received > 0 && !simulate)onEnergyChanged();
    return received;
  }

  @Override
  public int extractEnergy(int maxExtract, boolean simulate) {
    int extracted = super.extractEnergy(maxExtract, simulate);
    if (extracted > 0 && !simulate)onEnergyChanged();
    return extracted;
  }

  public void scaleCapacity(int upgrades){
    capacity = (int) (10000 * Math.pow(1.2,upgrades));
  }

  public void onEnergyChanged(){
    this.blockEntity.getWorld().notifyBlockUpdate(blockEntity.getPos(),blockEntity.getBlockState(),blockEntity.getBlockState(),3);
  }
}