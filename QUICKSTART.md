# EaglercraftX Server - Quick Start Guide

Welcome to EaglercraftX Server! This guide will help you set up and run your own Eaglercraft server in minutes.

## Prerequisites

Choose one of the following setups:

### Option 1: Docker (Recommended)
- Docker Desktop or Docker Engine installed
- 4GB RAM minimum available
- 2GB disk space

### Option 2: Manual Setup
- Java 17 or higher
- Gradle (optional, included as wrapper)
- 4GB RAM minimum
- 2GB disk space

---

## Quick Start with Docker 🐳

### 1. Clone the Repository
```bash
git clone https://github.com/retrosnipermalicoat-netizen/eaglerxserver.git
cd eaglerxserver
```

### 2. Start the Server
```bash
docker-compose up -d
```

The server will:
- Build automatically on first run
- Create necessary directories
- Generate default configuration
- Start listening on `localhost:25565`

### 3. Check Server Status
```bash
docker-compose logs -f eaglercraft-server
```

### 4. Stop the Server
```bash
docker-compose down
```

### 5. Access the Server
- **Server Address**: `localhost:25565`
- **Eaglercraft Client**: Use your Eaglercraft client and connect to your server's IP
- **Java Edition**: Standard Minecraft Java clients can also connect

---

## Manual Setup (Linux/macOS/Windows)

### 1. Clone the Repository
```bash
git clone https://github.com/retrosnipermalicoat-netizen/eaglerxserver.git
cd eaglerxserver
```

### 2. Build the Project
```bash
# macOS/Linux
chmod +x gradlew
./gradlew build

# Windows
gradlew.bat build
```

The build process will:
- Download all dependencies
- Compile all modules
- Generate JAR files in `build/libs/`

**Build time**: 5-15 minutes (first run)

### 3. Run the Server

#### Using the provided script:
```bash
# macOS/Linux
./run.sh

# Windows - Create run.bat with:
@echo off
java -Xmx4G -Xms2G -jar EaglerXServer.jar nogui
pause
```

#### Or manually:
```bash
java -Xmx4G -Xms2G -jar EaglerXServer.jar nogui
```

### 4. Initial Configuration
On first run, the server will generate:
- `/config/settings.yaml` - Main server settings
- `/config/listeners.yaml` - Network listener configuration
- `/config/ice_servers.cfg` - WebRTC/Voice settings

### 5. Access the Server
Once you see `Done (X.XXXs)!` in the console:
- Server is ready at `localhost:25565`
- For remote access, use your public IP address

---

## Configuration

### Basic Server Settings (`config/settings.yaml`)

```yaml
server_name: "My Eaglecraft Server"        # Server name shown in server list
server_uuid: ""                             # Unique server ID (auto-generated)

# Enable features
enable_authentication_events: false         # Advanced auth features
enable_backend_rpc_api: false              # Backend RPC support
enable_is_eagler_player_property: true     # Mark Eaglercraft players

# Protocol versions
protocols:
  protocol_v5_allowed: true                # Latest protocol
  protocol_v4_allowed: true                # EaglercraftX 1.8
  protocol_v3_allowed: true                # Older clients
  
# Skin and cosmetics
skin_service:
  download_vanilla_skins_to_clients: true  # Download player skins from Mojang
  
# Voice chat (optional)
voice_service:
  enable_voice_service: false              # Disable for public servers
```

### Network Configuration (`config/listeners.yaml`)

```yaml
listener_list:
  - bind_addr: "0.0.0.0:25565"            # Server bind address:port
    inject_address: "127.0.0.1:25565"     # Address to advertise to clients
    server_motd:
      - "&6Welcome to Eaglercraft!"
      - "&ePlay Minecraft in your browser!"
    allow_motd: true                       # Show server in server lists
    allow_query: true                      # Respond to server queries
    dual_stack: true                       # Support both Eaglercraft and Java Edition
```

### Full Configuration Reference

See [CONFIG.md](./CONFIG.md) for all available settings and options.

---

## Adding Plugins

### Install Spigot/Paper (Optional)

