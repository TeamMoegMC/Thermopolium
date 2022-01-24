/*
 * Copyright (c) 2021 TeamMoeg
 *
 * This file is part of Frosted Heart.
 *
 * Frosted Heart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Frosted Heart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Frosted Heart. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.thermopolium.event;

import com.teammoeg.thermopolium.data.recipes.BowlContainingRecipe;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.BlockState;
import net.minecraft.block.IBucketPickupHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class ForgeEvent {

	/**
	 * Perform temperature tick logic
	 *
	 * @param event fired every tick on player
	 */
	@SubscribeEvent
	public static void onItemUse(PlayerInteractEvent.RightClickItem event) {
		ItemStack is=event.getItemStack();
		if(is.getItem()==Items.BOWL) {
			PlayerEntity playerIn=event.getPlayer();
			Hand handIn=event.getHand();
			World worldIn=event.getWorld();
			ItemStack itemstack = playerIn.getHeldItem(handIn);
	        RayTraceResult raytraceresult = Item.rayTrace(worldIn, playerIn,RayTraceContext.FluidMode.SOURCE_ONLY);
	        if (raytraceresult.getType() != RayTraceResult.Type.BLOCK)
	            return;
			BlockRayTraceResult blockraytraceresult = (BlockRayTraceResult) raytraceresult;
			BlockPos blockpos = blockraytraceresult.getPos();
			BlockState blockstate1 = worldIn.getBlockState(blockpos);
			Fluid f=blockstate1.getFluidState().getFluid();
			if(f!=Fluids.EMPTY) {
				BowlContainingRecipe recipe=BowlContainingRecipe.recipes.get(f);
				is.shrink(1);
				ItemStack ret=recipe.handle(f);
				event.setCanceled(true);
				event.setCancellationResult(ActionResultType.func_233537_a_(worldIn.isRemote));
				if(!playerIn.addItemStackToInventory(ret)) {
					playerIn.dropItem(ret,false);
				}
			}else {
				TileEntity te=worldIn.getTileEntity(blockpos);
				if(te!=null) {
					te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,blockraytraceresult.getFace()).ifPresent(fh->{
						FluidStack fs=fh.drain(250,FluidAction.SIMULATE);
						BowlContainingRecipe recipe=BowlContainingRecipe.recipes.get(fs.getFluid());
						if(recipe!=null&&fs.getAmount()==250) {
							fs=fh.drain(fs,FluidAction.EXECUTE);
							if(fs.getAmount()==250) {
								ItemStack ret=recipe.handle(fs);
								event.setCanceled(true);
								event.setCancellationResult(ActionResultType.func_233537_a_(worldIn.isRemote));
								if(!playerIn.addItemStackToInventory(ret)) {
									playerIn.dropItem(ret,false);
								}
							}
						}
					});
				}
			}
		}
	}
}
