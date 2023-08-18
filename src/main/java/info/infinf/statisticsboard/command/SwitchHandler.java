package info.infinf.statisticsboard.command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.ClickEvent;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;
import net.minecraft.text.Texts;

public class SwitchHandler {
	public static int sendScoreboards(CommandContext<ServerCommandSource> ctx) {
		ctx.getSource().sendFeedback(Texts.join(
			ctx.getSource().getServer().getScoreboard().getObjectives(),
			Text.literal(" | "),
			sbo -> sbo.getDisplayName().copy().styled(style -> style
				.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
					"/infboard switch " + sbo.getName()))
				.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
					Text.literal("点击切换"))))), false);
		return 1;
	}
}
