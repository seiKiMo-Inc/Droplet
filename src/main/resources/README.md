# Obtaining Data

For Droplet to work, you will need to supply your own data.\
Some of this is generated using Droplet's data generator.

## Sources

- `droplet_block_palette.nbt` - Droplet Data Generator
  - `bedrock_block_palette.nbt` - [GeyserMC Bedrock Resources](https://github.com/GeyserMC/Geyser/blob/master/core/src/main/resources/bedrock)
  - `blocks.json` - [Minecraft: Java Edition's `Data Generator`](https://minecraft.wiki/w/Tutorials/Running_the_data_generator)
  - `blocksJ2B.json` - [PrismarineJS's `minecraft-data`](https://github.com/PrismarineJS/minecraft-data/tree/master/data/bedrock)
  - Use `java -DbundlerMainClass="moe.seikimo.droplet.data.DataGenerator" -jar droplet.jar` to run the data generator.
- `biome_definitions.dat` - [GeyserMC's `Geyser`](https://github.com/GeyserMC/Geyser/blob/master/core/src/main/resources/bedrock)
- `runtime_item_states.json` - [GeyserMC's `Geyser`](https://github.com/GeyserMC/Geyser/blob/master/core/src/main/resources/bedrock)
- `entity_identifiers.nbt` - [PocketMine-MP's `BedrockData`](https://github.com/pmmp/BedrockData)
- `creative_items.json` - [PocketMine-MP's `BedrockData`](https://github.com/pmmp/BedrockData)
