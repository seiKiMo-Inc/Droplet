package moe.seikimo.droplet.world.io;

import it.unimi.dsi.fastutil.ints.Int2IntArrayMap;
import moe.seikimo.droplet.block.BlockPalette;
import moe.seikimo.droplet.block.MinecraftBlock;
import moe.seikimo.droplet.utils.EncodingUtils;
import moe.seikimo.droplet.utils.FileUtils;
import moe.seikimo.droplet.utils.Log;
import moe.seikimo.droplet.utils.Preconditions;
import moe.seikimo.droplet.utils.objects.binary.SimpleBitArray;
import moe.seikimo.droplet.world.DropletWorld;
import moe.seikimo.droplet.world.World;
import moe.seikimo.droplet.world.chunk.Chunk;
import moe.seikimo.droplet.world.chunk.DropletChunk;
import moe.seikimo.droplet.world.chunk.DropletChunkSection;
import moe.seikimo.droplet.world.io.anvil.RegionFile;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.slf4j.Logger;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public final class AnvilFormatReader implements WorldReader {
    private final Logger logger = Log.newLogger("Anvil Reader");
    private final Set<Thread> threads = new HashSet<>();

    @Override
    public World read(File world) throws Exception {
        Preconditions.fileExists(world);
        Preconditions.isDirectory(world);
        Preconditions.canReadFile(world);

        // Read the world data.
        var levelFile = new File(world, "level.dat");
        Preconditions.fileExists(levelFile);
        Preconditions.canReadFile(levelFile);

        var levelNbt = FileUtils.readNbt(levelFile);
        var levelData = levelNbt.getCompound("Data");
        // Create the world instance.
        var worldInstance = new DropletWorld(
                levelData.getString("LevelName"),
                levelData.getCompound("WorldGenSettings")
                        .getLong("seed")
        );

        // Read all regions from the world.
        var regionFolder = new File(world, "region");
        Preconditions.fileExists(regionFolder);
        Preconditions.isDirectory(regionFolder);
        Preconditions.canReadFile(regionFolder);

        var regionFiles = regionFolder.listFiles((_, name) -> name.endsWith(".mca"));
        if (regionFiles == null) {
            throw new IllegalStateException("Region files are null.");
        }

        for (var regionFile : regionFiles) {
            var region = new RegionFile(regionFile);
            var thread = new Thread(() ->
                    this.readRegion(worldInstance, region));
            threads.add(thread);
        }

        for (var thread : threads) {
            thread.start();
        }

        return worldInstance;
    }

    /**
     * Reads a region from the world.
     *
     * @param world  The world.
     * @param region The region.
     */
    private void readRegion(World world, RegionFile region) {
        for (var x = 0; x < 32; x++) {
            for (var z = 0; z < 32; z++) {
                // Skip the chunk if it is not present.
                if (!region.hasChunk(x, z)) continue;

                var chunk = region.getChunkDataInputStream(x, z);
                if (chunk == null) {
                    this.logger.warn("Chunk ({}, {}) is null.", x, z);
                    continue;
                }

                var chunkNbt = EncodingUtils.nbtDecode(chunk);

                // Read the position of the chunk.
                var chunkX = chunkNbt.getInt("xPos");
                var chunkZ = chunkNbt.getInt("zPos");

                var chunkInstance = new DropletChunk(chunkX, chunkZ);

                // Read all sections.
                var sections = chunkNbt.getList("sections", NbtType.COMPOUND);
                for (var section : sections) {
                    this.readSection(chunkInstance, section);
                }

                // Read height maps.
                var heightMaps = chunkNbt.getCompound("Heightmaps");
                chunkInstance.setHeightMaps(heightMaps);

                // Add the chunk to the world.
                world.addChunk(chunkInstance);
            }
        }
    }

    /**
     * Reads a section from the world.
     *
     * @param chunk   The owning chunk.
     * @param section The section.
     */
    private void readSection(Chunk chunk, NbtMap section) {
        var sectionY = section.getByte("Y");
        var sectionInstance = new DropletChunkSection(sectionY);

        // Read blocks in the section.
        var blockStates = section.getCompound("block_states");
        var palette = blockStates.getList("palette", NbtType.COMPOUND);

        // Encode the palette for Droplet's global palette.
        var translated = new Int2IntArrayMap();
        for (var paletteEntry : palette) {
            var blockName = paletteEntry.getString("Name");
            var blockProperties = paletteEntry.getCompound("Properties");

            // Look up the block palette ID.
            var block = MinecraftBlock.fromNbt(blockName, blockProperties);
            var paletteIndex = BlockPalette.getJavaPaletteMap().get(block);
            if (paletteIndex == null) {
                this.logger.trace("Block {} has no Droplet mapping.", block);
                continue;
            }

            translated.put(palette.indexOf(paletteEntry), (int) paletteIndex);
        }

        // Read the block data.
        var data = blockStates.getLongArray("data");
        if (data != null && data.length == 256) {
            // Calculate how many bits are required.
            var bits = Math.max(EncodingUtils.bitLength(palette.size() - 1), 4);
            var bitArray = new SimpleBitArray(bits, 4096, data);

            for (var x = 0; x < 16; x++) {
                for (var y = 0; y < 16; y++) {
                    for (var z = 0; z < 16; z++) {
                        var index = EncodingUtils.anvilIndex(x, y, z);
                        var paletteIndex = bitArray.get(index);
                        var blockId = translated.get(paletteIndex);

                        sectionInstance.setBlockAt(x, y, z, blockId);
                    }
                }
            }
        }

        // TODO: Read biomes in the section.

        // Add the section to the chunk.
        chunk.setSection(sectionY, sectionInstance);
    }
}
