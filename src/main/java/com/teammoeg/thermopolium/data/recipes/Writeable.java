package com.teammoeg.thermopolium.data.recipes;

import com.google.gson.JsonElement;
import net.minecraft.network.PacketBuffer;

public interface Writeable {
	public JsonElement serialize();

	public void write(PacketBuffer buffer);
}
