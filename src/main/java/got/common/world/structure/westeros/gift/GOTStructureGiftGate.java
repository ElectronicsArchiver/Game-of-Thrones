package got.common.world.structure.westeros.gift;

import java.util.Random;

import got.common.database.GOTRegistry;
import got.common.world.structure.other.GOTStructureBase;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class GOTStructureGiftGate extends GOTStructureGiftBase {
	public GOTStructureGiftGate(boolean flag) {
		super(flag);
	}

	@Override
	public boolean generate(World world, Random random, int i, int j, int k, int rotation) {
		this.setOriginAndRotation(world, i, j, k, rotation, 0, 0);
		setupRandomBlocks(random);
		originY += 1;
		originZ -= 7;
		for (int x = -6; x <= 6; ++x) {
			for (int y = -6; y <= 6; ++y) {
				for (int z = 0; z <= 3; ++z) {
					setBlockAndMetadata(world, x, z, y, GOTRegistry.brickIce, 0);
				}
			}
		}
		for (int x = -5; x <= 5; ++x) {
			for (int y = -6; y <= 6; ++y) {
				for (int z = 4; z <= 7; ++z) {
					setBlockAndMetadata(world, x, z, y, GOTRegistry.brickIce, 0);
				}
			}
		}
		for (int x = -4; x <= 4; ++x) {
			for (int y = -6; y <= 6; ++y) {
				for (int z = 8; z <= 9; ++z) {
					setBlockAndMetadata(world, x, z, y, GOTRegistry.brickIce, 0);
				}
			}
		}
		for (int x = -3; x <= 3; ++x) {
			for (int y = -6; y <= 6; ++y) {
				setBlockAndMetadata(world, x, 10, y, GOTRegistry.brickIce, 0);
			}
		}
		for (int x = -2; x <= 2; ++x) {
			for (int y = -6; y <= 6; ++y) {
				setBlockAndMetadata(world, x, 11, y, GOTRegistry.brickIce, 0);
			}
		}
		for (int x = -3; x <= 3; ++x) {
			for (int y = -6; y <= 6; ++y) {
				for (int z = 0; z <= 6; ++z) {
					setAir(world, x, z, y);
				}
			}
		}
		for (int x = -3; x <= 3; ++x) {
			for (int z = 0; z <= 6; ++z) {
				setBlockAndMetadata(world, x, z, 4, GOTRegistry.gateIronBars, 2);
				setBlockAndMetadata(world, x, z, -4, GOTRegistry.gateWooden, 2);
			}
		}
		int wallTop = getTopBlock(world, 0, 0) - 1;
		for (int z = 0; z <= 6; ++z) {
			for (int y = -6; y <= 6; ++y) {
				if (y <= -4 || y >= 4) {
					setBlockAndMetadata(world, -4, z, y, woodBeamBlock, woodBeamMeta);
					setBlockAndMetadata(world, 4, z, y, woodBeamBlock, woodBeamMeta);
				} else {
					setBlockAndMetadata(world, -4, z, y, GOTRegistry.cobblebrick, 0);
					setBlockAndMetadata(world, 4, z, y, GOTRegistry.cobblebrick, 0);
				}
			}
		}
		for (int x = -3; x <= 3; ++x) {
			for (int y = -6; y <= 6; ++y) {
				if (y <= -4 || y >= 4) {
					setBlockAndMetadata(world, x, 7, y, woodBeamBlock, woodBeamMeta | 4);
					if (y >= 4) {
						setBlockAndMetadata(world, x, wallTop, y, woodBeamBlock, woodBeamMeta | 4);
					}
				} else {
					setBlockAndMetadata(world, x, 7, y, plankBlock, plankMeta);
				}
				Block block = Blocks.gravel;
				int meta = 0;
				float f = random.nextFloat();
				if (f < 0.5f) {
					block = GOTRegistry.dirtPath;
					meta = 0;
				} else if (f >= 0.5f && f < 0.8f) {
					block = Blocks.dirt;
					meta = 1;
				}
				setBlockAndMetadata(world, x, -1, y, block, meta);
			}
		}
		if (!isAbandoned) {
			setBlockAndMetadata(world, 3, 3, 1, Blocks.torch, 1);
			setBlockAndMetadata(world, 3, 3, -1, Blocks.torch, 1);
			setBlockAndMetadata(world, -3, 3, 1, Blocks.torch, 2);
			setBlockAndMetadata(world, -3, 3, -1, Blocks.torch, 2);
		}
		for (int z = 0; z <= wallTop; ++z) {
			setBlockAndMetadata(world, -4, z, 7, GOTRegistry.rope, 3);
			setBlockAndMetadata(world, 4, z, 7, GOTRegistry.rope, 3);
			setBlockAndMetadata(world, -4, z, 6, woodBeamBlock, woodBeamMeta);
			setBlockAndMetadata(world, 4, z, 6, woodBeamBlock, woodBeamMeta);
		}
		for (int x = -3; x <= 3; ++x) {
			for (int z = 8; z <= wallTop - 1; ++z) {
				setBlockAndMetadata(world, x, z, 6, fenceBlock, fenceMeta);
			}
			setBlockAndMetadata(world, x, wallTop, 6, woodBeamBlock, woodBeamMeta | 4);
		}
		for (int y = 0; y <= 6; ++y) {
			setBlockAndMetadata(world, -4, wallTop, y, woodBeamBlock, woodBeamMeta | 8);
			setBlockAndMetadata(world, 4, wallTop, y, woodBeamBlock, woodBeamMeta | 8);
		}
		return true;
	}

	public GOTStructureBase setIsAbandoned() {
		isAbandoned = true;
		return this;
	}
}