If you want to use Bukkit plugins:

1. Download Paper JAR: https://papermc.io/
2. Replace `EaglerXServer.jar` with Paper JAR
3. Install the EaglerXServer plugin into the `plugins/` folder
4. Add other plugins to `plugins/`

### Install Eaglercraft Plugins

Place plugin JARs in the `plugins/` directory:
- `EaglerMOTD.jar` - Custom MOTD
- `EaglerWeb.jar` - Web interface
- Third-party Eaglercraft plugins

---

## Firewall & Port Forwarding

### Opening Ports

If hosting from home, forward these ports to your server:
- **25565** - Main server port (Minecraft/Eaglercraft)
- **8080** - WebSocket port (if not using reverse proxy)
- **8443** - Secure WebSocket (if SSL enabled)

### Using a Reverse Proxy

For production, use nginx or Caddy:

**Nginx example:**
```nginx
server {
    listen 80;
    server_name yourdomain.com;
    
    location / {
        proxy_pass http://localhost:25565;
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

---

## Troubleshooting

### Server won't start
```
ERROR: Could not find or load main class...
```
**Solution**: Ensure Java 17+ is installed
```bash
java -version
```

### Port already in use
```
Failed to bind to 0.0.0.0:25565
```
**Solution**: Change port in `config/listeners.yaml` or kill the process using the port
```bash
# Linux/macOS
lsof -i :25565

# Windows
netstat -ano | findstr :25565
```

### Out of memory
```
java.lang.OutOfMemoryError: Java heap space
```
**Solution**: Increase allocated memory in `run.sh` or docker-compose
```bash
# Before: java -Xmx4G ...
# After: java -Xmx8G ...
```

### Can't connect from different network
- Use your **public IP** not `localhost`
- Ensure firewall allows port 25565
- Check port forwarding configuration
- Test with: `telnet yourip 25565`

### Skin not showing
- Set `download_vanilla_skins_to_clients: true` in settings
- Players need valid Minecraft accounts
- Ensure internet connection for skin downloads

---

## Performance Tuning

### Optimize for Low-End Servers
```yaml
# config/settings.yaml
eagler_players_view_distance: 8           # Reduce view distance

# Java options
java -Xms1G -Xmx2G -XX:+UseG1GC ...
```

### Optimize for High-Performance
```yaml
# Java options with more threads
java -Xms8G -Xmx16G \
  -XX:+UseG1GC \
  -XX:MaxGCPauseMillis=200 \
  -XX:G1NewCollectionPercentage=30 \
  -XX:InitiatingHeapOccupancyPercent=15 ...
```

---

## Backup and Restore

### Backup World Data
```bash
# Docker
docker-compose exec eaglercraft-server tar -czf /backups/world-backup.tar.gz /eaglercraft/world

# Manual
tar -czf backups/world-backup.tar.gz world/
```

### Restore from Backup
```bash
# Docker
docker-compose exec eaglercraft-server tar -xzf /backups/world-backup.tar.gz -C /eaglercraft

# Manual
tar -xzf backups/world-backup.tar.gz
```

---

## Updating the Server

### Docker Update
```bash
git pull origin main
docker-compose down
docker-compose up -d --build
```

### Manual Update
```bash
git pull origin main
./gradlew clean build
# Stop old server and start new one
```

---

## Next Steps

1. **Customize your MOTD** - Edit `config/listeners.yaml`
2. **Add plugins** - Place JAR files in `plugins/` folder
3. **Enable voice chat** - Set `enable_voice_service: true` (experimental)
4. **Set up backups** - Schedule regular world backups
5. **Join the community** - Discord: (Link if available)

---

## Support & Resources

- **GitHub Issues**: Report bugs and request features
- **CONFIG.md**: Complete configuration reference
- **README.md**: Detailed technical documentation
- **Eaglercraft Home**: https://eaglercraft.com

---

## License

EaglercraftX Server is provided as-is. See LICENSE file for details.

**Important**: This is not an official Minecraft product and is not affiliated with Mojang Studios or Microsoft.

---

Happy hosting! 🎮
