package com.teammoeg.thermopolium.network;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;

public abstract class INetworkTile extends TileEntity{
	public INetworkTile(TileEntityType<?> tileEntityTypeIn) {
		super(tileEntityTypeIn);
	}
	public abstract void handleMessage(short type,int data);
	public void sendMessage(short type,int data) {
		PacketHandler.sendToServer(new ClientDataMessage(this.pos,type,data));
	}
}
