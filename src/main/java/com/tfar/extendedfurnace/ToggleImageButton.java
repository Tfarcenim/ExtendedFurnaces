package com.tfar.extendedfurnace;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.item.ItemStack;

public class ToggleImageButton extends Button {

  public ExtendedFurnaceBlockEntity.RecipeType type;

  public ToggleImageButton(int xpos,int ypos, int width, int height, IPressable onPress, ExtendedFurnaceBlockEntity.RecipeType type) {
    super(xpos, ypos, width, height, "", onPress);
    this.type = type;
  }

  public void toggle(){
    int ordinal = type.ordinal();
    ordinal++;
    if (ordinal > 2)ordinal = 0;
    type = ExtendedFurnaceBlockEntity.recipeTypes[ordinal];
  }

  @Override
  public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
    super.render(p_render_1_, p_render_2_, p_render_3_);
    drawItemStack(type.icon, x+2, y+2);
  }

  /**
   * Draws an ItemStack.
   *
   * The z index is increased by 32 (and not decreased afterwards), and the item is then rendered at z=200.
   */
  private void drawItemStack(ItemStack stack, int x, int y) {

    ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
    RenderSystem.translatef(0.0F, 0.0F, 32.0F);
    this.setBlitOffset(200);
    itemRenderer.zLevel = 200.0F;
    net.minecraft.client.gui.FontRenderer font = stack.getItem().getFontRenderer(stack);
    //if (font == null) font = this.font;
    itemRenderer.renderItemAndEffectIntoGUI(stack, x, y);
    //itemRenderer.renderItemOverlayIntoGUI(font, stack, x, y - (this.draggedStack.isEmpty() ? 0 : 8), altText);
    this.setBlitOffset(0);
    itemRenderer.zLevel = 0.0F;
  }

}
