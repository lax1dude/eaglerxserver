# Server Access Guide

## 🎮 How to Join Your Server

### Eaglercraft Players
1. Go to Eaglercraft website or client
2. In server list, add custom server:
   - **Server Address:** `play.cecilmc.net`
   - **Port:** `443` (if using HTTPS) or `80` (if HTTP-only)
3. Click connect
4. Register/Login with your password (AuthMe)
5. Start playing!

### Java Edition Players (Regular Minecraft)
```
Server: play.cecilmc.net
Port: 25565 (standard Minecraft port)
```
- Add to server list
- Join the server
- Op status for `cecilthecreator`

---

## 🔧 Server Configuration

**Server Details:**
- 📍 **Address:** `play.cecilmc.net`
- 🌐 **IP Address:** `100.55.67.89`
- 🎮 **Eaglercraft Port:** 8080 (proxied via nginx)
- 🎮 **Minecraft Port:** 25565
- 🔐 **Authentication:** AuthMe (password protected)
- 👑 **Owner:** `cecilthecreator` (OP Level 4)

---

## 📋 Installed Plugins & Mods

### Building & Protection
- **WorldEdit 7.4.4** - Advanced world editing tools
- **WorldGuard 7.0.17** - Region protection and management
- **GriefPrevention** - Automatic land claim protection

### Fun Features
- **VehiclesCrafting 1.0.1** - Drivable cars and vehicles
- **Citizens** - NPCs and custom entities
- **EaglerMOTD** - Custom server MOTD

### Admin Tools
- **LuckPerms** - Permission management
- **AuthMe Reloaded** - Login protection
- **Protection API** - Advanced protection features

---

## 🔐 Security Setup

### Password Authentication (AuthMe)
**Owner Account:** `cecilthecreator`
- Set password on first login: `/register <password> <password>`
- Login each session: `/login <password>`
- Protected with BCRYPT encryption

### Permissions (LuckPerms)
- `cecilthecreator` has Level 4 OP status
- Full access to all commands
- Can manage all plugins

---

## 🚀 Getting Started

### First Time Setup
1. Ensure DNS is propagated: `nslookup play.cecilmc.net`
2. Start your server
3. Join as `cecilthecreator`
4. Register with AuthMe: `/register MyPassword MyPassword`
5. Configure your server settings

### Adding Players
- Players join and register their own accounts
- Use WorldGuard to protect spawn area
- Configure region flags for safety
- Set up land claims with GriefPrevention

### Server Management Commands
```
# General
/plugins - List all plugins
/help - Get help
/stop - Stop server
/save-all - Save world

# WorldEdit
//wand - Get selection wand
//pos1, //pos2 - Set selection
//set <block> - Fill area

# WorldGuard
/region define <id> - Create protection region
/region claim <id> - Claim a region
/region flag <id> <flag> <value> - Set flags

# AuthMe
/register <password> <password> - Register account
/login <password> - Login
/changepassword <old> <new> - Change password
```

---

## 🔗 Server Links

- **Main Domain:** `cecilmc.net`
- **Play Server:** `play.cecilmc.net`
- **GitHub:** `https://github.com/retrosnipermalicoat-netizen/eaglerxserver`

---

## 📞 Troubleshooting

### Can't connect to server?
- Verify DNS propagation: `ping play.cecilmc.net`
- Check firewall allows port 8080/443 (Eaglercraft) and 25565 (Minecraft)
- Ensure server is running: `docker-compose ps`
- Check logs: `docker-compose logs eaglercraft-server`

### AuthMe not working?
- Verify plugin is installed: `/plugins`
- Check authme.db exists: `ls plugins/AuthMe/`
- Clear cache and restart server

### Performance issues?
- Check server resources: `docker stats`
- Increase memory in docker-compose.yml
- Reduce view distance for players
- Use WorldGuard to limit active regions

---

## 📚 Additional Resources

- **WorldEdit Wiki:** https://worldedit.enginehub.org/
- **WorldGuard Wiki:** https://worldguard.enginehub.org/
- **Eaglercraft:** https://eaglercraft.com
- **LuckPerms Docs:** https://luckperms.net/wiki/

---

**Server is live and ready!** 🎉
