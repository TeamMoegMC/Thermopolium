/*
 * Copyright (c) 2022 TeamMoeg
 *
 * This file is part of Thermopolium.
 *
 * Thermopolium is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Thermopolium is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Thermopolium. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.thermopolium.api;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.teammoeg.thermopolium.fluid.SoupFluid;
import com.teammoeg.thermopolium.items.StewItem;
import com.teammoeg.thermopolium.util.FloatemStack;

import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
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
	public static ResourceLocation getBase(ItemStack stack) {
		if (stack.getItem() instanceof StewItem) {
			return StewItem.getBase(stack);
		}
		LazyOptional<IFluidHandlerItem> cap = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
		if (cap.isPresent()) {
			IFluidHandlerItem data = cap.resolve().get();
			return SoupFluid.getBase(data.getFluidInTank(0));
		}
		return new ResourceLocation("water");
	}
}
