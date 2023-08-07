package info.infinf.statisticsboard.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.BlockPosArgumentType;
import static net.minecraft.command.argument.BlockPosArgumentType.getBlockPos;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.ScoreboardObjectiveArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.scoreboard.Scoreboard;
import static net.minecraft.server.command.CommandManager.*;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.infinf.statisticsboard.Areas;
import info.infinf.statisticsboard.Config;

public final class Command {
	private static final Logger LOGGER = LoggerFactory.getLogger("infboard");

	public static void init(
			CommandDispatcher<ServerCommandSource> dispatcher,
			CommandRegistryAccess registryAccess,
			RegistrationEnvironment env) {
		dispatcher.register(literal("infboard")
			.then(literal("switch")
				.then(argument("boardname", ScoreboardObjectiveArgumentType.scoreboardObjective())
					.executes(ctx -> {
						try {
							var sbo = ScoreboardObjectiveArgumentType.getObjective(ctx, "boardname");
							sbo.getScoreboard().setObjectiveSlot(1, sbo);
							return 1;
						} catch (CommandSyntaxException e) {
							ctx.getSource().sendFeedback(Text.of("找不到计分板"), false);
							return -1;
						}
					}))
			)
			.then(literal("MiningAreaBlackList")
				.requires(src -> src.hasPermissionLevel(1))
				.then(addAreaHandler(Config.getMiningAreaBlackList()))
				.then(removeAreaHandler(Config.getMiningAreaBlackList()))
				.then(listAreaHandler(Config.getMiningAreaBlackList())))
			.then(literal("MiningAreaWhiteList")
				.requires(src -> src.hasPermissionLevel(1))
				.then(addAreaHandler(Config.getMiningAreaWhiteList()))
				.then(removeAreaHandler(Config.getMiningAreaWhiteList()))
				.then(listAreaHandler(Config.getMiningAreaWhiteList())))
			.then(literal("defaultMiningAreaTypeIsBlackList")
				.executes(ctx -> {
					ctx.getSource().sendFeedback(Text.of(
						Config.getDefaultMiningAreaType() ? "BlackList" : "WhiteList"), false);
					return 1;
				})
				.then(argument("isBlackList", BoolArgumentType.bool())
					.requires(src -> src.hasPermissionLevel(1))
					.executes(ctx -> {
						Config.setDefaultMiningAreaType(BoolArgumentType.getBool(ctx, "isBlackList"));
						return 1;
					})))
		);
	}

	private static LiteralArgumentBuilder<ServerCommandSource>
			addAreaHandler(Areas area) {
		return literal("add")
			.then(argument("dimension", DimensionArgumentType.dimension())
				.then(argument("from", BlockPosArgumentType.blockPos())
					.then(argument("to", BlockPosArgumentType.blockPos())
						.executes(ctx -> {
							if (area.add(
									BlockBox.create(getBlockPos(ctx, "from"), getBlockPos(ctx, "to")),
									DimensionArgumentType.getDimensionArgument(
										ctx, "dimension").getRegistryKey())) {
								area.save();
							} else {
								ctx.getSource().sendFeedback(Text.of("Area already exists"), false);
							}
							return 1;
						}))));
	}

	private static LiteralArgumentBuilder<ServerCommandSource>
			removeAreaHandler(Areas area) {
		return literal("remove")
			.then(argument("dimension", DimensionArgumentType.dimension())
				.then(argument("index", IntegerArgumentType.integer(1))
						.executes(ctx -> {
							if (area.remove(
									IntegerArgumentType.getInteger(ctx, "index"),
									DimensionArgumentType.getDimensionArgument(
										ctx, "dimension").getRegistryKey())) {
								area.save();
								return 1;
							} else {
								ctx.getSource().sendFeedback(Text.of("Index out of bound"), false);
								return -1;
							}
						})));
	}

	private static LiteralArgumentBuilder<ServerCommandSource>
			listAreaHandler(Areas area) {
		return literal("list").executes(ctx -> {
				ctx.getSource().sendFeedback(Text.of(area.toString()), false);
				return 1;
		});
	}
}