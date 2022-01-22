import java.io.IOException;

import com.teammoeg.thermopolium.Main;

import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class GenSoupItems extends ItemModelProvider {

	public GenSoupItems(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
		super(generator, modid, existingFileHelper);
	}

	@Override
	protected void registerModels() {
		ResourceLocation par=new ResourceLocation("minecraft","item/generated");
		try {
			
			super.existingFileHelper.getResource(new ResourceLocation(Main.MODID),ResourcePackType.CLIENT_RESOURCES,".png","");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
