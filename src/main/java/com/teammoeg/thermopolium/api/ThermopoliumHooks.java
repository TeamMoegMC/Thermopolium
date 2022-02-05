package com.teammoeg.thermopolium.api;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.teammoeg.thermopolium.fluid.SoupFluid;
import com.teammoeg.thermopolium.items.StewItem;
import com.teammoeg.thermopolium.util.FloatemStack;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;

public class ThermopoliumHooks {

	private ThermopoliumHooks() {
	}

	public static List<FloatemStack> getItems(ItemStack stack) {
		if (stack.getItem() instanceof StewItem) {
			return StewItem.getItems(stack);
		}
		LazyOptional<IFluidHandlerItem> cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
		if (cap.isPresent()) {
			IFluidHandlerItem data = cap.resolve().get();
			return SoupFluid.getItems(data.getFluidInTank(0));
		}
		return Lists.newArrayList();
	}
}
