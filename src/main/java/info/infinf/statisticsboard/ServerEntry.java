package info.infinf.statisticsboard;

import java.io.IOException;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.server.MinecraftServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.infinf.statisticsboard.Config;
import info.infinf.statisticsboard.command.Command;
import info.infinf.statisticsboard.scoreboard.DeathBoard;
import info.infinf.statisticsboard.scoreboard.KillBoard;
import info.infinf.statisticsboard.scoreboard.MiningBoard;

public final class ServerEntry implements ModInitializer {
	private static final Logger LOGGER = LoggerFactory.getLogger("infboard");
	@Override
	public void onInitialize() {
		ServerLifecycleEvents.SERVER_STARTED.register(this::onServerStarted);
		CommandRegistrationCallback.EVENT.register(Command::init);
		PlayerBlockBreakEvents.AFTER.register(MiningBoard::onBreakBlock);
	}

	private void onServerStarted(MinecraftServer server) {
		Config.init(server);
		var scoreboard = server.getScoreboard();
		// DeathBoard.init(scoreboard);
		// KillBoard.init(scoreboard);
		MiningBoard.init(scoreboard);
	}
}
