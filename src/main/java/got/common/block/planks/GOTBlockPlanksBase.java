package got.common.block.planks;

import java.util.List;

import cpw.mods.fml.relauncher.*;
import got.common.database.GOTCreativeTabs;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.*;
import net.minecraft.util.IIcon;

public abstract class GOTBlockPlanksBase extends Block {
	@SideOnly(value = Side.CLIENT)
	public IIcon[] plankIcons;
	public String[] plankTypes;

	public GOTBlockPlanksBase() {
		super(Material.wood);
		setHardness(2.0f);
		setResistance(5.0f);
		setStepSound(Block.soundTypeWood);
		setCreativeTab(GOTCreativeTabs.tabBlock);
	}

	@Override
	public int damageDropped(int i) {
		return i;
	}

	@SideOnly(value = Side.CLIENT)
	@Override
	public IIcon getIcon(int i, int j) {
		if (j >= plankTypes.length) {
			j = 0;
		}
		return plankIcons[j];
	}

	@SideOnly(value = Side.CLIENT)
	@Override
	public void getSubBlocks(Item item, CreativeTabs tab, List list) {
		for (int j = 0; j < plankTypes.length; ++j) {
			list.add(new ItemStack(item, 1, j));
		}
	}

	@SideOnly(value = Side.CLIENT)
	@Override
	public void registerBlockIcons(IIconRegister iconregister) {
		plankIcons = new IIcon[plankTypes.length];
		for (int i = 0; i < plankTypes.length; ++i) {
			plankIcons[i] = iconregister.registerIcon(getTextureName() + "_" + plankTypes[i]);
		}
	}

	public void setPlankTypes(String... types) {
		plankTypes = types;
	}
}
