# Minecraft Level Data Reader

A simple tool to read level.dat (1.7.10+) and output mod information as json.

## Usage

* For a directory: `java -jar MinecraftLevelDataReader.jar "saves/New World/"`
* For a zip archive: `java -jar MinecraftLevelDataReader.jar "Downloads/New World.zip"`
* For 'level.dat' itself: `java -jar MinecraftLevelDataReader.jar "saves/New World/level.dat"`

## Sample Output

```json
{
  "api": {
    "name": "forge",
    "version": "14.23.2.2611"
  },
  "modlist": [
    {
      "modid": "minecraft",
      "version": "1.12.2"
    },
    {
      "modid": "ic2",
      "version": "2.8.220-ex112"
    }
  ],
  "mod_count": 2,
  "creation_time": "2019-11-16T14:49:18Z",
  "file_name": "level.dat",
  "input_type": "directory"
}
```

## Sample Error Output

```json
{
  "error": "java.nio.file.NoSuchFileException: saves/New World/level.dat",
  "creation_time": "1970-01-01T00:00:00Z",
  "file_name": "level.dat",
  "input_type": "directory"
}
```
