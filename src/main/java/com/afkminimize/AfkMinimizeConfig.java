package com.afkminimize;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;
import net.runelite.client.config.Units;

@ConfigGroup("afkminimize")
public interface AfkMinimizeConfig extends Config
{
	@ConfigItem(
		keyName = "keepOpenKey",
		name = "Keep open key",
		description = "Hold this key while clicking to keep the client open"
	)
	default Keybind keepOpenKey()
	{
		return Keybind.NOT_SET;
	}

	@Units(Units.MILLISECONDS)
	@ConfigItem(
		keyName = "minimizeDelay",
		name = "Minimize delay",
		description = "How long after your last click the client minimizes. Each click restarts the countdown"
	)
	default int minimizeDelay()
	{
		return 1000;
	}
}
