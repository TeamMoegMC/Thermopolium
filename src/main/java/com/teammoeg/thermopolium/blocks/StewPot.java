package com.teammoeg.thermopolium.blocks;

import java.util.function.BiFunction;

import com.teammoeg.thermopolium.Contents;
import com.teammoeg.thermopolium.Main;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

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

}
