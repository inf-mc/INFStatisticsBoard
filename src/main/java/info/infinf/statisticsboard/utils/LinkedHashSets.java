package info.infinf.statisticsboard.utils;

import java.util.LinkedHashSet;

public abstract class LinkedHashSets {
	public static boolean remove(LinkedHashSet set, int index) {
		if (index > set.size()) {
			return false;
		}
		var iter = set.iterator();
		for (int i = 0; i < index; i++) {
			iter.next();
		}
		iter.remove();
		return true;
	}
}
