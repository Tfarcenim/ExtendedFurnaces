package com.tfar.examplemod;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

public class ExtendedFurnaceScreen extends ContainerScreen<ExtendedFurnanceContainer> {
  public ExtendedFurnaceScreen(ExtendedFurnanceContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
    super(screenContainer, inv, titleIn);
  }

  @Override
  protected void init() {
    super.init();
    addButton(new ToggleImageButton(guiLeft + 54,guiTop + 52,20,20,(b) ->{
      ((ToggleImageButton)b).toggle();
    }));
  }

  /**
   * Draws the background layer of this container (behind the items).
   *
   * @param partialTicks
   * @param mouseX
   * @param mouseY
   */
  @Override
  protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
    ResourceLocation texture = new ResourceLocation(ExtendedFurnaces.MODID,"textures/gui/extended_furnace.png");
    this.minecraft.getTextureManager().bindTexture(texture);
    int i = (this.width - this.xSize) / 2;
    int j = (this.height - this.ySize) / 2;
    this.blit(i, j, 0, 0, this.xSize, this.ySize);
//    31
    int x1 = 184;
    int x2 = x1 - 8;
    int y1 = 31;
    double scaledbar = 64 * this.container.te.energyStorage.getEnergyStored()/10000d;
    int y2 = y1 + 100 - (int) scaledbar;
    int y3 = (int)scaledbar;

    double scaledprogress = 24d * this.container.te.progress / this.container.te.progresstotal;
    this.blit(i + 79, j + 35, 176, 14, (int)scaledprogress, 16);

    //energy bar
    GlStateManager.color3f(1,0,0);
    this.blit(i + 9, j + 10, x1, y1, 8, 64);
    this.blit(i + 9, j + 10 + 64 - y3, x2, y1, 8, y3);
    GlStateManager.color3f(0,1,0);
    this.blit(i + 160, j + 10, x1, y1, 8, 64);
    this.minecraft.fontRenderer.drawString(this.container.te.energyStorage.getEnergyStored()+"",i+80,j+25,0x404040);
  }

  @Override
  public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
    this.renderBackground();
    super.render(p_render_1_, p_render_2_, p_render_3_);
    this.renderHoveredToolTip(p_render_1_, p_render_2_);
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    this.font.drawString(this.title.getFormattedText(), 40, 6, 0x404040);
  }
}
