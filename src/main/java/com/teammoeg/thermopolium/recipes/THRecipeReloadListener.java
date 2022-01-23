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

package com.teammoeg.thermopolium.recipes;

import java.util.Collection;
import java.util.List;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.resources.IResourceManager;
import net.minecraft.resources.IResourceManagerReloadListener;
import net.minecraftforge.client.event.RecipesUpdatedEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class THRecipeReloadListener implements IResourceManagerReloadListener {
    public THRecipeReloadListener() {
    }

    @Override
    public void onResourceManagerReload(@Nonnull IResourceManager resourceManager) {
    }

    RecipeManager clientRecipeManager;

    @SubscribeEvent
    public void onTagsUpdated(TagsUpdatedEvent event) {
    }

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
        CookingRecipe.recipes = filterRecipes(recipes, CookingRecipe.class, CookingRecipe.TYPE).collect(Collectors.toList());
        BoilingRecipe.recipes = filterRecipes(recipes, BoilingRecipe.class, BoilingRecipe.TYPE).collect(Collectors.toMap(e->e.before,UnaryOperator.identity()));
        DissolveRecipe.recipes = filterRecipes(recipes, DissolveRecipe.class, DissolveRecipe.TYPE).collect(Collectors.toMap(e->e.item,UnaryOperator.identity()));
    }

    static <R extends IRecipe<?>> Stream<R> filterRecipes(Collection<IRecipe<?>> recipes, Class<R> recipeClass, IRecipeType<?> recipeType) {
        return recipes.stream()
                .filter(iRecipe -> iRecipe.getType() == recipeType)
                .map(recipeClass::cast)
                ;
    }
}
