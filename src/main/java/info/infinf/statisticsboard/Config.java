package info.infinf.statisticsboard;

import java.io.File;
import java.io.IOException;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIntArray;
import net.minecraft.nbt.NbtIo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.infinf.statisticsboard.Areas;

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

	public static void init() {
		confDir = new File(FabricLoader.getInstance().getConfigDir().toFile(), "infboard");
		confDir.mkdirs();
		confFile = new File(confDir, CONF_NAME);
		var conf = loadNbt(confFile);
		fpPrefixFeature = conf.getBoolean("fpPrefixFeature");
		fpPrefix = conf.getString("fpPrefix");
		defaultMiningAreaType = conf.getBoolean("defaultMiningAreaType");

		var miningAreaBlackListFile = new File(confDir, "MiningAreaBlackList.nbt");
		miningAreaBlackList = new Areas(miningAreaBlackListFile, loadNbt(miningAreaBlackListFile));
		var miningAreaWhiteListFile = new File(confDir, "MiningAreaWhiteList.nbt");
		miningAreaWhiteList = new Areas(miningAreaWhiteListFile, loadNbt(miningAreaWhiteListFile));
	}

	private static NbtCompound loadNbt(File f) {
		try {
			var conf = NbtIo.read(f);
			if (conf == null) {
				return new NbtCompound();
			}
			return conf;
		} catch (IOException e) {
			LOGGER.error(
				String.format("An error occured when reading config file from %s", f));
			e.printStackTrace();
			return null;
		}
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
