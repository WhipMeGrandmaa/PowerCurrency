package me.whipmegrandma.powercurrency.menu;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import me.whipmegrandma.powercurrency.manager.PowerManager;
import me.whipmegrandma.powercurrency.model.ButtonChange;
import org.bukkit.block.CreatureSpawner;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.mineacademy.fo.*;
import org.mineacademy.fo.collection.SerializedMap;
import org.mineacademy.fo.menu.Menu;
import org.mineacademy.fo.menu.button.Button;
import org.mineacademy.fo.menu.model.ItemCreator;
import org.mineacademy.fo.model.ConfigSerializable;
import org.mineacademy.fo.model.HookManager;
import org.mineacademy.fo.model.Tuple;
import org.mineacademy.fo.remain.CompMaterial;
import org.mineacademy.fo.settings.ConfigItems;
import org.mineacademy.fo.settings.YamlConfig;

import java.util.*;

public class BuyMenu extends YamlConfig {

	private final static ConfigItems<BuyMenu> menus = ConfigItems.fromFile("", "menu/buymenu.yml", BuyMenu.class);

	private final String name;

	private String title;
	private int size;
	private List<ButtonData> buttons;

	private BuyMenu(String name) {
		this.name = name;

		this.setPathPrefix(name);
		this.loadConfiguration("menu/buymenu.yml");
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

					if (data.getPrice() > -1) {
						if (PowerManager.buy(player, data.getPrice())) {

							if (data.getCommand() != null)
								for (String command : data.getCommand())
									Common.dispatchCommandAsPlayer(player, HookManager.isPlaceholderAPILoaded() ? PlaceholderAPI.setPlaceholders(player, command) : command);

							if (data.isGetItem())
								PlayerUtil.addItemsOrDrop(player, data.getMaterial().toItem());

							if (data.getMenuToOpen() != null) {
								BuyMenu otherMenu = BuyMenu.findMenu(data.menuToOpen);

								if (otherMenu == null)
									menu.animateTitle("Invalid menu: " + data.getMenuToOpen());
								else
									otherMenu.toMenu(menu, player).displayTo(player);
							}

							if (data.isRandomSpawner())
								PlayerUtil.addItemsOrDrop(player, this.randomSpawner());

							if (data.isRandomEnchantment())
								PlayerUtil.addItemsOrDrop(player, this.randomEnchantment());

							menu.restartMenu(data.receiveMessage);

						} else
							menu.restartMenu(data.insufficientMessage);
					}

				}

				@Override
				public ItemStack getItem() {
					String title = HookManager.isPlaceholderAPILoaded() ? PlaceholderAPI.setPlaceholders(player, data.getTitle()) : data.getTitle();
					List<String> lore = HookManager.isPlaceholderAPILoaded() ? PlaceholderAPI.setPlaceholders(player, data.getLore()) : data.getLore();

					ItemCreator item = ItemCreator.of(data.getMaterial(), title, lore)
							.glow(data.isGlow());

					if (data.getPlayerSkullName() != null) {
						String playerSkullName = HookManager.isPlaceholderAPILoaded() ? PlaceholderAPI.setPlaceholders(player, data.getPlayerSkullName()) : data.getPlayerSkullName();

						item.skullOwner(playerSkullName);
					}

					ItemStack compiledItem = item.make();

					if (data.getCustomModelData() != -1) {
						ItemMeta meta = compiledItem.getItemMeta();

						meta.setCustomModelData(data.getCustomModelData());

						compiledItem.setItemMeta(meta);
					}

					return compiledItem;
				}

				private ItemStack randomSpawner() {

					EntityType type = randomMob();

					ItemStack spawner = ItemCreator.of(CompMaterial.SPAWNER).make();
					BlockStateMeta meta = (BlockStateMeta) spawner.getItemMeta();
					meta.setDisplayName(Common.colorize("&f" + ItemUtil.bountifyCapitalized(type.name()) + " Spawner"));
					CreatureSpawner creatureSpawner = (CreatureSpawner) meta.getBlockState();
					creatureSpawner.setSpawnedType(type);

					meta.setBlockState(creatureSpawner);
					spawner.setItemMeta(meta);

					return spawner;
				}

				private EntityType randomMob() {

					Random random = new Random();
					List<EntityType> list = new ArrayList<>(Arrays.asList(
							EntityType.CHICKEN,
							EntityType.PIG,
							EntityType.COW,
							EntityType.SHEEP,
							EntityType.CAVE_SPIDER,
							EntityType.SPIDER,
							EntityType.ZOMBIE,
							EntityType.SKELETON,
							EntityType.MAGMA_CUBE,
							EntityType.SLIME,
							EntityType.BLAZE));

					int number = random.nextInt(list.size());

					EntityType randomType = list.get(number);

					return randomType;
				}

				private ItemStack randomEnchantment() {

					Tuple<Enchantment, Integer> type = randomEnchant();

					ItemStack enchantment = ItemCreator.of(CompMaterial.ENCHANTED_BOOK)
							.enchant(type.getKey(), type.getValue())
							.make();

					return enchantment;
				}

				private Tuple<Enchantment, Integer> randomEnchant() {

					Random random = new Random();
					List<Tuple<Enchantment, Integer>> list = new ArrayList<>();

					for (int i = 1; i <= 4; i++)
						list.add(new Tuple<>(Enchantment.PROTECTION_ENVIRONMENTAL, i));

					for (int i = 1; i <= 4; i++)
						list.add(new Tuple<>(Enchantment.DURABILITY, i));

					for (int i = 2; i <= 4; i++)
						list.add(new Tuple<>(Enchantment.DAMAGE_ALL, i));

					for (int i = 1; i <= 3; i++)
						list.add(new Tuple<>(Enchantment.KNOCKBACK, i));

					int number = random.nextInt(list.size());

					Tuple<Enchantment, Integer> picked = list.get(number);

					return picked;
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
		private int customModelData;
		private String title;
		private List<String> lore;

		private List<String> command;
		private String menuToOpen;
		private boolean getItem;
		private boolean randomSpawner;
		private boolean randomEnchantment;
		private String receiveMessage;
		private String insufficientMessage;

		private int price;

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

			button.customModelData = map.getInteger("Custom_Model_Data", -1);

			button.title = map.getString("Title");
			Valid.checkNotNull(button.title, "Missing 'Title' key from button: " + map);

			button.lore = map.containsKey("Lore") ? map.getStringList("Lore") : new ArrayList<>();

			SerializedMap click = map.getMap("Click");

			button.command = click.getStringList("Command");
			button.menuToOpen = click.getString("Menu");
			button.getItem = click.getBoolean("Give_Item", false);
			button.randomSpawner = click.getBoolean("Random_Spawner", false);
			button.randomEnchantment = click.getBoolean("Random_Enchantment", false);
			button.receiveMessage = click.getString("Received_Message", "&aYou received a " + ItemUtil.bountifyCapitalized(button.getMaterial()) + "!");
			button.insufficientMessage = click.getString("Insufficient_Message", "&cInsufficient funds!");

			SerializedMap buy = click.getMap("Shop");

			button.price = buy.getInteger("Price", -1);

			return button;
		}
	}

	public static BuyMenu findMenu(String name) {
		return menus.findItem(name);
	}

	public static void loadMenus() {
		menus.loadItems();
	}
}
