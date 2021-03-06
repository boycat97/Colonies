package colonies.src;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.item.Item;
import net.minecraft.src.ModLoader;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.AchievementPage;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.MinecraftForge;
import colonies.anglewyrm.src.GuiHandlerColoniesChest;
import colonies.anglewyrm.src.PacketHandler;
import colonies.boycat97.src.BlockGuardHouse;
import colonies.boycat97.src.EntityGuard;
import colonies.boycat97.src.TileEntityGuardHouse;
import colonies.kzolp67.src.ColoniesTab;
import colonies.pmardle.src.BiomeGenVillage;
import colonies.pmardle.src.EntityBarbarian;
import colonies.pmardle.src.EntityBarbarianChief;
import colonies.src.buildings.BlockAlchemistShop;
import colonies.src.buildings.BlockColoniesChest;
import colonies.src.buildings.BlockEnchanter;
import colonies.src.buildings.BlockFishermanHut;
import colonies.src.buildings.BlockHouse;
import colonies.src.buildings.BlockHunterBlind;
import colonies.src.buildings.BlockLoggingCamp;
import colonies.src.buildings.BlockMine;
import colonies.src.buildings.BlockTownHall;
import colonies.src.buildings.TileEntityAlchemistShop;
import colonies.src.buildings.TileEntityColoniesChest;
import colonies.src.buildings.TileEntityEnchanterChest;
import colonies.src.buildings.TileEntityFishermanHut;
import colonies.src.buildings.TileEntityHouse;
import colonies.src.buildings.TileEntityHunterBlind;
import colonies.src.buildings.TileEntityLoggingCamp;
import colonies.src.buildings.TileEntityMine;
import colonies.src.buildings.TileEntityTownHall;
import colonies.src.citizens.EntityAlchemist;
import colonies.src.citizens.EntityCitizen;
import colonies.src.citizens.EntityEnchanter;
import colonies.src.citizens.EntityFisherman;
import colonies.src.citizens.EntityHunter;
import colonies.src.citizens.EntityLumberjack;
import colonies.src.citizens.EntityMiner;
import colonies.src.citizens.EntityPriestess;
import colonies.src.citizens.EntityWife;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.Init;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.Mod.PostInit;
import cpw.mods.fml.common.Mod.PreInit;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;

@Mod(modid = "Colonies", name = "Colonies", version = "8 Mar 2013")
@NetworkMod(
		channels = { "Colonies" },
		clientSideRequired = true,
		serverSideRequired = false,
		packetHandler = PacketHandler.class )

public class ColoniesMain 
{
	public static Block test; 	
	public static Item MeasuringTape;
	public static Block chestBlock;
	public static Block townHall;
	public static Block minerChest;
	public static Block enchanterChest;
	public static Block loggingCamp;
	public static Block house;
	public static Block hunterBlind;
	public static Block fishermanHut;
	public static Block alchemistShop;
	public static Block guardHouse;
	public static Item ancientTome;
	public static Item researchedTome;
	public static Block researchBlock; //For inclusion in enchanter's house for player to research Ancient Tomes
	public static BiomeGenBase VillageBiome; //for Village Biome
	/**Names of the male citizens*/
	public static String[] maleNames;
	/**Names of the female citizens*/
	public static String[] femaleNames;
	/**Last names of the citizens*/
	public static String[] lastNames;


	
	//public static GuiHandler guiHandlerChest;
	public static GuiHandlerColoniesChest guiHandlerChest;
	//public static List<TileEntityTownHall> townsList;
	
	public static CreativeTabs coloniesTab = new ColoniesTab("coloniesTab");


	@Instance
	public static ColoniesMain instance;

	@SidedProxy(clientSide = "colonies.src.ClientProxy", serverSide = "colonies.src.ServerProxy")
	public static ServerProxy proxy;

