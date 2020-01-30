package com.tfar.extendedfurnace;

import com.tfar.extendedfurnace.inventory.OutputSlot;
import com.tfar.extendedfurnace.inventory.SlotItemHandlerUnconditioned;
import com.tfar.extendedfurnace.inventory.UpgradeSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

public class ExtendedFurnanceContainer extends Container {

  public ExtendedFurnaceBlockEntity te;

  protected ExtendedFurnanceContainer(int id, World world, BlockPos pos, PlayerInventory inv) {
    super(ExtendedFurnaces.RegistryObjects.container_type, id);

    te = (ExtendedFurnaceBlockEntity) world.getTileEntity(pos);

    this.addSlot(new SlotItemHandlerUnconditioned(te.inv, 0, 56, 34));
    this.addSlot(new OutputSlot(te.inv, 1, 116, 34));

    this.addSlot(new UpgradeSlot(te.upgrades, 0, 177, 5 + 18 * 0, ExtendedFurnaces.RegistryObjects.speed_upgrade));
    this.addSlot(new UpgradeSlot(te.upgrades, 1, 177, 5 + 18 * 1, ExtendedFurnaces.RegistryObjects.efficiency_upgrade));
    this.addSlot(new UpgradeSlot(te.upgrades, 2, 177, 5 + 18 * 2, ExtendedFurnaces.RegistryObjects.xp_upgrade));
    this.addSlot(new UpgradeSlot(te.upgrades, 3, 177, 5 + 18 * 3, ExtendedFurnaces.RegistryObjects.multiplying_upgrade, 2));


    for (int i = 0; i < 3; ++i) {
      for (int j = 0; j < 9; ++j) {
        this.addSlot(new Slot(inv, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
      }
    }

    for (int k = 0; k < 9; ++k) {
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

  @Nonnull
  @Override
  public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
    ItemStack itemstack = ItemStack.EMPTY;
    Slot slot = this.inventorySlots.get(index);
    if (slot != null && slot.getHasStack()) {
      ItemStack itemstack1 = slot.getStack();
      itemstack = itemstack1.copy();
      if (index == 0) {
        if (!this.mergeItemStack(itemstack1, 6, 42, true)) {
          return ItemStack.EMPTY;
        }
      } else if (index == 1) {
        if (!this.mergeItemStack(itemstack1, 6, 42, true)) {
          return ItemStack.EMPTY;
        }
      } else if (index < 6) {
        if (!this.mergeItemStack(itemstack1, 6, 39, false)) {
          return ItemStack.EMPTY;
        }
      } else if (!this.mergeItemStack(itemstack1, 2, 6, false) &&
              !this.mergeItemStack(itemstack1, 0, 2, false)) {
        return ItemStack.EMPTY;
      }

      if (itemstack1.isEmpty()) {
        slot.putStack(ItemStack.EMPTY);
      } else {
        slot.onSlotChanged();
      }
    }
    return itemstack;
  }
}
