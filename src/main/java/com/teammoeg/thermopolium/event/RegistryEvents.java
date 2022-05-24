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

import static com.teammoeg.thermopolium.Contents.*;

import com.teammoeg.thermopolium.Main;
import com.teammoeg.thermopolium.api.ThermopoliumApi;
import com.teammoeg.thermopolium.blocks.StewPotTileEntity;
import com.teammoeg.thermopolium.data.recipes.BowlContainingRecipe;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.dispenser.OptionalDispenseBehavior;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.Effect;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import net.minecraftforge.fml.common.Mod;

import com.teammoeg.thermopolium.Contents.THPItems;

@Mod.EventBusSubscriber(modid = Main.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEvents {
	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {
		for (Block block : registeredBlocks) {
			try {
				event.getRegistry().register(block);
			} catch (Throwable e) {
				Main.logger.error("Failed to register a block. ({})", block);
				throw e;
			}
		}
	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		for (Item item : registeredItems) {
			try {
				event.getRegistry().register(item);
			} catch (Throwable e) {
				Main.logger.error("Failed to register an item. ({}, {})", item, item.getRegistryName());
				throw e;
			}
		}
		DispenserBlock.registerBehavior(Items.BOWL, new DefaultDispenseItemBehavior() {
			private final DefaultDispenseItemBehavior defaultBehaviour = new DefaultDispenseItemBehavior();

			@Override
			protected ItemStack execute(IBlockSource bp, ItemStack is) {

				Direction d = bp.getBlockState().getValue(DispenserBlock.FACING);
				BlockPos front = bp.getPos().relative(d);
				FluidState fs = bp.getLevel().getBlockState(front).getFluidState();
				TileEntity te = bp.getLevel().getBlockEntity(front);
				if (te != null) {
					LazyOptional<IFluidHandler> ip = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,
							d.getOpposite());
					if (ip.isPresent()) {
						ItemStack ret = ThermopoliumApi.fillBowl(ip.resolve().get()).orElse(null);
						if (ret != null) {
							if (is.getCount() == 1)
								return ret;
							is.shrink(1);
							if (bp.<DispenserTileEntity>getEntity().addItem(ret) == -1)
								this.defaultBehaviour.dispense(bp, ret);
						}
					}
					;
					return is;
				} else if (!fs.isEmpty()) {
					ItemStack ret = ThermopoliumApi.fillBowl(new FluidStack(fs.getType(), 250)).orElse(null);
					if (ret != null) {
						if (is.getCount() == 1)
							return ret;
						is.shrink(1);
						if (bp.<DispenserTileEntity>getEntity().addItem(ret) == -1)
							this.defaultBehaviour.dispense(bp, ret);
					}
					return is;
				}
				return this.defaultBehaviour.dispense(bp, is);
			}

		});
		IDispenseItemBehavior idispenseitembehavior1 = new DefaultDispenseItemBehavior() {
			private final DefaultDispenseItemBehavior defaultBehaviour = new DefaultDispenseItemBehavior();

			/**
			 * Dispense the specified stack, play the dispense sound and spawn particles.
			 */
			public ItemStack execute(IBlockSource source, ItemStack stack) {
				
				BlockPos blockpos = source.getPos().relative(source.getBlockState().getValue(DispenserBlock.FACING));
				World world = source.getLevel();
				Direction d = source.getBlockState().getValue(DispenserBlock.FACING);
				BlockPos front = source.getPos().relative(d);
				FluidState fs = source.getLevel().getBlockState(front).getFluidState();
				TileEntity te = source.getLevel().getBlockEntity(front);
				if (te != null) {
					LazyOptional<IFluidHandler> ip = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY,
							d.getOpposite());
					if (ip.isPresent()) {
						FluidActionResult fa = FluidUtil.tryEmptyContainerAndStow(stack,ip.resolve().get(), null, 1250,null,
								true);
						if (fa.isSuccess()) {
							if (fa.getResult() != null)
								return fa.getResult();
							stack.shrink(1);
							
						}
					}
					return stack;
				}
				return this.defaultBehaviour.dispense(source, stack);
			}
		};
		DispenserBlock.registerBehavior(Items.MILK_BUCKET, idispenseitembehavior1);
		DefaultDispenseItemBehavior ddib = new DefaultDispenseItemBehavior() {
			private final DefaultDispenseItemBehavior defaultBehaviour = new DefaultDispenseItemBehavior();

			@Override
			protected ItemStack execute(IBlockSource source, ItemStack stack) {
				FluidStack fs = BowlContainingRecipe.extractFluid(stack);
				Direction d = source.getBlockState().getValue(DispenserBlock.FACING);
				BlockPos front = source.getPos().relative(d);
				TileEntity te = source.getLevel().getBlockEntity(front);

				if (!fs.isEmpty()) {
					if (te instanceof StewPotTileEntity) {
						if (((StewPotTileEntity) te).tryAddFluid(fs)) {
							ItemStack ret = stack.getContainerItem();
							if (stack.getCount() == 1)
								return ret;
							stack.shrink(1);
							if (source.<DispenserTileEntity>getEntity().addItem(ret) == -1)
								this.defaultBehaviour.dispense(source, ret);
						}
					} else if (te != null) {
						LazyOptional<IFluidHandler> ip = te
								.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, d.getOpposite());
						if (ip.isPresent()) {
							IFluidHandler handler = ip.resolve().get();
							if (handler.fill(fs, FluidAction.SIMULATE) == fs.getAmount()) {
								handler.fill(fs, FluidAction.EXECUTE);
								ItemStack ret = stack.getContainerItem();
								if (stack.getCount() == 1)
									return ret;
								stack.shrink(1);
								if (source.<DispenserTileEntity>getEntity().addItem(ret) == -1)
									this.defaultBehaviour.dispense(source, ret);
							}
						}
					}
					return stack;
				}
				return this.defaultBehaviour.dispense(source, stack);
			}

		};
		for (Item i : THPItems.stews) {
			DispenserBlock.registerBehavior(i, ddib);
		}
	}

	@SubscribeEvent
	public static void registerFluids(RegistryEvent.Register<Fluid> event) {
		for (Fluid fluid : registeredFluids) {
			try {
				event.getRegistry().register(fluid);
			} catch (Throwable e) {
				Main.logger.error("Failed to register a fluid. ({}, {})", fluid, fluid.getRegistryName());
				throw e;
			}
		}
	}

	@SuppressWarnings("unused")
	@SubscribeEvent
	public static void registerEffects(final RegistryEvent.Register<Effect> event) {

	}

}
