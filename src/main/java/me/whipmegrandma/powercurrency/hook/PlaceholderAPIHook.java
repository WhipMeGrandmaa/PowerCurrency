package me.whipmegrandma.powercurrency.hook;

import lombok.Setter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import world.bentobox.bentobox.database.Database;

import java.util.Map;

@Setter
public class PlaceholderAPIHook extends PlaceholderExpansion {

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

	@Override
	public @NotNull String getIdentifier() {
		return "power";
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

		if ("playerpower".equals(params))
			return String.valueOf(PowerManager.getPower(player));

		if ("ptopname1".equals(params))
			return this.ptopname1;

		if ("ptopname2".equals(params))
			return this.ptopname2;

		if ("ptopname3".equals(params))
			return this.ptopname3;

		if ("ptopname4".equals(params))
			return this.ptopname4;

		if ("ptopname5".equals(params))
			return this.ptopname5;

		if ("ptopname6".equals(params))
			return this.ptopname6;

		if ("ptopname7".equals(params))
			return this.ptopname7;

		if ("ptopname8".equals(params))
			return this.ptopname8;


		if ("ptopbal1".equals(params))
			return this.ptopbal1;

		if ("ptopbal2".equals(params))
			return this.ptopbal2;

		if ("ptopbal3".equals(params))
			return this.ptopbal3;

		if ("ptopbal4".equals(params))
			return this.ptopbal4;

		if ("ptopbal5".equals(params))
			return this.ptopbal5;

		if ("ptopbal6".equals(params))
			return this.ptopbal6;

		if ("ptopbal7".equals(params))
			return this.ptopbal7;

		if ("ptopbal8".equals(params))
			return this.ptopbal8;

		return null;
	}

	public static void loadPapi() {

		Database.getInstance().pollAllCache(cache -> {

			PlaceholderApi api = new PlaceholderApi();

			int i = 0;

			for (Map.Entry<String, Integer> map : PowerManager.sortLeaderboard(cache)) {
				i++;

				if (i == 1) {
					api.setPtopname1(map.getKey());
					api.setPtopbal1(String.valueOf(map.getValue()));
				}

				if (i == 2) {
					api.setPtopname2(map.getKey());
					api.setPtopbal2(String.valueOf(map.getValue()));
				}

				if (i == 3) {
					api.setPtopname3(map.getKey());
					api.setPtopbal3(String.valueOf(map.getValue()));
				}

				if (i == 4) {
					api.setPtopname4(map.getKey());
					api.setPtopbal4(String.valueOf(map.getValue()));
				}

				if (i == 5) {
					api.setPtopname5(map.getKey());
					api.setPtopbal5(String.valueOf(map.getValue()));
				}

				if (i == 6) {
					api.setPtopname6(map.getKey());
					api.setPtopbal6(String.valueOf(map.getValue()));
				}

				if (i == 7) {
					api.setPtopname7(map.getKey());
					api.setPtopbal7(String.valueOf(map.getValue()));
				}

				if (i == 8) {
					api.setPtopname8(map.getKey());
					api.setPtopbal8(String.valueOf(map.getValue()));

					break;
				}
			}

		});
	}

}
