package com.tfar.examplemod;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;

import javax.annotation.Nonnull;

public class ExtendedFurnaceEnergyStorage extends EnergyStorage implements INBTSerializable<CompoundNBT> {

  public TileEntity blockEntity;

  public ExtendedFurnaceEnergyStorage(int capacity, int maxReceive, TileEntity blockEntity) {
    super(capacity, maxReceive, 0);
    this.blockEntity = blockEntity;
  }

  public void setEnergy(int energy) {
    this.energy = energy;
    onEnergyChanged();
  }

  public void consumePower(int energy) {
    if (energy == 0 || this.energy == 0)return;
    this.energy -= energy;
    if (this.energy < 0) {
      this.energy = 0;
    }
    onEnergyChanged();
  }

  @Override
  public CompoundNBT serializeNBT() {
    CompoundNBT compound = new CompoundNBT();
    compound.putInt("energy", energy);
    return compound;
  }

  @Override
  public void deserializeNBT(@Nonnull CompoundNBT compound) {
    energy = compound.contains("energy") ? compound.getInt("energy") : 0;
  }


  public void onEnergyChanged(){
    this.blockEntity.getWorld().notifyBlockUpdate(blockEntity.getPos(),blockEntity.getBlockState(),blockEntity.getBlockState(),3);
  }
}