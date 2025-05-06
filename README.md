# EaglercraftXServer

### An industrial-grade Eaglercraft server

a.k.a. "EaglerXServer"

> [!WARNING]
> This is not a "crack" for Minecraft, it simply changes how frames (and a few packets) are encoded in the Minecraft protocol to make it compatible with browsers and extend its capabilities, it does not tamper with the server's authentication settings or do anything to modify the authentication state of inbound connections.

**Delete EaglercraftXBungee and EaglercraftXVelocity today!**

## Key Features

- **EaglercraftX 1.8, Eaglercraft 1.12.2, and Eaglercraft 1.5.2 clients are supported**
- **Install on Spigot, BungeeCord, or Velocity from only a single JAR file**
- **Access dozens of exclusive Eaglercraft features through a cross-platform API**
- **Install packet translator modules for legacy protocol versions (1.5, 1.6, beta, etc)**
- **Native RPC protocol to access the API of a BungeeCord/Velocity server from Spigot** 
- **Synchronize multi-proxy setups seamlessly using EaglerXSupervisor**

## Downloads

**If you lack the motivation to figure this out yourself, you can get started quickly by downloading a complete Paper 1.12.2 Eaglercraft server distribution from [https://github.com/Eaglercraft-Templates/Eaglercraft-Server-Paper](https://github.com/Eaglercraft-Templates/Eaglercraft-Server-Paper) that supports EaglercraftX 1.8, Eaglercraft 1.12.2, and Eaglercraft 1.5.2 clients all from a single server.**

You will not receive support for issues installing EaglercraftXServer on Spigot (or forks of Spigot like Paper) if you are using a version of Spigot greater than 1.12.2, because limitations in plugin messages prevent protocol V4 and below EaglercraftX clients from working on these servers properly.

Please see the [Releases](https://github.com/lax1dude/eaglerxserver/releases) tab to get the latest stable binaries, the available files are:

- **EaglerXServer** - The core EaglercraftXServer plugin for Spigot, BungeeCord, and Velocity
- **EaglerXRewind** - Add support for Eaglercraft 1.5.2 by translating the packets to 1.8 like ViaVersion
- **EaglerXBackendRPC** - Access the API of a BungeeCord/Velocity eagler server from Spigot
- **EaglerMOTD** - A port of the EaglerMOTD plugin to the EaglercraftXServer API
- **EaglerWeb** - Allows you to host files via HTTP from your server address
- **EaglerXPlan** - Plan player analytics extension for Eaglercraft players
- **EaglerXSupervisor** - Standalone "supervisor" daemon for multi-proxy setups

## Installation

To get started, place the EaglerXServer JAR in the "plugins" folder of your Spigot, BungeeCord, or Velocity server. In most cases you will also need to use ViaVersion, ViaBackwards, and ViaRewind to make your Spigot servers compatible with 1.8. If you would like to support 1.5, add the EaglerXRewind JAR to the "plugins" folder as well. Add EaglerMOTD for animated MOTDs and EaglerWeb if you want to host a website from your server.

**Velocity Note:** You may have issues if you attempt to use EaglercraftXServer with other plugins that also register plugin message event handlers, because Velocity's event bus will cause the plugin messages to be observed in an undefined order if an async handler is given higher priority than EaglercraftXServer.

## Configuration

On BungeeCord and Velocity, EaglercraftXServer should generate detailed comments in the config files, describing each property in detail. However this does not work on most Spigot versions (like 1.12) due to the YAML parser/serializer bundled with it not supporting comments. There will eventually be a configuration guide for people who get stuck without comments in their config files, however this has not been completed yet.

## Plugin Development

One of the goals of EaglercraftXServer is to provide a proper API for interacting with EaglercraftX-based clients. The API allows you to do things such as changing player skins, creating and managing voice channels, and using the authentication and cookie system in the EaglercraftX client.

The main API module is fully cross-platform and provides support for Spigot, BungeeCord, and Velocity servers through one unified set of generic interfaces. However, due to limitations in the event buses found on Spigot and BungeeCord, the actual event types have to be defined per-platform in order to have the correct class heirarchy to allow them to be dispatched. Because of that, there are separate API modules for Spigot, BungeeCord, and Velocity to define the events, that also include the main API module automatically as a transitive dependency.

Additionally, the EaglercraftX 1.8 protocol module is exposed in the API as well. Its official use is in parts of the packet translator module interface (EaglerXRewind), but you can also send these packets directly to EaglercraftX clients with no gaurantee of forwards or backwards compatibility.

We also plan to write a javadoc for 100% of all the API's classes, interfaces, and methods. This has not been completed yet but will hopefully be soon.

### Maven Repository

The stable API is available through a Maven repository, add it to your `build.gradle` by putting the following lines in the `repositories` block:

```gradle
maven {
	name = "lax1dude"
	url = uri("https://repo.lax1dude.net/repository/releases/")
}
```

### Development on Spigot

Add the following line to your Spigot plugin's Gradle `dependencies` block to use EaglercraftXServer with the Spigot API:

```gradle
compileOnly "net.lax1dude.eaglercraft.backend:api-bukkit:1.0.0"
```

The native Spigot version of the API is Paper 1.12.2, but it will also work with most other legacy versions of the Spigot API. Your project must be using at least Java 17, otherwise Gradle will probably pretend that it can't find the dependencies.

Be sure to add `depend: [ EaglercraftXServer ]` to your `plugin.yml` or things will not work.

We don't currently support Folia, you'd be better off with BungeeCord or Velocity at that point.

Call `EaglerXServerAPI.instance()` (using `import net.lax1dude.eaglercraft.backend.server.api.bukkit.EaglerXServerAPI;`) during or after your plugin's `onLoad` handler has been called to access the API.

#### Known issues on Spigot

- You cannot set a player's UUID through any Eaglercraft login events

### Development on BungeeCord

Add the following line to your BungeeCord plugin's Gradle `dependencies` block to use EaglercraftXServer with the BungeeCord API:

```gradle
compileOnly "net.lax1dude.eaglercraft.backend:api-bungee:1.0.0"
```

Be sure to add `depends: [ EaglercraftXServer ]` to your `plugin.yml` or things will not work.

Your project must be using at least Java 17, otherwise Gradle will probably pretend that it can't find the dependencies.

Call `EaglerXServerAPI.instance()` (using `import net.lax1dude.eaglercraft.backend.server.api.bungee.EaglerXServerAPI;`) during or after your plugin's `onLoad` handler has been called to access the API.

#### Known issues on BungeeCord

- `PostLoginEvent` is blocked asynchronously until the EaglerXServer player is initialized, meaning you cannot reliably obtain the `IBasePlayer` or `IEaglerPlayer` from within a `PostLoginEvent` handler. You must use `EaglercraftInitializePlayerEvent` to reliably obtain the `IEaglerPlayer` or wait until after `PostLoginEvent` has fired before you attempt to access the player instance through the EaglercraftXServer API.

### Development on Velocity

Add the following line to your Velocity plugin's Gradle `dependencies` block to use EaglercraftXServer with the Velocity API:

```gradle
compileOnly "net.lax1dude.eaglercraft.backend:api-velocity:1.0.0"
```

Be sure to add `dependencies = { @Dependency(id = EaglerXServerAPI.PLUGIN_ID, optional = false) }` to your plugin annotation or things will not work.

Your project must be using at least Java 17, otherwise Gradle will probably pretend that it can't find the dependencies.

Call `EaglerXServerAPI.instance()` (using `import net.lax1dude.eaglercraft.backend.server.api.velocity.EaglerXServerAPI;`) during or after your plugin is constructed to access the API.

#### Known issues on Velocity

- Avoid async event handlers for code that is sensitive to the order events are observed in (like handling WebView message events), Velocity's event bus will cause the order to be undefined unless your handler, and all handlers of a higher priority, are not async. All event handlers in Velocity currently default to being async unless you explicitly add `async = false` to the subscribe annotation!

## Using the RPC API

If you are using EaglercraftXServer on BungeeCord or Velocity, and want to access the API from your backend Spigot servers, you can use the backend RPC API. Only a subset of the core API is currently available, mainly functions related to player objects, such as detecting Eaglercraft players or retrieving additional Eaglercraft-related information about a player.

Add the following line to your Spigot plugin's Gradle `dependencies` block to use EaglercraftXBackendRPC with the Spigot API:

```gradle
compileOnly "net.lax1dude.eaglercraft.backend:backend-rpc-api-bukkit:1.0.0"
```

You will also need to add the EaglercraftXBackendRPC JAR file to you Spigot plugins folder, and enable the backend RPC API in the EaglercraftXServer settings file.

Call `EaglerXBackendRPC.instance()` (using `import net.lax1dude.eaglercraft.backend.rpc.api.bukkit.EaglerXBackendRPC;`) during or after your plugin is constructed to access the API.

## Contributing to EaglercraftXServer

I'll accept PRs for bugfixes and performance improvements, but if you plan to change the API or add a new feature you need to create an issue first explaining what you plan to do. If you attempt to PR new features or API without talking it over in an issue first, there's no timeline for when your code will be actually be reviewed or merged.

Attention to detail is crucial, if your contributions were made as a result of attempting to apply knowledge of higher level languages and frameworks (such as JavaScript or Node.js) to Java and Netty without proper research, or your code is horribly inefficient due to not attempting to use proper data structures and/or accessing them correctly, your contributions will be rejected!

If you contribute to EaglercraftXServer, you must consent to your patches being considered part of the public domain. You are not allowed to GPL your contributions or attach any license to them besides what is in this repository's LICENSE file. If you try to attach some kind of foreign license to your contribution, it will not be accepted.

## Forking EaglercraftXServer

There are no limitations on what you are allowed to do with the source of EaglercraftXServer, although the creators will probably be less than thrilled if you try to take credit for portions of it without substantial modifications. There are some classes from open-source libraries like HPPC that have been copied into EaglercraftXServer which have additional restrictions on them, but there's nothing GPL in here that'll give you trouble if you don't make your patches to the code public. 
