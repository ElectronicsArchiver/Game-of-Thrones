package got.common.world.map;

import got.GOT;
import got.common.GOTConfig;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public enum GOTFixedStructures {
	SPAWN(809.5, 729.5), NIGHT_KING(613, 314);

	public static long nanoTimeElapsed;
	public int xCoord;
	public int zCoord;

	GOTFixedStructures(double x, double z) {
		xCoord = GOTWaypoint.mapToWorldX(x);
		zCoord = GOTWaypoint.mapToWorldZ(z);
	}

	public double distanceSqTo(EntityLivingBase entity) {
		double dx = entity.posX - (xCoord + 0.5);
		double dz = entity.posZ - (zCoord + 0.5);
		return dx * dx + dz * dz;
	}

	public boolean isAt(World world, int x, int z) {
		if (!GOTFixedStructures.hasMapFeatures(world)) {
			return false;
		}
		return xCoord == x && zCoord == z;
	}

	public static boolean[] _mountainNear_structureNear(World world, int x, int z) {
		long l = System.nanoTime();
		boolean mountainNear = false;
		boolean structureNear = false;
		if (GOTFixedStructures.hasMapFeatures(world)) {
			if (GOTMountains.mountainAt(x, z)) {
				mountainNear = true;
			}
			structureNear = GOTFixedStructures.structureNear(world, x, z, 256);
			if (!structureNear) {
				for (GOTWaypoint wp : GOTWaypoint.values()) {
					double dz;
					double range;
					double dx = x - wp.getXCoord();
					double distSq = dx * dx + (dz = z - wp.getZCoord()) * dz;
					range = 256.0;
					if (distSq >= range * range) {
						continue;
					}
					structureNear = true;
					break;
				}
			}
			if (!structureNear && GOTRoads.isRoadNear(x, z, 32) >= 0.0f) {
				structureNear = true;
			}
			if (!structureNear && GOTWalls.isWallNear(x, z, 32) >= 0.0f) {
				structureNear = true;
			}
		}
		nanoTimeElapsed += System.nanoTime() - l;
		return new boolean[] { mountainNear, structureNear };
	}

	public static boolean fixedAt(int i, int k, GOTWaypoint waypoint) {
		int x = GOTWaypoint.mapToWorldX(waypoint.pseudoX);
		int z = GOTWaypoint.mapToWorldZ(waypoint.pseudoY);
		return GOTFixedStructures.fixedAtfixedAtMapImageCoords(i, k, x, z);
	}

	public static boolean fixedAtfixedAtMapImageCoords(int i, int k, int x, int z) {
		return i >> 4 == x >> 4 && k >> 4 == z >> 4;
	}

	public static boolean hasMapFeatures(World world) {
		if (!GOTConfig.generateMapFeatures) {
			return false;
		}
		return world.getWorldInfo().getTerrainType() != GOT.worldTypeGOTClassic;
	}

	public static boolean structureNear(World world, int x, int z, int range) {
		for (GOTFixedStructures str : GOTFixedStructures.values()) {
			double dx = x - str.xCoord;
			double dz = z - str.zCoord;
			double distSq = dx * dx + dz * dz;
			double rangeSq = range * range;
			if (distSq >= rangeSq) {
				continue;
			}
			return true;
		}
		return false;
	}
}
