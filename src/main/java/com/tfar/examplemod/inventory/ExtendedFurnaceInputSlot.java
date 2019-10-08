package com.tfar.examplemod.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nonnull;

public class ExtendedFurnaceInputSlot extends SlotItemHandler {
  public ExtendedFurnaceInputSlot(IItemHandler itemHandler, int index, int xPosition, int yPosition) {
    super(itemHandler, index, xPosition, yPosition);
  }

  @Override
  public boolean isItemValid(@Nonnull ItemStack stack) {
    return super.isItemValid(stack);
  }
}
