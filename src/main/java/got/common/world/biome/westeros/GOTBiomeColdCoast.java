package got.common.world.biome.westeros;

import java.util.Random;

import got.client.sound.GOTBiomeMusic;
import got.client.sound.GOTBiomeMusic.MusicRegion;
import got.common.database.*;
import got.common.entity.animal.GOTEntityWalrus;
import got.common.world.biome.GOTBiome;
import got.common.world.biome.variant.GOTBiomeVariant;
import got.common.world.spawning.*;
import got.common.world.spawning.GOTBiomeSpawnList.SpawnListContainer;
import got.common.world.structure.westeros.wildling.GOTStructureWildlingVillage;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.gen.NoiseGeneratorPerlin;

public class GOTBiomeColdCoast extends GOTBiomeFarNorthSnowy {
	public NoiseGeneratorPerlin noiseDirt = new NoiseGeneratorPerlin(new Random(42956029606L), 1);
	public NoiseGeneratorPerlin noiseGravel = new NoiseGeneratorPerlin(new Random(7185609602367L), 1);
	public NoiseGeneratorPerlin noiseIceGravel = new NoiseGeneratorPerlin(new Random(12480634985056L), 1);

	public GOTBiomeColdCoast(int i, boolean major) {
		super(i, major);
		spawnableCreatureList.clear();
		spawnableWaterCreatureList.clear();
		spawnableCaveCreatureList.clear();
		spawnableGOTAmbientList.clear();
		decorator.addVillage(new GOTStructureWildlingVillage(this, 1.0f));
		spawnableCreatureList.add(new BiomeGenBase.SpawnListEntry(GOTEntityWalrus.class, 8, 1, 1));

		SpawnListContainer[] c0 = new SpawnListContainer[1];
		c0[0] = GOTBiomeSpawnList.entry(GOTSpawnList.WILDING_MILITARY, 10).setSpawnChance(GOTBiome.SPAWN);
		npcSpawnList.newFactionList(10).add(c0);
		setUnreliableChance(GOTEventSpawner.EventChance.NEVER);
	}

	@Override
	public void generateBiomeTerrain(World world, Random random, Block[] blocks, byte[] meta, int i, int k, double stoneNoise, int height, GOTBiomeVariant variant) {
		Block topBlock_pre = topBlock;
		int topBlockMeta_pre = topBlockMeta;
		Block fillerBlock_pre = fillerBlock;
		int fillerBlockMeta_pre = fillerBlockMeta;
		double d1 = noiseDirt.func_151601_a(i * 0.09, k * 0.09);
		double d2 = noiseDirt.func_151601_a(i * 0.6, k * 0.6);
		double d3 = noiseGravel.func_151601_a(i * 0.09, k * 0.09);
		double d4 = noiseGravel.func_151601_a(i * 0.6, k * 0.6);
		double d5 = noiseIceGravel.func_151601_a(i * 0.09, k * 0.09);
		if (d5 + noiseIceGravel.func_151601_a(i * 0.6, k * 0.6) > 0.5) {
			topBlock = Blocks.ice;
			topBlockMeta = 0;
		} else if (d3 + d4 > 0.6) {
			topBlock = Blocks.dirt;
			topBlockMeta = 0;
		} else if (d1 + d2 > 0.6) {
			topBlock = Blocks.dirt;
			topBlockMeta = 1;
		}
		super.generateBiomeTerrain(world, random, blocks, meta, i, k, stoneNoise, height, variant);
		topBlock = topBlock_pre;
		topBlockMeta = topBlockMeta_pre;
		fillerBlock = fillerBlock_pre;
		fillerBlockMeta = fillerBlockMeta_pre;
	}

	@Override
	public GOTAchievement getBiomeAchievement() {
		return GOTAchievement.enterColdCoast;
	}

	@Override
	public MusicRegion getBiomeMusic() {
		return GOTBiomeMusic.WESTEROS.getSubregion("coldCoast");
	}
}
