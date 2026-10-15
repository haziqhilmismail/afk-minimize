# AFK Minimize

A RuneLite plugin for AFK skilling while you do something else on your PC.

When a notification brings the client to the front (your inventory is full, or you stopped mining), you click what you need in the game. A second after your last click, the client minimizes itself and you are back in your other window.

![The client jumps to the front, one click starts the countdown, and the client minimizes](docs/demo-take4.gif)

## Install

AFK Minimize is not on the RuneLite Plugin Hub. The Plugin Hub does not accept plugins that minimize the client or change which window has focus. You can still load it into your own RuneLite as a sideloaded plugin.

1. Install a JDK, version 11 or later.
2. Clone this repo and build the plugin:

   ```
   git clone https://github.com/haziqhilmismail/afk-minimize.git
   cd afk-minimize
   gradlew jar
   ```

   On macOS or Linux, use `./gradlew jar`. The build makes `build/libs/afk-minimize.jar`.
3. Copy `afk-minimize.jar` to the `sideloaded-plugins` folder in your RuneLite folder. Create the folder if it does not exist.
   - Windows: `%USERPROFILE%\.runelite\sideloaded-plugins`
   - macOS and Linux: `~/.runelite/sideloaded-plugins`
4. Open **RuneLite (configure)**. Add `-ea` to **JVM arguments** and `--developer-mode` to **Client arguments**, then click **Save**. RuneLite only loads sideloaded plugins in developer mode.
5. Start RuneLite as usual. **AFK Minimize** is now in the plugin list.

If you update the plugin, build the jar again and replace the old one.

## Setup

1. Turn on **AFK Minimize**.
2. Turn on **Idle Notifier** and pick the alerts you want, for example **Idle animation** and **Inventory full**.
3. In the **RuneLite** settings, open **Notification Settings** and set **Request focus** to **Force**. This setting brings the client to the front when an alert fires.

## How it works

1. An alert fires while you are in another window, and the client comes to the front.
2. Each click in the game starts a countdown. A large timer at the top of the screen shows the time that is left. The timer goes from green to red.
3. If you click again before the timer gets to 0, the countdown starts again.
4. When the timer gets to 0, the client minimizes.

The plugin only arms when an alert fires while you are in another window. If you are already playing, your clicks never minimize the client.

## Keep the client open

To stop the countdown, click the timer box at the top of the screen. The click does not go to the game, so your character does not walk.

You can also set a **Keep open key** in the settings. Hold that key while you click to stop the countdown. It has no default, because Alt, Ctrl, and Shift already do things in RuneLite and the game.

The plugin stays armed, so your next normal click starts the countdown again.

## Settings

| Setting | Default | What it does |
| --- | --- | --- |
| Keep open key | Not set | Hold this key while you click to stop the countdown |
| Minimize delay | 1000 ms | Time from your last click until the client minimizes |

To move the timer, hold **Alt** and drag it.
