package com.tfar.extendedfurnace;

import com.tfar.extendedfurnace.inventory.AutomationSensitiveItemStackHandler;
import com.tfar.extendedfurnace.inventory.ExtendedFurnaceEnergyStorage;
import com.tfar.extendedfurnace.inventory.OutputOnlyFluidTank;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ExtendedFurnaceBlockEntity extends TileEntity implements INamedContainerProvider, ITickableTileEntity {

  public RecipeType recipeType = RecipeType.BLASTING;
  public static final RecipeType[] recipeTypes = RecipeType.values();

  public enum RecipeType {
      BLASTING(IRecipeType.BLASTING, new ItemStack(Items.BLAST_FURNACE)), SMOKING(IRecipeType.SMOKING,new ItemStack(Items.SMOKER)), SMELTING(IRecipeType.SMELTING,new ItemStack(Items.FURNACE));
      public IRecipeType iRecipeType;
      public ItemStack icon;
      RecipeType(IRecipeType iRecipeType, ItemStack icon){
        this.iRecipeType = iRecipeType;
        this.icon = icon;
      }
  }

  public AutomationSensitiveItemStackHandler inv = new TileStackHandler(2);

  public ItemStackHandler upgrades = new ItemStackHandler(4){
    @Override
    protected void onContentsChanged(int slot) {
      markDirty();
      if (slot == 1){
        energyStorage.scaleCapacity(upgrades.getStackInSlot(slot).getCount());
      }
    }
  };

  //public CombinedInvWrapper wrapper = new CombinedInvWrapper(inv,upgrades);

  public LazyOptional<IItemHandler> combinedLazyOptional = LazyOptional.of(() -> inv);

  ExtendedFurnaceEnergyStorage energyStorage = new ExtendedFurnaceEnergyStorage(10000, Integer.MAX_VALUE, this);
  public LazyOptional<IEnergyStorage> energyLazyOptional = LazyOptional.of(() -> energyStorage);

  public OutputOnlyFluidTank fluidStorage = new OutputOnlyFluidTank(Integer.MAX_VALUE);
  public LazyOptional<IFluidHandler> fluidLazyOptional = LazyOptional.of(() -> fluidStorage);

  private ITextComponent customName;

  protected AbstractCookingRecipe curRecipe;
  protected ItemStack failedMatch = ItemStack.EMPTY;


  int progress = 0;
  int progresstotal = 1;

  public ExtendedFurnaceBlockEntity() {
    super(ExtendedFurnaces.RegistryObjects.tile_type);
  }

  public void changeRecipeType(boolean client) {
    int ordinal = recipeType.ordinal();
    ordinal++;
    if (ordinal> 2)ordinal = 0;
    recipeType = recipeTypes[ordinal];
    if (client)return;
    curRecipe = null;
  }

  @Override
  public void tick() {
    if (!world.isRemote) {
      if (energyStorage.getEnergyStored() < getRequiredPower()) {
        // setState(FurnaceState.NOPOWER);
        return;
      }
      boolean canSmeltOn = this.canSmeltOn(0, 1);
      if (canSmeltOn) {
        if (curRecipe != null && progress < curRecipe.getCookTime()) {
          world.setBlockState(pos,this.getBlockState().with(ExtendedFurnaceBlock.LIT,true));
          energyStorage.extractEnergy(getRequiredPower(), false);
          incrementProgress();
          updateClient();
        } else if (progress >= curRecipe.getCookTime()) {
          finish();
        } else if (!this.inv.getStackInSlot(0).isEmpty()) {
          start();
        }
      }
      else {
        world.setBlockState(pos,this.getBlockState().with(ExtendedFurnaceBlock.LIT,false));
      }
    }
  }

  public int getRequiredPower(){
    int energyConsumption = 80;
    if (recipeType == RecipeType.SMELTING)energyConsumption /= 2;
    int speedcount = upgrades.getStackInSlot(0).getCount();
    int efficiencycount = upgrades.getStackInSlot(1).getCount();
    double multiplierfromspeed = Math.pow(1.44,speedcount);
    double multiplierfromefficiency = Math.pow(.8,efficiencycount);
    return (int) (energyConsumption * multiplierfromefficiency * multiplierfromspeed);
  }

  public boolean canSmeltOn(int theInput, int theOutput) {
    if (!this.inv.getStackInSlot(theInput).isEmpty()) {
      ItemStack output = getResult(inv.getStackInSlot(theInput));
      if (!output.isEmpty())
        return this.inv.getStackInSlot(theOutput).isEmpty() || this.inv.getStackInSlot(theOutput).isItemEqual(output) && (this.inv.getStackInSlot(theOutput).getCount()+ this.upgrades.getStackInSlot(3).getCount()) <= this.inv.getStackInSlot(theOutput).getMaxStackSize() - output.getCount();
    }
    return false;
  }

  private static final int INPUT_SLOT = 0;
  private static final int OUTPUT_SLOT = 1;

  private void start() {
    ItemStack result = getResult(inv.getStackInSlot(0));
    if (!result.isEmpty()) {
        markDirty();
      }
    }

    public void incrementProgress(){
    int speedcount = upgrades.getStackInSlot(0).getCount();
    double increase = Math.pow(1.2,speedcount);
    progress += increase;
    }

    public int getTotalsmelts(){
    return progress/curRecipe.getCookTime();
  }

  private void finish() {
    // This copy is very important!
    ItemStack result = curRecipe.getCraftingResult(new RecipeWrapper(this.inv)).copy();
      if (!result.isEmpty()) {
        int existingcount = this.inv.getStackInSlot(1).getCount();
        int multiplication = getMultiplier();
        int totalsmelts = this.getTotalsmelts();
        int inputcount = inv.getStackInSlot(0).getCount();
        if (inputcount < totalsmelts)totalsmelts = inputcount;
        int maxsmeltops = (this.inv.getStackInSlot(1).getMaxStackSize() - existingcount)/multiplication;

        if (maxsmeltops < totalsmelts)totalsmelts = maxsmeltops;

        if (totalsmelts > 1)result.grow((totalsmelts - 1) * multiplication);

        if (inv.getStackInSlot(1).isEmpty()){
          result.grow(multiplication - 1);
          inv.setStackInSlot(1,result);
        }
        else inv.getStackInSlot(1).grow(totalsmelts * multiplication);
        inv.getStackInSlot(0).shrink(totalsmelts);

          progress -= (curRecipe.getCookTime() * totalsmelts);
          this.fluidStorage.add(new FluidStack(ExtendedFurnaces.xp.get(),(int)Math.ceil(Math.pow(upgrades.getStackInSlot(2).getCount()+1,2) * totalsmelts * curRecipe.getExperience() * 10))
                  , IFluidHandler.FluidAction.EXECUTE);
          if (inv.getStackInSlot(0).isEmpty()) curRecipe = null;
      }
  }
    //setState(FurnaceState.OFF);

  private ItemStack getResult(ItemStack stackInSlot) {
    AbstractCookingRecipe irecipe = getRecipe(stackInSlot);
    return irecipe != null ? irecipe.getRecipeOutput() : ItemStack.EMPTY;
  }

  protected int getMultiplier(){
    return inv.getStackInSlot(0).getItem().isIn(ExtendedFurnaces.BLACKLISTED_ITEMS) ? 1 :
    this.upgrades.getStackInSlot(3).getCount() + 1;
  }

  protected AbstractCookingRecipe getRecipe(ItemStack input) {
    if (input.isEmpty() || input == failedMatch) return null;
    if (curRecipe != null && curRecipe.matches(new RecipeWrapper(this.inv), world)) return curRecipe;
    else {
      AbstractCookingRecipe rec = (AbstractCookingRecipe) world.getRecipeManager().getRecipe(this.recipeType.iRecipeType, new RecipeWrapper(this.inv), this.world).orElse(null);
      if (rec == null) failedMatch = input;
      else {
        failedMatch = ItemStack.EMPTY;
        progresstotal = rec.getCookTime();
      }
      return curRecipe = rec;
    }
  }


  @Nonnull
  @Override
  public CompoundNBT write(CompoundNBT tag) {
    CompoundNBT inv = this.inv.serializeNBT();
    tag.put("inv", inv);
    CompoundNBT upgrades = this.upgrades.serializeNBT();
    tag.put("upgrades", upgrades);
    if (this.customName != null) {
      tag.putString("CustomName", ITextComponent.Serializer.toJson(this.customName));
    }
    tag.putInt("progress", progress);

    tag.putInt("recipetype", recipeType.ordinal());
    tag.putInt("xp", fluidStorage.getFluidAmount());
    tag.putInt("progresstotal", progresstotal);
    CompoundNBT energyTag = this.energyStorage.serializeNBT();
    tag.put("energyinv", energyTag);
    return super.write(tag);
  }

  @Override
  public void read(CompoundNBT tag) {
    CompoundNBT invTag = tag.getCompound("inv");
    inv.deserializeNBT(invTag);
    CompoundNBT upgrades = tag.getCompound("upgrades");
    this.upgrades.deserializeNBT(upgrades);
    if (tag.contains("CustomName", 8)) {
      this.customName = ITextComponent.Serializer.fromJson(tag.getString("CustomName"));
    }
    recipeType = recipeTypes[tag.getInt("recipetype")];
    progress = tag.getInt("progress");
    fluidStorage.setFluid(new FluidStack(ExtendedFurnaces.xp.get(),tag.getInt("xp")));
    progresstotal = tag.getInt("progresstotal");
    CompoundNBT energyinv = tag.getCompound("energyinv");
    energyStorage.deserializeNBT(energyinv);
    super.read(tag);
  }


  @Override
  public ITextComponent getDisplayName() {
    return new TranslationTextComponent("Extended Furnace");
  }

  @Nonnull
  @Override
  public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
    return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY ? getItemHandlerLazyOptional(side).cast() : cap == CapabilityEnergy.ENERGY ? energyLazyOptional.cast() :
            cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? fluidLazyOptional.cast() :
                    super.getCapability(cap, side);
  }

  public void updateClient(){
    this.world.notifyBlockUpdate(this.pos, this.getBlockState(), this.getBlockState(), 2);
  }

  @Override
  public void markDirty() {
    super.markDirty();
    updateClient();
  }

  public LazyOptional<IItemHandler> getItemHandlerLazyOptional(Direction dir) {
    return combinedLazyOptional;
  }

  @Nullable
  @Override
  public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
    return new ExtendedFurnanceContainer(p_createMenu_1_, world, pos, p_createMenu_2_);
  }

  @Override
  public CompoundNBT getUpdateTag() {
    return this.write(new CompoundNBT());
  }

  @Override
  public SUpdateTileEntityPacket getUpdatePacket() {
    return new SUpdateTileEntityPacket(getPos(), 1, getUpdateTag());
  }

  @Override
  public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
    this.read(packet.getNbtCompound());
  }

  public AutomationSensitiveItemStackHandler.IAcceptor getAcceptor() {
    return (slot, stack, automation) -> !automation || slot == INPUT_SLOT /*&& StackUtil.isValid(FurnaceRecipes.instance().getSmeltingResult(stack))*/;
  }

  public AutomationSensitiveItemStackHandler.IRemover getRemover() {
    return (slot, automation) -> !automation || slot == OUTPUT_SLOT;
  }

  protected class TileStackHandler extends AutomationSensitiveItemStackHandler {

    protected TileStackHandler(int slots) {
      super(slots);
    }

    @Override
    public AutomationSensitiveItemStackHandler.IAcceptor getAcceptor() {
      return ExtendedFurnaceBlockEntity.this.getAcceptor();
    }

    @Override
    public AutomationSensitiveItemStackHandler.IRemover getRemover() {
      return ExtendedFurnaceBlockEntity.this.getRemover();
    }

    @Override
    protected void onContentsChanged(int slot) {
      super.onContentsChanged(slot);
      markDirty();
    }
  }
}
