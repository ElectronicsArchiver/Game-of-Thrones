package got.common.quest;

import java.util.*;

import cpw.mods.fml.common.FMLLog;
import got.common.GOTLore;
import got.common.database.*;
import got.common.entity.animal.GOTEntityCrocodile;
import got.common.entity.essos.legendary.quest.GOTEntityDaenerysTargaryen;
import got.common.entity.essos.legendary.warrior.GOTEntityTugarKhan;
import got.common.entity.other.*;
import got.common.entity.westeros.legendary.captain.GOTEntityRickardKarstark;
import got.common.entity.westeros.legendary.deco.GOTEntityTommenBaratheon;
import got.common.entity.westeros.legendary.quest.*;
import got.common.entity.westeros.legendary.reborn.GOTEntityTheonGreyjoy;
import got.common.entity.westeros.legendary.warrior.*;
import got.common.faction.GOTFaction;
import got.common.item.other.GOTItemBanner;
import net.minecraft.init.*;
import net.minecraft.item.ItemStack;

public enum GOTMQFactory {
	CRIMINAL(true), IBBEN(true), SUMMER(true), SOTHORYOS(true), ASSHAI(true), WILDLING(true), MOSSOVY(true), HOWLAND, BALON, DAENERYS, VARYS, OBERYN, STANNIS, JONSNOW, RENLY, KITRA, BUGAI, TYRION, CERSEI, RAMSAY, SANDOR, MELISANDRA, DORAN, MARGAERY, ELLARYA, ARYA, OLENNA, SAMWELL, LYSA, CATELYN, DAVEN, ARIANNE, MELLARIO, NORTH(true), RIVERLANDS(true), DORNE(true), REACH(true), STORMLANDS(true), IRONBORN(true), WESTERLANDS(true), ARRYN(true), CROWNLANDS(true), DRAGONSTONE(true), GIFT(true), HILLMEN(true), BRAAVOS(true), LORATH(true), NORVOS(true), QOHOR(true), PENTOS(true), LYS(true), MYR(true), TYROSH(true), VOLANTIS(true), GHISCAR(true), QARTH(true), LHAZAR(true), YI_TI(true), DOTHRAKI(true), JOGOS(true);

	public static Random rand;
	public static Map<Class<? extends GOTMQ>, Integer> questClassWeights;
	static {
		rand = new Random();
		questClassWeights = new HashMap<>();
	}
	public String baseName;
	public GOTMQFactory baseSpeechGroup;
	public Map<Class<? extends GOTMQ>, List<GOTMQ.QuestFactoryBase>> questFactories = new HashMap<>();
	public GOTAchievement questAchievement;
	public List<GOTLore.LoreCategory> loreCategories = new ArrayList<>();
	public GOTFaction alignmentRewardOverride;

	public boolean noAlignRewardForEnemy = false;

	GOTMQFactory() {
		baseName = "legendary";
		setAchievement(GOTAchievement.DO_LEGENDARY_QUEST);
	}

	GOTMQFactory(Boolean isStandart) {
		baseName = "standart";
		setAchievement(GOTAchievement.DO_QUEST);
	}

	public void addQuest(GOTMQ.QuestFactoryBase factory) {
		Class questClass = factory.getQuestClass();
		Class<? extends GOTMQ> registryClass = null;
		for (Class<? extends GOTMQ> c : questClassWeights.keySet()) {
			if (!questClass.equals(c)) {
				continue;
			}
			registryClass = c;
			break;
		}
		if (registryClass == null) {
			for (Class<? extends GOTMQ> c : questClassWeights.keySet()) {
				if (!c.isAssignableFrom(questClass)) {
					continue;
				}
				registryClass = c;
				break;
			}
		}
		if (registryClass == null) {
			throw new IllegalArgumentException("Could not find registered quest class for " + questClass.toString());
		}
		factory.setFactoryGroup(this);
		List<GOTMQ.QuestFactoryBase> list = questFactories.get(registryClass);
		if (list == null) {
			list = new ArrayList<>();
			questFactories.put(registryClass, list);
		}
		list.add(factory);
	}

	public GOTFaction checkAlignmentRewardFaction(GOTFaction fac) {
		if (alignmentRewardOverride != null) {
			return alignmentRewardOverride;
		}
		return fac;
	}

	public GOTMQ createQuest(GOTEntityNPC npc) {
		int totalWeight = GOTMQFactory.getTotalQuestClassWeight(this);
		if (totalWeight <= 0) {
			FMLLog.warning("GOT: No quests registered for %s!", baseName);
			return null;
		}
		int i = rand.nextInt(totalWeight);
		Iterator<Map.Entry<Class<? extends GOTMQ>, List<GOTMQ.QuestFactoryBase>>> iterator = questFactories.entrySet().iterator();
		List<GOTMQ.QuestFactoryBase> chosenFactoryList = null;
		while (iterator.hasNext()) {
			Map.Entry<Class<? extends GOTMQ>, List<GOTMQ.QuestFactoryBase>> next = iterator.next();
			chosenFactoryList = next.getValue();
			i -= GOTMQFactory.getQuestClassWeight(next.getKey());
			if (i >= 0) {
				continue;
			}
		}
		GOTMQ.QuestFactoryBase factory = chosenFactoryList.get(rand.nextInt(chosenFactoryList.size()));
		GOTMQ quest = factory.createQuest(npc, rand);
		if (quest != null) {
			quest.questGroup = this;
		}
		return quest;
	}

	public GOTAchievement getAchievement() {
		return questAchievement;
	}

	public String getBaseName() {
		return baseName;
	}

	public GOTMQFactory getBaseSpeechGroup() {
		if (baseSpeechGroup != null) {
			return baseSpeechGroup;
		}
		return this;
	}

	public List<GOTLore.LoreCategory> getLoreCategories() {
		return loreCategories;
	}

	public boolean isNoAlignRewardForEnemy() {
		return noAlignRewardForEnemy;
	}

	public void setAchievement(GOTAchievement a) {
		if (questAchievement != null) {
			throw new IllegalArgumentException("Miniquest achievement is already registered");
		}
		questAchievement = a;
	}

	public GOTMQFactory setAlignmentRewardOverride(GOTFaction fac) {
		alignmentRewardOverride = fac;
		return this;
	}

	public void setBaseSpeechGroup(GOTMQFactory qf) {
		baseSpeechGroup = qf;
	}

