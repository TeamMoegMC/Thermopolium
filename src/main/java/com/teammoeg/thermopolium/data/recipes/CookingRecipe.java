package com.teammoeg.thermopolium.data.recipes;

import java.util.List;
import com.google.gson.JsonObject;
import com.teammoeg.thermopolium.data.IDataRecipe;

import net.minecraft.fluid.Fluid;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.ForgeRegistries;

public class CookingRecipe extends IDataRecipe {

	public static List<CookingRecipe> recipes;
	public static IRecipeType<?> TYPE;
	public static RegistryObject<IRecipeSerializer<?>> SERIALIZER;
	@Override
	public IRecipeSerializer<?> getSerializer() {
		return SERIALIZER.get();
	}
	@Override
	public IRecipeType<?> getType() {
		return TYPE;
	}
	List<StewCondition> allow;
	List<StewCondition> deny;
	int priority;
	int time;
	float density;
	List<StewBaseCondition> base;
	public Fluid output;
	public CookingRecipe(ResourceLocation id) {
		super(id);
	}
	public CookingRecipe(ResourceLocation id,JsonObject data) {
		super(id);
		if(data.has("allow"))
			allow=StewSerializer.parseJsonList(data.get("allow"),StewSerializer::ofCondition);
		if(data.has("deny"))
			deny=StewSerializer.parseJsonList(data.get("deny"),StewSerializer::ofCondition);
		if(data.has("priority"))
			priority=data.get("priority").getAsInt();
		if(data.has("density"))
			density=data.get("density").getAsFloat();
		time=data.get("time").getAsInt();
		if(data.has("base"))
			base=StewSerializer.parseJsonList(data.get("base"),StewSerializer::ofBase);
		output=ForgeRegistries.FLUIDS.getValue(new ResourceLocation(data.get("output").getAsString()));
	}
	public CookingRecipe(ResourceLocation id,PacketBuffer data) {
		super(id);
		if(data.readBoolean())
			allow=StewSerializer.readList(data,StewSerializer::ofCondition);
		if(data.readBoolean())
			deny=StewSerializer.readList(data,StewSerializer::ofCondition);
		priority=data.readVarInt();
		density=data.readFloat();
		time=data.readVarInt();
		if(data.readBoolean())
			base=StewSerializer.readList(data,StewSerializer::ofBase);
		output=ForgeRegistries.FLUIDS.getValue(data.readResourceLocation());
	}
	

	public CookingRecipe(ResourceLocation id, List<StewCondition> allow, List<StewCondition> deny, int priority,
			int time, float density, List<StewBaseCondition> base, Fluid output, ResourceLocation result) {
		super(id);
		this.allow = allow;
		this.deny = deny;
		this.priority = priority;
		this.time = time;
		this.density = density;
		this.base = base;
		this.output = output;
		this.result = result;
	}
	public void write(PacketBuffer data) {
		if(allow!=null) {
			data.writeBoolean(true);
			StewSerializer.writeList(data,allow,StewSerializer::write);
		}else data.writeBoolean(false);
		if(deny!=null) {
			data.writeBoolean(true);
			StewSerializer.writeList(data,deny,StewSerializer::write);
		}else data.writeBoolean(false);
		data.writeVarInt(priority);
		data.writeFloat(density);
		data.writeVarInt(time);
		if(base!=null) {
			data.writeBoolean(true);
			StewSerializer.writeList(data,base,StewSerializer::write);
		}else data.writeBoolean(false);
		data.writeResourceLocation(output.getRegistryName());
	}

	ResourceLocation result;
	public boolean matches(StewPendingContext ctx) {
		if(ctx.getTotalItems()<density)
			return false;
		if(base!=null)
			if(base.stream().noneMatch(e->e.test(ctx.getInfo().base)||e.test(ctx.getCur())))
				return false;
		if(allow!=null)
			if(!allow.stream().allMatch(e->e.test(ctx)))
				return false;
		if(deny!=null)
			if(deny.stream().anyMatch(e->e.test(ctx)))
				return false;
		return true;
	}
	public ResourceLocation getResult() {
		return result;
	}
	@Override
	public void serialize(JsonObject json) {
		if(allow!=null) {
			json.add("allow",StewSerializer.toJsonList(allow,StewCondition::serialize));
		}
		if(deny!=null) {
			json.add("deny",StewSerializer.toJsonList(deny,StewCondition::serialize));
		}
		json.addProperty("priority",priority);
		json.addProperty("density",density);
		json.addProperty("time",time);
		if(base!=null) {
			json.add("base",StewSerializer.toJsonList(base,StewBaseCondition::serialize));
		}
		json.addProperty("output",output.getRegistryName().toString());
	}




}
