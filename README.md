# EaglercraftXServer

### An industrial-grade Eaglercraft server

a.k.a. "EaglerXServer"

> [!WARNING]
> This is not a "crack" for Minecraft, it simply changes how frames are encoded in the Minecraft protocol to make it compatible with browsers, it does not tamper with the server's authentication subsystem or do anything to force the authentication state of inbound connections.

**Delete EaglercraftXBungee and EaglercraftXVelocity today!**

### Key Features
- **EaglercraftX 1.8, Eaglercraft 1.12.2, and Eaglercraft 1.5.2 clients are supported**
- **Install on Spigot, BungeeCord, or Velocity from only a single JAR file**
- **Access dozens of exclusive Eaglercraft features through a cross-platform API**
- **Install packet translator modules for legacy protocol versions (1.5, 1.6, beta, etc)**
- **Native RPC protocol to access the API of a BungeeCord/Velocity server from Spigot** 
- **Synchronize multi-proxy setups seamlessly using EaglerXSupervisor**

### Downloads

If you lack the motivation to figure this out yourself, you can get started quickly by downloading a complete Paper 1.12.2 Eaglercraft server distribution from LINK that supports EaglercraftX 1.8, Eaglercraft 1.12.2, and Eaglercraft 1.5.2 clients all from a single server.

Please see the Releases tab to get the latest stable binaries, the available files are:

- **EaglerXServer** - The core EaglercraftXServer plugin for Spigot, BungeeCord, and Velocity
- **EaglerXRewind** - Add support for Eaglercraft 1.5.2 by translating the packets to 1.8 like ViaVersion
- **EaglerXBackendRPC** - Access the API of a BungeeCord/Velocity eagler server from Spigot
- **EaglerMOTD** - A port of the EaglerMOTD plugin to the EaglercraftXServer API
- **EaglerWeb** - Allows you to host files via HTTP from your server address
- **EaglerXPlan** - Plan player analytics extension for Eaglercraft players
- **EaglerXSupervisor** - Standalone "supervisor" daemon for multi-proxy setups

### Configuration

On BungeeCord and Velocity, EaglercraftXServer should generate detailed comments in the config files, describing each property in detail. However this does not work on most Spigot versions (like 1.12) due to the YAML parser/serializer bundled with it not supporting comments. We have not completed documentation yet for people to follow who get stuck without comments in their config file, sorry!