	public void setLore(GOTLore.LoreCategory... categories) {
		loreCategories = Arrays.asList(categories);
	}

	public GOTMQFactory setNoAlignRewardForEnemy() {
		noAlignRewardForEnemy = true;
		return this;
	}

	public static GOTMQFactory forName(String name) {
		for (GOTMQFactory group : GOTMQFactory.values()) {
			if (!group.getBaseName().equals(name)) {
				continue;
			}
			return group;
		}
		return null;
	}

	public static int getQuestClassWeight(Class<? extends GOTMQ> questClass) {
		Integer i = questClassWeights.get(questClass);
		if (i == null) {
			throw new RuntimeException("Encountered a registered quest class " + questClass.toString() + " which is not assigned a weight");
		}
		return i;
	}

	public static GOTFaction getRandomEnemy(GOTFaction owner) {
		ArrayList<GOTFaction> enemies = new ArrayList<>();
		for (GOTFaction fac : GOTFaction.values()) {
			if (owner.isBadRelation(fac) && fac != GOTFaction.WHITE_WALKER) {
				enemies.add(fac);
			}
		}
		return enemies.get(rand.nextInt(enemies.size()));
	}

	public static int getTotalQuestClassWeight(GOTMQFactory factory) {
		HashSet<Class<? extends GOTMQ>> registeredQuestTypes = new HashSet<>();
		for (Map.Entry<Class<? extends GOTMQ>, List<GOTMQ.QuestFactoryBase>> entry : factory.questFactories.entrySet()) {
			Class<? extends GOTMQ> questType = entry.getKey();
			registeredQuestTypes.add(questType);
		}
		int totalWeight = 0;
		for (Class c : registeredQuestTypes) {
			totalWeight += GOTMQFactory.getQuestClassWeight(c);
		}
		return totalWeight;
	}

