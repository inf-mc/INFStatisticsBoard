package info.infinf.statisticsboard.scoreboard;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.ScoreboardCriterion;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.text.Text;

public abstract class PlacementBoard {
	private static ScoreboardObjective scoreboardObj;
	public static final String NAME = "INFPlacementBoard";
	public static final String DISPLAY_NAME = "放置榜";

	public static void init(Scoreboard sb) {
		if ((scoreboardObj = sb.getObjective(NAME)) == null) {
			scoreboardObj = sb.addObjective(
					NAME,
					ScoreboardCriterion.DUMMY,
					Text.literal(DISPLAY_NAME),
					ScoreboardCriterion.RenderType.INTEGER);
		}
	}

	public static ScoreboardObjective getScoreboardObj() {
		return scoreboardObj;
	}
}
