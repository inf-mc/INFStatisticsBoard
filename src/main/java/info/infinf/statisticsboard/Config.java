package info.infinf.statisticsboard;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.infinf.statisticsboard.Areas;
import info.infinf.statisticsboard.utils.Nbts;

public abstract class Config {
	private static final Logger LOGGER = LoggerFactory.getLogger("infboard");
	private static final String CONF_NAME = "config.nbt";
	private static final String MINING_BLACKLIST_NAME = "MiningAreaBlackList.nbt";
	private static final String MINING_WHITELIST_NAME = "MiningAreaWhiteList.nbt";
	private static final String PLACEMENT_BLACKLIST_NAME =
		"PlacementAreaBlackList.nbt";
	private static final String PLACEMENT_WHITELIST_NAME =
		"PlacementAreaWhiteList.nbt";

	private static File confDir;
	private static File confFile;
	private static boolean fpPrefixFeature;
	private static String fpPrefix;
	private static boolean defaultMiningAreaType;
	private static Areas miningAreaWhiteList;
	private static Areas miningAreaBlackList;
	private static boolean defaultPlacementAreaType;
	private static Areas placementAreaWhiteList;
	private static Areas placementAreaBlackList;

	public static void init(MinecraftServer server) {
		// need better alternatives
		try {
			Constructor newWorldSavePath = WorldSavePath.class
				.getDeclaredConstructor(String.class);
			newWorldSavePath.setAccessible(true);
			confDir = server.getSavePath(
				(WorldSavePath)newWorldSavePath.newInstance("infboard")).toFile();
			newWorldSavePath.setAccessible(false);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		confDir.mkdirs();
		confFile = new File(confDir, CONF_NAME);
		var conf = Nbts.load(confFile);
		fpPrefixFeature = conf.getBoolean("fpPrefixFeature");
		fpPrefix = conf.getString("fpPrefix");
		defaultMiningAreaType = conf.getBoolean("defaultMiningAreaType");
		defaultPlacementAreaType = conf.getBoolean("defaultPlacementAreaType");

		miningAreaBlackList = Areas.loadNbtFile(
			new File(confDir, MINING_BLACKLIST_NAME));
		miningAreaWhiteList = Areas.loadNbtFile(
			new File(confDir, MINING_WHITELIST_NAME));
		placementAreaBlackList = Areas.loadNbtFile(
			new File(confDir, PLACEMENT_BLACKLIST_NAME));
		placementAreaWhiteList = Areas.loadNbtFile(
			new File(confDir, PLACEMENT_WHITELIST_NAME));
	}

	private static void save() {
		var conf = new NbtCompound();
		conf.putBoolean("fpPrefixFeature", fpPrefixFeature);
		conf.putString("fpPrefix", fpPrefix);
		conf.putBoolean("defaultMiningAreaType", defaultMiningAreaType);
		conf.putBoolean("defaultPlacementAreaType", defaultPlacementAreaType);
		try {
			NbtIo.write(conf, confFile);
		} catch (IOException e) {
			LOGGER.error("Error writing config file to {}", confFile);
			e.printStackTrace();
		}
	}

	public static boolean shouldNotCount(PlayerEntity pl) {
		return fpPrefixFeature &&
			pl.getEntityName().toLowerCase().startsWith(fpPrefix);
	}

	public static boolean getDefaultMiningAreaType() {
		return defaultMiningAreaType;
	}

	public static void setDefaultMiningAreaType(boolean defaultMiningAreaType) {
		Config.defaultMiningAreaType = defaultMiningAreaType;
		save();
	}

	public static boolean getDefaultPlacementAreaType() {
		return defaultPlacementAreaType;
	}

	public static void setDefaultPlacementAreaType(boolean defaultPlacementAreaType) {
		Config.defaultPlacementAreaType = defaultPlacementAreaType;
		save();
	}

	public static boolean getFpPrefixFeature() {
		return fpPrefixFeature;
	}

	public static void setFpPrefixFeature(boolean fpPrefixFeature) {
		Config.fpPrefixFeature = fpPrefixFeature;
		save();
	}

	public static String getFpPrefix() {
		return fpPrefix;
	}

	public static void setFpPrefix(String fpPrefix) {
		Config.fpPrefix = fpPrefix.toLowerCase();
		save();
	}

	public static Areas getMiningAreaBlackList() {
		return miningAreaBlackList;
	}

	public static Areas getMiningAreaWhiteList() {
		return miningAreaWhiteList;
	}

	public static Areas getPlacementAreaBlackList() {
		return placementAreaBlackList;
	}

	public static Areas getPlacementAreaWhiteList() {
		return placementAreaWhiteList;
	}
}
