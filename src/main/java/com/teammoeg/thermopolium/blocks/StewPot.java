package com.teammoeg.thermopolium.blocks;

import java.util.Random;
import java.util.function.BiFunction;

import com.teammoeg.thermopolium.Contents;
import com.teammoeg.thermopolium.Main;
import com.teammoeg.thermopolium.client.Particles;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class StewPot extends Block {
	public final String name;
	protected int lightOpacity;
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
    static final VoxelShape shape=Block.makeCuboidShape(1, 0, 1, 15, 14, 15);
    @Override
	public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos,
			ISelectionContext context) {
		return shape;
	}


	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
        if (!worldIn.isRemote && handIn == Hand.MAIN_HAND) {
            StewPotTileEntity tileEntity = (StewPotTileEntity) worldIn.getTileEntity(pos);
            if(tileEntity!=null)
            NetworkHooks.openGui((ServerPlayerEntity) player, tileEntity,tileEntity.getPos());
        }
        return ActionResultType.func_233537_a_(worldIn.isRemote);
	}


	@Override
	public void animateTick(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
		TileEntity te=worldIn.getTileEntity(pos);
		if (te instanceof StewPotTileEntity) {
			StewPotTileEntity pot = (StewPotTileEntity) te;
			if (pot.proctype==2) {
				double d0 = pos.getX();
				double d1 = pos.getY() + 1;
				double d2 = pos.getZ();
				if(rand.nextDouble()<0.8D) {
					int count=4;
					while(--count!=0)
						worldIn.addParticle(Particles.STEAM.get(), d0+rand.nextFloat(), d1, d2+rand.nextFloat(), 0.0D, 0.0D, 0.0D);
				}
			}
		}
	}


	@Override
	public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
		return shape;
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

}
