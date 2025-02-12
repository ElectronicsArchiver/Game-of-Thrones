package got.common.world;

import got.GOT;
import got.common.*;
import got.common.entity.other.GOTEntityPortal;
import got.common.world.map.GOTWaypoint;
import net.minecraft.entity.Entity;
import net.minecraft.world.*;

public class GOTTeleporter extends Teleporter {
	public WorldServer world;
	public boolean makeRingPortal;

	public GOTTeleporter(WorldServer worldserver, boolean flag) {
		super(worldserver);
		world = worldserver;
		makeRingPortal = flag;
	}

	@Override
	public void placeInPortal(Entity entity, double d, double d1, double d2, float f) {
		int k;
		int i;
		int j;
		if (world.provider.dimensionId == GOTDimension.GAME_OF_THRONES.dimensionID) {
			i = GOTWaypoint.Winterfell.xCoord;
			k = GOTWaypoint.Winterfell.zCoord;
			j = GOT.getTrueTopBlock(world, i, k);
		} else {
			i = GOTLevelData.overworldPortalX;
			k = GOTLevelData.overworldPortalZ;
			j = GOTLevelData.overworldPortalY;
		}
		entity.setLocationAndAngles(i + 0.5, j + 1.0, k + 0.5, entity.rotationYaw, 0.0f);
		if (world.provider.dimensionId == GOTDimension.GAME_OF_THRONES.dimensionID && GOTLevelData.madeGameOfThronesPortal == 0) {
			GOTLevelData.setMadeGameOfThronesPortal(1);
			if (makeRingPortal) {
				if (world.provider instanceof GOTWorldProvider) {
					((GOTWorldProvider) world.provider).setRingPortalLocation(i, j, k);
				}
				GOTEntityPortal portal = new GOTEntityPortal(world);
				portal.setLocationAndAngles(i + 0.5, j + 3.5, k + 0.5, 0.0f, 0.0f);
				world.spawnEntityInWorld(portal);
			}
		}
	}
}
