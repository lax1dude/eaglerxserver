# EaglercraftX Server Deployment Guide

This guide covers deploying EaglercraftX Server to various environments.

## Table of Contents

1. [Local Development](#local-development)
2. [Docker Deployment](#docker-deployment)
3. [Cloud Hosting](#cloud-hosting)
4. [Reverse Proxy Setup](#reverse-proxy-setup)
5. [Multi-Proxy Configuration](#multi-proxy-configuration)
6. [Performance Optimization](#performance-optimization)
7. [Monitoring & Logging](#monitoring--logging)
8. [Security Hardening](#security-hardening)

---

## Local Development

### Quick Setup

```bash
# Clone repository
git clone https://github.com/retrosnipermalicoat-netizen/eaglerxserver.git
cd eaglerxserver

# Build
./gradlew build

# Run
./run.sh
```

### Development with Live Reload

```bash
# Watch for changes and rebuild
./gradlew build --continuous

# In another terminal, run the server
./run.sh
```

---

## Docker Deployment

### Basic Docker Setup

```bash
# Build image
docker build -t eaglerxserver:latest .

# Run container
docker run -d \
  --name eaglercraft \
  -p 25565:25565 \
  -p 8080:8080 \
  -v eaglercraft-data:/eaglercraft \
  -e EULA=true \
  eaglerxserver:latest
```

### Docker Compose (Recommended)

```bash
# Start services
docker-compose up -d

# View logs
docker-compose logs -f eaglercraft-server

# Stop services
docker-compose down

# Stop and remove volumes
docker-compose down -v
```

### Custom Configuration with Docker

```bash
# Create custom config
mkdir -p ./config
cp config/settings.example.yaml ./config/settings.yaml
# Edit ./config/settings.yaml

# Mount config volume
docker run -d \
  --name eaglercraft \
  -v ./config:/eaglercraft/config \
  -v ./plugins:/eaglercraft/plugins \
  -e EULA=true \
  eaglerxserver:latest
```

### Docker Environment Variables

| Variable | Default | Description |
|----------|---------|-------------|
| EULA | false | Accept Minecraft EULA |
| SERVER_NAME | EaglercraftX Server | Server display name |
| MAX_MEMORY | 4G | Maximum heap memory |
| MIN_MEMORY | 2G | Minimum heap memory |
| JAVA_OPTS | (default) | Additional JVM options |

### Scaling with Docker

```bash
# Scale to multiple instances (with load balancer)
docker-compose up -d --scale eaglercraft-server=3

# Access with DNS round-robin or HAProxy
```

---

## Cloud Hosting

### AWS Deployment

#### 1. Create EC2 Instance

```bash
# Security group rules
- Inbound: 25565 (TCP) - Minecraft/Eaglercraft
- Inbound: 8080 (TCP) - WebSocket
- Inbound: 22 (TCP) - SSH

# Recommended specs
- Instance Type: t3a.medium or t3a.large
- Storage: 20GB SSD
- OS: Ubuntu 22.04 LTS
```

#### 2. Install Docker

```bash
sudo apt update
sudo apt install -y docker.io docker-compose
sudo usermod -aG docker $USER
newgrp docker
```

#### 3. Deploy Server

```bash
git clone https://github.com/retrosnipermalicoat-netizen/eaglerxserver.git
cd eaglerxserver
docker-compose up -d
```

### Google Cloud Platform

```bash
# Create Compute Engine instance
gcloud compute instances create eaglercraft-server \
  --zone=us-central1-a \
  --machine-type=n2-standard-2 \
  --image-family=ubuntu-2204-lts \
  --image-project=ubuntu-os-cloud \
  --scopes=default

# SSH into instance
gcloud compute ssh eaglercraft-server --zone=us-central1-a

# Follow AWS deployment steps above
```

### DigitalOcean / Linode / Vultr

1. Create 2GB RAM / 1 vCPU droplet with Ubuntu 22.04
2. Use Docker one-click deployment (if available)
3. SSH and run:
   ```bash
   curl -fsSL https://get.docker.com -o get-docker.sh
   sudo sh get-docker.sh
   sudo usermod -aG docker $USER
   
   git clone https://github.com/retrosnipermalicoat-netizen/eaglerxserver.git
   cd eaglerxserver
   docker-compose up -d
   ```

### Heroku Deployment (Limited - no persistent storage)

```bash
# Create Procfile
echo "web: java -Xmx1G -Xms1G -jar EaglerXServer.jar nogui" > Procfile

# Deploy
heroku login
heroku create your-eaglercraft-server
git push heroku main
```

---

## Reverse Proxy Setup

### Nginx Configuration

```nginx
upstream eaglercraft_backend {
    server localhost:25565;
}

server {
    listen 80;
    server_name eaglercraft.example.com;
    
    # Redirect to HTTPS
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name eaglercraft.example.com;
    
    # SSL certificates (Let's Encrypt)
    ssl_certificate /etc/letsencrypt/live/eaglercraft.example.com/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/eaglercraft.example.com/privkey.pem;
    
    # SSL settings
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers 'ECDHE-ECDSA-AES128-GCM-SHA256:ECDHE-RSA-AES128-GCM-SHA256';
    ssl_prefer_server_ciphers on;
    ssl_session_cache shared:SSL:10m;
    ssl_session_timeout 10m;
    ssl_stapling on;
    ssl_stapling_verify on;
    
    # Security headers
    add_header Strict-Transport-Security "max-age=31536000; includeSubDomains" always;
    add_header X-Content-Type-Options nosniff always;
    add_header X-Frame-Options DENY always;
    
    # Proxy configuration
    location / {
        proxy_pass http://eaglercraft_backend;
        proxy_http_version 1.1;
        
        # WebSocket support
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        
        # Headers
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # Timeouts
        proxy_connect_timeout 7d;
        proxy_send_timeout 7d;
        proxy_read_timeout 7d;
    }
    
    # Health check endpoint
    location /health {
        access_log off;
        return 200 "healthy\n";
        add_header Content-Type text/plain;
    }
}
```

### Caddy Configuration

```caddy
eaglercraft.example.com {
    encode gzip
    
    @websocket {
        header Connection *Upgrade*
        header Upgrade websocket
    }
    
    reverse_proxy localhost:25565 {
        @websocket
        websocket
        header_up X-Real-IP {remote_host}
    }
    
    # Health check
    @health {
        path /health
    }
    
    respond @health 200 "healthy"
}
```

### HAProxy Load Balancing

```haproxy
global
    log stdout local0
    log stdout local1 notice
    chroot /var/lib/haproxy
    stats socket /run/haproxy/admin.sock mode 660 level admin
    stats timeout 30s
    daemon

defaults
    log     global
    mode    http
    option  httplog
    option  dontlognull
    timeout connect 5000
    timeout client  50000
    timeout server  50000

frontend eaglercraft_in
    bind *:80
    bind *:443 ssl crt /etc/ssl/certs/eaglercraft.pem
    mode http
    
    # Redirect HTTP to HTTPS
    redirect scheme https code 301 if !{ ssl_fc }
    
    default_backend eaglercraft_servers

backend eaglercraft_servers
    mode http
    balance roundrobin
    option httpchk GET /health
    
    # Multiple server instances
    server server1 localhost:8001 check
    server server2 localhost:8002 check
    server server3 localhost:8003 check
```

---

## Multi-Proxy Configuration

### BungeeCord Setup

1. **Install BungeeCord**
   ```bash
   wget https://ci.md-5.net/job/BungeeCord/lastSuccessfulBuild/artifact/target/BungeeCord.jar
   java -Xms1G -Xmx2G -jar BungeeCord.jar nogui
   ```

2. **Configure listeners.yaml**
   ```yaml
   listener_list:
     - bind_addr: "0.0.0.0:25565"
       inject_address: "bungee.example.com:25565"
   ```

3. **Configure backend servers**
   ```yaml
   servers:
     backend:
       address: localhost:25566
       restricted: false
   ```

### Velocity Setup

1. **Install Velocity** (Java 11+)
   ```bash
   wget https://api.papermc.io/v2/projects/velocity/versions/latest/builds/latest/downloads/velocity-latest.jar
   java -Xms512M -Xmx1G -jar velocity-latest.jar nogui
   ```

2. **Enable forwarding**
   ```yaml
   forwarded-mode: true  # Required for proper IP forwarding
   ```

### EaglerXSupervisor (Multi-Proxy Sync)

For synchronized multi-proxy setups:

```bash
# Deploy supervisor
java -Xms512M -Xmx1G -jar EaglerXSupervisor.jar

# Configure sync between proxies
# See CONFIG.md for supervisor options
```

---

## Performance Optimization

### JVM Tuning

```bash
java \
  -Xms4G -Xmx8G \                          # Heap size
  -XX:+UseG1GC \                            # G1 garbage collector
  -XX:MaxGCPauseMillis=200 \               # GC pause target
  -XX:+UnlockExperimentalVMOptions \
  -XX:G1NewCollectionPercentage=30 \
  -XX:G1MaxNewGenPercent=40 \
  -XX:InitiatingHeapOccupancyPercent=15 \
  -XX:G1HeapRegionSize=16M \
  -XX:MinMetaspaceSize=128M \
  -XX:MaxMetaspaceSize=512M \
  -XX:+ParallelRefProcEnabled \
  -XX:+AlwaysPreTouch \
  -jar EaglerXServer.jar nogui
```

### Server Configuration Optimization

```yaml
# Reduce view distance for low-end servers
eagler_players_view_distance: 8

# Enable compression
http_websocket_compression_level: 9  # Max compression

# Optimize fragmentation
protocol_v4_defrag_max_packets: 32   # Lower for more fragmentation

# Reduce skin cache
skin_service:
  skin_cache_memory_max_objects: 1024
  skin_cache_thread_count: 2
```

### Network Optimization

- Use SSD storage (especially for skin cache)
- Enable TCP_NODELAY to reduce latency
- Configure nginx buffer sizes
- Use gzip compression on reverse proxy

---

## Monitoring & Logging

### Docker Logging

```bash
# View logs
docker-compose logs eaglercraft-server

# Follow logs in real-time
docker-compose logs -f eaglercraft-server

# View last 100 lines
docker-compose logs --tail=100 eaglercraft-server

# Export logs to file
docker-compose logs eaglercraft-server > server.log
```

### Prometheus Monitoring

```yaml
# docker-compose.yml addition
prometheus:
  image: prom/prometheus:latest
  volumes:
    - ./prometheus.yml:/etc/prometheus/prometheus.yml
    - prometheus-data:/prometheus
  ports:
    - "9090:9090"

# prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'eaglercraft'
    static_configs:
      - targets: ['localhost:9999']
```

### Log Aggregation (ELK Stack)

```yaml
# docker-compose.yml
elasticsearch:
  image: docker.elastic.co/elasticsearch/elasticsearch:8.0.0
  environment:
    - discovery.type=single-node
  ports:
    - "9200:9200"

kibana:
  image: docker.elastic.co/kibana/kibana:8.0.0
  ports:
    - "5601:5601"

filebeat:
  image: docker.elastic.co/beats/filebeat:8.0.0
  volumes:
    - ./logs:/eaglercraft/logs:ro
```

---

## Security Hardening

### Firewall Configuration

```bash
# UFW (Ubuntu)
sudo ufw allow 22/tcp      # SSH
sudo ufw allow 25565/tcp   # Minecraft/Eaglercraft
sudo ufw allow 8080/tcp    # WebSocket
sudo ufw allow 443/tcp     # HTTPS
sudo ufw enable

# iptables (Advanced)
iptables -A INPUT -p tcp --dport 25565 -j ACCEPT
iptables -A INPUT -p tcp --dport 8080 -j ACCEPT
```

### SSL/TLS Security

```bash
# Generate Let's Encrypt certificate
sudo certbot certonly --standalone -d eaglercraft.example.com

# Auto-renewal
sudo systemctl enable certbot.timer
sudo systemctl start certbot.timer
```

### DDoS Protection

1. **Use Cloudflare or similar CDN**
   - Enable DDoS protection
   - Use strict firewall rules
   - Enable rate limiting

2. **Rate limiting in EaglercraftX**
   ```yaml
   ratelimit:
     ip:
       enable: true
       limit: 60
       lockout_duration: 1200
   ```

3. **Fail2ban**
   ```bash
   sudo apt install fail2ban
   # Configure /etc/fail2ban/jail.local
   ```

### Regular Updates

```bash
# Check for updates monthly
docker-compose pull
docker-compose up -d

# Or build from source
git pull origin main
./gradlew build
```

### Backup Strategy

```bash
# Daily backups
0 2 * * * docker-compose exec eaglercraft-server tar -czf /backups/world-$(date +\%Y\%m\%d).tar.gz /eaglercraft/world

# Retention
find /backups -name "world-*.tar.gz" -mtime +30 -delete
```

---

## Troubleshooting Deployments

### Common Issues

**Container won't start**
```bash
docker-compose logs eaglercraft-server
# Check EULA=true and available memory
```

**Connection refused**
```bash
# Verify ports
docker-compose port eaglercraft-server 25565
netstat -tlnp | grep 25565
```

**High memory usage**
```bash
# Increase max memory
docker-compose down
# Edit MAX_MEMORY in docker-compose.yml or Dockerfile
docker-compose up -d
```

**Slow performance**
```bash
# Check logs for GC pauses
docker-compose logs | grep -i "gc"
# Enable more verbose GC logging and tune JVM
```

---

## Production Checklist

- [ ] SSL/TLS certificates configured
- [ ] Firewall rules properly configured
- [ ] Backups scheduled and tested
- [ ] Monitoring/logging enabled
- [ ] Rate limiting configured
- [ ] Health checks working
- [ ] Regular security updates scheduled
- [ ] Disaster recovery plan documented
- [ ] Performance baseline established
- [ ] User authentication/moderation plan in place

---

For more help, see [QUICKSTART.md](./QUICKSTART.md) and [CONFIG.md](./CONFIG.md).
