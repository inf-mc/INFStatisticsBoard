package info.infinf.statisticsboard;

import java.io.File;
import java.io.IOException;
import static java.lang.Math.min;
import static java.lang.Math.max;
import java.util.HashMap;
import java.util.LinkedHashSet;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtIo;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockBox;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.infinf.statisticsboard.utils.LinkedHashSets;
import info.infinf.statisticsboard.utils.Nbts;

public class Areas {
	private static final Logger LOGGER = LoggerFactory.getLogger("infboard");
	private final File savePath;
	private final HashMap<RegistryKey<World>, LinkedHashSet<BlockBox>> areas;

	public Areas(File savePath) {
		this.savePath = savePath;
		areas = Maps.newHashMap();
	}

	public Areas(File savePath, NbtCompound compound) {
		this.savePath = savePath;
		areas = Maps.newHashMap();
		for (var world: compound.getKeys()) {;
			if (!Identifier.isValid(world)) {
				continue;
			}
			var worldKey = RegistryKey.of(RegistryKeys.WORLD, new Identifier(world));
			var arr = compound.getIntArray(world);
			var boxSets = new LinkedHashSet<BlockBox>(arr.length/3);
			areas.put(worldKey, boxSets);
			for (int i = 0; i + 6 <= arr.length; i += 6) {
				boxSets.add(new BlockBox(
					min(arr[i], arr[i+3]), min(arr[i+1], arr[i+4]), min(arr[i+2], arr[i+5]),
					max(arr[i], arr[i+3]), max(arr[i+1], arr[i+4]), max(arr[i+2], arr[i+5])));
			}
		}
	}

	public static Areas loadNbtFile(File f) {
		return new Areas(f, Nbts.load(f));
	}

	public void save() {
		var result = new NbtCompound();
		for (var entry: areas.entrySet()) {
			var boxes = entry.getValue();
			var arr = new int[boxes.size()*6];
			int i = 0;
			for (var box: boxes) {
				arr[i + 0] = box.getMinX();
				arr[i + 1] = box.getMinY();
				arr[i + 2] = box.getMinZ();
				arr[i + 3] = box.getMaxX();
				arr[i + 4] = box.getMaxY();
				arr[i + 5] = box.getMaxZ();
				i += 6;
			}
			result.putIntArray(entry.getKey().getValue().toString(), arr);
		}
		try {
			NbtIo.write(result, savePath);
		} catch (IOException e) {
			LOGGER.error(String.format("An error occured when writing config file to %s",
						savePath.getAbsolutePath()));
			e.printStackTrace();
		}
	}

	public boolean contains(BlockPos pos, RegistryKey<World> world) {
		var boxes = areas.get(world);
		if (boxes == null) {
			return false;
		}
		for (var box: boxes) {
			if (box.contains(pos)) {
				return true;
			}
		}
		return false;
	}

	public boolean add(BlockBox box, RegistryKey<World> world) {
		return areas.computeIfAbsent(world, w -> Sets.newLinkedHashSet()).add(box);
	}

	public boolean remove(int index, RegistryKey<World> world) {
		var boxes = areas.get(world);
		if (boxes == null) {
			return false;
		}
		return LinkedHashSets.remove(boxes, index);
	}

	@Override
	public String toString() {
		var sb = new StringBuilder();
		for (var entry: areas.entrySet()) {
			sb.append(entry.getKey().getValue().toString());
			sb.append(":\n");
			int i = 1;
			for (var box: entry.getValue()) {
				sb.append('[');
				sb.append(i);
				sb.append(']');
				sb.append('从');
				sb.append(box.getMinX());
				sb.append(',');
				sb.append(box.getMinY());
				sb.append(',');
				sb.append(box.getMinZ());
				sb.append('到');
				sb.append(box.getMaxX());
				sb.append(',');
				sb.append(box.getMaxY());
				sb.append(',');
				sb.append(box.getMaxZ());
				sb.append("\n");
				i++;
			}
		}
		return sb.toString();
	}
}
