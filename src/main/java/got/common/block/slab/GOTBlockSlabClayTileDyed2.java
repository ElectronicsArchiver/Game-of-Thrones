package got.common.block.slab;

import cpw.mods.fml.relauncher.*;
import got.common.database.*;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;

public class GOTBlockSlabClayTileDyed2 extends GOTBlockSlabBase {
	public GOTBlockSlabClayTileDyed2(boolean flag) {
		super(flag, Material.rock, 8);
		setCreativeTab(GOTCreativeTabs.tabBlock);
		setHardness(1.25f);
		setResistance(7.0f);
		setStepSound(Block.soundTypeStone);
	}

	@SideOnly(value = Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		j &= 7;
		return GOTRegistry.clayTileDyed.getIcon(i, j += 8);
	}

	@SideOnly(value = Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconregister) {
	}
}
