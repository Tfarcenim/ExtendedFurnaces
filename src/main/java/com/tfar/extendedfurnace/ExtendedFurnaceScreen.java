package com.tfar.extendedfurnace;

import com.mojang.blaze3d.platform.GlStateManager;
import com.tfar.extendedfurnace.network.C2SRecipeModePacket;
import com.tfar.extendedfurnace.network.C2SXpDumpPacket;
import com.tfar.extendedfurnace.network.PacketHandler;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.client.config.GuiUtils;

import java.util.ArrayList;
import java.util.List;

public class ExtendedFurnaceScreen extends ContainerScreen<ExtendedFurnanceContainer> {

  public ExtendedFurnaceScreen(ExtendedFurnanceContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
    super(screenContainer, inv, titleIn);
    this.xSize+= 22;
  }

  @Override
  protected void init() {
    super.init();
    addButton(new ToggleImageButton(guiLeft + 54,guiTop + 52,20,20,b ->{
      ((ToggleImageButton)b).toggle();
      container.te.changeRecipeType(true);
      PacketHandler.INSTANCE.sendToServer(new C2SRecipeModePacket());
    },container.te.recipeType));
    addButton(new XPDumpButton(guiLeft + 138,guiTop + 33,20,20,(b) -> PacketHandler.INSTANCE.sendToServer(new C2SXpDumpPacket())));
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
    int x1 = 206;
    int x2 = x1 - 8;
    int y1 = 17;
    double scaledbar = 64d * this.container.te.energyStorage.getEnergyStored()/this.container.te.energyStorage.getMaxEnergyStored();
    int y3 = (int)scaledbar;

    double scaledprogress = MathHelper.clamp(24d * this.container.te.progress / this.container.te.progresstotal,0,24);
    this.blit(i + 79, j + 35, 198, 0, (int)scaledprogress, 16);

    //energy bar
    GlStateManager.color3f(1,0,0);
    this.blit(i + 9, j + 10, x1, y1, 8, 64);
    this.blit(i + 9, j + 10 + 64 - y3, x2, y1, 8, y3);
    GlStateManager.color3f(0,1,0);
    this.blit(i + 160, j + 10, x1, y1, 8, 64);

    //font.drawString(container.te.getRequiredPower() +" FE/t",guiLeft + 72,guiTop +25,0x404040);
  }

  @Override
  public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
    this.renderBackground();
    super.render(p_render_1_, p_render_2_, p_render_3_);
    this.renderHoveredToolTip(p_render_1_, p_render_2_);
  }

  @Override
  protected void renderHoveredToolTip(int mouseX, int mouseY) {
    super.renderHoveredToolTip(mouseX, mouseY);
    int i = (this.width - this.xSize) / 2;
    int j = (this.height - this.ySize) / 2;

    if (isPointInRegion(9,6,8,70,mouseX,mouseY)){
      List<String> tooltip = new ArrayList<>();
      tooltip.add(this.container.te.energyStorage.getEnergyStored()+" FE");
      tooltip.add(container.te.getRequiredPower() +" FE/t");
      GuiUtils.drawHoveringText(tooltip,mouseX,mouseY,this.width,this.height,100,this.font);
    }

    if (isPointInRegion(160,6,8,70,mouseX,mouseY)){
      List<String> tooltip = new ArrayList<>();
      tooltip.add(this.container.te.fluidStorage.getFluidAmount()+" xp");
      GuiUtils.drawHoveringText(tooltip,mouseX,mouseY,this.width,this.height,100,this.font);
    }
  }

  @Override
  protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
    this.font.drawString(this.title.getFormattedText(), 40, 6, 0x404040);
  }
}
