package got.common.world.structure.essos.ghiscar;

import java.util.*;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.*;

public class GOTStructureGhiscarPyramidComponent extends StructureComponent {
	public static GOTStructureGhiscarPyramid pyramidGen = new GOTStructureGhiscarPyramid(false);
	public static Random pyramidRand;
	static {
		GOTStructureGhiscarPyramidComponent.pyramidGen.restrictions = false;
		pyramidRand = new Random();
	}
	public int posX;
	public int posY = -1;
	public int posZ;
	public int direction;

	public long pyramidSeed = -1L;

	public GOTStructureGhiscarPyramidComponent() {
	}

	public GOTStructureGhiscarPyramidComponent(World world, int l, Random random, int i, int k) {
		super(l);
		int r = GOTStructureGhiscarPyramid.RADIUS + 5;
		boundingBox = new StructureBoundingBox(i - r, 0, k - r, i + r, 255, k + r);
		posX = i;
		posZ = k;
		direction = random.nextInt(4);
	}

	@Override
	public boolean addComponentParts(World world, Random random, StructureBoundingBox structureBoundingBox) {
		if (posY == -1) {
			posY = world.getTopSolidOrLiquidBlock(structureBoundingBox.getCenterX(), structureBoundingBox.getCenterZ());
		}
		if (pyramidSeed == -1L) {
			pyramidSeed = random.nextLong();
		}
		pyramidGen.setStructureBB(structureBoundingBox);
		pyramidRand.setSeed(pyramidSeed);
		pyramidGen.generate(world, pyramidRand, posX, posY, posZ, direction);
		return true;
	}

	@Override
	public void buildComponent(StructureComponent component, List list, Random random) {
	}

	@Override
	public void func_143011_b(NBTTagCompound nbt) {
		posX = nbt.getInteger("PyrX");
		posY = nbt.getInteger("PyrY");
		posZ = nbt.getInteger("PyrZ");
		direction = nbt.getInteger("Direction");
		pyramidSeed = nbt.getLong("Seed");
	}

	@Override
	public void func_143012_a(NBTTagCompound nbt) {
		nbt.setInteger("PyrX", posX);
		nbt.setInteger("PyrY", posY);
		nbt.setInteger("PyrZ", posZ);
		nbt.setInteger("Direction", direction);
		nbt.setLong("Seed", pyramidSeed);
	}
}
