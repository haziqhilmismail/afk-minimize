package com.afkminimize;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.inject.Inject;
import javax.swing.SwingUtilities;
import net.runelite.api.Client;
import net.runelite.client.input.MouseListener;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ComponentConstants;
import net.runelite.client.ui.overlay.components.TextComponent;
import net.runelite.client.util.ColorUtil;

// The countdown is also a button: a left click on it keeps the client open
class AfkMinimizeOverlay extends Overlay implements MouseListener
{
	private static final Font TIME_FONT = FontManager.getRunescapeBoldFont().deriveFont(32f);
	private static final Font HINT_FONT = FontManager.getRunescapeFont();
	private static final String HINT = "Click here to keep open";
	private static final int PADDING = 8;
	private static final Color HOVER_BACKGROUND = new Color(90, 75, 50, 220);

	private final Client client;
	private final AfkMinimizePlugin plugin;
	private final AfkMinimizeConfig config;
	private final TextComponent text = new TextComponent();

	// Set when a press lands on the button, so the matching release and click do not reach the game
	private boolean pressConsumed;

	@Inject
	AfkMinimizeOverlay(Client client, AfkMinimizePlugin plugin, AfkMinimizeConfig config)
	{
		super(plugin);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.TOP_CENTER);
		text.setOutline(true);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		long minimizeAt = plugin.getMinimizeAt();
		if (minimizeAt == 0)
		{
			return null;
		}

		long remaining = Math.max(0, minimizeAt - System.currentTimeMillis());
		// Green when the countdown starts, red when it is about to minimize
		double left = Math.min(1, remaining / (double) Math.max(1, config.minimizeDelay()));
		String time = String.format("Minimize in %.1fs", remaining / 1000.0);

		FontMetrics timeMetrics = graphics.getFontMetrics(TIME_FONT);
		FontMetrics hintMetrics = graphics.getFontMetrics(HINT_FONT);
		int width = Math.max(timeMetrics.stringWidth(time), hintMetrics.stringWidth(HINT)) + 2 * PADDING;
		int height = timeMetrics.getHeight() + hintMetrics.getHeight() + 2 * PADDING;

		net.runelite.api.Point mouse = client.getMouseCanvasPosition();
		Rectangle bounds = getBounds();
		boolean hover = mouse != null && bounds.contains(mouse.getX(), mouse.getY());
		graphics.setColor(hover ? HOVER_BACKGROUND : ComponentConstants.STANDARD_BACKGROUND_COLOR);
		graphics.fillRect(0, 0, width, height);
		graphics.setColor(Color.BLACK);
		graphics.drawRect(0, 0, width - 1, height - 1);

		text.setFont(TIME_FONT);
		text.setText(time);
		text.setColor(ColorUtil.colorLerp(Color.RED, Color.GREEN, left));
		text.setPosition(new Point((width - timeMetrics.stringWidth(time)) / 2, PADDING + timeMetrics.getAscent()));
		text.render(graphics);

		text.setFont(HINT_FONT);
		text.setText(HINT);
		text.setColor(Color.WHITE);
		text.setPosition(new Point((width - hintMetrics.stringWidth(HINT)) / 2,
			PADDING + timeMetrics.getHeight() + hintMetrics.getAscent()));
		text.render(graphics);

		return new Dimension(width, height);
	}

	private boolean onButton(MouseEvent e)
	{
		return plugin.getMinimizeAt() != 0 && getBounds().contains(e.getPoint());
	}

	@Override
	public MouseEvent mousePressed(MouseEvent e)
	{
		if (SwingUtilities.isLeftMouseButton(e) && onButton(e))
		{
			plugin.keepOpen();
			pressConsumed = true;
			e.consume();
		}
		return e;
	}

	@Override
	public MouseEvent mouseReleased(MouseEvent e)
	{
		if (pressConsumed)
		{
			e.consume();
		}
		return e;
	}

	@Override
	public MouseEvent mouseClicked(MouseEvent e)
	{
		if (pressConsumed)
		{
			pressConsumed = false;
			e.consume();
		}
		return e;
	}

	@Override
	public MouseEvent mouseEntered(MouseEvent e)
	{
		return e;
	}

	@Override
	public MouseEvent mouseExited(MouseEvent e)
	{
		return e;
	}

	@Override
	public MouseEvent mouseDragged(MouseEvent e)
	{
		return e;
	}

	@Override
	public MouseEvent mouseMoved(MouseEvent e)
	{
		return e;
	}
}
