/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Botania Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 *
 * File Created @ [Aug 25, 2015, 7:11:35 PM (GMT)]
 */
package vazkii.botania.common.block;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.BannerPattern;
import vazkii.botania.common.item.ModItems;

public final class ModBanners {

	public static void init() {
		Class<? extends Enum<?>> clazz = BannerPattern.class;
		addPattern(clazz, "flower", "flr", new ItemStack(ModItems.livingwoodTwig));
		addPattern(clazz, "lexicon", "lex", new ItemStack(ModItems.lexicon));
		addPattern(clazz, "logo", "lgo", new ItemStack(ModItems.terrasteel));
		addPattern(clazz, "sapling", "spl", new ItemStack(ModItems.dreamwoodTwig));
		addPattern(clazz, "tiny_potato", "tpt", new ItemStack(ModBlocks.tinyPotato));

		addPattern(clazz, "spark_dispersive", "sds", new ItemStack(ModItems.sparkUpgradeDispersive));
		addPattern(clazz, "spark_dominant", "sdm", new ItemStack(ModItems.sparkUpgradeDominant));
		addPattern(clazz, "spark_recessive", "src", new ItemStack(ModItems.sparkUpgradeRecessive));
		addPattern(clazz, "spark_isolated", "sis", new ItemStack(ModItems.sparkUpgradeIsolated));

		addPattern(clazz, "fish", "fis", new ItemStack(Items.COD));
		addPattern(clazz, "axe", "axe", new ItemStack(Items.IRON_AXE));
		addPattern(clazz, "hoe", "hoe", new ItemStack(Items.IRON_HOE));
		addPattern(clazz, "pickaxe", "pik", new ItemStack(Items.IRON_PICKAXE));
		addPattern(clazz, "shovel", "shv", new ItemStack(Items.IRON_SHOVEL));
		addPattern(clazz, "sword", "srd", new ItemStack(Items.IRON_SWORD));
	}

	public static void addPattern(Class<? extends Enum<?>> clazz, String name, String id, ItemStack craftingItem) {
		name = "botania_" + name;
		id = "bt_" + id;
		EnumHelper.addEnum(clazz, name.toUpperCase(), new Class[] { String.class, String.class, ItemStack.class }, name, id, craftingItem);
	}
}
