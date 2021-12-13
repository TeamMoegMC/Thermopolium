/*
 * Copyright (c) 2021 TeamMoeg
 *
 * This file is part of Frosted Heart.
 *
 * Frosted Heart is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * Frosted Heart is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Frosted Heart. If not, see <https://www.gnu.org/licenses/>.
 */

package com.teammoeg.stewcraft.items;

import com.teammoeg.stewcraft.Contents;
import com.teammoeg.stewcraft.Main;

import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

public class SCBlockItem extends BlockItem {
    public SCBlockItem(Block block, Item.Properties props) {
        super(block, props);
    }
    public SCBlockItem(Block block, Item.Properties props, String name) {
        this(block,props.group(Main.itemGroup));
        this.setRegistryName(Main.MODID, name);
        Contents.registeredItems.add(this);
    }
}
