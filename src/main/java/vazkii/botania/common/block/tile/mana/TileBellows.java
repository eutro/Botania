/*
 * This class is distributed as part of the Botania Mod.
 * Get the Source Code in github:
 * https://github.com/Vazkii/Botania
 *
 * Botania is Open Source and distributed under the
 * Botania License: http://botaniamod.net/license.php
 */
package vazkii.botania.common.block.tile.mana;

import com.mojang.datafixers.util.Pair;

import net.minecraft.block.FurnaceBlock;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.tileentity.FurnaceTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraftforge.registries.ObjectHolder;

import vazkii.botania.api.internal.VanillaPacketDispatcher;
import vazkii.botania.common.Botania;
import vazkii.botania.common.block.tile.TileMod;
import vazkii.botania.common.core.handler.ExoflameFurnaceHandler;
import vazkii.botania.common.core.handler.ModSounds;
import vazkii.botania.common.lib.LibBlockNames;
import vazkii.botania.common.lib.LibMisc;

import javax.annotation.Nullable;

public class TileBellows extends TileMod implements ITickableTileEntity {
	@ObjectHolder(LibMisc.MOD_ID + ":" + LibBlockNames.BELLOWS) public static TileEntityType<TileBellows> TYPE;
	private static final String TAG_ACTIVE = "active";

	public float movePos;
	public boolean active = false;
	public float moving = 0F;

	public TileBellows() {
		super(TYPE);
	}

	public void interact() {
		if (moving == 0F) {
			setActive(true);
		}
	}

	@Override
	public void tick() {
		boolean disable = true;
		TileEntity tile = getLinkedTile();
		if (!active && tile instanceof TilePool) {
			TilePool pool = (TilePool) tile;
			boolean transfer = pool.isDoingTransfer;
			if (transfer) {
				if (pool.ticksDoingTransfer > 0) {
					setActive(true);
				}
				disable = false;
			}
		}

		float max = 0.9F;
		float min = 0F;

		float incr = max / 20F;

		if (movePos < max && active && moving >= 0F) {
			if (moving == 0F) {
				world.playSound(null, pos, ModSounds.bellows, SoundCategory.BLOCKS, 0.1F, 3F);
			}

			if (tile instanceof AbstractFurnaceTileEntity) {
				AbstractFurnaceTileEntity furnace = (AbstractFurnaceTileEntity) tile;
				Pair<AbstractCookingRecipe, Boolean> p = canSmelt(furnace);
				if (p != null) {
					AbstractCookingRecipe recipe = p.getFirst();
					boolean canSmelt = p.getSecond();
					if (canSmelt) {
						furnace.cookTime = Math.min(recipe.getCookTime() - 1, furnace.cookTime + 20);
						furnace.burnTime = Math.max(0, furnace.burnTime - 10);
					}

					if (furnace instanceof FurnaceTileEntity
							&& furnace.hasWorld() && furnace.getBlockState().get(FurnaceBlock.LIT)) {
						// [VanillaCopy] BlockFurnace
						double d0 = (double) pos.getX() + 0.5D;
						double d1 = (double) pos.getY();
						double d2 = (double) pos.getZ() + 0.5D;

						Direction enumfacing = furnace.getBlockState().get(FurnaceBlock.FACING);
						Direction.Axis enumfacing$axis = enumfacing.getAxis();
						double d3 = 0.52D;
						double d4 = world.rand.nextDouble() * 0.6D - 0.3D;
						double d5 = enumfacing$axis == Direction.Axis.X ? (double) enumfacing.getXOffset() * 0.52D : d4;
						double d6 = world.rand.nextDouble() * 6.0D / 16.0D;
						double d7 = enumfacing$axis == Direction.Axis.Z ? (double) enumfacing.getZOffset() * 0.52D : d4;
						world.addParticle(ParticleTypes.SMOKE, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
						world.addParticle(ParticleTypes.FLAME, d0 + d5, d1 + d6, d2 + d7, 0.0D, 0.0D, 0.0D);
					}
				}
			}

			movePos += incr * 3;
			moving = incr * 3;
			if (movePos >= max) {
				movePos = Math.min(max, movePos);
				moving = 0F;
				if (disable) {
					setActive(false);
				}
			}
		} else if (movePos > min) {
			movePos -= incr;
			moving = -incr;
			if (movePos <= min) {
				movePos = Math.max(min, movePos);
				moving = 0F;
			}
		}

	}

	public TileEntity getLinkedTile() {
		Direction side = getBlockState().get(BlockStateProperties.HORIZONTAL_FACING);
		return world.getTileEntity(getPos().offset(side));
	}

	@Override
	public void writePacketNBT(CompoundNBT cmp) {
		cmp.putBoolean(TAG_ACTIVE, active);
	}

	@Override
	public void readPacketNBT(CompoundNBT cmp) {
		active = cmp.getBoolean(TAG_ACTIVE);
	}

	public void setActive(boolean active) {
		if (!world.isRemote) {
			boolean diff = this.active != active;
			this.active = active;
			if (diff) {
				VanillaPacketDispatcher.dispatchTEToNearbyPlayers(world, pos);
			}
		}
	}

	@Nullable
	@SuppressWarnings("unchecked")
	public static Pair<AbstractCookingRecipe, Boolean> canSmelt(AbstractFurnaceTileEntity furnace) {
		IRecipeType<AbstractCookingRecipe> rt;
		AbstractCookingRecipe recipe;
		boolean canSmelt;
		try {
			rt = (IRecipeType<AbstractCookingRecipe>) ExoflameFurnaceHandler.getRecipeType(furnace);
			recipe = furnace.getWorld().getRecipeManager().getRecipe(rt, furnace, furnace.getWorld()).orElse(null);
			canSmelt = ExoflameFurnaceHandler.canSmelt(furnace, recipe);
			return Pair.of(recipe, canSmelt);
		} catch (Throwable e) {
			Botania.LOGGER.error("Failed to reflect furnace", e);
			return null;
		}
	}

}
