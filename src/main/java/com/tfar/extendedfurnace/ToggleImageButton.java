package com.tfar.extendedfurnace;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;

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
    GlStateManager.enableRescaleNormal();
    RenderHelper.enableGUIStandardItemLighting();

    Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(type.icon, x+2, y+2);

    Minecraft.getInstance().getItemRenderer().renderItemOverlays(Minecraft.getInstance().fontRenderer, type.icon, x, y);
    RenderHelper.disableStandardItemLighting();
    GlStateManager.disableRescaleNormal();
    GlStateManager.popMatrix();
  }
}
