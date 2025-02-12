package got.common.world.structure.essos.qarth;

import java.util.Random;

import got.common.database.*;
import got.common.entity.essos.qarth.*;
import got.common.world.structure.essos.common.GOTStructureEssosBazaar;
import got.common.world.structure.other.GOTStructureBase;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class GOTStructureQarthBazaar extends GOTStructureEssosBazaar {
	public static Class[] stalls = { Lumber.class, Mason.class, Fish.class, Baker.class, Goldsmith.class, Farmer.class, Blacksmith.class, Brewer.class, Miner.class, Florist.class, Butcher.class };

	public GOTStructureQarthBazaar(boolean flag) {
		super(flag);
		isQarth = true;
	}

	@Override
	public Class[] getStallClasses() {
		return stalls;
	}

	public static class Baker extends GOTStructureBase {
		public Baker(boolean flag) {
			super(flag);
		}

		@Override
		public boolean generate(World world, Random random, int i, int j, int k, int rotation) {
			this.setOriginAndRotation(world, i, j, k, rotation, 0);
			setBlockAndMetadata(world, 0, 1, 1, Blocks.furnace, 2);
			setBlockAndMetadata(world, -1, 1, 1, GOTRegistry.planks2, 2);
			setBlockAndMetadata(world, 1, 1, 1, GOTRegistry.planks2, 2);
			placePlateItem(world, random, -1, 2, 1, GOTRegistry.ceramicPlateBlock, new ItemStack(Items.bread, 1 + random.nextInt(3)), true);
			placePlateItem(world, random, 1, 2, 1, GOTRegistry.ceramicPlateBlock, new ItemStack(GOTRegistry.oliveBread, 1 + random.nextInt(3)), true);
			placeFlowerPot(world, random.nextBoolean() ? -2 : 2, 2, 0, getRandomFlower(world, random));
			GOTEntityQarthBaker trader = new GOTEntityQarthBaker(world);
			spawnNPCAndSetHome(trader, world, 0, 1, 0, 4);
			return true;
		}
	}

	public static class Blacksmith extends GOTStructureBase {
		public Blacksmith(boolean flag) {
			super(flag);
		}

		@Override
		public boolean generate(World world, Random random, int i, int j, int k, int rotation) {
			this.setOriginAndRotation(world, i, j, k, rotation, 0);
			setBlockAndMetadata(world, -1, 1, 1, Blocks.anvil, 3);
			placeWeaponRack(world, -2, 2, 0, 1, new GOTStructureQarthBazaar(false).getRandomWeapon(random));
			placeWeaponRack(world, 2, 2, 0, 3, new GOTStructureQarthBazaar(false).getRandomWeapon(random));
			GOTEntityQarthBlacksmith trader = new GOTEntityQarthBlacksmith(world);
			spawnNPCAndSetHome(trader, world, 0, 1, 0, 4);
			return true;
		}
	}

	public static class Brewer extends GOTStructureBase {
		public Brewer(boolean flag) {
			super(flag);
		}

		@Override
		public boolean generate(World world, Random random, int i, int j, int k, int rotation) {
			this.setOriginAndRotation(world, i, j, k, rotation, 0);
			setBlockAndMetadata(world, -1, 1, 1, GOTRegistry.stairsCedar, 6);
			setBlockAndMetadata(world, -1, 2, 1, GOTRegistry.barrel, 2);
			setBlockAndMetadata(world, 1, 1, 1, GOTRegistry.stairsCedar, 6);
			setBlockAndMetadata(world, 1, 2, 1, GOTRegistry.barrel, 2);
			this.placeMug(world, random, -2, 2, 0, 1, GOTFoods.ESSOS_DRINK);
			this.placeMug(world, random, 2, 2, 0, 1, GOTFoods.ESSOS_DRINK);
			GOTEntityQarthBrewer trader = new GOTEntityQarthBrewer(world);
			spawnNPCAndSetHome(trader, world, 0, 1, 0, 4);
			return true;
		}
	}

	public static class Butcher extends GOTStructureBase {
		public Butcher(boolean flag) {
			super(flag);
		}

		@Override
		public boolean generate(World world, Random random, int i, int j, int k, int rotation) {
			this.setOriginAndRotation(world, i, j, k, rotation, 0);
			setBlockAndMetadata(world, 0, 1, 1, Blocks.furnace, 2);
			placeKebabStand(world, random, 0, 2, 1, GOTRegistry.kebabStand, 3);
			placePlateItem(world, random, -2, 2, 0, GOTRegistry.ceramicPlateBlock, new ItemStack(GOTRegistry.muttonRaw, 1 + random.nextInt(3), 0), true);
			placePlateItem(world, random, 2, 2, 0, GOTRegistry.ceramicPlateBlock, new ItemStack(GOTRegistry.camelRaw, 1 + random.nextInt(3), 1), true);
			GOTEntityQarthButcher trader = new GOTEntityQarthButcher(world);
			spawnNPCAndSetHome(trader, world, 0, 1, 0, 4);
			return true;
		}
	}

	public static class Farmer extends GOTStructureBase {
		public Farmer(boolean flag) {
			super(flag);
		}

		@Override
		public boolean generate(World world, Random random, int i, int j, int k, int rotation) {
			this.setOriginAndRotation(world, i, j, k, rotation, 0);
			setBlockAndMetadata(world, -1, 1, 1, Blocks.cauldron, 3);
			setBlockAndMetadata(world, 1, 1, 1, Blocks.hay_block, 0);
			setBlockAndMetadata(world, -1, 1, -1, Blocks.hay_block, 0);
			placePlateItem(world, random, -2, 2, 0, GOTRegistry.woodPlateBlock, new ItemStack(GOTRegistry.orange, 1 + random.nextInt(3), 0), true);
			placePlateItem(world, random, 2, 2, 0, GOTRegistry.woodPlateBlock, new ItemStack(GOTRegistry.lettuce, 1 + random.nextInt(3), 1), true);
			GOTEntityQarthFarmer trader = new GOTEntityQarthFarmer(world);
			spawnNPCAndSetHome(trader, world, 0, 1, 0, 4);
			return true;
		}
	}

	public static class Fish extends GOTStructureBase {
		public Fish(boolean flag) {
			super(flag);
		}

		@Override
		public boolean generate(World world, Random random, int i, int j, int k, int rotation) {
			this.setOriginAndRotation(world, i, j, k, rotation, 0);
			setBlockAndMetadata(world, 1, 1, 1, Blocks.cauldron, 3);
			setBlockAndMetadata(world, -1, 1, -1, Blocks.sponge, 0);
			placePlateItem(world, random, -2, 2, 0, GOTRegistry.ceramicPlateBlock, new ItemStack(Items.fish, 1 + random.nextInt(3), 0), true);
			placePlateItem(world, random, 2, 2, 0, GOTRegistry.ceramicPlateBlock, new ItemStack(Items.fish, 1 + random.nextInt(3), 1), true);
			GOTEntityQarthFishmonger trader = new GOTEntityQarthFishmonger(world);
			spawnNPCAndSetHome(trader, world, 0, 1, 0, 4);
			return true;
		}
	}

	public static class Florist extends GOTStructureBase {
		public Florist(boolean flag) {
			super(flag);
		}

		@Override
		public boolean generate(World world, Random random, int i, int j, int k, int rotation) {
			this.setOriginAndRotation(world, i, j, k, rotation, 0);
			placeFlowerPot(world, -2, 2, 0, getRandomFlower(world, random));
			placeFlowerPot(world, 2, 2, 0, getRandomFlower(world, random));
			setBlockAndMetadata(world, -1, 0, 1, Blocks.grass, 0);
			setBlockAndMetadata(world, -1, 1, 1, GOTRegistry.doubleFlower, 3);
			setBlockAndMetadata(world, -1, 2, 1, GOTRegistry.doubleFlower, 11);
			setBlockAndMetadata(world, 1, 1, 1, Blocks.grass, 0);
			plantFlower(world, random, 1, 2, 1);
			setBlockAndMetadata(world, 1, 1, 0, Blocks.trapdoor, 12);
			setBlockAndMetadata(world, 0, 1, 1, Blocks.trapdoor, 15);
			GOTEntityQarthFlorist trader = new GOTEntityQarthFlorist(world);
			spawnNPCAndSetHome(trader, world, 0, 1, 0, 4);
			return true;
		}
	}

	public static class Goldsmith extends GOTStructureBase {
		public Goldsmith(boolean flag) {
			super(flag);
		}

		@Override
		public boolean generate(World world, Random random, int i, int j, int k, int rotation) {
			this.setOriginAndRotation(world, i, j, k, rotation, 0);
			setBlockAndMetadata(world, -1, 1, -1, GOTRegistry.goldBars, 0);
			setBlockAndMetadata(world, 1, 1, -1, GOTRegistry.goldBars, 0);
			setBlockAndMetadata(world, -1, 1, 1, GOTRegistry.goldBars, 0);
			setBlockAndMetadata(world, 1, 1, 1, GOTRegistry.goldBars, 0);
			setBlockAndMetadata(world, random.nextBoolean() ? -1 : 1, 2, -1, GOTRegistry.birdCage, 2);
			setBlockAndMetadata(world, random.nextBoolean() ? -1 : 1, 2, 1, GOTRegistry.birdCage, 3);
			GOTEntityQarthGoldsmith trader = new GOTEntityQarthGoldsmith(world);
			spawnNPCAndSetHome(trader, world, 0, 1, 0, 4);
			return true;
		}
	}

	public static class Lumber extends GOTStructureBase {
		public Lumber(boolean flag) {
			super(flag);
		}

		@Override
		public boolean generate(World world, Random random, int i, int j, int k, int rotation) {
			this.setOriginAndRotation(world, i, j, k, rotation, 0);
			setBlockAndMetadata(world, -1, 1, 1, GOTRegistry.wood4, 10);
			setBlockAndMetadata(world, 1, 1, 1, GOTRegistry.wood4, 2);
			setBlockAndMetadata(world, 1, 2, 1, GOTRegistry.wood4, 2);
			placeFlowerPot(world, -2, 2, 0, new ItemStack(GOTRegistry.sapling4, 1, 2));
			placeFlowerPot(world, 2, 2, 0, new ItemStack(GOTRegistry.sapling8, 1, 3));
			GOTEntityQarthLumberman trader = new GOTEntityQarthLumberman(world);
			spawnNPCAndSetHome(trader, world, 0, 1, 0, 4);
			return true;
		}
	}

	public static class Mason extends GOTStructureBase {
		public Mason(boolean flag) {
			super(flag);
		}

		@Override
		public boolean generate(World world, Random random, int i, int j, int k, int rotation) {
			this.setOriginAndRotation(world, i, j, k, rotation, 0);
			setBlockAndMetadata(world, -1, 1, 1, GOTRegistry.brick1, 15);
			setBlockAndMetadata(world, -1, 2, 1, GOTRegistry.slabSingle4, 0);
			setBlockAndMetadata(world, 1, 1, 1, GOTRegistry.brick3, 13);
			placeWeaponRack(world, 1, 2, 1, 2, new ItemStack(GOTRegistry.bronzePickaxe));
			GOTEntityQarthMason trader = new GOTEntityQarthMason(world);
			spawnNPCAndSetHome(trader, world, 0, 1, 0, 4);
			return true;
		}
	}

	public static class Miner extends GOTStructureBase {
		public Miner(boolean flag) {
			super(flag);
		}

		@Override
		public boolean generate(World world, Random random, int i, int j, int k, int rotation) {
			this.setOriginAndRotation(world, i, j, k, rotation, 0);
			setBlockAndMetadata(world, -1, 1, 1, GOTRegistry.oreTin, 0);
			setBlockAndMetadata(world, -1, 2, 1, GOTRegistry.oreCopper, 0);
			setBlockAndMetadata(world, 1, 1, 1, GOTRegistry.oreCopper, 0);
			placeWeaponRack(world, 1, 2, 1, 2, new ItemStack(GOTRegistry.bronzePickaxe));
			GOTEntityQarthMiner trader = new GOTEntityQarthMiner(world);
			spawnNPCAndSetHome(trader, world, 0, 1, 0, 4);
			return true;
		}
	}

}
