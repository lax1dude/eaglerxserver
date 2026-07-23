# Nginx Reverse Proxy Configuration for Eaglercraft
# Place this in /etc/nginx/sites-available/eaglercraft or your nginx config directory

upstream eaglercraft_backend {
    server 127.0.0.1:8080;
    keepalive 64;
}

# Redirect HTTP to HTTPS (optional but recommended)
server {
    listen 80;
    listen [::]:80;
    server_name play.cecilmc.net;
    
    # Let's Encrypt verification (if using HTTPS)
    location /.well-known/acme-challenge/ {
        root /var/www/certbot;
    }
    
    # Redirect to HTTPS
    location / {
        return 301 https://$server_name$request_uri;
    }
}

# HTTPS Server (Recommended)
server {
    listen 443 ssl http2;
    listen [::]:443 ssl http2;
    server_name play.cecilmc.net;

    # SSL Certificate paths (use Let's Encrypt or your own)
    ssl_certificate /etc/letsencrypt/live/play.cecilmc.net/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/play.cecilmc.net/privkey.pem;
    
    # SSL Configuration
    ssl_protocols TLSv1.2 TLSv1.3;
    ssl_ciphers HIGH:!aNULL:!MD5;
    ssl_prefer_server_ciphers on;

    # Logging
    access_log /var/log/nginx/eaglercraft_access.log;
    error_log /var/log/nginx/eaglercraft_error.log;

    # WebSocket Proxy Configuration
    location / {
        proxy_pass http://eaglercraft_backend;
        
        # WebSocket headers
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        
        # Standard proxy headers
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # Timeouts for WebSocket
        proxy_read_timeout 86400;
        proxy_send_timeout 86400;
        proxy_connect_timeout 7d;
        
        # Buffering
        proxy_buffering off;
    }

    # Health check endpoint
    location /health {
        access_log off;
        return 200 "OK";
        add_header Content-Type text/plain;
    }
}

# HTTP-only Server (if not using HTTPS)
# Uncomment this section if you don't want HTTPS
/*
server {
    listen 80;
    listen [::]:80;
    server_name play.cecilmc.net;

    access_log /var/log/nginx/eaglercraft_access.log;
    error_log /var/log/nginx/eaglercraft_error.log;

    location / {
        proxy_pass http://eaglercraft_backend;
        
        # WebSocket headers
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
        
        # Standard proxy headers
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        
        # Timeouts
        proxy_read_timeout 86400;
        proxy_send_timeout 86400;
        proxy_connect_timeout 7d;
        
        # Buffering
        proxy_buffering off;
    }
}
*/
