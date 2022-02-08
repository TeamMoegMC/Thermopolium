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

package com.teammoeg.thermopolium.datagen;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.teammoeg.thermopolium.Contents.THPItems;
import com.teammoeg.thermopolium.Main;
import com.teammoeg.thermopolium.data.TranslationProvider;
import com.teammoeg.thermopolium.data.recipes.CookingRecipe;
import com.teammoeg.thermopolium.data.recipes.SerializeUtil;
import com.teammoeg.thermopolium.data.recipes.StewBaseCondition;
import com.teammoeg.thermopolium.data.recipes.baseconditions.FluidTag;
import com.teammoeg.thermopolium.data.recipes.baseconditions.FluidType;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.item.Item;
import net.minecraft.resources.IResource;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.data.ExistingFileHelper;

public class THPBookGenerator implements IDataProvider {
	private static final Logger LOGGER = LogManager.getLogger();
	private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
	private JsonParser jp=new JsonParser();
	protected final DataGenerator generator;
	private Path bookmain;
	private ExistingFileHelper helper;
	private Map<String,JsonObject> langs=new HashMap<>();
	private Map<String,CookingRecipe> recipes;
	class DatagenTranslationProvider implements TranslationProvider{
		String lang;
		public DatagenTranslationProvider(String lang) {
			super();
			this.lang = lang;
		}
		@Override
		public String getTranslation(String key, Object... objects) {
			if(langs.get(lang).has(key))
				return String.format(langs.get(lang).get(key).getAsString(),objects);
			return new TranslationTextComponent(key,objects).getString();
		}
		
	}

	public THPBookGenerator(DataGenerator generatorIn,ExistingFileHelper efh) {
		this.generator = generatorIn;
		this.helper=efh;
	}

	@Override
	public void act(DirectoryCache cache) throws IOException {
		bookmain=this.generator.getOutputFolder().resolve("data/"+Main.MODID+"/patchouli_books/book/");
		recipes=THPRecipeProvider.recipes.stream().map(e->((CookingRecipe)e)).collect(Collectors.toMap(e->e.output.getRegistryName().getPath(),e->e));
		loadLang("zh_cn");
		loadLang("en_us");
		for(String s:THPItems.items)
			if(helper.exists(new ResourceLocation(Main.MODID,"textures/gui/recipes/"+s+".png"),ResourcePackType.CLIENT_RESOURCES))
			defaultPage(cache,s);
	}
	private void loadLang(String locale) {
		try {
			IResource rc=helper.getResource(new ResourceLocation(Main.MODID,"lang/"+locale+".json"),ResourcePackType.CLIENT_RESOURCES);
			JsonObject jo=jp.parse(new InputStreamReader(rc.getInputStream(),"UTF-8")).getAsJsonObject();
			langs.put(locale,jo);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@Override
	public String getName() {
		return Main.MODID+" recipe patchouli generator";
	}
	private void defaultPage(DirectoryCache cache,String name) {
		saveEntry(name,"en_us",cache,createRecipe(name,"en_us"));
		saveEntry(name,"zh_cn",cache,createRecipe(name,"zh_cn"));
	}
	StewBaseCondition anyW=new FluidTag(THPRecipeProvider.anyWater);
	StewBaseCondition stock=new FluidType(THPRecipeProvider.stock);
	StewBaseCondition milk=new FluidType(THPRecipeProvider.milk);
	private JsonObject createRecipe(String name,String locale) {
		JsonObject page=new JsonObject();
		page.add("name",langs.get(locale).get("item.thermopolium."+name));
		page.addProperty("icon",new ResourceLocation(Main.MODID,name).toString());
		page.addProperty("category","recipes");
		CookingRecipe r=recipes.get(name);
		Item baseType=THPItems.any;
		if(r.getBase()!=null&&!r.getBase().isEmpty()) {
			StewBaseCondition sbc=r.getBase().get(0);
			if(sbc.equals(anyW))
				baseType=THPItems.anyWater;
			else if(sbc.equals(stock))
				baseType=THPItems.stock;
			else if(sbc.equals(milk))
				baseType=THPItems.milk;
		}
		JsonArray pages=new JsonArray();
		JsonObject imgpage=new JsonObject();
		imgpage.addProperty("type","thermopolium:thprecipe");
		imgpage.addProperty("img",new ResourceLocation(Main.MODID,"textures/gui/recipes/"+name+".png").toString());
		imgpage.addProperty("result",new ResourceLocation(Main.MODID,name).toString());
		imgpage.addProperty("base",baseType.getRegistryName().toString());
		pages.add(imgpage);
		page.add("pages", pages);
		return page;
	}
	private void saveEntry(String name,String locale,DirectoryCache cache,JsonObject entry) {
		saveJson(cache,entry,bookmain.resolve(locale+"/entries/recipes/"+name+".json"));
	}
	private static void saveJson(DirectoryCache cache, JsonObject recipeJson, Path path) {
		try {
			String s = GSON.toJson(recipeJson);
			String s1 = HASH_FUNCTION.hashUnencodedChars(s).toString();
			if (!Objects.equals(cache.getPreviousHash(path), s1) || !Files.exists(path)) {
				Files.createDirectories(path.getParent());

				try (BufferedWriter bufferedwriter = Files.newBufferedWriter(path)) {
					bufferedwriter.write(s);
				}
			}

			cache.recordHash(path, s1);
		} catch (IOException ioexception) {
			LOGGER.error("Couldn't save data json {}", path, ioexception);
		}

	}
}
