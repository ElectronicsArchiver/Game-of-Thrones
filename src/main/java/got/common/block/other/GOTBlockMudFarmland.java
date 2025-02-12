package got.common.block.other;

import java.util.Random;

import cpw.mods.fml.relauncher.*;
import got.common.database.GOTRegistry;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import net.minecraft.world.*;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

public class GOTBlockMudFarmland extends BlockFarmland {
	public GOTBlockMudFarmland() {
		setHardness(0.6f);
		setStepSound(Block.soundTypeGravel);
	}

	@Override
	public boolean canSustainPlant(IBlockAccess world, int i, int j, int k, ForgeDirection direction, IPlantable plantable) {
		return Blocks.farmland.canSustainPlant(world, i, j, k, direction, plantable);
	}

	@SideOnly(value = Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		if (i == 1) {
			return super.getIcon(i, j);
		}
		return GOTRegistry.mud.getBlockTextureFromSide(i);
	}

	@SideOnly(value = Side.CLIENT)
	@Override
	public Item getItem(World world, int i, int j, int k) {
		return Item.getItemFromBlock(GOTRegistry.mud);
	}

	@Override
	public Item getItemDropped(int i, Random random, int j) {
		return GOTRegistry.mud.getItemDropped(0, random, j);
	}

	@Override
	public boolean isFertile(World world, int i, int j, int k) {
		return Blocks.farmland.isFertile(world, i, j, k);
	}

	@Override
	public void onFallenUpon(World world, int i, int j, int k, Entity entity, float f) {
		super.onFallenUpon(world, i, j, k, entity, f);
		if (world.getBlock(i, j, k) == Blocks.dirt) {
			world.setBlock(i, j, k, GOTRegistry.mud);
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int i, int j, int k, Block block) {
		super.onNeighborBlockChange(world, i, j, k, block);
		if (world.getBlock(i, j, k) == Blocks.dirt) {
			world.setBlock(i, j, k, GOTRegistry.mud);
		}
	}

	@Override
	public void onPlantGrow(World world, int i, int j, int k, int sourceX, int sourceY, int sourceZ) {
		world.setBlock(i, j, k, GOTRegistry.mud, 0, 2);
	}

	@Override
	public void updateTick(World world, int i, int j, int k, Random random) {
		super.updateTick(world, i, j, k, random);
		if (world.getBlock(i, j, k) == Blocks.dirt) {
			world.setBlock(i, j, k, GOTRegistry.mud);
		}
	}
}
