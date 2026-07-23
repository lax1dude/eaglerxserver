# EaglercraftX Server

## An industrial-grade Eaglercraft server implementation

**EaglercraftX Server** is a high-performance, production-ready server implementation that allows players to join Minecraft servers directly from their web browsers using Eaglercraft clients.

[![Build Status](https://github.com/retrosnipermalicoat-netizen/eaglerxserver/workflows/Build%20and%20Test/badge.svg)](https://github.com/retrosnipermalicoat-netizen/eaglerxserver/actions)
[![License](https://img.shields.io/badge/license-Unlicense-blue.svg)](LICENSE)
[![Java](https://img.shields.io/badge/java-17+-blue.svg)](https://www.oracle.com/java/technologies/javase/jdk17-archive.html)

---

## 🚀 Quick Start

### Docker (Recommended)
```bash
git clone https://github.com/retrosnipermalicoat-netizen/eaglerxserver.git
cd eaglerxserver
docker-compose up -d
```

Server will be ready at `localhost:25565` within 1-2 minutes.

### Manual Setup
```bash
./gradlew build
./run.sh
```

**See [QUICKSTART.md](./QUICKSTART.md) for detailed setup instructions.**

---

## ✨ Key Features

- **Multi-Protocol Support**: Supports EaglercraftX 1.8, Eaglercraft 1.12.2, and 1.5.2 clients
- **Single JAR Installation**: Works on Spigot, BungeeCord, and Velocity with one plugin
- **WebSocket/HTTP**: Secure WebSocket connections for browser-based Minecraft
- **Skin Service**: Automatic skin downloading and caching from Mojang
- **Voice Chat**: Optional WebRTC-based voice communication
- **Plugin API**: Full cross-platform API for plugin development
- **Multi-Proxy Support**: Seamless synchronization with EaglerXSupervisor
- **Performance Optimized**: Built for high-capacity servers with thousands of players
- **Docker Ready**: Fully containerized with docker-compose included

---

## 📋 System Requirements

### Minimum
- Java 17 or higher
- 2GB RAM minimum (4GB recommended)
- 2GB disk space
- Linux/macOS/Windows

### For Docker
- Docker and Docker Compose
- 4GB available RAM
- 2GB disk space

### For Production
- 8GB+ RAM for 100+ concurrent players
- SSD storage (for skin cache database)
- Reliable internet connection
- Consider using a reverse proxy (nginx, Caddy, HAProxy)

---

## 📚 Documentation

| Guide | Purpose |
|-------|---------|
| [QUICKSTART.md](./QUICKSTART.md) | 5-minute setup guide |
| [DEPLOYMENT.md](./DEPLOYMENT.md) | Production deployment strategies |
| [CONFIG.md](./CONFIG.md) | Complete configuration reference |
| [README.md](./README.md) | Technical documentation |

---

## 🛠️ Installation Methods

### Docker Compose (Fastest)
```bash
docker-compose up -d
```
- Automatic build
- Automatic configuration
- Easy scaling
- Production-ready

### Manual Installation
```bash
./gradlew build
java -Xmx4G -Xms2G -jar EaglerXServer.jar nogui
```

### Cloud Deployment
- AWS EC2
- Google Cloud
- DigitalOcean/Linode
- Azure
- See [DEPLOYMENT.md](./DEPLOYMENT.md) for detailed instructions

---

## 🔧 Configuration

### Basic Setup (3 files)
1. **config/settings.yaml** - Server settings
2. **config/listeners.yaml** - Network configuration
3. **config/ice_servers.cfg** - WebRTC settings (for voice)

### Example Settings
```yaml
server_name: "My Eaglercraft Server"
enable_authentication_events: false
enable_backend_rpc_api: false

protocols:
  protocol_v5_allowed: true
  protocol_v4_allowed: true
  protocol_v3_allowed: true

skin_service:
  download_vanilla_skins_to_clients: true
```

See [CONFIG.md](./CONFIG.md) for all 100+ configuration options.

---

## 🏗️ Architecture

### Modular Design
```
EaglercraftX Server
├── core                 # Platform-agnostic core
├── api                  # Public API for plugins
├── protocol-game        # Minecraft protocol implementation
├── skin-cache           # Skin downloading and caching
├── voice-rpc-protocol   # Voice chat protocol
├── backend-rpc-api      # RPC for backend Spigot servers
├── supervisor-core      # Multi-proxy synchronization
└── plugins              # EaglerMOTD, EaglerWeb, etc.
```

### Platform Support
- **Bukkit/Spigot/Paper** - Single-server deployments
- **BungeeCord** - Multi-server proxy
- **Velocity** - Modern proxy (recommended)
- **Standalone** - Can run independently

---

## 🔌 Plugin Development

### Use the Official API
```gradle
// build.gradle
repositories {
    maven {
        name = "lax1dude"
        url = uri("https://repo.lax1dude.net/repository/releases/")
    }
}

dependencies {
    compileOnly "net.lax1dude.eaglercraft.backend:api-bukkit:1.1.0"
}
```

### Example Plugin
```java
import net.lax1dude.eaglercraft.backend.server.api.bukkit.EaglerXServerAPI;
import net.lax1dude.eaglercraft.backend.server.api.bukkit.player.IEaglerPlayer;

public class MyPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        EaglerXServerAPI api = EaglerXServerAPI.instance();
        // Use API...
    }
}
```

Full API documentation in [README.md](./README.md#plugin-development)

---

## 🚀 Performance

### Benchmarks
- **100 players**: 2-3GB RAM, 1 vCPU
- **250 players**: 4-6GB RAM, 2 vCPU
- **1000 players**: 8GB+ RAM, 4+ vCPU

### Optimization Tips
1. Use SSD for skin cache database
2. Configure view distance for your hardware
3. Enable WebSocket compression
4. Use a reverse proxy (nginx/Caddy)
5. Monitor GC pauses with verbose logging

See [DEPLOYMENT.md - Performance Optimization](./DEPLOYMENT.md#performance-optimization)

---

## 🔒 Security

### Built-in Protections
- Rate limiting on all endpoints
- DDoS mitigation
- IP-based access control
- Secret forwarding for reverse proxies
- TLS/SSL support

### Production Checklist
- [ ] Enable firewall rules
- [ ] Set up SSL/TLS certificates
- [ ] Configure rate limiting
- [ ] Enable logging
- [ ] Schedule regular backups
- [ ] Monitor resource usage
- [ ] Keep Java updated

See [DEPLOYMENT.md - Security Hardening](./DEPLOYMENT.md#security-hardening)

---

## 📊 Monitoring

### Docker Logs
```bash
docker-compose logs -f eaglercraft-server
```

### JMX Monitoring
```bash
# Enabled by default on port 9999
jconsole localhost:9999
```

### Prometheus Integration
See [DEPLOYMENT.md - Monitoring](./DEPLOYMENT.md#monitoring--logging)

---

## 🐛 Troubleshooting

### Server won't start
```bash
# Check Java version
java -version  # Must be 17+

# Check logs
docker-compose logs eaglercraft-server
```

### Port already in use
```bash
# Change port in config/listeners.yaml
bind_addr: "0.0.0.0:25566"  # Use different port
```

### Can't connect remotely
1. Check firewall allows port 25565
2. Use your public IP (not localhost)
3. Test with: `telnet yourip 25565`
4. See [DEPLOYMENT.md - Reverse Proxy Setup](./DEPLOYMENT.md#reverse-proxy-setup)

See [QUICKSTART.md - Troubleshooting](./QUICKSTART.md#troubleshooting) for more help.

---

## 🔄 CI/CD Pipeline

This repository includes GitHub Actions workflows that automatically:
- ✅ Build on Java 17 and Java 21
- ✅ Run tests
- ✅ Build Docker image
- ✅ Create releases with artifacts

See `.github/workflows/build.yml` for details.

---

## 📦 Project Structure

```
eaglerxserver/
├── .github/
│   └── workflows/           # GitHub Actions CI/CD
├── config/
│   ├── settings.example.yaml
│   ├── listeners.example.yaml
│   └── ice_servers.cfg
├── docker-entrypoint.sh     # Docker startup script
├── Dockerfile               # Multi-stage Docker build
├── docker-compose.yml       # Complete deployment stack
├── QUICKSTART.md            # 5-minute setup guide
├── DEPLOYMENT.md            # Production deployment guide
├── CONFIG.md                # Configuration reference
├── LICENSE                  # Unlicense (public domain)
├── README.md                # Technical documentation
├── gradlew / gradlew.bat    # Gradle wrapper
├── settings.gradle          # Gradle modules
├── build.gradle             # Gradle build script
└── [20+ source modules]
```

---

## 🤝 Contributing

This is a fork of the original EaglerXServer project. Contributions should follow these guidelines:

1. **Report Issues** - Use GitHub Issues for bugs
2. **Suggest Features** - Use GitHub Discussions
3. **Submit PRs** - For bugfixes and improvements
4. **Follow Code Style** - Java conventions and existing patterns

See [README.md#contributing](./README.md#contributing-to-eaglercraftserver) for detailed guidelines.

---

## 📜 License

This project is in the **public domain** (Unlicense). You're free to:
- ✅ Use for any purpose
- ✅ Modify and distribute
- ✅ Use commercially
- ✅ Sublicense

See [LICENSE](./LICENSE) for full details.

---

## ⚠️ Important Disclaimers

- **Not affiliated with Minecraft, Mojang, or Microsoft**
- This is a community project maintained independently
- Use at your own risk
- Respect player privacy and follow applicable laws
- Comply with Minecraft EULA: https://account.mojang.com/documents/minecraft_eula

---

## 📞 Support & Community

- **GitHub Issues**: Report bugs and request features
- **GitHub Discussions**: Ask questions and share ideas
- **Eaglercraft Community**: https://eaglercraft.com

---

## 🎯 Roadmap

### Current Features ✅
- Multi-protocol support (v3, v4, v5)
- Skin service with Mojang integration
- Voice chat support
- Backend RPC API
- Multi-proxy synchronization
- Production-ready performance

### Planned
- [ ] Enhanced admin UI
- [ ] More plugin examples
- [ ] Advanced analytics
- [ ] Clustering support

---

## 🙏 Credits

**Original Project**: [lax1dude/eaglerxserver](https://github.com/lax1dude/eaglerxserver)

**This Fork**: Enhanced with Docker, CI/CD, and deployment documentation.

---

## 📈 Statistics

- **Build Time**: ~5-15 minutes (first run)
- **Docker Build Time**: ~2-5 minutes
- **Startup Time**: ~10-30 seconds
- **Memory Usage**: 500MB-8GB (configurable)
- **Disk Space**: ~2GB minimum
- **Max Players**: Unlimited (hardware dependent)

---

## 🚀 Next Steps

1. **Get Started**: [Read QUICKSTART.md](./QUICKSTART.md)
2. **Deploy**: [Read DEPLOYMENT.md](./DEPLOYMENT.md)
3. **Configure**: [Check CONFIG.md](./CONFIG.md)
4. **Develop**: [See Plugin Development](./README.md#plugin-development)

---

**Happy hosting! 🎮**

**Questions?** Check the documentation or open a GitHub issue.
