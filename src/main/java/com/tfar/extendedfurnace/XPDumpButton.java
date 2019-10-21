package com.tfar.extendedfurnace;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class XPDumpButton extends Button {

  public static final ItemStack xpIcon = new ItemStack(Items.EXPERIENCE_BOTTLE);

  public XPDumpButton(int xpos, int ypos, int width, int height, IPressable onPress) {
    super(xpos, ypos, width, height, "", onPress);
  }

  @Override
  public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
    super.render(p_render_1_, p_render_2_, p_render_3_);
    GlStateManager.enableRescaleNormal();
    RenderHelper.enableGUIStandardItemLighting();

    Minecraft.getInstance().getItemRenderer().renderItemAndEffectIntoGUI(xpIcon, x+2, y+2);

    Minecraft.getInstance().getItemRenderer().renderItemOverlays(Minecraft.getInstance().fontRenderer, xpIcon, x, y);
    RenderHelper.disableStandardItemLighting();
    GlStateManager.disableRescaleNormal();
    GlStateManager.popMatrix();
  }
}
