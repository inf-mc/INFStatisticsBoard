package info.infinf.statisticsboard.mixins;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.ServerStatHandler;
import net.minecraft.stat.Stats;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import info.infinf.statisticsboard.Config;
import info.infinf.statisticsboard.scoreboard.DeathBoard;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntity_onDeath {
	@Shadow @Final private ServerStatHandler statHandler;

	@Inject(method = "onDeath", at = @At("TAIL"))
	private void increaseDeathCount(
			DamageSource damageSource, CallbackInfo ci) {
		var pl = (ServerPlayerEntity)(Object)this;
		if (Config.shouldNotCount(pl)) {
			return;
		}
		var pc = pl.getScoreboard()
			.getPlayerScore(pl.getEntityName(), DeathBoard.getScoreboardObj());
		pc.setScore(this.statHandler.getStat(Stats.CUSTOM.getOrCreateStat(Stats.DEATHS)));
	}
}
