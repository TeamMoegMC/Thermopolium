package com.teammoeg.thermopolium.blocks;

import java.util.function.BiFunction;

import com.teammoeg.thermopolium.Contents;
import com.teammoeg.thermopolium.Main;
import com.teammoeg.thermopolium.tiles.StewPotTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockReader;

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
