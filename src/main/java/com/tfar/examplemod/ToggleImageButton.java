package com.tfar.examplemod;

import net.minecraft.client.gui.widget.button.Button;

public class ToggleImageButton extends Button {
  public ToggleImageButton(int xpos,int ypos, int width, int height, IPressable onPress) {
    super(xpos, ypos, width, height, "", onPress);
  }
  public void toggle(){

  }

  @Override
  public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
    super.render(p_render_1_, p_render_2_, p_render_3_);
  }
}
