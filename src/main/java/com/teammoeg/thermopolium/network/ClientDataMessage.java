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

package com.teammoeg.thermopolium.network;

import java.util.Objects;
import java.util.function.Supplier;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent;

public class ClientDataMessage {
	private final short type;
	private final int message;
	private final BlockPos pos;

	public ClientDataMessage(BlockPos pos, short type, int message) {
		this.pos = pos;
		this.type = type;
		this.message = message;
	}

	ClientDataMessage(PacketBuffer buffer) {
		pos = buffer.readBlockPos();
		type = buffer.readShort();
		message = buffer.readInt();
	}

	void encode(PacketBuffer buffer) {
		buffer.writeBlockPos(pos);
		buffer.writeShort(type);
		buffer.writeInt(message);
	}

	void handle(Supplier<NetworkEvent.Context> context) {
		context.get().enqueueWork(() -> {
			ServerWorld world = Objects.requireNonNull(context.get().getSender()).getLevel();
			if (world.isAreaLoaded(pos, 1)) {
				TileEntity tile = world.getBlockEntity(pos);
				if (tile instanceof INetworkTile)
					((INetworkTile) tile).handleMessage(type, message);
			}
		});
		context.get().setPacketHandled(true);
	}
}
