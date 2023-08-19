package info.infinf.statisticsboard;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.WorldSavePath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.infinf.statisticsboard.Areas;
import info.infinf.statisticsboard.utils.Nbts;

public final class Config {
	private static final Logger LOGGER = LoggerFactory.getLogger("infboard");
	private static final String CONF_NAME = "config.nbt";

	private static File confDir;
	private static File confFile;
	private static boolean fpPrefixFeature;
	private static String fpPrefix;
	private static boolean defaultMiningAreaType;
	private static Areas miningAreaWhiteList;
	private static Areas miningAreaBlackList;

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
		LOGGER.info(confDir.getAbsolutePath());
		confDir.mkdirs();
		confFile = new File(confDir, CONF_NAME);
		var conf = Nbts.load(confFile);
		fpPrefixFeature = conf.getBoolean("fpPrefixFeature");
		fpPrefix = conf.getString("fpPrefix");
		defaultMiningAreaType = conf.getBoolean("defaultMiningAreaType");

		miningAreaBlackList = Areas.loadNbtFile(
			new File(confDir, "MiningAreaBlackList.nbt"));
		miningAreaWhiteList = Areas.loadNbtFile(
			new File(confDir, "MiningAreaWhiteList.nbt"));
	}

	private static void save() {
		var conf = new NbtCompound();
		conf.putBoolean("fpPrefixFeature", fpPrefixFeature);
		conf.putString("fpPrefix", fpPrefix);
		conf.putBoolean("defaultMiningAreaType", defaultMiningAreaType);
		try {
			NbtIo.write(conf, confFile);
		} catch (IOException e) {
			LOGGER.error(
				String.format("An error occured when writing config file to %s", confFile));
			e.printStackTrace();
		}
	}

	public static boolean getDefaultMiningAreaType() {
		return defaultMiningAreaType;
	}

	public static void setDefaultMiningAreaType(boolean defaultMiningAreaType) {
		Config.defaultMiningAreaType = defaultMiningAreaType;
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
		Config.fpPrefix = fpPrefix;
		save();
	}

	public static Areas getMiningAreaBlackList() {
		return miningAreaBlackList;
	}

	public static Areas getMiningAreaWhiteList() {
		return miningAreaWhiteList;
	}
}
