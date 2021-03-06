package colonies.src.citizens;

import java.util.LinkedList;

import colonies.src.Point;
import colonies.src.Utility;


import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;

// AI is given a set of stock levels to maintain in their personal inventory
// They will check their place of employment to gather shortages
public class EntityAIMaintainInventoryLevels extends EntityAIBase
{
	EntityCitizen citizen;
	static int INVENTORY_CHECK_FREQUENCY = 100; // about 5 seconds between scans
	int updateCounter = INVENTORY_CHECK_FREQUENCY;
	ItemStack objectOfDesire;
	Point destination;
	
	public EntityAIMaintainInventoryLevels(EntityCitizen _citizen){
		citizen = _citizen;	
		this.setMutexBits(1);
	}
   
	@Override
	public boolean shouldExecute() 
	{
		// reasons to idle this task
		if(citizen == null) return false;
		if(citizen.homeTown == null) return false;
		if(!citizen.worldObj.isDaytime()) return false; // don't work during night
		if(citizen.employer == null) return false;      // needs an employer chest to get stuff from
		if(citizen.inventory.isFull()) return false;    // need room to put stuff

		// if nearby employer chest, check wants every time
		if(destination == null){
			destination = new Point(citizen.employer);
		}
		if(destination.getDistance(citizen) < 3){
			citizen.stopNavigating();
			return wantsSomething();
		} // else too far away (would need to path there)
		
		// using memory of want, and updating want infrequently
		if(--updateCounter > 0){
			return citizen.wantsSomething;
		}
		else{
			updateCounter = INVENTORY_CHECK_FREQUENCY;
			return wantsSomething();
		}
	}
	
	public void startExecuting(){
		destination.set(citizen.employer);
		if(destination.getDistance(citizen.employer) < 3) return;
		citizen.getNavigator().tryMoveToXYZ(destination.x, destination.y+1, destination.z, 0.35f);
	}
	
    public boolean continueExecuting()
    {
    	if(citizen.employer == null) return false; // employer destroyed?
    	
    	// if arrived, get supplies and return false
    	destination.set(citizen.employer);
    	if(destination.getDistance(citizen) < 3.0){
    		// arrived at location, transfer supplies
    		citizen.stopNavigating();
    		
    		if(citizen.getItemFromChest(citizen.employer, objectOfDesire) != null){ 
    			Utility.chatMessage("Citizen #" +citizen.ssn + " got supplies");
    		}
    		citizen.wantsSomething = false;
    		objectOfDesire = null;
    		return false;
    	}// else still pathing to location
    	
    	return !this.citizen.getNavigator().noPath();
    }

	
	// Returns true if citizen has an item in desiredInventory,
	// but the citizen's inventory has less of that item.
	private boolean wantsSomething()
	{
		// check main inventory
		for(int slot = 0; slot < citizen.desiredInventory.getSizeInventory(); slot++)
		{
			ItemStack thisDesire = citizen.desiredInventory.getStackInSlot(slot);
			if(thisDesire == null) continue;
			
			if(citizen.desiredInventory.countItems(thisDesire.itemID) > citizen.inventory.countItems(thisDesire.itemID)){
				//Utility.chatMessage(citizen.ssn + " wants something");
				objectOfDesire = thisDesire;
				citizen.wantsSomething = true;
				return true;
			}
		}
		// Utility.chatMessage("Citizen is content");
		objectOfDesire = null;
		citizen.wantsSomething = false;
		return false;
	}
}

