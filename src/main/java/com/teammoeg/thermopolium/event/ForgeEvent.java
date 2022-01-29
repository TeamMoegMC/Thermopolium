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

package com.teammoeg.thermopolium.event;

import com.teammoeg.thermopolium.data.RecipeReloadListener;
import com.teammoeg.thermopolium.data.recipes.BowlContainingRecipe;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ForgeEvent {
	@SubscribeEvent
	public static void addReloadListeners(AddReloadListenerEvent event) {
		event.addListener(new RecipeReloadListener(event.getDataPackRegistries()));
	}

	@SubscribeEvent
	public static void onBlockClick(PlayerInteractEvent.RightClickBlock event) {
		ItemStack is = event.getItemStack();
		if (is.getItem() == Items.BOWL) {
			PlayerEntity playerIn = event.getPlayer();
			World worldIn = event.getWorld();
			BlockPos blockpos = event.getPos();
			TileEntity te = worldIn.getTileEntity(blockpos);
			if (te != null) {
				te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, event.getFace())
						.ifPresent(handler -> {
							FluidStack stack = handler.drain(250, FluidAction.SIMULATE);
							BowlContainingRecipe recipe = BowlContainingRecipe.recipes.get(stack.getFluid());
							if (recipe != null && stack.getAmount() == 250) {
								stack = handler.drain(stack, FluidAction.EXECUTE);
								if (stack.getAmount() == 250) {

									ItemStack ret = recipe.handle(stack);
									event.setCanceled(true);
									event.setCancellationResult(ActionResultType.func_233537_a_(worldIn.isRemote));
									if (is.getCount() > 1) {
										is.shrink(1);
										if (!playerIn.addItemStackToInventory(ret)) {
											playerIn.dropItem(ret, false);
										}
									} else
										playerIn.setHeldItem(event.getHand(), ret);
								}
							}
						});
			}

		}
	}

	@SubscribeEvent
	public static void onItemUse(PlayerInteractEvent.RightClickItem event) {
		ItemStack is = event.getItemStack();
		if (is.getItem() == Items.BOWL) {
			World worldIn = event.getWorld();
			PlayerEntity playerIn = event.getPlayer();
			BlockRayTraceResult ray = Item.rayTrace(worldIn, playerIn, FluidMode.SOURCE_ONLY);
			if (ray.getType() == Type.BLOCK) {
				BlockPos blockpos = ray.getPos();
				BlockState blockstate1 = worldIn.getBlockState(blockpos);
				Fluid f = blockstate1.getFluidState().getFluid();
				if (f != Fluids.EMPTY) {
					BowlContainingRecipe recipe = BowlContainingRecipe.recipes.get(f);

					ItemStack ret = recipe.handle(f);
					event.setCanceled(true);
					event.setCancellationResult(ActionResultType.func_233537_a_(worldIn.isRemote));
					if (is.getCount() > 1) {
						is.shrink(1);
						if (!playerIn.addItemStackToInventory(ret)) {
							playerIn.dropItem(ret, false);
						}
					} else
						playerIn.setHeldItem(event.getHand(), ret);
				}
			}
		}
	}
	@SubscribeEvent
	public static void onItemUseFinish(PlayerInteractEvent.RightClickItem event) {
		
	}
}
