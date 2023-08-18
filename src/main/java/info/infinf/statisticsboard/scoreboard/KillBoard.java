package info.infinf.statisticsboard.scoreboard;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;

public final class KillBoard {
	private static ScoreboardObjective scoreboardObj;
	public static final String NAME = "INFKillBoard";
	public static final String DISPLAY_NAME = "击杀榜";

	public static void init(Scoreboard sb) {
		if ((scoreboardObj = sb.getObjective(NAME)) == null) {
			scoreboardObj = sb.addObjective(
					NAME,
					ScoreboardCriterion.TOTAL_KILL_COUNT,
					Text.literal(DISPLAY_NAME),
					ScoreboardCriterion.RenderType.INTEGER);
		}
	}
}
