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

package com.teammoeg.thermopolium.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import com.teammoeg.thermopolium.data.recipes.BoilingRecipe;
import com.teammoeg.thermopolium.data.recipes.BowlContainingRecipe;
import com.teammoeg.thermopolium.data.recipes.CookingRecipe;
import com.teammoeg.thermopolium.data.recipes.CountingTags;
import com.teammoeg.thermopolium.data.recipes.DissolveRecipe;

import net.minecraft.client.Minecraft;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.resources.DataPackRegistries;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@SuppressWarnings("deprecation")
public class RecipeReloadListener implements IResourceManagerReloadListener {
	DataPackRegistries data;
    public RecipeReloadListener(DataPackRegistries dpr) {
    	data=dpr;
    }

    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
    	buildRecipeLists(data.getRecipeManager());
    }

    RecipeManager clientRecipeManager;


    @SubscribeEvent(priority = EventPriority.HIGH)
    public void onRecipesUpdated(RecipesUpdatedEvent event) {
        clientRecipeManager = event.getRecipeManager();
        if (!Minecraft.getInstance().isSingleplayer())
            buildRecipeLists(clientRecipeManager);
    }

    public static void buildRecipeLists(RecipeManager recipeManager) {
        Collection<IRecipe<?>> recipes = recipeManager.getRecipes();
        if (recipes.size() == 0)
            return;
        BowlContainingRecipe.recipes = filterRecipes(recipes,BowlContainingRecipe.class,BowlContainingRecipe.TYPE).collect(Collectors.toMap(e->e.fluid,UnaryOperator.identity()));
        CookingRecipe.recipes = filterRecipes(recipes, CookingRecipe.class, CookingRecipe.TYPE).collect(Collectors.toMap(e->e.output,UnaryOperator.identity()));
        CookingRecipe.sorted=new ArrayList<>(CookingRecipe.recipes.values());
        CookingRecipe.sorted.sort((t2,t1)->t1.getPriority()-t2.getPriority());
        BoilingRecipe.recipes = filterRecipes(recipes, BoilingRecipe.class, BoilingRecipe.TYPE).collect(Collectors.toMap(e->e.before,UnaryOperator.identity()));
        DissolveRecipe.recipes = filterRecipes(recipes, DissolveRecipe.class, DissolveRecipe.TYPE).collect(Collectors.toList());
        CookingRecipe.cookables=CookingRecipe.recipes.values().stream().flatMap(CookingRecipe::getAllNumbers).collect(Collectors.toSet());
        CountingTags.tags=Stream.concat(filterRecipes(recipes,CountingTags.class,CountingTags.TYPE).flatMap(r->r.tag.stream()),CookingRecipe.recipes.values().stream().flatMap(CookingRecipe::getTags)).collect(Collectors.toSet());
        System.out.println(CountingTags.tags);
    }

    static <R extends IRecipe<?>> Stream<R> filterRecipes(Collection<IRecipe<?>> recipes, Class<R> recipeClass, IRecipeType<?> recipeType) {
        return recipes.stream()
                .filter(iRecipe -> iRecipe.getType() == recipeType)
                .map(recipeClass::cast);
    }
}
