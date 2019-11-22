package com.tfar.extendedfurnace.inventory;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

public class OutputOnlyFluidTank extends FluidTank {

  public OutputOnlyFluidTank(int capacity) {
    super(capacity);
  }

  @Override
  public int fill(FluidStack resource, FluidAction action) {
    return 0;
  }

  //replaces fill, internal use only
  public int add(FluidStack resource, FluidAction action) {
    if (action.simulate()) {
      if (fluid.isEmpty()) {
        return Math.min(capacity, resource.getAmount());
      }
      if (!fluid.isFluidEqual(resource)) {
        return 0;
      }
      return Math.min(capacity - fluid.getAmount(), resource.getAmount());
    }
    if (fluid.isEmpty()) {
      fluid = new FluidStack(resource, Math.min(capacity, resource.getAmount()));
      onContentsChanged();
      return fluid.getAmount();
    }
    if (!fluid.isFluidEqual(resource)) {
      return 0;
    }
    int filled = capacity - fluid.getAmount();

    if (resource.getAmount() < filled) {
      fluid.grow(resource.getAmount());
      filled = resource.getAmount();
    } else {
      fluid.setAmount(capacity);
    }
    if (filled > 0)
      onContentsChanged();
    return filled;
  }
}
