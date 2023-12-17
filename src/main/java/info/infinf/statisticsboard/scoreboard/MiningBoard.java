package info.infinf.statisticsboard.scoreboard;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.scoreboard.ScoreboardPlayerScore;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import info.infinf.statisticsboard.Config;

public abstract class MiningBoard {
	private static ScoreboardObjective scoreboardObj;
	public static final String NAME = "INFMiningBoard";
	public static final String DISPLAY_NAME = "挖掘榜";

	public static void init(Scoreboard sb) {
		if ((scoreboardObj = sb.getObjective(NAME)) == null) {
			scoreboardObj = sb.addObjective(
					NAME,
					ScoreboardCriterion.DUMMY,
					Text.literal(DISPLAY_NAME),
					ScoreboardCriterion.RenderType.INTEGER);
		}
	}

	public static void onBreakBlock(
			World world,
			PlayerEntity pl,
			BlockPos pos,
			BlockState blk,
			BlockEntity be) {
		// 防止发生错误，如果这里错误不捕获会导致挖掘不掉落方块
		try {
			if (Config.shouldNotCount(pl)) {
				return;
			}
			if (Config.getDefaultMiningAreaType()) {
				if (!Config.getMiningAreaWhiteList().contains(pos, world.getRegistryKey()) ||
						Config.getMiningAreaBlackList().contains(pos, world.getRegistryKey())) {
					return;
				}
			} else {
				if (Config.getMiningAreaBlackList().contains(pos, world.getRegistryKey()) &&
						!Config.getMiningAreaWhiteList().contains(pos, world.getRegistryKey())) {
					return;
				}
			}
			var pc = pl.getScoreboard()
				.getPlayerScore(pl.getEntityName(), scoreboardObj);
			pc.setScore(pc.getScore() + 1);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
