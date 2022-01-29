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

import com.teammoeg.thermopolium.Main;
import com.teammoeg.thermopolium.api.ThermopoliumHooks;
import com.teammoeg.thermopolium.data.recipes.FoodValueRecipe;
import com.teammoeg.thermopolium.items.StewItem;
import com.teammoeg.thermopolium.util.FloatemStack;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import top.theillusivec4.diet.api.DietApi;
import top.theillusivec4.diet.api.IDietGroup;
import top.theillusivec4.diet.api.IDietResult;
import top.theillusivec4.diet.common.impl.DietApiImpl;
import top.theillusivec4.diet.common.util.DietResult;

//As Diet's author didn't add such a more flexible api, I have to resort to mixin.
@Mixin(DietApiImpl.class)
public class DietApiImplMixin extends DietApi {
	private static ResourceLocation stew=new ResourceLocation(Main.MODID,"stews");
	@Inject(at = @At("HEAD"), require = 1, method = "get(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;)Ltop/theillusivec4/diet/api/IDietResult;", cancellable = true, remap = false)
	public void get(PlayerEntity player, ItemStack input, CallbackInfoReturnable<IDietResult> result) {
		if (input.getItem().getTags().contains(stew)) {
			List<FloatemStack> is = ThermopoliumHooks.getItems(input);
			Map<IDietGroup, Float> groups = new HashMap<>();
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
						groups.merge(me.getKey(), me.getValue() * sx.getCount(), Float::sum);
			}
			result.setReturnValue(new DietResult(groups));
		}
	}

	/**
	 * @param heal
	 * @param sat
	 */
	@Inject(at = @At("HEAD"), require = 1, method = "get(Lnet/minecraft/entity/player/PlayerEntity;Lnet/minecraft/item/ItemStack;IF)Ltop/theillusivec4/diet/api/IDietResult;", cancellable = true, remap = false)
	public void get(PlayerEntity player, ItemStack input, int heal, float sat,
			CallbackInfoReturnable<IDietResult> result) {
		if (input.getItem().getTags().contains(stew)) {
			List<FloatemStack> is = ThermopoliumHooks.getItems(input);
			Map<IDietGroup, Float> groups = new HashMap<>();
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
						groups.merge(me.getKey(), me.getValue() * sx.getCount(), Float::sum);
			}
			result.setReturnValue(new DietResult(groups));
		}
	}

}
