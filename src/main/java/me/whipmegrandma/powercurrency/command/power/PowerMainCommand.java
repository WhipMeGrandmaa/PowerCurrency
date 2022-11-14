package me.whipmegrandma.powercurrency.command.power;

import org.mineacademy.fo.Common;
import org.mineacademy.fo.annotation.AutoRegister;
import org.mineacademy.fo.command.ReloadCommand;
import org.mineacademy.fo.command.SimpleCommandGroup;
import org.mineacademy.fo.model.SimpleComponent;

import java.util.Arrays;
import java.util.List;

@AutoRegister
public final class PowerMainCommand extends SimpleCommandGroup {

	public PowerMainCommand() {
		super("power|p");
	}

	@Override
	protected void registerSubcommands() {
		this.registerSubcommand(new ReloadCommand());
		this.registerSubcommand(new PowerBalSubCommand(this));
		this.registerSubcommand(new PowerSetSubCommand(this));
		this.registerSubcommand(new PowerResetSubCommand(this));
		this.registerSubcommand(new PowerGiveSubCommand(this));
		this.registerSubcommand(new PowerTakeSubCommand(this));
		this.registerSubcommand(new PowerEditSubCommand(this));
		this.registerSubcommand(new PowerBuySubCommand(this));
		this.registerSubcommand(new PowerPaySubCommand(this));
	}

	@Override
	protected String[] getHelpHeader() {
		return new String[]{Common.colorize("{prefix} The following commands are available:")};
	}


	@Override
	protected List<SimpleComponent> getNoParamsHeader() {
		return Arrays.asList(SimpleComponent.of("{prefix} Use /power ? to list the commands."));
	}
}
