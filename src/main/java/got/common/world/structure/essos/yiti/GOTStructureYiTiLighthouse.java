package got.common.world.structure.essos.yiti;

import java.util.Random;

import got.common.database.GOTRegistry;
import got.common.world.structure.other.GOTStructureBase;
import net.minecraft.world.World;

public class GOTStructureYiTiLighthouse extends GOTStructureBase {

	@Override
	public boolean generate(World world, Random random, int i, int j, int k, int rotation) {
		rotationMode = 2;
		this.setOriginAndRotation(world, i, j, k, rotation, 0);
		loadStrScan("lighthouse");
		associateBlockMetaAlias("BRICK", GOTRegistry.brick2, 11);
		generateStrScan(world, random, 0, 0, 0);
		return true;
	}
}