	public static void onInit() {
		GOTMQFactory.registerQuestClass(GOTMQCollect.class, 10);
		GOTMQFactory.registerQuestClass(GOTMQPickpocket.class, 6);
		GOTMQFactory.registerQuestClass(GOTMQKill.class, 8);
		GOTMQFactory.registerQuestClass(GOTMQBounty.class, 4);
		BALON.addQuest(new GOTMQKillEntity.QFKillEntity("balon").setKillEntity(GOTEntityEuronGreyjoy.class, 1, 1).setRewardFactor(100.0f).setIsLegendary());
		VARYS.addQuest(new GOTMQKillEntity.QFKillEntity("varys").setKillEntity(GOTEntityDaenerysTargaryen.class, 1, 1).setRewardFactor(100.0f).setIsLegendary());
		OBERYN.addQuest(new GOTMQKillEntity.QFKillEntity("oberyn").setKillEntity(GOTEntityGeroldDayne.class, 1, 1).setRewardFactor(100.0f).setIsLegendary());
		STANNIS.addQuest(new GOTMQKillEntity.QFKillEntity("stannis").setKillEntity(GOTEntityRenlyBaratheon.class, 1, 1).setRewardFactor(100.0f).setIsLegendary());
		RENLY.addQuest(new GOTMQKillEntity.QFKillEntity("renly").setKillEntity(GOTEntityStannisBaratheon.class, 1, 1).setRewardFactor(100.0f).setIsLegendary());
		BUGAI.addQuest(new GOTMQKillEntity.QFKillEntity("bugai").setKillEntity(GOTEntityTugarKhan.class, 1, 1).setRewardFactor(100.0f).setIsLegendary());
		MARGAERY.addQuest(new GOTMQKillEntity.QFKillEntity("margaery").setKillEntity(GOTEntityCerseiLannister.class, 1, 1).setRewardFactor(100.0f).setIsLegendary());
		HOWLAND.addQuest(new GOTMQKillEntity.QFKillEntity("howland").setKillEntity(GOTEntityCrocodile.class, 50, 50).setRewardFactor(50.0f).setIsLegendary());
		OLENNA.addQuest(new GOTMQKillEntity.QFKillEntity("olenna").setKillEntity(GOTEntityJoffreyBaratheon.class, 1, 1).setRewardFactor(100.0f).setIsLegendary());
		MELISANDRA.addQuest(new GOTMQCollect.QFCollect("melisandra").setCollectItem(new ItemStack(GOTRegistry.bloodOfTrueKings), 1, 1).setRewardFactor(100.0f).setIsLegendary());
		TYRION.addQuest(new GOTMQCollect.QFCollect("tyrion").setCollectItem(new ItemStack(GOTRegistry.mugRedWine), 1, 10).setRewardFactor(5.0f).setIsLegendary());
		CERSEI.addQuest(new GOTMQCollect.QFCollect("cersei").setCollectItem(new ItemStack(GOTRegistry.wildFireJar), 1, 10).setRewardFactor(0.0f).setIsLegendary());
		RAMSAY.addQuest(new GOTMQCollect.QFCollect("ramsay").setCollectItem(new ItemStack(GOTRegistry.brandingIron), 1, 1).setRewardFactor(5.0f).setIsLegendary());
		DAENERYS.addQuest(new GOTMQCollect.QFCollect("daenerys").setCollectItem(new ItemStack(Blocks.dragon_egg), 3, 3).setRewardFactor(100.0f).setIsLegendary());
		JONSNOW.addQuest(new GOTMQCollect.QFCollect("jonsnow").setCollectItem(new ItemStack(GOTRegistry.valyrianSword), 1, 1).setHiring());
		SANDOR.addQuest(new GOTMQCollect.QFCollect("sandor").setCollectItem(new ItemStack(Items.cooked_chicken), 1, 10).setHiring());
		DORAN.addQuest(new GOTMQCollect.QFCollect("doran").setCollectItem(new ItemStack(GOTRegistry.mugPoppyMilk), 1, 10).setRewardFactor(5.0f).setIsLegendary());
		ELLARYA.addQuest(new GOTMQKillEntity.QFKillEntity("ellarya").setKillEntity(GOTEntityTywinLannister.class, 1, 1).setRewardFactor(100.0f).setIsLegendary());
		ARYA.addQuest(new GOTMQCollect.QFCollect("arya").setCollectItem(new ItemStack(Items.wooden_sword), 1, 1).setHiring());
		SAMWELL.addQuest(new GOTMQCollect.QFCollect("samwell").setCollectItem(new ItemStack(GOTRegistry.sothoryosDagger), 1, 1).setRewardFactor(50.0f).setIsLegendary());
		LYSA.addQuest(new GOTMQKillEntity.QFKillEntity("lysa").setKillEntity(GOTEntityTyrionLannister.class, 1, 1).setRewardFactor(100.0f).setIsLegendary());
		CATELYN.addQuest(new GOTMQKillEntity.QFKillEntity("catelyn").setKillEntity(GOTEntityTheonGreyjoy.TheonGreyjoyNormal.class, 1, 1).setRewardFactor(100.0f).setIsLegendary());
		DAVEN.addQuest(new GOTMQKillEntity.QFKillEntity("daven").setKillEntity(GOTEntityRickardKarstark.class, 1, 1).setRewardFactor(100.0f).setIsLegendary());
		ARIANNE.addQuest(new GOTMQKillEntity.QFKillEntity("arianne").setKillEntity(GOTEntityTommenBaratheon.class, 1, 1).setRewardFactor(100.0f).setIsLegendary());
		MELLARIO.addQuest(new GOTMQKillEntity.QFKillEntity("mellario").setKillEntity(GOTEntityDoranMartell.class, 1, 1).setRewardFactor(100.0f).setIsLegendary());

		HashMap<GOTMQFactory, GOTFaction> kingdoms = new HashMap<>();
		kingdoms.put(NORTH, GOTFaction.NORTH);
		kingdoms.put(RIVERLANDS, GOTFaction.RIVERLANDS);
		kingdoms.put(WESTERLANDS, GOTFaction.WESTERLANDS);
		kingdoms.put(IRONBORN, GOTFaction.IRONBORN);
		kingdoms.put(ARRYN, GOTFaction.ARRYN);
		kingdoms.put(CROWNLANDS, GOTFaction.CROWNLANDS);
		kingdoms.put(DRAGONSTONE, GOTFaction.DRAGONSTONE);
		kingdoms.put(STORMLANDS, GOTFaction.STORMLANDS);
		kingdoms.put(REACH, GOTFaction.REACH);
		kingdoms.put(DORNE, GOTFaction.DORNE);
		kingdoms.put(GIFT, GOTFaction.NIGHT_WATCH);

		HashMap<GOTMQFactory, GOTFaction> cities = new HashMap<>();
		cities.put(BRAAVOS, GOTFaction.BRAAVOS);
		cities.put(LORATH, GOTFaction.LORATH);
		cities.put(NORVOS, GOTFaction.NORVOS);
		cities.put(QOHOR, GOTFaction.QOHOR);
		cities.put(PENTOS, GOTFaction.PENTOS);
		cities.put(MYR, GOTFaction.MYR);
		cities.put(LYS, GOTFaction.LYS);
		cities.put(TYROSH, GOTFaction.TYROSH);
		cities.put(VOLANTIS, GOTFaction.VOLANTIS);
		cities.put(GHISCAR, GOTFaction.GHISCAR);
		cities.put(QARTH, GOTFaction.QARTH);

		for (GOTMQFactory kingdom : kingdoms.keySet()) {
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.deerCooked), 5, 20).setRewardFactor(2.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugAle), 5, 10).setRewardFactor(2.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugCider), 5, 10).setRewardFactor(2.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugMead), 5, 10).setRewardFactor(2.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugRum), 5, 10).setRewardFactor(2.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.muttonCooked), 5, 20).setRewardFactor(2.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.rabbitCooked), 5, 20).setRewardFactor(2.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.bread), 5, 20).setRewardFactor(1.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.cooked_beef), 5, 20).setRewardFactor(2.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.cooked_chicken), 5, 20).setRewardFactor(2.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.cooked_porkchop), 5, 20).setRewardFactor(2.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.log, 1, 0), 20, 50).setRewardFactor(0.25f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.planks, 1, 0), 50, 100).setRewardFactor(0.25f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.planks, 1, 1), 50, 100).setRewardFactor(0.25f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.stonebrick), 50, 100).setRewardFactor(0.5f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.wool, 1, 0), 6, 15).setRewardFactor(1.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.wool, 1, 12), 6, 15).setRewardFactor(1.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.wool, 1, 14), 6, 15).setRewardFactor(1.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.wool, 1, 15), 6, 15).setRewardFactor(1.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.wood2, 1, 0), 20, 50).setRewardFactor(0.25f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.wood2, 1, 1), 20, 50).setRewardFactor(0.25f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.wood4, 1, 0), 20, 50).setRewardFactor(0.25f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.wood6, 1, 1), 20, 50).setRewardFactor(0.25f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.chisel), 1, 5).setRewardFactor(4.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.ironBattleaxe), 1, 5).setRewardFactor(4.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.ironDagger), 1, 5).setRewardFactor(2.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.ironPike), 1, 5).setRewardFactor(4.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.ironThrowingAxe), 1, 5).setRewardFactor(3.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.westerosBow), 1, 5).setRewardFactor(3.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.westerosDagger), 1, 5).setRewardFactor(3.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.westerosDaggerPoisoned), 1, 5).setRewardFactor(3.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.westerosHammer), 1, 5).setRewardFactor(3.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.westerosHorseArmor), 1, 5).setRewardFactor(3.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.westerosLance), 1, 5).setRewardFactor(3.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.westerosPike), 1, 5).setRewardFactor(3.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.westerosSpear), 1, 5).setRewardFactor(3.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.westerosSword), 1, 5).setRewardFactor(3.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.arrow), 20, 50).setRewardFactor(0.25f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.bucket), 1, 5).setRewardFactor(3.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.iron_axe), 1, 5).setRewardFactor(4.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.iron_boots), 1, 5).setRewardFactor(3.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.iron_chestplate), 1, 5).setRewardFactor(4.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.iron_helmet), 1, 5).setRewardFactor(3.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.iron_hoe), 1, 5).setRewardFactor(2.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.iron_ingot), 1, 5).setRewardFactor(2.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.iron_leggings), 1, 5).setRewardFactor(3.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.iron_shovel), 1, 5).setRewardFactor(4.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.iron_sword), 1, 5).setRewardFactor(3.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.shears), 1, 5).setRewardFactor(4.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.cobblestone), 30, 80).setRewardFactor(0.25f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.gold_ore), 10, 5).setRewardFactor(5.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.iron_ore), 10, 20).setRewardFactor(2.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.oreCopper), 10, 5).setRewardFactor(2.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.oreTin), 10, 5).setRewardFactor(2.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.rock, 1, 0), 20, 50).setRewardFactor(0.25f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.rock, 1, 1), 20, 50).setRewardFactor(0.25f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.copperIngot), 1, 5).setRewardFactor(2.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.silverIngot), 1, 5).setRewardFactor(4.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.tinIngot), 1, 5).setRewardFactor(4.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.coal), 20, 50).setRewardFactor(0.5f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.gold_ingot), 1, 5).setRewardFactor(4.0f));
			kingdom.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.leather), 5, 20).setRewardFactor(0.5f));
			kingdom.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(kingdoms.get(kingdom)), 10, 30));
			kingdom.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(kingdoms.get(kingdom)), 30, 50));
			kingdom.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(kingdoms.get(kingdom)), 50, 70));
			kingdom.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(kingdoms.get(kingdom)), 70, 100));
			kingdom.addQuest(new GOTMQBounty.QFBounty("bounty"));
			if (kingdom != DORNE) {
				kingdom.addQuest(new GOTMQKillEntity.QFKillEntity("kill").setKillEntity(GOTEntityWesterosBandit.class, 1, 3).setRewardFactor(8.0f));
			} else {
				kingdom.addQuest(new GOTMQKillEntity.QFKillEntity("kill").setKillEntity(GOTEntityEssosBandit.class, 1, 3).setRewardFactor(8.0f));
			}
		}

		for (GOTMQFactory city : cities.keySet()) {
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.date), 8, 15).setRewardFactor(2.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.deerCooked), 5, 10).setRewardFactor(1.5f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.kebab), 4, 8).setRewardFactor(2.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.lemon), 4, 12).setRewardFactor(2.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.lime), 4, 12).setRewardFactor(2.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.lionCooked), 2, 4).setRewardFactor(4.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugAraq), 3, 5).setRewardFactor(4.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugBananaBeer), 4, 10).setRewardFactor(2.5f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugCactusLiqueur), 4, 10).setRewardFactor(2.5f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugCarrotWine), 4, 10).setRewardFactor(2.5f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugCornLiquor), 4, 10).setRewardFactor(2.5f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugLemonLiqueur), 2, 6).setRewardFactor(4.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugOrangeJuice), 2, 6).setRewardFactor(4.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugRum), 4, 10).setRewardFactor(2.5f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.muttonCooked), 4, 8).setRewardFactor(2.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.orange), 4, 12).setRewardFactor(2.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.plum), 4, 12).setRewardFactor(2.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.rabbitCooked), 5, 10).setRewardFactor(1.5f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.waterskin), 8, 20).setRewardFactor(0.75f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.cooked_beef), 4, 8).setRewardFactor(2.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.cooked_porkchop), 4, 8).setRewardFactor(2.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.chest), 8, 16).setRewardFactor(1.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.wool, 1, 0), 6, 15).setRewardFactor(1.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.wool, 1, 12), 6, 15).setRewardFactor(1.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.wool, 1, 14), 6, 15).setRewardFactor(1.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.wool, 1, 15), 6, 15).setRewardFactor(1.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.brick1, 1, 15), 30, 60).setRewardFactor(0.5f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.brick3, 1, 13), 30, 60).setRewardFactor(0.75f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.brick6, 1, 6), 30, 60).setRewardFactor(0.5f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.chestBasket), 5, 10).setRewardFactor(2.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.driedReeds), 10, 20).setRewardFactor(0.5f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.planks2, 1, 11), 60, 120).setRewardFactor(0.2f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.planks2, 1, 2), 60, 120).setRewardFactor(0.125f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.planks2, 1, 2), 60, 120).setRewardFactor(0.2f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.planks3, 1, 3), 60, 120).setRewardFactor(0.2f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.pouch, 1, 0), 3, 5).setRewardFactor(5.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.rope), 5, 12).setRewardFactor(1.1f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.rope), 5, 12).setRewardFactor(1.1f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.slabSingleThatch, 1, 1), 20, 40).setRewardFactor(0.25f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.thatch, 1, 1), 10, 20).setRewardFactor(0.5f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.wood4, 1, 2), 30, 60).setRewardFactor(0.25f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.string), 5, 12).setRewardFactor(1.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.bottlePoison), 2, 4).setRewardFactor(5.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.essosDagger), 1, 2).setRewardFactor(20.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.essosHammer), 1, 3).setRewardFactor(5.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.essosPike), 1, 1).setRewardFactor(5.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.essosSpear), 1, 3).setRewardFactor(5.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.essosSword), 1, 3).setRewardFactor(5.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.sandstone, 1, 0), 30, 80).setRewardFactor(0.25f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.stone, 1, 0), 30, 80).setRewardFactor(0.25f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.rock, 1, 0), 30, 50).setRewardFactor(0.5f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.gold_ingot), 3, 6).setRewardFactor(4.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.iron_ingot), 4, 8).setRewardFactor(2.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.lava_bucket), 2, 4).setRewardFactor(5.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.leather), 10, 20).setRewardFactor(0.75f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.string), 5, 12).setRewardFactor(1.0f));
			city.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.water_bucket), 3, 5).setRewardFactor(5.0f));
			city.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(cities.get(city)), 10, 30));
			city.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(cities.get(city)), 30, 50));
			city.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(cities.get(city)), 50, 70));
			city.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(cities.get(city)), 70, 100));
			city.addQuest(new GOTMQBounty.QFBounty("bounty"));
			if (city != GHISCAR) {
				city.addQuest(new GOTMQKillEntity.QFKillEntity("kill").setKillEntity(GOTEntityWesterosBandit.class, 1, 3).setRewardFactor(8.0f));
			} else {
				city.addQuest(new GOTMQKillEntity.QFKillEntity("kill").setKillEntity(GOTEntityEssosBandit.class, 1, 3).setRewardFactor(8.0f));
			}
		}

		ASSHAI.addQuest(new GOTMQBounty.QFBounty("bounty"));
		ASSHAI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.asshaiFlower), 6, 15).setRewardFactor(1.0f));
		ASSHAI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.bottlePoison), 6, 15).setRewardFactor(1.0f));

		CRIMINAL.addQuest(new GOTMQPickpocket.QFPickpocket("criminal").setPickpocketNumber(1, 9).setIsLegendary());

		WILDLING.addQuest(new GOTMQBounty.QFBounty("bounty"));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.iron_boots), 2, 5).setRewardFactor(3.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.iron_chestplate), 2, 5).setRewardFactor(4.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.iron_helmet), 2, 5).setRewardFactor(3.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.iron_leggings), 2, 5).setRewardFactor(3.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.furChestplate), 2, 5).setRewardFactor(4.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.furHelmet), 2, 5).setRewardFactor(4.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.furBoots), 2, 5).setRewardFactor(3.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.furLeggings), 2, 5).setRewardFactor(3.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.bronzeChestplate), 2, 5).setRewardFactor(3.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.bronzeHelmet), 2, 5).setRewardFactor(3.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.bronzeBoots), 2, 5).setRewardFactor(3.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.bronzeLeggings), 2, 5).setRewardFactor(3.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.deerCooked), 4, 8).setRewardFactor(2.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.muttonCooked), 4, 8).setRewardFactor(2.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.cooked_beef), 4, 8).setRewardFactor(2.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.cooked_chicken), 4, 8).setRewardFactor(2.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.cooked_porkchop), 4, 8).setRewardFactor(2.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.bronzeIngot), 3, 10).setRewardFactor(2.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.copperIngot), 3, 10).setRewardFactor(2.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.iron_ingot), 3, 10).setRewardFactor(2.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugEthanol), 2, 5).setRewardFactor(3.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.bronzeThrowingAxe), 3, 6).setRewardFactor(3.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.ironThrowingAxe), 3, 6).setRewardFactor(3.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.silverIngot), 3, 6).setRewardFactor(4.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.gold_ingot), 3, 6).setRewardFactor(4.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.wildlingHammer), 1, 5).setRewardFactor(4.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.bronzeBattleaxe), 1, 5).setRewardFactor(4.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.ironBattleaxe), 1, 5).setRewardFactor(4.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.ironPike), 1, 5).setRewardFactor(4.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.wildlingPolearm), 1, 5).setRewardFactor(4.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.wildlingSword), 1, 5).setRewardFactor(3.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.bronzeSword), 1, 5).setRewardFactor(3.0f));
		WILDLING.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.iron_sword), 1, 5).setRewardFactor(3.0f));
		WILDLING.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.WILDLING), 10, 30));
		WILDLING.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.WILDLING), 30, 50));
		WILDLING.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.WILDLING), 50, 70));
		WILDLING.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.WILDLING), 70, 100));

		DOTHRAKI.addQuest(new GOTMQBounty.QFBounty("bounty"));
		DOTHRAKI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.carpet, 1), 8, 15).setRewardFactor(0.75f));
		DOTHRAKI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.carpet, 10), 8, 15).setRewardFactor(0.75f));
		DOTHRAKI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.carpet, 11), 8, 15).setRewardFactor(0.75f));
		DOTHRAKI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.carpet, 13), 8, 15).setRewardFactor(0.75f));
		DOTHRAKI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.carpet, 14), 8, 15).setRewardFactor(0.75f));
		DOTHRAKI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.carpet, 3), 8, 15).setRewardFactor(0.75f));
		DOTHRAKI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.carpet, 4), 8, 15).setRewardFactor(0.75f));
		DOTHRAKI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.carpet, 4), 8, 15).setRewardFactor(0.75f));
		DOTHRAKI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.carpet, 5), 8, 15).setRewardFactor(0.75f));
		DOTHRAKI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.date), 8, 15).setRewardFactor(2.0f));
		DOTHRAKI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.lemon), 4, 12).setRewardFactor(2.0f));
		DOTHRAKI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.lime), 4, 12).setRewardFactor(2.0f));
		DOTHRAKI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.orange), 4, 12).setRewardFactor(2.0f));
		DOTHRAKI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.plum), 4, 12).setRewardFactor(2.0f));
		DOTHRAKI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.bottlePoison), 2, 4).setRewardFactor(5.0f));
		DOTHRAKI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.dothrakiChestplate), 1, 2).setRewardFactor(8.0f));
		DOTHRAKI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.dothrakiHelmet), 1, 2).setRewardFactor(8.0f));
		DOTHRAKI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.nomadSword), 1, 2).setRewardFactor(8.0f));
		DOTHRAKI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugLemonLiqueur), 2, 6).setRewardFactor(4.0f));
		DOTHRAKI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugOrangeJuice), 2, 6).setRewardFactor(4.0f));
		DOTHRAKI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.dothrakiBoots), 1, 2).setRewardFactor(8.0f));
		DOTHRAKI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.dothrakiLeggings), 1, 2).setRewardFactor(8.0f));
		DOTHRAKI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.nomadBattleaxe), 1, 2).setRewardFactor(8.0f));
		DOTHRAKI.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.DOTHRAKI), 10, 30));
		DOTHRAKI.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.DOTHRAKI), 30, 50));
		DOTHRAKI.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.DOTHRAKI), 50, 70));
		DOTHRAKI.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.DOTHRAKI), 70, 100));

		HILLMEN.addQuest(new GOTMQBounty.QFBounty("bounty"));
		HILLMEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.cobblestone), 30, 80).setRewardFactor(0.25f));
		HILLMEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.log, 1, 0), 30, 80).setRewardFactor(0.25f));
		HILLMEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.log, 1, 1), 30, 80).setRewardFactor(0.25f));
		HILLMEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.deerCooked), 3, 8).setRewardFactor(2.0f));
		HILLMEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.hillmenBoots), 1, 3).setRewardFactor(10.0f));
		HILLMEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.hillmenChestplate), 1, 3).setRewardFactor(10.0f));
		HILLMEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.hillmenHelmet), 1, 3).setRewardFactor(10.0f));
		HILLMEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.hillmenLeggings), 1, 3).setRewardFactor(10.0f));
		HILLMEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugAle), 3, 10).setRewardFactor(2.0f));
		HILLMEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugCider), 3, 10).setRewardFactor(2.0f));
		HILLMEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugMead), 3, 10).setRewardFactor(2.0f));
		HILLMEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugRum), 3, 10).setRewardFactor(2.0f));
		HILLMEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.muttonCooked), 3, 8).setRewardFactor(2.0f));
		HILLMEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.rabbitCooked), 3, 12).setRewardFactor(2.0f));
		HILLMEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.bread), 5, 15).setRewardFactor(1.0f));
		HILLMEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.coal), 10, 30).setRewardFactor(0.5f));
		HILLMEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.cooked_beef), 3, 8).setRewardFactor(2.0f));
		HILLMEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.cooked_chicken), 3, 8).setRewardFactor(2.0f));
		HILLMEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.cooked_porkchop), 3, 8).setRewardFactor(2.0f));
		HILLMEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.iron_ingot), 3, 10).setRewardFactor(1.5f));
		HILLMEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.leather), 10, 30).setRewardFactor(0.5f));
		HILLMEN.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.MOSSOVY), 10, 30));
		HILLMEN.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.MOSSOVY), 30, 50));
		HILLMEN.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.MOSSOVY), 50, 70));
		HILLMEN.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.MOSSOVY), 70, 100));

		IBBEN.addQuest(new GOTMQBounty.QFBounty("bounty"));
		IBBEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.ibbenBoots), 1, 4).setRewardFactor(3.0f));
		IBBEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.ibbenChestplate), 1, 4).setRewardFactor(3.0f));
		IBBEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.ibbenHarpoon), 1, 4).setRewardFactor(3.0f));
		IBBEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.ibbenLeggings), 1, 4).setRewardFactor(3.0f));
		IBBEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.ibbenSword), 1, 4).setRewardFactor(3.0f));
		IBBEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.iron_ingot), 3, 8).setRewardFactor(2.0f));
		IBBEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.deerCooked), 3, 8).setRewardFactor(2.0f));
		IBBEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.muttonCooked), 3, 8).setRewardFactor(2.0f));
		IBBEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.rabbitCooked), 3, 12).setRewardFactor(2.0f));
		IBBEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.bread), 5, 15).setRewardFactor(1.0f));
		IBBEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.cooked_beef), 3, 8).setRewardFactor(2.0f));
		IBBEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.cooked_chicken), 3, 8).setRewardFactor(2.0f));
		IBBEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.cooked_porkchop), 3, 8).setRewardFactor(2.0f));
		IBBEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugMead), 3, 20).setRewardFactor(1.0f));
		IBBEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.cobblestone), 30, 80).setRewardFactor(0.25f));
		IBBEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.log, 1, 0), 20, 60).setRewardFactor(0.25f));
		IBBEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.log, 1, 1), 20, 60).setRewardFactor(0.25f));
		IBBEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.planks, 1, 0), 80, 160).setRewardFactor(0.125f));
		IBBEN.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.planks, 1, 1), 80, 160).setRewardFactor(0.125f));
		IBBEN.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.IBBEN), 10, 30));
		IBBEN.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.IBBEN), 30, 50));
		IBBEN.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.IBBEN), 50, 70));
		IBBEN.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.IBBEN), 70, 100));

		JOGOS.addQuest(new GOTMQBounty.QFBounty("bounty"));
		JOGOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.carpet, 1), 8, 15).setRewardFactor(0.75f));
		JOGOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.carpet, 10), 8, 15).setRewardFactor(0.75f));
		JOGOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.carpet, 11), 8, 15).setRewardFactor(0.75f));
		JOGOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.carpet, 13), 8, 15).setRewardFactor(0.75f));
		JOGOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.carpet, 14), 8, 15).setRewardFactor(0.75f));
		JOGOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.carpet, 3), 8, 15).setRewardFactor(0.75f));
		JOGOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.carpet, 4), 8, 15).setRewardFactor(0.75f));
		JOGOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.carpet, 4), 8, 15).setRewardFactor(0.75f));
		JOGOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.carpet, 5), 8, 15).setRewardFactor(0.75f));
		JOGOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.date), 8, 15).setRewardFactor(2.0f));
		JOGOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.lemon), 4, 12).setRewardFactor(2.0f));
		JOGOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.lime), 4, 12).setRewardFactor(2.0f));
		JOGOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.orange), 4, 12).setRewardFactor(2.0f));
		JOGOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.plum), 4, 12).setRewardFactor(2.0f));
		JOGOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.bottlePoison), 2, 4).setRewardFactor(5.0f));
		JOGOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.jogosChestplate), 1, 2).setRewardFactor(8.0f));
		JOGOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.jogosHelmet), 1, 2).setRewardFactor(8.0f));
		JOGOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.nomadSword), 1, 2).setRewardFactor(8.0f));
		JOGOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugLemonLiqueur), 2, 6).setRewardFactor(4.0f));
		JOGOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugOrangeJuice), 2, 6).setRewardFactor(4.0f));
		JOGOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.jogosBoots), 1, 2).setRewardFactor(8.0f));
		JOGOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.jogosLeggings), 1, 2).setRewardFactor(8.0f));
		JOGOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.nomadBow), 1, 2).setRewardFactor(8.0f));
		JOGOS.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.JOGOS), 10, 30));
		JOGOS.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.JOGOS), 30, 50));
		JOGOS.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.JOGOS), 50, 70));
		JOGOS.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.JOGOS), 70, 100));

		LHAZAR.addQuest(new GOTMQBounty.QFBounty("bounty"));
		LHAZAR.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.date), 8, 15).setRewardFactor(2.0f));
		LHAZAR.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.lhazarClub), 2, 3).setRewardFactor(6.0f));
		LHAZAR.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.lhazarSpear), 2, 3).setRewardFactor(4.0f));
		LHAZAR.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.lhazarSword), 2, 3).setRewardFactor(5.0f));
		LHAZAR.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.bottlePoison), 2, 4).setRewardFactor(5.0f));
		LHAZAR.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.lhazarDagger), 1, 2).setRewardFactor(20.0f));
		LHAZAR.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugLemonLiqueur), 2, 6).setRewardFactor(4.0f));
		LHAZAR.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.lhazarBoots), 1, 1).setRewardFactor(8.0f));
		LHAZAR.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.lhazarChestplate), 1, 1).setRewardFactor(8.0f));
		LHAZAR.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.lhazarHelmet), 1, 1).setRewardFactor(8.0f));
		LHAZAR.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.lhazarLeggings), 1, 1).setRewardFactor(8.0f));
		LHAZAR.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugOrangeJuice), 2, 6).setRewardFactor(4.0f));
		LHAZAR.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.sandstone, 1, 0), 30, 80).setRewardFactor(0.25f));
		LHAZAR.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.boneBlock, 1, 0), 5, 10).setRewardFactor(2.0f));
		LHAZAR.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.brick1, 1, 15), 30, 60).setRewardFactor(0.5f));
		LHAZAR.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.brick3, 1, 13), 30, 60).setRewardFactor(0.5f));
		LHAZAR.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.planks3, 1, 3), 60, 120).setRewardFactor(0.125f));
		LHAZAR.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.planks3, 1, 4), 60, 120).setRewardFactor(0.125f));
		LHAZAR.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.thatch, 1, 1), 10, 20).setRewardFactor(0.5f));
		LHAZAR.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.wood8, 1, 3), 30, 60).setRewardFactor(0.25f));
		LHAZAR.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.wood9, 1, 0), 30, 60).setRewardFactor(0.25f));
		LHAZAR.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.LHAZAR), 10, 30));
		LHAZAR.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.LHAZAR), 30, 50));
		LHAZAR.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.LHAZAR), 50, 70));
		LHAZAR.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.LHAZAR), 70, 100));

		MOSSOVY.addQuest(new GOTMQBounty.QFBounty("bounty"));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.bucket), 1, 4).setRewardFactor(3.0f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugAle), 1, 6).setRewardFactor(3.0f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugCherryLiqueur), 1, 6).setRewardFactor(3.0f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugCider), 1, 6).setRewardFactor(3.0f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugMead), 1, 6).setRewardFactor(4.0f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugPerry), 1, 6).setRewardFactor(3.0f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.appleCrumbleItem), 2, 5).setRewardFactor(3.0f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.appleGreen), 3, 12).setRewardFactor(1.5f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.cherryPieItem), 2, 5).setRewardFactor(3.0f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.deerCooked), 5, 20).setRewardFactor(0.75f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugAle), 5, 15).setRewardFactor(1.0f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugCider), 5, 15).setRewardFactor(1.0f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.muttonCooked), 5, 20).setRewardFactor(0.75f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.pear), 3, 12).setRewardFactor(1.5f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.rabbitCooked), 3, 15).setRewardFactor(1.0f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.rabbitStew), 3, 8).setRewardFactor(2.0f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.apple), 3, 12).setRewardFactor(1.5f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.baked_potato), 10, 30).setRewardFactor(0.5f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.bread), 10, 30).setRewardFactor(0.5f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.cooked_beef), 5, 20).setRewardFactor(0.75f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.cooked_chicken), 5, 20).setRewardFactor(0.75f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.cooked_porkchop), 5, 20).setRewardFactor(0.75f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.log, 1, 0), 10, 30).setRewardFactor(0.5f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.wood2, 1, 1), 10, 30).setRewardFactor(0.5f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.wood3, 1, 0), 10, 30).setRewardFactor(0.5f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.wood4, 1, 0), 10, 30).setRewardFactor(0.5f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.wood6, 1, 1), 10, 30).setRewardFactor(0.5f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mossovyBoots), 1, 2).setRewardFactor(8.0f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mossovyChestplate), 1, 2).setRewardFactor(8.0f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mossovyLeggings), 1, 2).setRewardFactor(8.0f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mossovySword), 1, 2).setRewardFactor(8.0f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.bronzeAxe), 1, 3).setRewardFactor(4.0f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.bronzeHoe), 1, 3).setRewardFactor(4.0f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.bronzeShovel), 1, 3).setRewardFactor(4.0f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.chisel), 1, 3).setRewardFactor(4.0f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.bucket), 1, 4).setRewardFactor(3.0f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.iron_axe), 1, 3).setRewardFactor(4.0f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.iron_hoe), 1, 3).setRewardFactor(4.0f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.iron_shovel), 1, 3).setRewardFactor(4.0f));
		MOSSOVY.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.pipeweed), 20, 40).setRewardFactor(0.25f));
		MOSSOVY.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.MOSSOVY), 10, 30));
		MOSSOVY.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.MOSSOVY), 30, 50));
		MOSSOVY.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.MOSSOVY), 50, 70));
		MOSSOVY.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.MOSSOVY), 70, 100));

		SOTHORYOS.addQuest(new GOTMQBounty.QFBounty("bounty"));
		SOTHORYOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.banana), 4, 6).setRewardFactor(4.0f));
		SOTHORYOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.bananaBread), 5, 8).setRewardFactor(2.0f));
		SOTHORYOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.banner, 1, GOTItemBanner.BannerType.SOTHORYOS.bannerID), 5, 15).setRewardFactor(1.5f));
		SOTHORYOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.corn), 6, 12).setRewardFactor(1.5f));
		SOTHORYOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.cornBread), 5, 8).setRewardFactor(2.0f));
		SOTHORYOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.cornCooked), 5, 10).setRewardFactor(2.0f));
		SOTHORYOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.dart), 20, 40).setRewardFactor(0.5f));
		SOTHORYOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.dartPoisoned), 10, 20).setRewardFactor(1.0f));
		SOTHORYOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mango), 4, 6).setRewardFactor(4.0f));
		SOTHORYOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.melonSoup), 3, 8).setRewardFactor(2.0f));
		SOTHORYOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.obsidianShard), 10, 30).setRewardFactor(0.75f));
		SOTHORYOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.sothoryosAmulet), 1, 4).setRewardFactor(20.0f));
		SOTHORYOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.sothoryosAxe), 1, 4).setRewardFactor(5.0f));
		SOTHORYOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.sothoryosBattleaxe), 1, 4).setRewardFactor(5.0f));
		SOTHORYOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.sothoryosDagger), 1, 4).setRewardFactor(4.0f));
		SOTHORYOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.sothoryosDaggerPoisoned), 1, 3).setRewardFactor(6.0f));
		SOTHORYOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.sothoryosHammer), 1, 4).setRewardFactor(5.0f));
		SOTHORYOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.sothoryosPike), 1, 4).setRewardFactor(5.0f));
		SOTHORYOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.sothoryosSpear), 1, 4).setRewardFactor(5.0f));
		SOTHORYOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.sothoryosSword), 1, 4).setRewardFactor(5.0f));
		SOTHORYOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.bread), 5, 8).setRewardFactor(2.0f));
		SOTHORYOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.dye, 1, 3), 8, 20).setRewardFactor(1.0f));
		SOTHORYOS.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.melon), 10, 20).setRewardFactor(0.75f));
		SOTHORYOS.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.SOTHORYOS), 10, 30));
		SOTHORYOS.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.SOTHORYOS), 30, 50));
		SOTHORYOS.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.SOTHORYOS), 50, 70));
		SOTHORYOS.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.SOTHORYOS), 70, 100));

		SUMMER.addQuest(new GOTMQBounty.QFBounty("bounty"));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.water_bucket), 3, 5).setRewardFactor(5.0f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.log, 1, 0), 20, 60).setRewardFactor(0.25f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.planks, 1, 0), 80, 160).setRewardFactor(0.125f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.sandstone, 1, 0), 30, 80).setRewardFactor(0.25f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.sandstone, 1, 1), 15, 40).setRewardFactor(0.5f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.planks2, 1, 2), 80, 160).setRewardFactor(0.125f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.thatch, 1, 1), 20, 40).setRewardFactor(0.5f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.wood4, 1, 2), 20, 60).setRewardFactor(0.25f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.rock, 1, 0), 30, 50).setRewardFactor(0.5f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.date), 8, 15).setRewardFactor(2.0f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.lemon), 4, 12).setRewardFactor(2.0f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.lime), 4, 12).setRewardFactor(2.0f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.orange), 4, 12).setRewardFactor(2.0f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.plum), 4, 12).setRewardFactor(2.0f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.deerCooked), 5, 12).setRewardFactor(1.5f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.lemon), 4, 8).setRewardFactor(2.0f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.orange), 4, 8).setRewardFactor(2.5f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.rabbitCooked), 5, 12).setRewardFactor(1.5f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.bread), 5, 8).setRewardFactor(2.0f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.bottlePoison), 2, 4).setRewardFactor(5.0f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.arrow), 20, 40).setRewardFactor(0.5f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.doubleFlower, 1, 3), 5, 15).setRewardFactor(1.5f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.lionFur), 3, 6).setRewardFactor(3.0f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.olive), 10, 20).setRewardFactor(1.0f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.dye, 1, 4), 3, 8).setRewardFactor(3.0f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugLemonLiqueur), 2, 6).setRewardFactor(4.0f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugOrangeJuice), 2, 6).setRewardFactor(4.0f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.banana), 2, 4).setRewardFactor(4.0f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.lemon), 4, 8).setRewardFactor(2.0f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.lionCooked), 3, 6).setRewardFactor(3.0f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mango), 2, 4).setRewardFactor(4.0f));
		SUMMER.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.orange), 4, 8).setRewardFactor(2.0f));
		SUMMER.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.SUMMER_ISLANDS), 10, 30));
		SUMMER.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.SUMMER_ISLANDS), 30, 50));
		SUMMER.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.SUMMER_ISLANDS), 50, 70));
		SUMMER.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.SUMMER_ISLANDS), 70, 100));

		YI_TI.addQuest(new GOTMQBounty.QFBounty("bounty"));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.date), 4, 12).setRewardFactor(2.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.pomegranate), 4, 12).setRewardFactor(2.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.sapling8, 1, 1), 3, 5).setRewardFactor(3.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.yitiFlower, 1, 0), 2, 5).setRewardFactor(2.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.yitiFlower, 1, 1), 2, 5).setRewardFactor(2.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.yitiFlower, 1, 2), 2, 5).setRewardFactor(2.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.yitiFlower, 1, 3), 2, 5).setRewardFactor(2.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.yitiFlower, 1, 4), 2, 5).setRewardFactor(2.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.deerCooked), 2, 8).setRewardFactor(2.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.deerRaw), 5, 10).setRewardFactor(2.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugRedWine), 3, 8).setRewardFactor(3.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.mugWhiteWine), 3, 8).setRewardFactor(3.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.muttonCooked), 2, 8).setRewardFactor(2.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.oliveBread), 5, 8).setRewardFactor(2.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.rabbitRaw), 5, 10).setRewardFactor(2.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.raisins), 6, 12).setRewardFactor(1.5f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.whiteBisonHorn), 1, 2).setRewardFactor(10.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.beef), 5, 10).setRewardFactor(2.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.bread), 5, 8).setRewardFactor(2.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.cooked_beef), 2, 8).setRewardFactor(2.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.cooked_fished), 2, 8).setRewardFactor(2.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.feather), 8, 16).setRewardFactor(1.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Items.leather), 8, 16).setRewardFactor(1.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.bottlePoison), 2, 4).setRewardFactor(5.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.sulfur), 4, 8).setRewardFactor(2.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.yitiBoots), 2, 4).setRewardFactor(4.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.yitiChestplate), 2, 4).setRewardFactor(4.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.yitiHelmet), 2, 4).setRewardFactor(4.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.yitiLeggings), 2, 4).setRewardFactor(4.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.yitiSpear), 3, 5).setRewardFactor(3.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.yitiSteelIngot), 5, 10).setRewardFactor(2.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.yitiSword), 3, 5).setRewardFactor(3.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.cobblestone, 1, 0), 40, 100).setRewardFactor(0.25f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(Blocks.log, 1, 0), 30, 60).setRewardFactor(0.25f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.brick5, 1, 11), 40, 100).setRewardFactor(0.2f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.saltpeter), 4, 8).setRewardFactor(2.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.sulfur), 4, 8).setRewardFactor(2.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.whiteBisonHorn), 1, 1).setRewardFactor(30.0f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.wood6, 1, 2), 30, 60).setRewardFactor(0.25f));
		YI_TI.addQuest(new GOTMQCollect.QFCollect("collect").setCollectItem(new ItemStack(GOTRegistry.wood8, 1, 1), 30, 60).setRewardFactor(0.25f));
		YI_TI.addQuest(new GOTMQKillEntity.QFKillEntity("kill").setKillEntity(GOTEntityWesterosBandit.class, 1, 3).setRewardFactor(8.0f));
		YI_TI.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.YI_TI), 10, 30));
		YI_TI.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.YI_TI), 30, 50));
		YI_TI.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.YI_TI), 50, 70));
		YI_TI.addQuest(new GOTMQKillFaction.QFKillFaction("kill").setKillFaction(getRandomEnemy(GOTFaction.YI_TI), 70, 100));
	}

	public static void registerQuestClass(Class<? extends GOTMQ> questClass, int weight) {
		questClassWeights.put(questClass, weight);
	}
}