package me.whipmegrandma.powercurrency.hook;

import com.google.common.primitives.Ints;
import lombok.Getter;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.whipmegrandma.powercurrency.manager.PowerLeaderboardManager;
import me.whipmegrandma.powercurrency.manager.PowerManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PlaceholderAPIHook extends PlaceholderExpansion {
	@Getter
	private final static Object lock = new Object();

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

		if ("playerpower".equals(params))
			return String.valueOf(PowerManager.getPower(player));

		if (params.startsWith("ptopname")) {
			String filtered = params.replaceAll("[^0-9]", "");
			Integer position = Ints.tryParse(filtered);

			if (position == null || position < 1 || position > PowerLeaderboardManager.getSize())
				return null;
			else
				return PowerLeaderboardManager.getName(position);
		}

		if (params.startsWith("ptopbal")) {
			String filtered = params.replaceAll("[^0-9]", "");
			Integer position = Ints.tryParse(filtered);

			if (position == null || position < 1 || position > PowerLeaderboardManager.getSize())
				return null;
			else
				return PowerLeaderboardManager.getPower(position);
		}

		return null;
	}

}
