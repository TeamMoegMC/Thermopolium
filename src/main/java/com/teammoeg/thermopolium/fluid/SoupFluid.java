package com.teammoeg.thermopolium.fluid;

import com.teammoeg.thermopolium.util.SoupInfo;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.ForgeFlowingFluid;

public class SoupFluid extends ForgeFlowingFluid {

	@Override
	public Fluid getStillFluid() {
		return this;
	}

	@Override
	public Fluid getFlowingFluid() {
		return this;
	}

	@Override
	public Item getFilledBucket() {
		return Items.AIR;
	}

	@Override
	protected BlockState getBlockState(FluidState state) {
		return Blocks.AIR.getDefaultState();
	}
    @Override
    public boolean isEquivalentTo(Fluid fluidIn) {
        return fluidIn == this;
    }
	@Override
	public boolean isSource(FluidState p_207193_1_) {
		return true;
	}
	public static SoupInfo getInfo(FluidStack stack){
		if(stack.hasTag())
			return new SoupInfo(stack.getOrCreateChildTag("soup"));
		return new SoupInfo(stack.getFluid().getRegistryName());
	}
	public static void setInfo(FluidStack stack,SoupInfo si) {
		stack.getOrCreateTag().put("soup",si.save());
	}
	@Override
	public int getLevel(FluidState p_207192_1_) {
		return 0;
	}
	public SoupFluid(Properties properties) {
		super(properties);
	}


	public static class SoupAttributes extends FluidAttributes {
		//private static final String DefName="fluid."+Main.MODID+".soup";
		public SoupAttributes(Builder builder, Fluid fluid) {
			super(builder, fluid);
		}

		@Override
		public int getColor(FluidStack stack) {
			return super.getColor();
		}
		

		@Override
		public String getTranslationKey(FluidStack stack) {
			ResourceLocation f=stack.getFluid().getRegistryName();
			return "item."+f.getNamespace()+"."+f.getPath();
		}
	    /**
	     * Returns the localized name of this fluid.
	     */
	    public ITextComponent getDisplayName(FluidStack stack)
	    {
	        return new TranslationTextComponent(getTranslationKey(stack));
	    }

		private static class SoupAttributesBuilder extends Builder{

			protected SoupAttributesBuilder(ResourceLocation stillTexture, ResourceLocation flowingTexture) {
				super(stillTexture, flowingTexture,SoupAttributes::new);
			}
			
		}
		public static Builder builder(ResourceLocation stillTexture, ResourceLocation flowingTexture) {
			return new SoupAttributesBuilder(stillTexture,flowingTexture);
		}
	}



}