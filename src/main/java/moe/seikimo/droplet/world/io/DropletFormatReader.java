package moe.seikimo.droplet.world.io;

import moe.seikimo.droplet.utils.EncodingUtils;
import moe.seikimo.droplet.utils.Preconditions;
import moe.seikimo.droplet.world.DropletWorld;
import moe.seikimo.droplet.world.World;
import moe.seikimo.droplet.world.WorldFormat;
import moe.seikimo.droplet.world.chunk.DropletChunk;
import moe.seikimo.droplet.world.chunk.section.DropletChunkSection;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public final class DropletFormatReader implements WorldReader {
    @Override
    public World read(File worldFile)
            throws IOException {
        // Check if the file is readable.
        Preconditions.fileExists(worldFile);
        Preconditions.notDirectory(worldFile);
        Preconditions.canReadFile(worldFile);

        // Read the file from protobuf.
        var worldData = WorldFormat.World.parseFrom(new FileInputStream(worldFile));
        var world = new DropletWorld(worldData.getName(), worldData.getSeed());

        // Read the chunks.
        worldData.getChunksList().forEach(chunkData -> {
            var chunk = new DropletChunk(chunkData.getX(), chunkData.getZ());

            // Load the sections.
            chunkData.getSectionsList().forEach(sectionData -> {
                var section = new DropletChunkSection(sectionData.getY());

                sectionData.getBlocksList().forEach(blockData -> {
                    // Check palette index.
                    var palette = section.getPalette();
                    if (!palette.contains(blockData.getState())) {
                        palette.add(blockData.getState());
                    }

                    var blockPosition = EncodingUtils.decodePosition(blockData.getPosition());
                    section.setBlockAt(
                            blockPosition.getX(),
                            blockPosition.getY(),
                            blockPosition.getZ(),
                            palette.indexOf(blockData.getState()));
                });

                chunk.setSection(sectionData.getY(), section);
            });

            // Load all entities.
            chunkData.getEntitiesList().forEach(entityData -> {
                // TODO: Use a factory to create an instance of the entity.
            });

            world.addChunk(chunk);
        });

        return world;
    }
}
