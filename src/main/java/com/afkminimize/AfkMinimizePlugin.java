package com.afkminimize;

import com.google.inject.Provides;
import java.awt.Frame;
import java.awt.Window;
import java.awt.event.KeyEvent;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.MenuOptionClicked;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.NotificationFired;
import net.runelite.client.input.KeyListener;
import net.runelite.client.input.KeyManager;
import net.runelite.client.input.MouseManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

@Slf4j
@PluginDescriptor(
	name = "AFK Minimize",
	description = "Minimizes the client a moment after you answer a notification",
	tags = {"afk", "minimize", "notification", "focus", "idle"}
)
public class AfkMinimizePlugin extends Plugin implements KeyListener
{
	@Inject
	private Client client;

	@Inject
	private AfkMinimizeConfig config;

	@Inject
	private KeyManager keyManager;

	@Inject
	private MouseManager mouseManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private AfkMinimizeOverlay overlay;

	// Set when a notification fires while you are in another window
	private volatile boolean armed;
	private volatile boolean keepOpenKeyDown;
	// When the client minimizes, in System.currentTimeMillis() time; 0 means no countdown
	@Getter
	private volatile long minimizeAt;

	// A Swing timer waits without blocking the client and fires on the UI thread
	private final Timer minimizeTimer = new Timer(0, e -> minimize());

	@Override
	protected void startUp()
	{
		minimizeTimer.setRepeats(false);
		keyManager.registerKeyListener(this);
		mouseManager.registerMouseListener(overlay);
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		keyManager.unregisterKeyListener(this);
		mouseManager.unregisterMouseListener(overlay);
		overlayManager.remove(overlay);
		SwingUtilities.invokeLater(minimizeTimer::stop);
		armed = false;
		keepOpenKeyDown = false;
		minimizeAt = 0;
	}

	@Subscribe
	public void onNotificationFired(NotificationFired event)
	{
		// RuneLite posts this event before it brings the client to the front,
		// so a focused window here means you are already playing
		Window window = clientWindow();
		if (window != null && !window.isFocused())
		{
			armed = true;
		}
	}

	@Subscribe
	public void onMenuOptionClicked(MenuOptionClicked event)
	{
		if (!armed)
		{
			return;
		}

		if (keepOpenKeyDown)
		{
			keepOpen();
			return;
		}

		// Every click restarts the countdown
		int delay = config.minimizeDelay();
		minimizeAt = System.currentTimeMillis() + delay;
		SwingUtilities.invokeLater(() ->
		{
			minimizeTimer.setInitialDelay(delay);
			minimizeTimer.restart();
		});
	}

	// Stop the countdown but stay armed for the next click
	void keepOpen()
	{
		minimizeAt = 0;
		SwingUtilities.invokeLater(minimizeTimer::stop);
	}

	private void minimize()
	{
		armed = false;
		minimizeAt = 0;
		Window window = clientWindow();
		if (window instanceof Frame)
		{
			((Frame) window).setState(Frame.ICONIFIED);
		}
	}

	@Override
	public void keyPressed(KeyEvent e)
	{
		if (config.keepOpenKey().matches(e))
		{
			keepOpenKeyDown = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e)
	{
		if (config.keepOpenKey().matches(e))
		{
			keepOpenKeyDown = false;
		}
	}

	@Override
	public void keyTyped(KeyEvent e)
	{
	}

	private Window clientWindow()
	{
		return client.getCanvas() == null ? null : SwingUtilities.getWindowAncestor(client.getCanvas());
	}

	@Provides
	AfkMinimizeConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AfkMinimizeConfig.class);
	}
}
