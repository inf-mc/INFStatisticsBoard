package info.infinf.statisticsboard.command;

import java.util.function.Supplier;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.BlockPosArgumentType;
import net.minecraft.command.argument.DimensionArgumentType;
import net.minecraft.command.argument.ScoreboardObjectiveArgumentType;
import net.minecraft.command.argument.TextArgumentType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.infinf.statisticsboard.Areas;
import info.infinf.statisticsboard.command.SwitchHandler;
import info.infinf.statisticsboard.Config;

import static net.minecraft.server.command.CommandManager.*;
import static net.minecraft.command.argument.BlockPosArgumentType.getBlockPos;

public abstract class Command {
	private static final Logger LOGGER = LoggerFactory.getLogger("infboard");

	public static void init(
			CommandDispatcher<ServerCommandSource> dispatcher,
			CommandRegistryAccess registryAccess,
			RegistrationEnvironment env) {
		dispatcher.register(literal("infboard")
			.then(literal("switch")
				.executes(SwitchHandler::sendScoreboards)
				.then(argument("boardname", ScoreboardObjectiveArgumentType.scoreboardObjective())
					.executes(ctx -> {
						var sbo = ScoreboardObjectiveArgumentType.getObjective(ctx, "boardname");
						sbo.getScoreboard().setObjectiveSlot(1, sbo);
						return 1;
					})))
			.then(literal("setDisplayName")
				.requires(src -> src.hasPermissionLevel(1))
				.then(argument("boardname", ScoreboardObjectiveArgumentType.scoreboardObjective())
					.then(argument("displayName", TextArgumentType.text())
						.executes(ctx -> {
							ScoreboardObjectiveArgumentType.getObjective(ctx, "boardname")
								.setDisplayName(TextArgumentType.getTextArgument(ctx, "displayName"));
							return 1;
						}))))
			.then(literal("miningAreaBlackList")
				.then(addAreaHandler(Config::getMiningAreaBlackList))
				.then(removeAreaHandler(Config::getMiningAreaBlackList))
				.then(listAreaHandler(Config::getMiningAreaBlackList)))
			.then(literal("miningAreaWhiteList")
				.then(addAreaHandler(Config::getMiningAreaWhiteList))
				.then(removeAreaHandler(Config::getMiningAreaWhiteList))
				.then(listAreaHandler(Config::getMiningAreaWhiteList)))
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
			.then(literal("placementAreaBlackList")
				.then(addAreaHandler(Config::getPlacementAreaBlackList))
				.then(removeAreaHandler(Config::getPlacementAreaBlackList))
				.then(listAreaHandler(Config::getPlacementAreaBlackList)))
			.then(literal("placementAreaWhiteList")
				.then(addAreaHandler(Config::getPlacementAreaWhiteList))
				.then(removeAreaHandler(Config::getPlacementAreaWhiteList))
				.then(listAreaHandler(Config::getPlacementAreaWhiteList)))
			.then(literal("defaultPlacementAreaTypeIsBlackList")
				.executes(ctx -> {
					ctx.getSource().sendFeedback(Text.of(
						Config.getDefaultPlacementAreaType() ? "BlackList" : "WhiteList"), false);
					return 1;
				})
				.then(argument("isBlackList", BoolArgumentType.bool())
					.requires(src -> src.hasPermissionLevel(1))
					.executes(ctx -> {
						Config.setDefaultPlacementAreaType(BoolArgumentType.getBool(ctx, "isBlackList"));
						return 1;
					})))
			.then(literal("fakePlayerPrefix")
				.executes(ctx -> {
					ctx.getSource().sendFeedback(Text.of(
						Config.getFpPrefixFeature() ?
						"假人前缀功能已开启，前缀为\"" + Config.getFpPrefix() + "\"" :
						"假人前缀功能已关闭"), false);
					return 1;
				})
				.then(literal("switch")
					.requires(src -> src.hasPermissionLevel(1))
					.then(argument("value", BoolArgumentType.bool())
						.executes(ctx -> {
							Config.setFpPrefixFeature(BoolArgumentType.getBool(ctx, "value"));
							return 1;
						})))
				.then(literal("set")
					.requires(src -> src.hasPermissionLevel(1))
					.then(argument("prefix", StringArgumentType.word())
						.executes(ctx -> {
							Config.setFpPrefix(StringArgumentType.getString(ctx, "prefix"));
							return 1;
						}))))
		);
	}

	private static LiteralArgumentBuilder<ServerCommandSource>
			addAreaHandler(Supplier<Areas> area) {
		return literal("add")
			.requires(src -> src.hasPermissionLevel(1))
			.then(argument("dimension", DimensionArgumentType.dimension())
				.then(argument("from", BlockPosArgumentType.blockPos())
					.then(argument("to", BlockPosArgumentType.blockPos())
						.executes(ctx -> {
							if (area.get().add(
									BlockBox.create(getBlockPos(ctx, "from"), getBlockPos(ctx, "to")),
									DimensionArgumentType.getDimensionArgument(
										ctx, "dimension").getRegistryKey())) {
								area.get().save();
							} else {
								ctx.getSource().sendFeedback(Text.of(
									"Area has already existed"), false);
							}
							return 1;
						}))));
	}

	private static LiteralArgumentBuilder<ServerCommandSource>
			removeAreaHandler(Supplier<Areas> area) {
		return literal("remove")
			.requires(src -> src.hasPermissionLevel(1))
			.then(argument("dimension", DimensionArgumentType.dimension())
				.then(argument("index", IntegerArgumentType.integer(1))
						.executes(ctx -> {
							if (area.get().remove(
									IntegerArgumentType.getInteger(ctx, "index"),
									DimensionArgumentType.getDimensionArgument(
										ctx, "dimension").getRegistryKey())) {
								area.get().save();
								return 1;
							} else {
								ctx.getSource().sendFeedback(Text.of("Index out of bound"), false);
								return -1;
							}
						})));
	}

	private static LiteralArgumentBuilder<ServerCommandSource>
			listAreaHandler(Supplier<Areas> area) {
		return literal("list").executes(ctx -> {
			try {
				ctx.getSource().sendFeedback(Text.of(area.get().toString()), false);
			} catch(Exception e) {
				e.printStackTrace();
			}
			return 1;
		});
	}
}
