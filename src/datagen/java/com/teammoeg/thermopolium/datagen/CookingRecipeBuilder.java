package com.teammoeg.thermopolium.datagen;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import com.teammoeg.thermopolium.Main;
import com.teammoeg.thermopolium.data.recipes.CookingRecipe;
import com.teammoeg.thermopolium.data.recipes.StewBaseCondition;
import com.teammoeg.thermopolium.data.recipes.StewCondition;
import com.teammoeg.thermopolium.data.recipes.StewNumber;
import com.teammoeg.thermopolium.data.recipes.baseconditions.FluidTag;
import com.teammoeg.thermopolium.data.recipes.baseconditions.FluidType;
import com.teammoeg.thermopolium.data.recipes.baseconditions.FluidTypeType;
import com.teammoeg.thermopolium.data.recipes.conditions.Halfs;
import com.teammoeg.thermopolium.data.recipes.conditions.Mainly;
import com.teammoeg.thermopolium.data.recipes.conditions.MainlyOfType;
import com.teammoeg.thermopolium.data.recipes.conditions.Must;
import com.teammoeg.thermopolium.data.recipes.numbers.Add;
import com.teammoeg.thermopolium.data.recipes.numbers.ConstNumber;
import com.teammoeg.thermopolium.data.recipes.numbers.ItemIngredient;
import com.teammoeg.thermopolium.data.recipes.numbers.ItemTag;
import com.teammoeg.thermopolium.data.recipes.numbers.ItemType;
import com.teammoeg.thermopolium.data.recipes.numbers.NopNumber;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.ResourceLocation;

public class CookingRecipeBuilder {
	public static class StewNumberBuilder{
		private StewConditionsBuilder parent;
		private List<StewNumber> types=new ArrayList<>();
		private Consumer<StewNumber> fin;

		public StewNumberBuilder(StewConditionsBuilder parent, Consumer<StewNumber> fin) {
			super();
			this.parent = parent;
			this.fin = fin;
		}
		public StewNumberBuilder of(float n) {
			types.add(new ConstNumber(n));
			return this;
		}
		public StewNumberBuilder of(Ingredient i) {
			types.add(new ItemIngredient(i));
			return this;
		}
		public StewNumberBuilder of(ResourceLocation i) {
			types.add(new ItemTag(i));
			return this;
		}
		public StewNumberBuilder of(Item i) {
			types.add(new ItemType(i));
			return this;
		}
		public StewNumberBuilder plus(float n) {
			types.add(new ConstNumber(n));
			return this;
		}
		public StewNumberBuilder plus(Ingredient i) {
			types.add(new ItemIngredient(i));
			return this;
		}
		public StewNumberBuilder plus(ResourceLocation i) {
			types.add(new ItemTag(i));
			return this;
		}
		public StewNumberBuilder plus(Item i) {
			types.add(new ItemType(i));
			return this;
		}
		public StewNumberBuilder nop() {
			types.add(NopNumber.INSTANCE);
			return this;
		}
		public StewConditionsBuilder and() {
			if(!types.isEmpty()) {
				if(types.size()==1) 
					fin.accept(types.get(0));
				else
					fin.accept(new Add(types));
			}
			return parent;
		}
	}
	public static class StewConditionsBuilder{
		private CookingRecipeBuilder parent;
		private List<StewCondition> li,al,dy;
		public StewConditionsBuilder(CookingRecipeBuilder parent,List<StewCondition> cr,List<StewCondition> al,List<StewCondition> dy) {
			super();
			this.parent = parent;
			this.li=cr;
			this.al=al;
			this.dy=dy;
		}
		public StewNumberBuilder half() {
			return new StewNumberBuilder(this,this::makeHalf);
		}
		public StewNumberBuilder halft() {
			return new StewNumberBuilder(this,this::makeHalft);
		}
		private void makeHalf(StewNumber sn) {
			li.add(new Halfs(sn));
		}
		private void makeHalft(StewNumber sn) {
			li.add(new Halfs(sn,false));
		}
		public StewNumberBuilder typeMainly(ResourceLocation rs) {
			return new StewNumberBuilder(this,sn->li.add(new MainlyOfType(sn,rs)));
		}
		public StewNumberBuilder mainly() {
			return new StewNumberBuilder(this,this::makeMainly);
		}
		private void makeMainly(StewNumber sn) {
			li.add(new Mainly(sn));
		}
		public StewNumberBuilder mainlyt() {
			return new StewNumberBuilder(this,this::makeMainlyt);
		}
		private void makeMainlyt(StewNumber sn) {
			li.add(new Mainly(sn,false));
		}
		public StewNumberBuilder any() {
			return new StewNumberBuilder(this,this::makeMust);
		}
		public StewConditionsBuilder require() {
			return new StewConditionsBuilder(parent,al,al,dy);
		}
		public StewConditionsBuilder not() {
			return new StewConditionsBuilder(parent,dy,al,dy);
		}
		private void makeMust(StewNumber sn) {
			li.add(new Must(sn));
		}
		public CookingRecipeBuilder then() {
			return parent;
		}
	}
	public static class StewBaseBuilder{
		private CookingRecipeBuilder parent;

		public StewBaseBuilder(CookingRecipeBuilder parent) {
			super();
			this.parent = parent;
		}
		public StewBaseBuilder tag(ResourceLocation rl) {
			parent.base.add(new FluidTag(rl));
			return this;
		}
		public StewBaseBuilder type(Fluid f) {
			parent.base.add(new FluidType(f));
			return this;
		}
		public StewBaseBuilder only(Fluid f) {
			parent.base.add(new FluidTypeType(f));
			return this;
		}
		public CookingRecipeBuilder and() {
			return parent;
		}
	}
	private List<StewCondition> allow=new ArrayList<>();
	private List<StewCondition> deny=new ArrayList<>();
	private int priority=0;
	private int time=200;
	private float density=0.75f;
	private List<StewBaseCondition> base=new ArrayList<>();
	private Fluid output;
	private ResourceLocation id;
	public CookingRecipeBuilder(ResourceLocation id,Fluid out) {
		output=out;
		this.id=id;
	}
	public static CookingRecipeBuilder start(Fluid out) {
		return new CookingRecipeBuilder(new ResourceLocation(Main.MODID,"cooking/"+out.getRegistryName().getPath()),out);
	}
	public StewConditionsBuilder require() {
		return new StewConditionsBuilder(this,allow,allow,deny);
	}
	public StewConditionsBuilder not() {
		return new StewConditionsBuilder(this,deny,allow,deny);
	}
	public StewBaseBuilder base() {
		return new StewBaseBuilder(this);
	}
	public CookingRecipeBuilder prio(int p) {
		priority=p;
		return this;
	}
	public CookingRecipeBuilder time(int t) {
		time=t;
		return this;
	}
	public CookingRecipeBuilder dense(double d) {
		density=(float) d;
		return this;
	}
	public CookingRecipe end() {
		return new CookingRecipe(id,allow,deny,priority,time,density,base,output);
	}
	public CookingRecipe finish(Consumer<? super CookingRecipe> csr) {
		CookingRecipe r=end();
		csr.accept(r);
		return r;
	}
}
