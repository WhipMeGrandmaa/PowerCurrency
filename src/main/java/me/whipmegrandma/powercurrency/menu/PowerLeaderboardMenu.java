package me.whipmegrandma.powercurrency.menu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import me.whipmegrandma.powercurrency.model.ButtonChange;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.mineacademy.fo.MathUtil;
import org.mineacademy.fo.Valid;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.ConfigItems;
import org.mineacademy.fo.settings.YamlConfig;

import java.util.*;

public class PowerLeaderboardMenu extends YamlConfig {

	private final static ConfigItems<PowerLeaderboardMenu> menus = ConfigItems.fromFile("", "menu/powerleaderboardmenu.yml", PowerLeaderboardMenu.class);

	private final String name;

	private String title;
	private int size;
	private List<ButtonData> buttons;

	private PowerLeaderboardMenu(String name) {
		this.name = name;

		this.setPathPrefix(name);
		this.loadConfiguration("menu/powerleaderboardmenu.yml");
	}

	@Override
	protected void onLoad() {
		this.title = this.getString("Title");
		this.size = (int) MathUtil.calculate(this.getString("Size"));
		this.buttons = this.loadButtons();
	}

	private List<ButtonData> loadButtons() {
		List<ButtonData> compiledButtons = new ArrayList<>();

		for (Map.Entry<String, Object> entry : this.getMap("Buttons", String.class, Object.class).entrySet()) {
			String buttonName = entry.getKey();
			SerializedMap buttonSettings = SerializedMap.of(entry.getValue());

			compiledButtons.add(ButtonData.deserialize(buttonName, buttonSettings));
		}

		return compiledButtons;
	}

	public Menu toMenu(Menu parent, Player player) {
		Map<Integer, ButtonChange> buttons = this.getButtons(player);

		return new Menu(parent) {

			{
				this.setTitle(title);
				this.setSize(size);
			}

			@Override
			protected List<Button> getButtonsToAutoRegister() {
				return new ArrayList<>(buttons.values());
			}

			@Override
			public ItemStack getItemAt(int slot) {

				if (buttons.containsKey(slot))
					return buttons.get(slot).getItem();

				return NO_ITEM;
			}

		};
	}

	public void displayTo(Player player) {
		this.toMenu(player).displayTo(player);
	}

	public Menu toMenu(Player player) {
		return this.toMenu(null, player);
	}

	public Map<Integer, ButtonChange> getButtons(Player player) {
		Map<Integer, ButtonChange> buttons = new HashMap<>();

		for (ButtonData data : this.buttons) {
			buttons.put(data.getSlot(), new ButtonChange(player) {

				@Override
				public void onClickedInMenu(Player player, Menu menu, ClickType click) {

					if (data.getMenuToOpen() != null) {
						PowerLeaderboardMenu otherMenu = PowerLeaderboardMenu.findMenu(data.menuToOpen);

						if (otherMenu == null)
							menu.animateTitle("Invalid menu: " + data.getMenuToOpen());
						else
							otherMenu.toMenu(menu, player).displayTo(player);
					}

				}

				@Override
				public ItemStack getItem() {
					String title = HookManager.isPlaceholderAPILoaded() ? PlaceholderAPI.setPlaceholders(player, data.getTitle()) : data.getTitle();
					List<String> lore = HookManager.isPlaceholderAPILoaded() ? PlaceholderAPI.setPlaceholders(player, data.getLore()) : data.getLore();

					if (data.getPlayerSkullName() != null) {
						String playerSkullName = HookManager.isPlaceholderAPILoaded() ? PlaceholderAPI.setPlaceholders(player, data.getPlayerSkullName()) : data.getPlayerSkullName();

						if (playerSkullName.contains("%"))
							return null;

						return ItemCreator.of(data.getMaterial(), title, lore)
								.glow(data.isGlow()).skullOwner(playerSkullName).make();
					}

					return ItemCreator.of(data.getMaterial(), title, lore)
							.glow(data.isGlow()).make();
				}


			});
		}
		return buttons;
	}

	@Getter
	@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
	private static class ButtonData implements ConfigSerializable {

		private final String name;

		private int slot;
		private CompMaterial material;
		private String playerSkullName;
		private boolean glow;
		private String title;
		private List<String> lore;

		private String menuToOpen;

		@Override
		public SerializedMap serialize() {
			return null;
		}

		public static ButtonData deserialize(String buttonName, SerializedMap map) {
			ButtonData button = new ButtonData(buttonName);

			button.slot = map.containsKey("Slot") ? (int) MathUtil.calculate(map.getString("Slot")) : -1;
			Valid.checkBoolean(button.slot != -1, "Missing 'Slot' key from button: " + map);

			button.material = map.getMaterial("Material");
			Valid.checkNotNull(button.material, "Missing 'Material' key from button: " + map);

			button.playerSkullName = map.getString("Player_Skull_Name");

			button.glow = map.getBoolean("Glow", false);

			button.title = map.getString("Title");
			Valid.checkNotNull(button.title, "Missing 'Title' key from button: " + map);

			button.lore = map.containsKey("Lore") ? map.getStringList("Lore") : Arrays.asList("");

			SerializedMap click = map.getMap("Click");

			button.menuToOpen = click.getString("Menu");

			return button;
		}
	}

	public static PowerLeaderboardMenu findMenu(String name) {
		return menus.findItem(name);
	}

	public static void loadMenus() {
		menus.loadItems();
	}
}
