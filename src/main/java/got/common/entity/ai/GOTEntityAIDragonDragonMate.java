package got.common.entity.ai;

import java.util.List;

import got.common.entity.dragon.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.world.World;

public class GOTEntityAIDragonDragonMate extends EntityAIBase {

	public GOTEntityDragon dragon;
	public GOTEntityDragon dragonMate;
	public World theWorld;
	public int spawnBabyDelay = 0;
	public double speed;

	public GOTEntityAIDragonDragonMate(GOTEntityDragon dragon, double speed) {
		this.dragon = dragon;
		this.speed = speed;
		theWorld = dragon.worldObj;
		setMutexBits(3);
	}

	@Override
	public boolean continueExecuting() {
		return dragonMate.isEntityAlive() && dragonMate.isInLove() && spawnBabyDelay < 60;
	}

	public GOTEntityDragon getNearbyMate() {
		double range = 12;
		List<Entity> nearbyEntities = theWorld.getEntitiesWithinAABB(GOTEntityDragon.class, dragon.boundingBox.expand(range, range, range));

		for (Entity entity : nearbyEntities) {
			GOTEntityDragon nearbyDragon = (GOTEntityDragon) entity;
			if (dragon.canMateWith(nearbyDragon)) {
				return nearbyDragon;
			}
		}

		return null;
	}

	@Override
	public void resetTask() {
		dragonMate = null;
		spawnBabyDelay = 0;
	}

	@Override
	public boolean shouldExecute() {
		if (!dragon.isInLove()) {
			return false;
		}
		dragonMate = getNearbyMate();
		return dragonMate != null;
	}

	public void spawnBaby() {
		GOTEntityDragon dragonBaby = (GOTEntityDragon) dragon.createChild(dragonMate);

		if (dragonBaby != null) {
			dragon.setGrowingAge(6000);
			dragonMate.setGrowingAge(6000);

			dragon.resetInLove();
			dragonMate.resetInLove();

			dragonBaby.setLocationAndAngles(dragon.posX, dragon.posY, dragon.posZ, 0, 0);
			dragonBaby.getLifeStageHelper().setLifeStage(GOTDragonLifeStage.EGG);

			theWorld.spawnEntityInWorld(dragonBaby);

		}
	}

	@Override
	public void updateTask() {
		dragon.getLookHelper().setLookPositionWithEntity(dragonMate, 10.0F, dragon.getVerticalFaceSpeed());
		dragon.getNavigator().tryMoveToEntityLiving(dragonMate, speed);

		++spawnBabyDelay;

		if (spawnBabyDelay == 60) {
			spawnBaby();
		}
	}
}
