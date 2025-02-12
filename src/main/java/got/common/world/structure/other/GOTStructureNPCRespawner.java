package got.common.world.structure.other;

import java.util.Random;

import got.common.entity.other.GOTEntityNPCRespawner;
import net.minecraft.world.World;

public abstract class GOTStructureNPCRespawner extends GOTStructureBase {
	public GOTStructureNPCRespawner(boolean flag) {
		super(flag);
	}

	@Override
	public boolean generate(World world, Random random, int i, int j, int k, int rotation) {
		this.setOriginAndRotation(world, i, j, k, rotation, 0);
		GOTEntityNPCRespawner spawner = new GOTEntityNPCRespawner(world);
		setupRespawner(spawner);
		placeNPCRespawner(spawner, world, 0, 1, 0);
		return true;
	}

	public abstract void setupRespawner(GOTEntityNPCRespawner var1);
}
