package com.teammoeg.thermopolium.tiles;

import com.teammoeg.thermopolium.Contents.SCTileTypes;

import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;

public class StewPotTileEntity extends TileEntity implements ITickableTileEntity {

	public StewPotTileEntity() {
		super(SCTileTypes.STEW_POT.get());
	}

	@Override
	public void tick() {
	}

}