	@PreInit
	public void preInit(FMLPreInitializationEvent event)
	{
		System.out.println("Initializing Colonies " + Version()); 
		Configuration config = new Configuration(event.getSuggestedConfigurationFile());
		config.load();
		setConfig(config);
		config.save();
		MinecraftForge.EVENT_BUS.register(new ColoniesSoundManager());
		MinecraftForge.EVENT_BUS.register(new AncientTomeDropsEvent());
	}

	@Init
	public void init(FMLInitializationEvent evt)
	{	
		registerColoniesStuff(); // at bottom of this file for legibility
		 guiHandlerChest = new GuiHandlerColoniesChest();
		//guiHandlerChest = new GuiHandler();
		NetworkRegistry.instance().registerGuiHandler(this, guiHandlerChest);

		 //guiHandlerResearchBlock = new GuiHandlerResearchBlock();
		 //NetworkRegistry.instance().registerGuiHandler(this, guiHandlerResearchBlock);
		 
		proxy.registerRenderInformation(); 
		
		//Biome
		VillageBiome = new BiomeGenVillage(53).setColor(2900485).setBiomeName("Village Biome").setTemperatureRainfall(1F, 0.5F).setMinMaxHeight(0.1F, 0.1F);
        GameRegistry.addBiome(VillageBiome);
 	
		Recipes.registerRecipes();
		
		ColoniesAchievements.addAchievementLocalizations();
		AchievementPage.registerAchievementPage(ColoniesAchievements.page1);
		GameRegistry.registerCraftingHandler(new ColoniesAchievements());
		GameRegistry.registerTileEntity(TileEntityResearchBlock.class, "ResearchBlock");
	

		//Barbarian
		EntityRegistry.registerGlobalEntityID(EntityBarbarian.class, "Barbarian", EntityRegistry.findGlobalUniqueEntityId(), 32324, 2243);
		EntityRegistry.registerModEntity(EntityBarbarian.class, "Barbarian", EntityRegistry.findGlobalUniqueEntityId(), this, 80, 3, true);
		EntityRegistry.addSpawn(EntityBarbarian.class, 3, 1, 3, EnumCreatureType.creature, ColoniesMain.VillageBiome, BiomeGenBase.taigaHills, BiomeGenBase.jungle, BiomeGenBase.jungleHills, BiomeGenBase.plains, BiomeGenBase.taiga, BiomeGenBase.forest, BiomeGenBase.forestHills, BiomeGenBase.swampland, BiomeGenBase.river, BiomeGenBase.beach, BiomeGenBase.desert, BiomeGenBase.extremeHills, BiomeGenBase.extremeHillsEdge); // low
	    LanguageRegistry.instance().addStringLocalization("entity.Barbarian.name", "Barbarian");
	    //Note: EntityRegistry.addspawn(<Entity.Class>, <Chance of Spawning (10=high, 1=low)>, Min Number of mobs per spawn, Max No. of mobs per spawn, <Creature Type> (Monster = Night only, Creature = day) <Biomes to spawn in>
	
	    //Barbarian Chief
		EntityRegistry.registerGlobalEntityID(EntityBarbarianChief.class, "Barbarian Chief", EntityRegistry.findGlobalUniqueEntityId(), 7587, 8323);
		EntityRegistry.registerModEntity(EntityBarbarianChief.class, "Barbarian Chief", EntityRegistry.findGlobalUniqueEntityId(), this, 80, 3, true);		
		LanguageRegistry.instance().addStringLocalization("entity.Barbarian Chief.name", "Barbarian Chief");
		EntityRegistry.addSpawn(EntityBarbarianChief.class, 2, 1, 1, EnumCreatureType.creature, ColoniesMain.VillageBiome, BiomeGenBase.taigaHills, BiomeGenBase.jungle, BiomeGenBase.jungleHills, BiomeGenBase.plains, BiomeGenBase.taiga, BiomeGenBase.forest, BiomeGenBase.forestHills, BiomeGenBase.swampland, BiomeGenBase.river, BiomeGenBase.beach, BiomeGenBase.desert, BiomeGenBase.extremeHills, BiomeGenBase.extremeHillsEdge);
		
	}

