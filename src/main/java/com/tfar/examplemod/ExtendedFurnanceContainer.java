package com.tfar.examplemod;

import com.tfar.examplemod.inventory.ExtendedFurnaceInputSlot;
import com.tfar.examplemod.inventory.ExtendedFurnaceOutputSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.items.SlotItemHandler;

public class ExtendedFurnanceContainer extends Container {

  public ExtendedFurnaceBlockEntity te;
  protected ExtendedFurnanceContainer(int id, World world, BlockPos pos, PlayerInventory inv) {
    super(ExtendedFurnaces.RegistryObjects.container_type, id);

    te = (ExtendedFurnaceBlockEntity) world.getTileEntity(pos);

    this.addSlot(new ExtendedFurnaceInputSlot(te.input, 0, 56, 34));
    this.addSlot(new ExtendedFurnaceOutputSlot(te.output, 0, 116, 34));

    for(int i = 0; i < 3; ++i) {
      for(int j = 0; j < 9; ++j) {
        this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
      }
    }

    for(int k = 0; k < 9; ++k) {
      this.addSlot(new Slot(inv, k, 8 + k * 18, 142));
    }
  }

  /**
   * Determines whether supplied player can use this container
   *
   * @param playerIn
   */
  @Override
  public boolean canInteractWith(PlayerEntity playerIn) {
    return true;
  }

  @Override
  public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
    ItemStack itemstack = ItemStack.EMPTY;
    Slot slot = this.inventorySlots.get(index);
    if (slot != null && slot.getHasStack()) {
      ItemStack itemstack1 = slot.getStack();
      itemstack = itemstack1.copy();
      if (index < 2) {
        if (!this.mergeItemStack(itemstack1, 0, 2, true)) {
          return ItemStack.EMPTY;
        }
      } else if (!this.mergeItemStack(itemstack1, 2, 2 + 36, false)) {
        return ItemStack.EMPTY;
      }

      if (itemstack1.isEmpty()) {
        slot.putStack(ItemStack.EMPTY);
      } else {
        slot.onSlotChanged();
      }
    }

    return itemstack;  }
}
