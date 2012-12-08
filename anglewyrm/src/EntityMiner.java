package colonies.anglewyrm.src;

import java.util.HashMap;
import colonies.anglewyrm.src.Utility;
import net.minecraft.src.EntityAIAttackOnCollide;
import net.minecraft.src.EntityAIHurtByTarget;
import net.minecraft.src.EntityAISwimming;
import net.minecraft.src.EntityAIWander;
import net.minecraft.src.EntityAIWatchClosest;
import net.minecraft.src.EntityPlayer;
import net.minecraft.src.Item;
import net.minecraft.src.World;
import colonies.anglewyrm.src.EntityCitizen.jobs;

public class EntityMiner extends EntityCitizen{
	public EntityMiner(World world){ 
		super(world);
		this.texture = ConfigFile.getSkin("skinMiner");
		this.skills = new HashMap<jobs, Integer>(10);
	    this.skills.put(jobs.unemployed, 10);
	    
	    // TODO: Would like miners to go hostile with a pickaxe if attacked
	}
	
	public String getTexture() {
		if(this.isInWater()){
			return ConfigFile.getSkin("skinMinerSwimming");
		}
		return ConfigFile.getSkin("skinMiner");
    }

	protected String getLivingSound(){
		if(citizenGreetings){
			if(Utility.getLootCategory()==3){
				return "colonies.m-hello";
			}
		}
		return "";
    }
	
    // Mob Loot for default Citizen
    protected int getDropItemId() {
    	int lootID=1;
    	switch(Utility.getLootCategory()){
    	case 1: // Common
    			switch(Utility.getLootCategory(3)){
    			case 1: return Item.appleRed.shiftedIndex;
    			case 2: return Item.pickaxeStone.shiftedIndex;
    			default:return Item.pickaxeSteel.shiftedIndex;
    			}
    	case 2: // Uncommon
    		return Item.coal.shiftedIndex;
    	case 3: // Rare
    		return Item.goldNugget.shiftedIndex;
    	default: // Legendary
    		return Item.ingotGold.shiftedIndex;
    	}
    }

	public void onLivingUpdate()
	{
		// custom miner behaviors
		super.onLivingUpdate();
    }
	
}
