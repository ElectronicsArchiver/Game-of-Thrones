package got.common.world.structure.essos.ibben;

import java.util.Random;

import com.google.common.math.IntMath;

import got.common.database.GOTRegistry;
import got.common.entity.essos.ibben.*;
import got.common.world.structure.other.GOTStructureBase;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public abstract class GOTStructureIbbenMarketStall extends GOTStructureIbbenBase {
	public static Class[] allStallTypes = { Blacksmith.class, Farmer.class, Lumber.class, Builder.class, Brewer.class, Butcher.class, Fish.class, Baker.class, Orcharder.class };

	public GOTStructureIbbenMarketStall(boolean flag) {
		super(flag);
	}

	public abstract GOTEntityIbbenMan createTrader(World var1);

	@Override
	public boolean generate(World world, Random random, int i, int j, int k, int rotation) {
		int k1;
		int i1;
		this.setOriginAndRotation(world, i, j, k, rotation, 3);
		setupRandomBlocks(random);
		if (restrictions) {
			for (i1 = -2; i1 <= 2; ++i1) {
				for (k1 = -2; k1 <= 2; ++k1) {
					int j1 = getTopBlock(world, i1, k1) - 1;
					if (isSurface(world, i1, j1, k1)) {
						continue;
					}
					return false;
				}
			}
		}
		for (i1 = -2; i1 <= 2; ++i1) {
			for (k1 = -2; k1 <= 2; ++k1) {
				int j1;
				int i2 = Math.abs(i1);
				int k2 = Math.abs(k1);
				for (j1 = 0; (j1 >= 0 || !isOpaque(world, i1, j1, k1)) && getY(j1) >= 0; --j1) {
					setBlockAndMetadata(world, i1, j1, k1, GOTRegistry.dirtPath, 0);
					setGrassToDirt(world, i1, j1 - 1, k1);
				}
				for (j1 = 1; j1 <= 4; ++j1) {
					setAir(world, i1, j1, k1);
				}
				if (i2 == 2 && k2 == 2) {
					if (k1 < 0) {
						for (j1 = 1; j1 <= 4; ++j1) {
							setBlockAndMetadata(world, i1, j1, k1, fenceBlock, fenceMeta);
						}
						continue;
					}
					for (j1 = 1; j1 <= 3; ++j1) {
						setBlockAndMetadata(world, i1, j1, k1, fenceBlock, fenceMeta);
					}
					continue;
				}
				int j2 = 4;
				if (k1 == 2 || k1 == 1 && i2 == 2) {
					j2 = 3;
				}
				generateRoof(world, random, i1, j2, k1);
			}
		}
		setBlockAndMetadata(world, -1, 1, -2, plankStairBlock, 4);
		setBlockAndMetadata(world, 0, 1, -2, plankStairBlock, 6);
		setBlockAndMetadata(world, 1, 1, -2, plankStairBlock, 5);
		setBlockAndMetadata(world, -1, 1, 2, plankStairBlock, 4);
		setBlockAndMetadata(world, 0, 1, 2, plankStairBlock, 7);
		setBlockAndMetadata(world, 1, 1, 2, plankStairBlock, 5);
		setBlockAndMetadata(world, 2, 1, -1, plankStairBlock, 7);
		setBlockAndMetadata(world, 2, 1, 0, plankStairBlock, 4);
		setBlockAndMetadata(world, 2, 1, 1, plankStairBlock, 6);
		setBlockAndMetadata(world, -2, 1, -1, plankBlock, plankMeta);
		setBlockAndMetadata(world, -2, 1, 0, fenceGateBlock, 1);
		setBlockAndMetadata(world, -2, 1, 1, plankBlock, plankMeta);
		for (i1 = -1; i1 <= 1; ++i1) {
			setBlockAndMetadata(world, i1, 1, 1, plank2StairBlock, 6);
			setBlockAndMetadata(world, i1, 3, 1, plankSlabBlock, plankSlabMeta | 8);
		}
		for (int k12 = -1; k12 <= 0; ++k12) {
			setBlockAndMetadata(world, -2, 3, k12, plankSlabBlock, plankSlabMeta | 8);
			setBlockAndMetadata(world, 2, 3, k12, plankSlabBlock, plankSlabMeta | 8);
		}
		setBlockAndMetadata(world, 1, 1, -1, Blocks.chest, 3);
		for (i1 = -1; i1 <= 1; ++i1) {
			setBlockAndMetadata(world, i1, 3, -2, fenceBlock, fenceMeta);
		}
		GOTEntityIbbenMan trader = createTrader(world);
		spawnNPCAndSetHome(trader, world, 0, 1, 0, 4);
		return true;
	}

	public abstract void generateRoof(World var1, Random var2, int var3, int var4, int var5);

	public static GOTStructureBase getRandomStall(Random random, boolean flag) {
		try {
			Class cls = allStallTypes[random.nextInt(allStallTypes.length)];
			return (GOTStructureBase) cls.getConstructor(Boolean.TYPE).newInstance(flag);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static class Baker extends GOTStructureIbbenMarketStall {
		public Baker(boolean flag) {
			super(flag);
		}

		@Override
		public GOTEntityIbbenMan createTrader(World world) {
			return new GOTEntityIbbenBaker(world);
		}

		@Override
		public void generateRoof(World world, Random random, int i1, int j1, int k1) {
			int i2 = Math.abs(i1);
			Math.abs(k1);
			if (i2 % 2 == 0) {
				setBlockAndMetadata(world, i1, j1, k1, Blocks.wool, 12);
			} else {
				setBlockAndMetadata(world, i1, j1, k1, Blocks.wool, 1);
			}
		}
	}

	public static class Blacksmith extends GOTStructureIbbenMarketStall {
		public Blacksmith(boolean flag) {
			super(flag);
		}

		@Override
		public GOTEntityIbbenMan createTrader(World world) {
			return new GOTEntityIbbenBlacksmith(world);
		}

		@Override
		public void generateRoof(World world, Random random, int i1, int j1, int k1) {
			int i2 = Math.abs(i1);
			if (i2 + Math.abs(k1) >= 3) {
				setBlockAndMetadata(world, i1, j1, k1, Blocks.wool, 7);
			} else {
				setBlockAndMetadata(world, i1, j1, k1, Blocks.wool, 15);
			}
		}
	}

	public static class Brewer extends GOTStructureIbbenMarketStall {
		public Brewer(boolean flag) {
			super(flag);
		}

		@Override
		public GOTEntityIbbenMan createTrader(World world) {
			return new GOTEntityIbbenBrewer(world);
		}

		@Override
		public void generateRoof(World world, Random random, int i1, int j1, int k1) {
			int i2 = Math.abs(i1);
			Math.abs(k1);
			if (i2 % 2 == 1) {
				setBlockAndMetadata(world, i1, j1, k1, Blocks.wool, 12);
			} else {
				setBlockAndMetadata(world, i1, j1, k1, Blocks.wool, 4);
			}
		}
	}

	public static class Builder extends GOTStructureIbbenMarketStall {
		public Builder(boolean flag) {
			super(flag);
		}

		@Override
		public GOTEntityIbbenMan createTrader(World world) {
			return new GOTEntityIbbenBuilder(world);
		}

		@Override
		public void generateRoof(World world, Random random, int i1, int j1, int k1) {
			int i2 = Math.abs(i1);
			int k2 = Math.abs(k1);
			if (k2 % 2 == 0 && i2 % 2 == k2 / 2 % 2) {
				setBlockAndMetadata(world, i1, j1, k1, Blocks.wool, 12);
			} else {
				setBlockAndMetadata(world, i1, j1, k1, Blocks.wool, 7);
			}
		}
	}

	public static class Butcher extends GOTStructureIbbenMarketStall {
		public Butcher(boolean flag) {
			super(flag);
		}

		@Override
		public GOTEntityIbbenMan createTrader(World world) {
			return new GOTEntityIbbenButcher(world);
		}

		@Override
		public void generateRoof(World world, Random random, int i1, int j1, int k1) {
			if (random.nextInt(3) == 0) {
				setBlockAndMetadata(world, i1, j1, k1, Blocks.wool, 14);
			} else {
				setBlockAndMetadata(world, i1, j1, k1, Blocks.wool, 6);
			}
		}
	}

	public static class Farmer extends GOTStructureIbbenMarketStall {
		public Farmer(boolean flag) {
			super(flag);
		}

		@Override
		public GOTEntityIbbenMan createTrader(World world) {
			return new GOTEntityIbbenFarmer(world);
		}

		@Override
		public void generateRoof(World world, Random random, int i1, int j1, int k1) {
			if (random.nextInt(3) == 0) {
				setBlockAndMetadata(world, i1, j1, k1, Blocks.wool, 0);
			} else {
				setBlockAndMetadata(world, i1, j1, k1, Blocks.wool, 8);
			}
		}
	}

	public static class Fish extends GOTStructureIbbenMarketStall {
		public Fish(boolean flag) {
			super(flag);
		}

		@Override
		public GOTEntityIbbenMan createTrader(World world) {
			return new GOTEntityIbbenFishmonger(world);
		}

		@Override
		public void generateRoof(World world, Random random, int i1, int j1, int k1) {
			int i2 = Math.abs(i1);
			int k2 = Math.abs(k1);
			if (k2 % 2 == 1) {
				setBlockAndMetadata(world, i1, j1, k1, Blocks.wool, 11);
			} else if (i2 % 2 == k2 / 2 % 2) {
				setBlockAndMetadata(world, i1, j1, k1, Blocks.wool, 0);
			} else {
				setBlockAndMetadata(world, i1, j1, k1, Blocks.wool, 3);
			}
		}
	}

	public static class Lumber extends GOTStructureIbbenMarketStall {
		public Lumber(boolean flag) {
			super(flag);
		}

		@Override
		public GOTEntityIbbenMan createTrader(World world) {
			return new GOTEntityIbbenLumberman(world);
		}

		@Override
		public void generateRoof(World world, Random random, int i1, int j1, int k1) {
			int i2 = Math.abs(i1);
			if (i2 + Math.abs(k1) >= 3) {
				setBlockAndMetadata(world, i1, j1, k1, Blocks.wool, 13);
			} else {
				setBlockAndMetadata(world, i1, j1, k1, Blocks.wool, 12);
			}
		}
	}

	public static class Orcharder extends GOTStructureIbbenMarketStall {
		public Orcharder(boolean flag) {
			super(flag);
		}

		@Override
		public GOTEntityIbbenMan createTrader(World world) {
			return new GOTEntityIbbenOrcharder(world);
		}

		@Override
		public void generateRoof(World world, Random random, int i1, int j1, int k1) {
			int i2 = Math.abs(i1);
			if (IntMath.mod(i2 + Math.abs(k1), 2) == 0) {
				setBlockAndMetadata(world, i1, j1, k1, Blocks.wool, 14);
			} else {
				setBlockAndMetadata(world, i1, j1, k1, Blocks.wool, 5);
			}
		}
	}

}
