# Fish Slap

You spawn in with nothing but your wits and a raw cod. Go forth and conquer.

This is a lightweight but fun minigame. The goal is to knock other players into the void using your extremely powerful fish. If you knock someone in, you get a point. Players don't have to wait to die in the void—you can specify the y-level that they'll be teleported back to spawn from.

The top five players are displayed on a scoreboard!

![Scoreboard Example](https://i.imgur.com/Su7BMcU.png)

This game works well as a game on the server hub—a little something to do while you're hanging out.

Drop [this bad boy](https://github.com/TheKingElessar/FishSlap/releases) in your `plugins` folder. Start the server, and the necessary files, particularly the `config.yml` file, will be generated. You can see an example below.

This plugin was custom-made, but was inspired by the [CubeKrowd](https://www.cubekrowd.net/) server. The server has a bunch of great minigames—check it out!

### Commands:      
`/fishslap setspawn`: Sets the server spawn to your current location.

`/fishslap setspawn <x> <y> <z> <yaw> <pitch>`: Sets the server spawn to the specified location. `yaw` and `pitch` are optional.

`/fishslap level <y-level>`: Sets the level that players will be teleported to spawn from.

`/fishslap reload`: Reloads the config file.

### Permissions:
`fishslap.setspawn`: Allows setting of the spawn-point.

`fishslap.level`: Allows setting of the minimum level from which players will be teleported back to spawn.

`fishslap.reload`: Allows reloading of the config.

### Config
```
# The level players can fall to
min_y_level: 135

# The server spawn point
spawn_x: 0.5
spawn_y: 163.5
spawn_z: 0.5
yaw: -270
pitch: 0
```