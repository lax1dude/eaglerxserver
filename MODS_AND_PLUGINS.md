# EaglercraftX Server - Recommended Mods & Plugins Guide

This guide covers installing popular mods and plugins for your EaglercraftX server.

## 📦 Plugin Installation

### Where to Install
Place all plugin JAR files in the `plugins/` directory:
```
eaglerxserver/
└── plugins/
    ├── WorldEdit.jar
    ├── WorldGuard.jar
    ├── Cars.jar
    ├── Furniture.jar
    └── [other plugins]
```

### Restart server after adding plugins
```bash
# Docker
docker-compose restart eaglercraft-server

# Manual
# Stop the server and restart it
```

---

## 🛠️ WorldEdit

**The ultimate building tool for Minecraft servers**

### Installation
1. Download from: https://dev.bukkit.org/projects/worldedit
2. Place `WorldEdit-*.jar` in `plugins/` folder
3. Restart server

### Basic Commands
```
//wand              - Get selection wand
//pos1              - Set position 1
//pos2              - Set position 2
//set <block>       - Fill selection with block
//replace <block>   - Replace blocks in selection
//copy             - Copy selection
//paste            - Paste copied area
//undo             - Undo last action
//redo             - Redo last action
```

### Configuration
```yaml
# plugins/WorldEdit/config.yml
chunk-batching-mask: 255
history-size: 15
max-points-selected: 5242880
max-polygon-points: 20
default-locale: en
snapshots:
  directory: snapshots
```

### Permissions
```
worldedit.selection.pos
worldedit.selection.wand
worldedit.selection.expand
worldedit.history.undo
worldedit.history.redo
worldedit.clipboard.copy
worldedit.clipboard.paste
worldedit.region.fill
worldedit.region.replace
worldedit.schematic.save
worldedit.schematic.load
```

---

## 🛡️ WorldGuard

**Protect regions and prevent griefing**

### Installation
1. Install WorldEdit first (required)
2. Download from: https://dev.bukkit.org/projects/worldguard
3. Place `WorldGuard-*.jar` in `plugins/` folder
4. Restart server

### Basic Commands
```
/region define <id>           - Define a new region
/region info <id>             - Get region info
/region claim <id>            - Claim a region
/region addmember <id> <name> - Add member to region
/region addowner <id> <name>  - Add owner to region
/region remove <id>           - Remove region
/region flag <id> <flag>      - Set region flag
```

### Common Flags
```
/region flag <id> pvp deny           - Disable PvP
/region flag <id> tnt deny           - Disable TNT
/region flag <id> fire-spread deny   - Disable fire spread
/region flag <id> liquid-flow deny   - Disable lava/water flow
/region flag <id> creeper-explosion deny  - Disable creeper explosions
/region flag <id> use allow          - Allow block use
/region flag <id> build deny         - Prevent building
/region flag <id> passthrough allow  - Allow passing through region
```

### Configuration
```yaml
# plugins/WorldGuard/config.yml
regions:
  uuid-migration:
    enabled: true
  wand-item: minecraft:wooden_axe
  max-claim-volume: -1
  claim-only-inside-existing-regions: false
  maximum-claim-radius: -1

blacklist:
  file: blacklist.txt
  logging:
    log-console: true
    log-file: true
```

### Permissions
```
worldguard.region.define
worldguard.region.claim
worldguard.region.addmember
worldguard.region.addowner
worldguard.region.flag.flags.*
worldguard.region.info.own
worldguard.region.list.own
```

---

## 🚗 Cars Plugin

**Add drivable vehicles to your server**

### Installation
1. Download Cars plugin JAR
2. Place in `plugins/` folder
3. Restart server

### Basic Usage
```
/car give          - Get a car vehicle
/car drive <name>  - Start driving
/car eject         - Exit vehicle
/car speed <speed> - Set vehicle speed
/car fuel <amount> - Add fuel
```

### Configuration
```yaml
# plugins/Cars/config.yml
cars:
  enabled: true
  default-speed: 20
  max-speed: 50
  fuel-consumption: 1.0
  requires-fuel: true
  
vehicles:
  car:
    speed: 30
    health: 100
    passengers: 4
  
  truck:
    speed: 25
    health: 150
    passengers: 2
```

### Permissions
```
cars.give
cars.drive
cars.admin
cars.fuel
cars.speed
cars.damage
```

### Tips
- Use `/car give` to spawn vehicles
- Vehicles require fuel to operate
- Different vehicle types have different speeds
- Can be configured in config.yml
- Supports custom vehicles via plugins

---

## 🪑 Furniture Plugin

**Add decorative furniture and seating to your server**

### Installation
1. Download Furniture plugin JAR
2. Place in `plugins/` folder
3. Restart server

### Basic Commands
```
/furniture give <furniture>       - Get furniture item
/furniture place                  - Place furniture
/furniture sit                    - Sit on furniture
/furniture rotate                 - Rotate furniture
/furniture list                   - List available furniture
/furniture info <furniture>       - Get furniture details
```

