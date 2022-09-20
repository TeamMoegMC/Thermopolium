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

package com.teammoeg.thermopolium.mixin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.teammoeg.thermopolium.Config;
import com.teammoeg.thermopolium.api.ThermopoliumHooks;
import com.teammoeg.thermopolium.data.recipes.FluidFoodValueRecipe;
import com.teammoeg.thermopolium.data.recipes.FoodValueRecipe;
import com.teammoeg.thermopolium.util.FloatemStack;
import com.teammoeg.thermopolium.util.SoupInfo;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import top.theillusivec4.diet.api.DietApi;
import top.theillusivec4.diet.api.IDietGroup;
import top.theillusivec4.diet.api.IDietResult;
import top.theillusivec4.diet.common.impl.DietApiImpl;
import top.theillusivec4.diet.common.util.DietResult;

//As Diet's author didn't add such a more flexible api, I have to resort to mixin.
@Mixin(DietApiImpl.class)
public class DietApiImplMixin extends DietApi {
	
	private static void THP$getResult(PlayerEntity player,ItemStack input, CallbackInfoReturnable<IDietResult> result) {
		SoupInfo ois = ThermopoliumHooks.getInfo(input);
		if(ois==null)return;
		List<FloatemStack> is=ois.stacks;
		Map<IDietGroup, Float> groups = new HashMap<>();
		float b=Config.COMMON.benefitModifier.get();
		float h=Config.COMMON.harmfulModifier.get();
		for (FloatemStack sx : is) {
			FoodValueRecipe fvr = FoodValueRecipe.recipes.get(sx.getItem());
			ItemStack stack;
			if (fvr == null || fvr.getRepersent() == null)
				stack = sx.getStack();
			else
				stack = fvr.getRepersent();
			IDietResult dr = DietApiImpl.getInstance().get(player, stack);
			if (dr != DietResult.EMPTY)
				for (Entry<IDietGroup, Float> me : dr.get().entrySet())
					if(me.getKey().isBeneficial()) {
						groups.merge(me.getKey(), me.getValue()*sx.getCount()*b, Float::sum);
					}else
						groups.merge(me.getKey(), me.getValue()*sx.getCount()*h, Float::sum);
		}
		FluidFoodValueRecipe ffvr=FluidFoodValueRecipe.recipes.get(ois.base);
		if(ffvr!=null&&ffvr.getRepersent()!=null) {
			IDietResult dr = DietApiImpl.getInstance().get(player,ffvr.getRepersent());
			if (dr != DietResult.EMPTY)
				for (Entry<IDietGroup, Float> me : dr.get().entrySet())
					if(me.getKey().isBeneficial()) {
						groups.merge(me.getKey(), me.getValue()*(ois.shrinkedFluid+1)/ffvr.parts*b, Float::sum);
					}else
						groups.merge(me.getKey(), me.getValue()*(ois.shrinkedFluid+1)/ffvr.parts*h, Float::sum);
		}
		result.setReturnValue(new DietResult(groups));
	}
	@Inject(at = @At("HEAD"), require = 1, method = "get(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)Ltop/theillusivec4/diet/api/IDietResult;", cancellable = true, remap = false)
	public void get(PlayerEntity player, ItemStack input, CallbackInfoReturnable<IDietResult> result) {
		THP$getResult(player,input,result);
	}

	/**
	 * @param heal
	 * @param sat
	 */
	@Inject(at = @At("HEAD"), require = 1, method = "get(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;IF)Ltop/theillusivec4/diet/api/IDietResult;", cancellable = true, remap = false)
	public void get(PlayerEntity player, ItemStack input, int heal, float sat,
			CallbackInfoReturnable<IDietResult> result) {
		THP$getResult(player,input,result);
	}

}
