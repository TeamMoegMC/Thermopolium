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

import java.util.Optional;
import java.util.Random;

import com.mojang.datafixers.util.Pair;
import com.teammoeg.thermopolium.data.recipes.BowlContainingRecipe;
import com.teammoeg.thermopolium.fluid.SoupFluid;
import com.teammoeg.thermopolium.items.StewItem;
import com.teammoeg.thermopolium.util.SoupInfo;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ActionResultType;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;

public class ThermopoliumApi {

	private ThermopoliumApi() {
	}
	public static SoupInfo getInfo(ItemStack item) {
		return StewItem.getInfo(item);
	}
	public static SoupInfo getInfo(FluidStack item) {
		return SoupFluid.getInfo(item);
	}
	public static SoupInfo getInfo(CompoundNBT nbt) {
		return new SoupInfo(nbt);
	}
	public static void setInfo(ItemStack item,SoupInfo info) {
		StewItem.setInfo(item,info);
	}
	public static void setInfo(FluidStack item,SoupInfo info) {
		SoupFluid.setInfo(item,info);
	}
	public static void setInfo(CompoundNBT nbt,SoupInfo info) {
		info.write(nbt);
	}
	public static void applyStew(World worldIn, LivingEntity entityLiving,SoupInfo info) {
		if (!worldIn.isRemote) {
			for (EffectInstance eff :info.effects) {
				if (eff != null) {
					entityLiving.addPotionEffect(eff);
				}
			}
			Random r=entityLiving.getRNG();
			for(Pair<EffectInstance, Float> ef:info.foodeffect) {
				if(r.nextFloat()<ef.getSecond())
					entityLiving.addPotionEffect(ef.getFirst());
			}
			if (entityLiving instanceof PlayerEntity) {
				PlayerEntity player = (PlayerEntity) entityLiving;
				player.getFoodStats().addStats(info.healing,info.saturation);
			}
		}
	}
	public static Optional<ItemStack> fillBowl(IFluidHandler handler) {
		FluidStack stack = handler.drain(250, FluidAction.SIMULATE);
		if(stack.getAmount()==250)
			return fillBowl(handler.drain(stack, FluidAction.EXECUTE));
		return Optional.empty();
	}
	public static Optional<ItemStack> fillBowl(FluidStack stack) {
		if(stack.getAmount() != 250)return Optional.empty();
		BowlContainingRecipe recipe = BowlContainingRecipe.recipes.get(stack.getFluid());
		if (recipe != null) {
			ItemStack ret = recipe.handle(stack);
			return Optional.of(ret);
		}
		return Optional.empty();
	}
}
