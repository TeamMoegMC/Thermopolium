package com.teammoeg.thermopolium.network;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
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
	public void syncData() {
		this.world.notifyBlockUpdate(this.pos, this.getBlockState(), this.getBlockState(), 3);
        this.markDirty();
	}
    public abstract void readCustomNBT(CompoundNBT nbt,boolean isClient);
	public abstract void writeCustomNBT(CompoundNBT nbt,boolean isClient);
	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		this.readCustomNBT(pkt.getNbtCompound(),true);
	}

	@Override
	public void read(BlockState state, CompoundNBT nbt) {
		super.read(state, nbt);
		this.readCustomNBT(nbt,false);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		this.writeCustomNBT(compound,false);
		return super.write(compound);
	}
    @Override
	public void handleUpdateTag(BlockState state, CompoundNBT tag) {
		super.read(state, tag);
		this.readCustomNBT(tag,true);
	}
}
