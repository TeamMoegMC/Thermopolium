package com.teammoeg.stewcraft.tiles;

import com.teammoeg.stewcraft.Contents.SCTileTypes;

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
