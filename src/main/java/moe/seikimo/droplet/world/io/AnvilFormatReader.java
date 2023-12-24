package moe.seikimo.droplet.world.io;

import moe.seikimo.droplet.block.BlockPalette;
import moe.seikimo.droplet.block.MinecraftBlock;
import moe.seikimo.droplet.utils.EncodingUtils;
import moe.seikimo.droplet.utils.FileUtils;
import moe.seikimo.droplet.utils.Log;
import moe.seikimo.droplet.utils.Preconditions;
import moe.seikimo.droplet.utils.objects.binary.BitArrayVersion;
import moe.seikimo.droplet.world.DropletWorld;
import moe.seikimo.droplet.world.World;
import moe.seikimo.droplet.world.chunk.Chunk;
import moe.seikimo.droplet.world.chunk.DropletChunk;
import moe.seikimo.droplet.world.chunk.section.DropletChunkSection;
import moe.seikimo.droplet.world.io.anvil.RegionFile;
import org.cloudburstmc.nbt.NbtMap;
import org.cloudburstmc.nbt.NbtType;
import org.slf4j.Logger;

import java.io.File;

public final class AnvilFormatReader implements WorldReader {
    private final Logger logger = Log.newLogger("Anvil Reader");

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
            this.readRegion(worldInstance, region);
        }

        return worldInstance;
    }

    /**
     * Reads a region from the world.
     *
     * @param world The world.
     * @param region The region.
     * @throws Exception If there is an error with reading the region.
     */
    private void readRegion(World world, RegionFile region)
            throws Exception {
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

                // Add the chunk to the world.
                world.addChunk(chunkInstance);
            }
        }
    }

    /**
     * Reads a section from the world.
     *
     * @param chunk The owning chunk.
     * @param section The section.
     */
    private void readSection(Chunk chunk, NbtMap section) {
        var sectionY = section.getInt("Y");
        var sectionInstance = new DropletChunkSection(sectionY);

        // Read blocks in the section.
        var blockStates = section.getCompound("block_states");
        var palette = blockStates.getList("palette", NbtType.COMPOUND);
        var bits = Math.max(EncodingUtils.bitLength(palette.size() - 1), 4);

        // Encode the palette for Droplet's global palette.
        for (var paletteEntry : palette) {
            var blockName = paletteEntry.getString("Name");
            var blockProperties = paletteEntry.getCompound("Properties");

            // Look up the block palette ID.
            var block = MinecraftBlock.fromNbt(blockName, blockProperties);
            var paletteIndex = BlockPalette.getJavaBlockMap().get(block);
            if (paletteIndex == null) {
                this.logger.warn("Block {} has no Droplet mapping.", block);
                continue;
            }

            sectionInstance.getPalette().add((int) paletteIndex);
        }

        // Read the block data.
        var data = blockStates.getLongArray("data");
        if (data != null && data.length == 256) {
            for (var x = 0; x < 16; x++) {
                for (var y = 0; y < 16; y++) {
                    for (var z = 0; z < 16; z++) {
                        var index = EncodingUtils.anvilIndex(x, y, z);
                        var blockIndex = getBlockIndex(index, bits, data);
                        sectionInstance.setBlockAt(x, y, z, blockIndex);
                    }
                }
            }
        }

        // TODO: Read biomes in the section.

        // Add the section to the chunk.
        chunk.setSection(sectionY, sectionInstance);
    }

    /**
     * Gets the block index from the block data.
     *
     * @param index The index.
     * @param bits The bits.
     * @param data The data.
     * @return The block index.
     */
    private static int getBlockIndex(int index, int bits, long[] data) {
        var state = index / (64 / bits);
        var bitData = data[state];

        var d = 0L;
        var modified = false;
        if (bitData < 0) {
            d = bitData;
            modified = true;
        }

        var shiftedData = modified ? d : bitData >> (index % (64 / bits) * bits);
        return (int) (shiftedData & ((1 << bits) - 1));
    }
}
