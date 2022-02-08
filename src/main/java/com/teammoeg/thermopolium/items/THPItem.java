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

package com.teammoeg.thermopolium.items;

import com.teammoeg.thermopolium.Contents;
import com.teammoeg.thermopolium.Main;
import net.minecraft.item.Item;

public class THPItem extends Item {

	public THPItem(String name, Properties properties) {
		super(properties);
		setRegistryName(Main.MODID, name);
		Contents.registeredItems.add(this);
	}

}
