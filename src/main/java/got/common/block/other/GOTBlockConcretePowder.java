package got.common.block.other;

import java.util.Random;

import got.common.database.*;
import got.common.entity.other.GOTEntityFallingConcrete;
import got.common.util.GOTEnumDyeColor;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.world.World;

public class GOTBlockConcretePowder extends BlockFalling {
	public GOTEnumDyeColor color;

	public GOTBlockConcretePowder(GOTEnumDyeColor color) {
		this.color = color;
		setBlockName("concrete_powder_" + this.color);
		setStepSound(soundTypeSand);
		setCreativeTab(GOTCreativeTabs.tabBlock);
		setHardness(0.5f);
		setBlockTextureName("got:concrete_powder_" + this.color.getName());
	}

	public void func_149830_m(World p_149830_1_, int p_149830_2_, int p_149830_3_, int p_149830_4_) {
		if (BlockFalling.func_149831_e(p_149830_1_, p_149830_2_, p_149830_3_ - 1, p_149830_4_) && p_149830_3_ >= 0) {
			int b0 = 32;
			if (!fallInstantly && p_149830_1_.checkChunksExist(p_149830_2_ - b0, p_149830_3_ - b0, p_149830_4_ - b0, p_149830_2_ + b0, p_149830_3_ + b0, p_149830_4_ + b0)) {
				if (!p_149830_1_.isRemote) {
					GOTEntityFallingConcrete entityfallingblock = new GOTEntityFallingConcrete(p_149830_1_, p_149830_2_ + 0.5f, p_149830_3_ + 0.5f, p_149830_4_ + 0.5f, this, p_149830_1_.getBlockMetadata(p_149830_2_, p_149830_3_, p_149830_4_));
					func_149829_a(entityfallingblock);
					p_149830_1_.spawnEntityInWorld(entityfallingblock);
				}
			} else {
				p_149830_1_.setBlockToAir(p_149830_2_, p_149830_3_, p_149830_4_);
				while (BlockFalling.func_149831_e(p_149830_1_, p_149830_2_, p_149830_3_ - 1, p_149830_4_) && p_149830_3_ > 0) {
					--p_149830_3_;
				}
				if (p_149830_3_ > 0) {
					p_149830_1_.setBlock(p_149830_2_, p_149830_3_, p_149830_4_, this);
				}
			}
		}
	}

	public GOTEnumDyeColor getColor() {
		return color;
	}

	@Override
	public void onBlockAdded(World world, int x, int y, int z) {
		if (world.getBlock(x + 1, y, z).getMaterial() == Material.water || world.getBlock(x - 1, y, z).getMaterial() == Material.water || world.getBlock(x, y + 1, z).getMaterial() == Material.water || world.getBlock(x, y - 1, z).getMaterial() == Material.water || world.getBlock(x, y, z + 1).getMaterial() == Material.water || world.getBlock(x, y, z - 1).getMaterial() == Material.water) {
			world.setBlock(x, y, z, getConcreteFromColor(color));
		} else {
			world.scheduleBlockUpdate(x, y, z, this, tickRate(world));
		}
	}

	@Override
	public void onNeighborBlockChange(World world, int x, int y, int z, Block block) {
		if (world.getBlock(x + 1, y, z).getMaterial() == Material.water || world.getBlock(x - 1, y, z).getMaterial() == Material.water || world.getBlock(x, y + 1, z).getMaterial() == Material.water || world.getBlock(x, y - 1, z).getMaterial() == Material.water || world.getBlock(x, y, z + 1).getMaterial() == Material.water || world.getBlock(x, y, z - 1).getMaterial() == Material.water) {
			world.setBlock(x, y, z, getConcreteFromColor(color));
		} else {
			world.scheduleBlockUpdate(x, y, z, this, tickRate(world));
		}
	}

	@Override
	public void updateTick(World worldIn, int x, int y, int z, Random rand) {
		if (!worldIn.isRemote) {
			func_149830_m(worldIn, x, y, z);
		}
	}

	public static boolean canFallThrough(World worldIn, int x, int y, int z) {
		Block block = worldIn.getBlock(x, y, z);
		Material material = block.getMaterial();
		return block == Blocks.fire || material == Material.air || material == Material.water || material == Material.lava;
	}

	public static GOTBlockConcrete getConcreteFromColor(GOTEnumDyeColor dye) {
		return GOTRegistry.concrete.get(dye);
	}
}
