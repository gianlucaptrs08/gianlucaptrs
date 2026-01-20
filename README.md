🟡 GunGame – Minecraft Plugin (Spigot 1.21.11)

🧑‍💻 Entwickler: Gianlucaptrs
Entwickelt für Lotus Gaming Community

Ein vollständig konfigurierbares GunGame-Plugin mit
Maprotation, Safezones, Level-System, Kits, Scoreboard und Statistiken (YAML oder MySQL).

📦 Features
➜ GunGame-Gameplay mit Leveln & Kits
➜ Mehrere Maps mit automatischem Mapwechsel
➜ Mapwechsel alle 30 Minuten + Countdown
➜ Safezone (kein PvP im Spawnbereich)
➜ Stats-System (Kills, Tode, K/D, Level-Rekord)
➜ YAML oder MySQL (In der config.yml konfigurierbar)
➜ Live-Scoreboard (Kills / Tode / K/D / Level / Rekord / Countdown für Mapwechsel)
➜ Konfigurierbares PvP-Verhalten (1.8 PVP-System)
➜ Admin-Befehle zum Setzen von Spawns, Kits, Wetter, Zeit usw.

⚙️ Installation
1. Server stoppen
2. GunGame.jar in den Ordner
3. Server starten
4. Plugin erstellt automatisch:
/plugins/GunGame/
├── config.yml
├── spawns.yml
├── kits.yml
└── stats.yml (wenn MySQL deaktiviert)

🗺️ Maps einrichten
1. Spawn für eine Map setzen
➜ /gungame setspawn <mapname>
Beispiel:
➜ /gungame setspawn map1

2. Safezone setzen
➜ /gungame safezone <mapname> <1-4>
Beispiel:
➜ /gungame safezone map1 4

🔫 Kits einrichten (GunGame-Level)
Es gibt 20 Kit-Level.

Kit für ein Level speichern
➜ /gungame setkit <1-20>
Beispiel:
➜ /gungame setkit 1

➡️ Das komplette Inventar inkl. Rüstung & Offhand wird gespeichert.

⭐Maprotation & Zeit
Mapwechsel alle 30 Minuten
Ab 15 Minuten Restzeit:
➜ Ankündigung alle 5 Minuten
➜ Letzte 10 Sekunden: Countdown
Manueller Mapwechsel:
➜ /mapchange

🔐 Permissions
gungame.admin (ADMINISTRATOR)
gungame.VIP (Für: /mapchange)

🛠️ Admin-Befehle
Befehl	Beschreibung
/gungame	Hilfe
/gungame setspawn <map>	Map-Spawn setzen
/gungame safezone <map> <radius>	Safezone setzen
/gungame setkit <1-20>	Kit speichern
/gungame settime <day/night>	Zeit festlegen
/gungame setweather <sunny/rain/thunder>	Wetter
/mapchange	Map in 10 Sekunden wechseln
/stats	Eigene Stats
/resetstats	Alle Stats zurücksetzen
