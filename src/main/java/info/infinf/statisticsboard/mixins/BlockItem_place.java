package info.infinf.statisticsboard.mixins;

import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import info.infinf.statisticsboard.Config;
import info.infinf.statisticsboard.scoreboard.PlacementBoard;

@Mixin(BlockItem.class)
public abstract class BlockItem_place {
	@Inject(
		method = "place(Lnet/minecraft/item/ItemPlacementContext;)Lnet/minecraft/util/ActionResult;",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/advancement/criterion/PlacedBlockCriterion;trigger(Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/item/ItemStack;)V")
	)
	private void increasePlacementCount(
			ItemPlacementContext ctx, CallbackInfoReturnable cir) {
		var pl = ctx.getPlayer();
		if (Config.shouldNotCount(pl)) {
			return;
		}
		var pos = ctx.getBlockPos();
		var world = ctx.getWorld();
		if (Config.getDefaultPlacementAreaType()) {
			if (!Config.getPlacementAreaWhiteList().contains(pos, world.getRegistryKey()) ||
					Config.getPlacementAreaBlackList().contains(pos, world.getRegistryKey())) {
				return;
			}
		} else {
			if (Config.getPlacementAreaBlackList().contains(pos, world.getRegistryKey()) &&
					!Config.getPlacementAreaWhiteList().contains(pos, world.getRegistryKey())) {
				return;
			}
		}
		var pc = pl.getScoreboard().getPlayerScore(
			pl.getEntityName(), PlacementBoard.getScoreboardObj());
		pc.setScore(pc.getScore() + 1);
	}
}
