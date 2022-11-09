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

package com.teammoeg.thermopolium.blocks;

import java.util.Random;
import java.util.function.BiFunction;

import com.teammoeg.thermopolium.Contents;
import com.teammoeg.thermopolium.Main;
import com.teammoeg.thermopolium.client.Particles;
import com.teammoeg.thermopolium.data.recipes.BowlContainingRecipe;
import com.teammoeg.thermopolium.items.StewItem;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidActionResult;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fml.network.NetworkHooks;

public class StewPot extends Block {
	public final String name;
	protected int lightOpacity;
	public static final EnumProperty<Axis> FACING = BlockStateProperties.HORIZONTAL_AXIS;

	public StewPot(String name, Properties blockProps, BiFunction<Block, Item.Properties, Item> createItemBlock) {
		super(blockProps.variableOpacity());
		this.name = name;
		lightOpacity = 15;

		ResourceLocation registryName = createRegistryName();
		setRegistryName(registryName);

		Contents.registeredBlocks.add(this);
		Item item = createItemBlock.apply(this, new Item.Properties().group(Main.itemGroup));
		if (item != null) {
			item.setRegistryName(registryName);
			Contents.registeredItems.add(item);
		}
	}

	static final VoxelShape shape = Block.makeCuboidShape(1, 0, 1, 15, 14, 15);

	@Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos,
			ISelectionContext context) {
		return shape;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return shape;
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		ActionResultType p = super.onBlockActivated(state, worldIn, pos, player, handIn, hit);
		if (p.isSuccessOrConsume())
			return p;
		StewPotTileEntity tileEntity = (StewPotTileEntity) worldIn.getTileEntity(pos);
		if (tileEntity.canAddFluid()) {
			ItemStack held = player.getHeldItem(handIn);
			if (held.isEmpty() && player.isSneaking()) {
				tileEntity.getTank().setFluid(FluidStack.EMPTY);
				tileEntity.current = null;
				return ActionResultType.SUCCESS;
			}
			if (held.getItem() instanceof StewItem) {
				if (tileEntity.tryAddFluid(BowlContainingRecipe.extractFluid(held))) {
					ItemStack ret = held.getContainerItem();
					held.shrink(1);
					if (!player.addItemStackToInventory(ret))
						player.dropItem(ret, false);
				}

				return ActionResultType.func_233537_a_(worldIn.isRemote);
			}
			if (FluidUtil.interactWithFluidHandler(player, handIn, tileEntity.getTank()))
				return ActionResultType.SUCCESS;

		}
		if (handIn == Hand.MAIN_HAND) {
			if (tileEntity != null && !worldIn.isRemote)
				NetworkHooks.openGui((ServerPlayerEntity) player, tileEntity, tileEntity.getPos());
			return ActionResultType.SUCCESS;
		}
		return p;
	}

	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		TileEntity te = worldIn.getTileEntity(pos);
		if (te instanceof StewPotTileEntity) {
			StewPotTileEntity pot = (StewPotTileEntity) te;
			if (pot.proctype == 2 && pot.working) {
				double d0 = pos.getX();
				double d1 = pos.getY() + 1;
				double d2 = pos.getZ();
				int count = 2;
				while (--count != 0)
					worldIn.addParticle(Particles.STEAM.get(), d0 + rand.nextFloat(), d1, d2 + rand.nextFloat(), 0.0D,
							0.0D, 0.0D);
			}
		}
	}

	public ResourceLocation createRegistryName() {
		return new ResourceLocation(Main.MODID, name);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return new StewPotTileEntity();
	}

	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		TileEntity tileEntity = worldIn.getTileEntity(pos);
		if (tileEntity instanceof StewPotTileEntity && state.getBlock() != newState.getBlock()) {
			StewPotTileEntity te = (StewPotTileEntity) tileEntity;
			if (te.proctype != 2)
				for (int i = 0; i < 11; i++) {
					ItemStack is = te.getInv().getStackInSlot(i);
					if (!is.isEmpty())
						super.spawnAsEntity(worldIn, pos, is);
				}
		}
		super.onReplaced(state, worldIn, pos, newState, isMoving);
	}

	@Override
	protected void fillStateContainer(net.minecraft.state.StateContainer.Builder<Block, BlockState> builder) {
		super.fillStateContainer(builder);
		builder.add(FACING);
	}

	@Override
	public void fillWithRain(World worldIn, BlockPos pos) {
		if (worldIn.rand.nextInt(25) == 1) {
			float f = worldIn.getBiome(pos).getTemperature(pos);
			if (!(f < 0.15F)) {
				TileEntity tileEntity = worldIn.getTileEntity(pos);
				if (tileEntity instanceof StewPotTileEntity) {
					StewPotTileEntity te = (StewPotTileEntity) tileEntity;
					if (te.canAddFluid())
						te.tryAddFluid(new FluidStack(Fluids.WATER, 250));
				}

			}
		}

	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getAxis());

	}

}