	@PostInit
	public void postInit(FMLPostInitializationEvent evt)
	{
		// TODO: Add Post-Initialization code such as mod hooks
		//NetworkRegistry.instance().registerGuiHandler(this, guihBusiness);
	}

	public String Version(){
		return "PreAlpha, Revision 14";
	}

	// Configuration Settings
	// Appear here as public statics, and are set below in setConfig()
	public static int testBlockID;
	public static int measuringTapeID;
	public static int defaultChestID;
	public static int townHallID;
	public static int minerChestID;
	public static int enchanterChestID;
	public static int loggingCampID;
	public static int blockHouseID;
	public static int hunterBlindID;
	public static int fishermanHutID;
	public static int alchemistShopID;
	public static int guardHouseID;
	public static int ancientTomeID;
	public static int researchedTomeID;
	public static int researchBlockID;
	public static int BarbarianID;
	public static int BarbarianChiefID;
	
	public static boolean offensiveLanguageFilter;
	public static boolean citizenGreetings;
	public static float   citizenMoveSpeed;
	
	public static String skinDefault;
	public static String skinMaleSwimming;
	public static String skinMiner;
	public static String skinMinerSwimming;
	public static String skinWife;
	public static String skinFemaleSwimming;
	public static String skinPriestess;
	public static String skinPriestessSwimming;
	public static String skinLumberjack;
	public static String skinHunter;
	public static String skinFisherman;
	public static String skinAlchemist;
	public static String skinGuard;
	public static String skinArcher;
	public static String skinSergeant;
	public static String skinEnchanter;
	
	public static String guiChestBackground;
	public static String guiResearchBlockBackground;
	
