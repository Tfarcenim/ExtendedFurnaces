package com.tfar.extendedfurnace.jei;

import com.tfar.extendedfurnace.ExtendedFurnaces;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.constants.VanillaRecipeCategoryUid;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

@mezz.jei.api.JeiPlugin
public class JeiPlugin implements IModPlugin {

  @Override
  public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
    registration.addRecipeCatalyst(new ItemStack(ExtendedFurnaces.RegistryObjects.extended_furnace), VanillaRecipeCategoryUid.SMOKING,VanillaRecipeCategoryUid.FURNACE,VanillaRecipeCategoryUid.BLASTING);
  }

  @Override
  public ResourceLocation getPluginUid() {
    return new ResourceLocation(ExtendedFurnaces.MODID,ExtendedFurnaces.MODID);
  }
}
