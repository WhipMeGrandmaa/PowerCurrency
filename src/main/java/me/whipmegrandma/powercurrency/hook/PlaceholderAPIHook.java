package me.whipmegrandma.powercurrency.hook;

import lombok.Getter;
import lombok.Setter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.whipmegrandma.powercurrency.database.PowerDatabase;
import me.whipmegrandma.powercurrency.manager.PowerManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.mineacademy.fo.Common;

import java.util.*;

@Setter
@Getter
public class PlaceholderAPIHook extends PlaceholderExpansion {
	@Setter
	@Getter
	private static PlaceholderAPIHook instance;

	private String ptopname1;
	private String ptopname2;
	private String ptopname3;
	private String ptopname4;
	private String ptopname5;
	private String ptopname6;
	private String ptopname7;
	private String ptopname8;

	private String ptopbal1;
	private String ptopbal2;
	private String ptopbal3;
	private String ptopbal4;
	private String ptopbal5;
	private String ptopbal6;
	private String ptopbal7;
	private String ptopbal8;

	public PlaceholderAPIHook() {
		setInstance(this);
	}

	@Override
	public @NotNull String getIdentifier() {
		return "powercurrency";
	}

	@Override
	public @NotNull String getAuthor() {
		return "WhipMeGrandma";
	}

	@Override
	public @NotNull String getVersion() {
		return "1.0.0";
	}

	@Override
	public boolean canRegister() {
		return true;
	}

	@Override
	public boolean persist() {
		return true;
	}

	@Override
	public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
		if (player == null)
			return "";

		Common.runLater(() -> PowerDatabase.getInstance().pollAllCache(PlaceholderAPIHook::updateVariables));

		if ("playerpower".equals(params))
			return String.valueOf(PowerManager.getPower(player));

		if ("ptopname1".equals(params))
			return getInstance().getPtopname1();

		if ("ptopname2".equals(params))
			return getInstance().getPtopname2();

		if ("ptopname3".equals(params))
			return getInstance().getPtopname3();

		if ("ptopname4".equals(params))
			return getInstance().getPtopname4();

		if ("ptopname5".equals(params))
			return getInstance().getPtopname5();

		if ("ptopname6".equals(params))
			return getInstance().getPtopname6();

		if ("ptopname7".equals(params))
			return getInstance().getPtopname7();

		if ("ptopname8".equals(params))
			return getInstance().getPtopname8();

		if ("ptopbal1".equals(params))
			return getInstance().getPtopbal1();

		if ("ptopbal2".equals(params))
			return getInstance().getPtopbal2();

		if ("ptopbal3".equals(params))
			return getInstance().getPtopbal3();

		if ("ptopbal4".equals(params))
			return getInstance().getPtopbal4();

		if ("ptopbal5".equals(params))
			return getInstance().getPtopbal5();

		if ("ptopbal6".equals(params))
			return getInstance().getPtopbal6();

		if ("ptopbal7".equals(params))
			return getInstance().getPtopbal7();

		if ("ptopbal8".equals(params))
			return getInstance().getPtopbal8();

		return null;
	}

	private static void updateVariables(HashMap<String, Integer> map) {

		List<Map.Entry<String, Integer>> sorted = new ArrayList<>(map.entrySet());

		Collections.sort(sorted, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));

		int i = 0;

		for (Map.Entry<String, Integer> variable : sorted) {
			i++;

			if (i == 1) {
				getInstance().setPtopname1(variable.getKey());
				getInstance().setPtopbal1(variable.getValue().toString());
			}

			if (i == 2) {
				getInstance().setPtopname2(variable.getKey());
				getInstance().setPtopbal2(variable.getValue().toString());
			}

			if (i == 3) {
				getInstance().setPtopname3(variable.getKey());
				getInstance().setPtopbal3(variable.getValue().toString());
			}

			if (i == 4) {
				getInstance().setPtopname4(variable.getKey());
				getInstance().setPtopbal4(variable.getValue().toString());
			}

			if (i == 5) {
				getInstance().setPtopname5(variable.getKey());
				getInstance().setPtopbal5(variable.getValue().toString());
			}

			if (i == 6) {
				getInstance().setPtopname6(variable.getKey());
				getInstance().setPtopbal6(variable.getValue().toString());
			}

			if (i == 7) {
				getInstance().setPtopname7(variable.getKey());
				getInstance().setPtopbal7(variable.getValue().toString());
			}

			if (i == 8) {
				getInstance().setPtopname8(variable.getKey());
				getInstance().setPtopbal8(variable.getValue().toString());

				break;
			}
		}
	}

}