	private void setConfig(Configuration config)
	{
		testBlockID     = config.getBlock("testBlockID",     1100).getInt();
		measuringTapeID = config.getBlock("measuringTapeID", 1101).getInt();
		defaultChestID  = config.getBlock("defaultChestID",  1102).getInt();
		townHallID      = config.getBlock("townHallID",      1103).getInt();
		minerChestID    = config.getBlock("minerChestID",    1104).getInt();
		loggingCampID   = config.getBlock("loggingCampID",   1105).getInt();
		blockHouseID    = config.getBlock("blockHouseID",    1106).getInt();
		hunterBlindID   = config.getBlock("hunterBlindID",   1107).getInt();
		fishermanHutID  = config.getBlock("fishermanHutID",  1108).getInt();
		alchemistShopID = config.getBlock("alchemistShopID", 1109).getInt();
		guardHouseID	= config.getBlock("guardHouseID", 	 1110).getInt();
		ancientTomeID	= config.getBlock("ancientTomeID", 	 1111).getInt();
		researchedTomeID	= config.getBlock("researchedTomeID", 	 1112).getInt();
		researchBlockID	= config.getBlock("researchBlockID", 	 1113).getInt();
		enchanterChestID    = config.getBlock("enchanterChestID",    1114).getInt();

		
		offensiveLanguageFilter = config.get(Configuration.CATEGORY_GENERAL, "offensiveLanguageFilter", false).getBoolean(false);
		citizenGreetings = config.get(Configuration.CATEGORY_GENERAL, "citizenGreetings", true).getBoolean(true);
		citizenMoveSpeed = Float.parseFloat(config.get(Configuration.CATEGORY_GENERAL, "citizenMoveSpeed", "0.25f").value);
		
		skinDefault           = config.get("Skins", "skinDefault",           "/colonies/grahammarcellus/gfx/unemployedskin1.png").value;
		skinMaleSwimming      = config.get("Skins", "skinMaleSwimming",      "/colonies/anglewyrm/gfx/m-swimskin.png").value;
		skinMiner             = config.get("Skins", "skinMiner",             "/colonies/irontaxi/gfx/minerSkin1.png").value;
		skinMinerSwimming     = config.get("Skins", "skinMinerSwimming",     "/colonies/anglewyrm/gfx/miner_swim.png").value;
		skinWife              = config.get("Skins", "skinWife",              "/colonies/austensible/gfx/wife2.png").value;
		skinFemaleSwimming    = config.get("Skins", "skinFemaleSwimming",    "/colonies/anglewyrm/gfx/white_bikini.png").value;
		skinPriestess         = config.get("Skins", "skinPriestess",         "/colonies/anglewyrm/gfx/priestess.png").value;
		skinPriestessSwimming = config.get("Skins", "skinPriestessSwimming", "/colonies/anglewyrm/gfx/priestess_swimsuit.png").value;
		skinLumberjack        = config.get("Skins", "skinLumberjack",        "/colonies/anglewyrm/gfx/lumberjack.png").value;
		skinHunter            = config.get("Skins", "skinHunter",            "/colonies/kzolp67/gfx/Hunter.png").value;
		skinFisherman         = config.get("Skins", "skinFisherman",         "/colonies/irontaxi/gfx/fisherman2.png").value;
		skinAlchemist         = config.get("Skins", "skinAlchemist",         "/colonies/irontaxi/gfx/alchemist.png").value;
		skinArcher			  = config.get("Skins", "skinArcher", 			 "/colonies/boycat97/gfx/skin_archer.png").value;
		skinGuard			  = config.get("Skins", "skinGuard", 			 "/colonies/boycat97/gfx/skin_footsoldier.png").value;
		skinSergeant		  = config.get("Skins", "skinSergeant", 		 "/colonies/boycat97/gfx/skin_sergeant.png").value;
		skinEnchanter		  = config.get("Skins", "skinEnchanter", 		 "/colonies/pmardle/gfx/skin_enchanter.png").value;
		
		guiChestBackground = config.get("GUI", "guiChestBackground2", "/colonies/boycat97/gfx/windowBackground.png").value;
		guiResearchBlockBackground = config.get("GUI", "guiResearchBlockBackground", "/colonies/gfx/researchBlockBackground.png").value;
		
		maleNames   = config.get("Names","maleNames","Michle,Antalas,Olli,Yehoyakim,Bob,Hugo").value.split(",");
		femaleNames = config.get("Names","femaleNames","Anna,Mirian,Kiki,Carina,Marijana,Jessica").value.split(",");
		lastNames   = config.get("Names","lastnames","Huberman,Hironaka,Newton,Rosenfield,Dixon,Dell'antonio").value.split(",");
	}
	