### Available Furniture
```
Seating:
  - chairs
  - benches
  - sofas
  - thrones
  - stools

Decorative:
  - tables
  - desks
  - shelves
  - cabinets
  - lamps
  - paintings
  - plants
```

### Configuration
```yaml
# plugins/Furniture/config.yml
furniture:
  enabled: true
  
  defaults:
    rotatable: true
    removable: true
    physical: false
  
  items:
    chair:
      material: oak_wood
      seat_height: 0.4
      rotation: 0
    
    table:
      material: oak_wood
      collision: true
      
    sofa:
      material: wool
      seat_height: 0.3
      seats: 3
```

### Permissions
```
furniture.give
furniture.place
furniture.rotate
furniture.sit
furniture.remove
furniture.admin
```

### Tips
- Right-click to sit on seating furniture
- Use /furniture rotate to change direction
- Can be placed on any surface
- Compatible with WorldEdit for copying furniture layouts
- Custom furniture can be added via config

---

## 📋 Installation Summary

### Step-by-Step Setup

**1. Download Plugins**
```bash
# Create plugins directory
mkdir -p plugins

# Download latest versions
# WorldEdit: https://dev.bukkit.org/projects/worldedit
# WorldGuard: https://dev.bukkit.org/projects/worldguard
# Cars: [from your preferred source]
# Furniture: [from your preferred source]
```

**2. Install Plugins**
```bash
# Copy JAR files to plugins folder
cp WorldEdit-*.jar plugins/
cp WorldGuard-*.jar plugins/
cp Cars-*.jar plugins/
cp Furniture-*.jar plugins/
```

**3. Restart Server**
```bash
# Docker
docker-compose restart eaglercraft-server

# Manual
# Stop server and restart
```

**4. Verify Installation**
```
# In-game console check:
/plugins
# Should list: WorldEdit, WorldGuard, Cars, Furniture
```

---

## 🔧 Configuration Files

After first run, configuration files are created in:
```
plugins/
├── WorldEdit/
│   └── config.yml
├── WorldGuard/
│   ├── config.yml
│   └── regions/ (saved regions)
├── Cars/
│   └── config.yml
└── Furniture/
    └── config.yml
```

---

## 🎮 Common Use Cases

### Create a Protected Spawn Area
```
1. Stand at corner: //pos1
2. Stand at opposite corner: //pos2
3. Create region: /region define spawn
4. Claim region: /region claim spawn
5. Disable PvP: /region flag spawn pvp deny
6. Prevent building: /region flag spawn build deny
```

### Set Up a Building Arena
```
1. Define region: /region define arena
2. Allow PvP: /region flag arena pvp allow
3. Allow building: /region flag arena build allow
4. Clear area: //sel arena, //set air
```

### Create Furniture Showroom
```
1. Build a room
2. Place various furniture: /furniture place
3. Protect area: /region define showroom
4. Disable modification: /region flag showroom build deny
```

### Vehicle Parking Area
```
1. Define parking lot: /region define parking
2. Allow cars: /region flag parking allow-cars true
3. Prevent griefing: /region flag parking build deny
4. Add vehicles: /car give car
```

---

## ⚠️ Important Notes

### Compatibility
- WorldEdit and WorldGuard work best with Paper/Spigot
- Some plugins may require additional dependencies
- Check plugin documentation for version compatibility

### Performance
- WorldEdit can be resource-intensive on large selections
- Limit region size for better performance
- Use asynchronous operations for large edits

### Backups
```bash
# Always backup before installing plugins
docker-compose exec eaglercraft-server tar -czf /backups/plugins-backup.tar.gz /eaglercraft/plugins
```

### Permissions
- Use a permission plugin like LuckPerms for better control
- Different players can have different permissions
- Create player ranks with custom permissions

---

## 🐛 Troubleshooting

### Plugin won't load
```
# Check logs
docker-compose logs eaglercraft-server | grep -i "error"

# Verify JAR name matches expected plugin name
ls -la plugins/
```

### Commands not working
```
# Check if player has permissions
/perm info <player>

# Verify plugin is loaded
/plugins
```

### Crashes after plugin install
```
# Remove the plugin
rm plugins/[problematic-plugin].jar

# Restart and check logs
docker-compose restart eaglercraft-server
```

### WorldGuard regions not saving
```
# Check permissions on regions folder
ls -la plugins/WorldGuard/regions/

# Ensure folder is writable
chmod 755 plugins/WorldGuard/regions/
```

---

## 📚 Additional Resources

- **WorldEdit Wiki**: https://worldedit.enginehub.org/en/latest/
- **WorldGuard Wiki**: https://worldguard.enginehub.org/
- **Bukkit Plugin Search**: https://dev.bukkit.org/projects

---

## 🎯 Next Steps

1. Install plugins listed above
2. Configure each plugin for your needs
3. Set up protection regions with WorldGuard
4. Test all functionality
5. Train admins and moderators
6. Create server rules and guidelines

---

**Happy building! 🏗️**
