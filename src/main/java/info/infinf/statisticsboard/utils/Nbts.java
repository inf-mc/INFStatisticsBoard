package info.infinf.statisticsboard.utils;

import java.io.File;
import java.io.IOException;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;

import info.infinf.statisticsboard.Areas;

public abstract class Nbts {
	public static NbtCompound load(File f) {
		try {
			var compound = NbtIo.read(f);
			if (compound == null) {
				return new NbtCompound();
			}
			return compound;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
