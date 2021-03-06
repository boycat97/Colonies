package colonies.src;

import colonies.pmardle.src.EntityBarbarian;
import colonies.pmardle.src.EntityBarbarianChief;
import colonies.pmardle.src.RenderBarbarian;
import colonies.pmardle.src.RenderBarbarianChief;
import colonies.src.buildings.ColoniesChestRenderHelper;
import colonies.src.buildings.TileEntityColoniesChest;
import colonies.src.buildings.TileEntityColoniesChestRenderer;
import net.minecraft.block.Block;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.client.renderer.ChestItemRenderHelper;
import net.minecraft.world.World;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;

//
public class ClientProxy extends ServerProxy
{
	@Override
	public void registerRenderInformation() {
		
		// renderers
		ChestItemRenderHelper.instance = new ColoniesChestRenderHelper();
		RenderingRegistry.registerEntityRenderingHandler(EntityBarbarian.class, new RenderBarbarian(new ModelBiped(), 1.0f));
		RenderingRegistry.registerEntityRenderingHandler(EntityBarbarianChief.class, new RenderBarbarianChief(new ModelBiped(), 1.0f));
	
		// old code for custom female model
		//RenderingRegistry.instance().registerEntityRenderingHandler(
		//		EntityWife.class, new RenderLiving(new ModelFemale(), 0.5F));
		
		// Grave Stone (start)
		//RenderingRegistry.instance().registerEntityRenderingHandler(
		//		EntityGraveStone.class, new RenderLiving(new ModelGraveStone(), 0.5F));
		
		// preloaded textures
        MinecraftForgeClient.preloadTexture(ITEMS_PNG);
		MinecraftForgeClient.preloadTexture(BLOCK_PNG);
		
        MinecraftForgeClient.preloadTexture(BLACKSMITHCHEST_PNG);
        MinecraftForgeClient.preloadTexture(BUILDERCHEST_PNG); 
        MinecraftForgeClient.preloadTexture(FARMERCHEST_PNG);
        MinecraftForgeClient.preloadTexture(HOUSECHEST_PNG);
        MinecraftForgeClient.preloadTexture(LOGGINGCAMP_PNG);
        MinecraftForgeClient.preloadTexture(MINERCHEST_PNG);
        MinecraftForgeClient.preloadTexture(TOWNHALLCHEST_PNG);
        MinecraftForgeClient.preloadTexture(HUNTERBLIND_PNG);
        MinecraftForgeClient.preloadTexture(CHESTCONTAINER_PNG);
        MinecraftForgeClient.preloadTexture(FISHERMANHUT_PNG);
        MinecraftForgeClient.preloadTexture(GUARDHOUSE_PNG);
        MinecraftForgeClient.preloadTexture(ALCHEMISTCHEST_PNG);
        MinecraftForgeClient.preloadTexture(ENCHANTERCHEST_PNG);
        
	}


	public void registerTileEntitySpecialRenderer(Class<TileEntityColoniesChest> colonieschesttileentity) {
		ClientRegistry.bindTileEntitySpecialRenderer(colonieschesttileentity, new TileEntityColoniesChestRenderer());
		ClientRegistry.bindTileEntitySpecialRenderer(TileEntityResearchBlock.class, new RenderResearchBlock());
		}

  	
	@Override
    public World getClientWorld(){
        return FMLClientHandler.instance().getClient().theWorld;
    }
}