	// Register Colonies stuff with Minecraft Forge
	private void registerColoniesStuff()
	{		
		// Chest block
//		chestBlock = new BlockColoniesChest(defaultChestID);
//		LanguageRegistry.addName(chestBlock, "Colonies Chest");
//		GameRegistry.registerBlock(chestBlock);
			

		GameRegistry.registerTileEntity(TileEntityColoniesChest.class, "container.colonieschest");
		LanguageRegistry.instance().addStringLocalization("container.colonieschest", "en_US", "Colonies Chest");
		proxy.registerTileEntitySpecialRenderer(TileEntityColoniesChest.class);

		minerChest = new BlockMine(minerChestID).setBlockName("Mine");
		LanguageRegistry.addName(minerChest, "Miner Chest");
		GameRegistry.registerBlock(minerChest);
		GameRegistry.registerTileEntity(TileEntityMine.class, "container.mine");
		LanguageRegistry.instance().addStringLocalization("container.mine", "en_US", "Mine");
		
		enchanterChest = new BlockEnchanter(enchanterChestID).setBlockName("Enchanter Chest");
		LanguageRegistry.addName(enchanterChest, "Enchanter Chest");
		GameRegistry.registerBlock(enchanterChest);
		GameRegistry.registerTileEntity(TileEntityEnchanterChest.class, "container.enchanter");
		LanguageRegistry.instance().addStringLocalization("container.enchanter", "en_US", "Enchanter Chest");
		
		// Logging Camp
		loggingCamp = new BlockLoggingCamp(loggingCampID);
		LanguageRegistry.addName(loggingCamp, "Logging Camp");
		GameRegistry.registerBlock(loggingCamp);
		GameRegistry.registerTileEntity(TileEntityLoggingCamp.class, "container.loggingcamp");
		LanguageRegistry.instance().addStringLocalization("container.loggingcamp", "en_US", "Logging Camp");

		// House
		house = new BlockHouse(blockHouseID);
		LanguageRegistry.addName(house, "House");
		GameRegistry.registerBlock(house);
		GameRegistry.registerTileEntity(TileEntityHouse.class, "container.house");
		LanguageRegistry.instance().addStringLocalization("container.house", "en_US", "House");
		
		// Town Hall
		townHall = new BlockTownHall(townHallID);
		LanguageRegistry.addName(townHall, "Town Hall");
		GameRegistry.registerBlock(townHall);
		GameRegistry.registerTileEntity(TileEntityTownHall.class, "container.townhall");
		LanguageRegistry.instance().addStringLocalization("container.townhall", "en_US", "MyTown Town Hall");

		// Hunter Blind
		hunterBlind = new BlockHunterBlind(hunterBlindID);
		LanguageRegistry.addName(hunterBlind, "Hunter Blind");
		GameRegistry.registerBlock(hunterBlind);
		GameRegistry.registerTileEntity(TileEntityHunterBlind.class, "container.hunterBlind");
		LanguageRegistry.instance().addStringLocalization("container.hunterBlind", "en_US", "Hunter Blind");

		// Fisherman's Hut
		fishermanHut = new BlockFishermanHut(fishermanHutID);
		LanguageRegistry.addName(fishermanHut, "Fisherman's Hut");
		GameRegistry.registerBlock(fishermanHut);
		GameRegistry.registerTileEntity(TileEntityFishermanHut.class, "container.fishermanHut");
		LanguageRegistry.instance().addStringLocalization("container.fishermanHut", "en_US", "Fisherman's Hut");

		// Alchemist's Shop
		alchemistShop = new BlockAlchemistShop(alchemistShopID);
		LanguageRegistry.addName(alchemistShop, "Alchemist's Shop");
		GameRegistry.registerBlock(alchemistShop);
		GameRegistry.registerTileEntity(TileEntityAlchemistShop.class, "container.alchemistShop");
		LanguageRegistry.instance().addStringLocalization("container.alchemistShop", "en_US", "Alchemist's Shop");

		// Guard House
		guardHouse = new BlockGuardHouse(guardHouseID);
		LanguageRegistry.addName(guardHouse, "Guard House");
		GameRegistry.registerBlock(guardHouse);
		GameRegistry.registerTileEntity(TileEntityGuardHouse.class, "container.guardhouse");
		LanguageRegistry.instance().addStringLocalization("container.guardhouse", "en_US", "Guard House");
		
		// Measuring tape
		MeasuringTape = new ItemMeasuringTape(measuringTapeID).setItemName("Measuring Tape");
		LanguageRegistry.addName(MeasuringTape,"Measuring Tape");
		
		// Ancient Tome
		ancientTome = new ItemAncientTome(ancientTomeID).setItemName("Ancient Tome");
		LanguageRegistry.addName(ancientTome,"Ancient Tome");
		
		// Researched Tome
		researchedTome = new ItemResearchedTome(researchedTomeID).setItemName("Researched Tome");
		LanguageRegistry.addName(researchedTome,"Researched Tome");
		
		// Research Block
		researchBlock = new BlockResearchBlock(researchBlockID, false)
		.setBlockName("researchBlock").setHardness(0.75f).setCreativeTab(ColoniesMain.coloniesTab);
		LanguageRegistry.addName(researchBlock, "Research Bench");
		GameRegistry.registerBlock(researchBlock);
		GameRegistry.registerTileEntity(TileEntityResearchBlock.class, "container.researchBlock");
		LanguageRegistry.instance().addStringLocalization("container.researchBlock", "en_US", "Research Bench");


		// Test block
		test = (TestBlock) new TestBlock(testBlockID, 3, Material.ground)
		.setBlockName("test").setHardness(0.75f).setCreativeTab(CreativeTabs.tabBlock);
		MinecraftForge.setBlockHarvestLevel(test, "shovel", 0);
		LanguageRegistry.addName(test, "Test Block");
		GameRegistry.registerBlock(test);

		// Citizens
		// the three parameters after the class are ChanceWeight, minPackSize and maxPackSize
		EntityRegistry.registerGlobalEntityID(EntityCitizen.class, "Citizen", ModLoader.getUniqueEntityId(), 0xCCCCFF, 0xFF4444);
		LanguageRegistry.instance().addStringLocalization("entity.Citizen.name", "en_US", "Wanderer");
		
		// Miner
		EntityRegistry.registerGlobalEntityID(EntityMiner.class, "Miner", ModLoader.getUniqueEntityId(), 0xCCCCFF, 0xFF8888);
		LanguageRegistry.instance().addStringLocalization("entity.Miner.name", "en_US", "Miner");

		// Lumberjack
		EntityRegistry.registerGlobalEntityID(EntityLumberjack.class, "Lumberjack", ModLoader.getUniqueEntityId(), 0xCCCCFF, 0x888800);
		LanguageRegistry.instance().addStringLocalization("entity.Lumberjack.name", "en_US", "Lumberjack");

		// Wife
		EntityRegistry.registerGlobalEntityID(EntityWife.class, "Wife", ModLoader.getUniqueEntityId(), 0xCCCCFF, 0xFFcccc);
		LanguageRegistry.instance().addStringLocalization("entity.Wife.name", "en_US", "Wife");

		// Priestess
		EntityRegistry.registerGlobalEntityID(EntityPriestess.class, "Priestess", ModLoader.getUniqueEntityId(), 0xCCCCFF, 0x00FF00);
		LanguageRegistry.instance().addStringLocalization("entity.Priestess.name", "en_US", "Cleric");

		// Hunter
		EntityRegistry.registerGlobalEntityID(EntityHunter.class, "Hunter", ModLoader.getUniqueEntityId(), 0xCCCCFF, 0x099990);
		LanguageRegistry.instance().addStringLocalization("entity.Hunter.name", "en_US", "Hunter");

		// Fisherman
		EntityRegistry.registerGlobalEntityID(EntityFisherman.class, "Fisherman", ModLoader.getUniqueEntityId(), 0xCCCCFF, 0x099990);
		LanguageRegistry.instance().addStringLocalization("entity.Fisherman.name", "en_US", "Fisherman");

		// Alchemist
		EntityRegistry.registerGlobalEntityID(EntityAlchemist.class, "Alchemist", ModLoader.getUniqueEntityId(), 0xCCCCFF, 0x099990);
		LanguageRegistry.instance().addStringLocalization("entity.Alchemist.name", "en_US", "Alchemist");
		
		// Guard
		EntityRegistry.registerGlobalEntityID(EntityGuard.class, "Guard", ModLoader.getUniqueEntityId(), 0xCCCCFF, 0x099990);
		LanguageRegistry.instance().addStringLocalization("entity.Guard.name", "en_US", "Guard");
		
		// Enchanter
		EntityRegistry.registerGlobalEntityID(EntityEnchanter.class, "Enchanter", EntityRegistry.findGlobalUniqueEntityId(), 35342,2342);
		LanguageRegistry.instance().addStringLocalization("entity.Enchanter.name", "en_US", "Enchanter");
		
		LanguageRegistry.instance().addStringLocalization("itemGroup.coloniesTab", "en_US", "Colonies");

		

	}
}

