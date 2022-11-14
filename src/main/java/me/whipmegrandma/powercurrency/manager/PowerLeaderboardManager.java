package me.whipmegrandma.powercurrency.manager;

import org.mineacademy.fo.model.Tuple;

import java.util.ArrayList;
import java.util.List;

public class PowerLeaderboardManager {

	private static List<Tuple<String, String>> powerLeaderboardManager = new ArrayList<>();

	public static void add(Tuple<String, String> data) {
		powerLeaderboardManager.add(data);
	}

	public static void clear() {
		powerLeaderboardManager.clear();
	}

	public static String getName(int position) {
		return powerLeaderboardManager.get(position - 1).getKey();
	}

	public static String getPower(int position) {
		return powerLeaderboardManager.get(position - 1).getValue();
	}

	public static int getSize() {
		return powerLeaderboardManager.size();
	}
}
