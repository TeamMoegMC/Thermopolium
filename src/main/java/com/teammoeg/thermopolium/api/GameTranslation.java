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

package com.teammoeg.thermopolium.api;

import com.teammoeg.thermopolium.data.TranslationProvider;

import net.minecraft.util.text.TranslationTextComponent;

public class GameTranslation implements TranslationProvider {
	private static GameTranslation INSTANCE;
	private GameTranslation() {
	}
	public static TranslationProvider get(){
		if(INSTANCE==null)INSTANCE=new GameTranslation();
		return INSTANCE;
	}

	@Override
	public String getTranslation(String key, Object... objects) {
		return new TranslationTextComponent(key,objects).getString();
	}

}
