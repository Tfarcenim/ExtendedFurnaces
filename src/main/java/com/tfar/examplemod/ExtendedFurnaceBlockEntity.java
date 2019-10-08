package com.tfar.examplemod;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExtendedFurnaceBlockEntity extends TileEntity implements INamedContainerProvider, ITickableTileEntity {

  protected IRecipeType recipeType;

  public ItemStackHandler input = new ItemStackHandler();
  public LazyOptional<IItemHandler> inputLazyOptional = LazyOptional.of(() -> input);

  public ItemStackHandler output = new ItemStackHandler();
  public LazyOptional<IItemHandler> outputLazyOptional = LazyOptional.of(() -> output);

  MyEnergyStorage energyStorage = new MyEnergyStorage(MAXENERGY, RFPERTICK);
  public LazyOptional<IEnergyStorage> energyLazyOptional = LazyOptional.of(() -> energyStorage);

  private ITextComponent customName;

  protected AbstractCookingRecipe curRecipe;
  protected ItemStack failedMatch = ItemStack.EMPTY;

  int progress = 0;

  public ExtendedFurnaceBlockEntity() {
    super(ExtendedFurnaces.RegistryObjects.tile_type);
    this.recipeType = IRecipeType.BLASTING;
  }

  public void changeRecipeType(){

  }

  @Override
  public void tick() {
    if (!world.isRemote)
    if (energyStorage.getEnergyStored() < RFPERTICK) {
     // setState(FurnaceState.NOPOWER);
      if (System.currentTimeMillis()%50 == 0)System.out.println("no power");
      return;
    }

    if (progress > 0) {
      //setState(FurnaceState.WORKING);
      energyStorage.consumePower(RFPERTICK);
      progress--;
      if (progress <= 0) {
        attemptSmelt();
      }
      markDirty();
    } else {
      startSmelt();
    }
  }

  private static final int MAXENERGY = 10000;
  private static final int RFPERTICK = 40;
  private static final int INPUT_SLOTS = 1;
  private static final int OUTPUT_SLOTS = 1;

  private void attemptSmelt() {
    for (int i = 0 ; i < INPUT_SLOTS ; i++) {
      ItemStack result = getResult(input.getStackInSlot(i));
      if (!result.isEmpty()) {
        // This copy is very important!(
        if (insertOutput(result.copy(), false)) {
          input.extractItem(i, 1, false);
          break;
        }
      }
    }
  }

  private boolean insertOutput(ItemStack result, boolean simulate) {
    for (int i = 0 ; i < OUTPUT_SLOTS ; i++) {
      ItemStack remaining = output.insertItem(i, result, simulate);
      if (remaining.isEmpty()) {
        return true;
      }
    }
    return false;
  }

  private void startSmelt() {
    for (int i = 0 ; i < INPUT_SLOTS ; i++) {
      ItemStack result = getResult(input.getStackInSlot(i));
      if (!result.isEmpty()) {
        if (insertOutput(result.copy(), true)) {
          //setState(FurnaceState.WORKING);
          progress = 200;
          markDirty();
          return;
        }
      }
    }
    //setState(FurnaceState.OFF);
  }


  private ItemStack getResult(ItemStack stackInSlot) {
    AbstractCookingRecipe irecipe = getRecipe(stackInSlot);
    if (irecipe != null)return irecipe.getRecipeOutput();
    //return FurnaceRecipes.instance().getSmeltingResult(stackInSlot);
    return ItemStack.EMPTY;
  }

  protected AbstractCookingRecipe getRecipe(ItemStack input) {
    if (input.isEmpty() || input == failedMatch) return null;
    if (curRecipe != null && curRecipe.matches(new RecipeWrapper(this.input), world)) return curRecipe;
    else {
      AbstractCookingRecipe rec = (AbstractCookingRecipe) world.getRecipeManager().getRecipe(this.recipeType, new RecipeWrapper(this.input), this.world).orElse(null);
      if (rec == null) failedMatch = input;
      else failedMatch = ItemStack.EMPTY;
      return curRecipe = rec;
    }
  }


  @Nonnull
  @Override
  public CompoundNBT write(CompoundNBT tag) {
    CompoundNBT compound = this.input.serializeNBT();
    tag.put("inv", compound);
    if (this.customName != null) {
      tag.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
    }
    compound.putInt("progress", progress);
    compound.putInt("energy", energyStorage.getEnergyStored());    return super.write(tag);
  }

  @Override
  public void read(CompoundNBT tag) {
    CompoundNBT invTag = tag.getCompound("inv");
    input.deserializeNBT(invTag);
    if (tag.contains("CustomName", 8)) {
      this.customName = ITextComponent.Serializer.fromJson(tag.getString("CustomName"));
    }
    progress = tag.getInt("progress");
    energyStorage.setEnergy(tag.getInt("energy"));
    super.read(tag);
  }


  @Override
  public ITextComponent getDisplayName() {
    return new TranslationTextComponent("Extended Furnace");
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
    return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? inputLazyOptional.cast() :
            cap == CapabilityEnergy.ENERGY ? energyLazyOptional.cast() :
                    super.getCapability(cap, side);
  }



  @Nullable
  @Override
  public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
    return new ExtendedFurnanceContainer(p_createMenu_1_,world,pos,p_createMenu_2_);
  }

  @Override
  public CompoundNBT getUpdateTag() {
return this.write(new CompoundNBT());
  }

  @Override
  public SUpdateTileEntityPacket getUpdatePacket()
  {
    CompoundNBT nbt = new CompoundNBT();
    this.write(nbt);

    return new SUpdateTileEntityPacket(getPos(), 1, nbt);
  }

  @Override
  public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
    this.read(packet.getNbtCompound());
  }
}
