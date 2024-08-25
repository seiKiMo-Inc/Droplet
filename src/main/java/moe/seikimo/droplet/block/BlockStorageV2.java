package moe.seikimo.droplet.block;

import io.netty.buffer.ByteBuf;
import lombok.Getter;
import moe.seikimo.droplet.utils.Array;
import moe.seikimo.droplet.utils.MathUtils;
import moe.seikimo.droplet.utils.Null;
import org.cloudburstmc.protocol.common.util.VarInts;

import java.util.List;
import java.util.function.Function;

// Taken from @serenityjs/world
@Getter
public final class BlockStorageV2 {
    private static final int MAX_SIZE = 16 * 16 * 16;

    /**
     * @param x The X coordinate of the block.
     * @param y The Y coordinate of the block.
     * @param z The Z coordinate of the block.
     * @return The block's index in the palette. A bedrock value only!
     */
    public static int getBedrockIndex(int x, int y, int z) {
        return ((x & 0xF) << 8) | ((z & 0xF) << 4) | (y & 0xF);
    }

    private final List<Integer> palette, blocks;

    /** Air block runtime ID. */
    private final int air;

    /**
     * Creates an empty block storage.
     */
    public BlockStorageV2() {
        this(
                BlockPalette.getAirBlock(),
                Array.single(BlockPalette.getAirBlock()),
                Array.fill(MAX_SIZE, BlockPalette.getAirBlock())
        );
    }

    public BlockStorageV2(int air, List<Integer> palette, List<Integer> blocks) {
        this.air = air;
        this.palette = palette;
        this.blocks = blocks;
    }

    /**
     * @return True if the storage comprises only air blocks.
     */
    public boolean isEmpty() {
        return this.palette.size() == 1 &&
                this.palette.getFirst() == this.air;
    }

    /**
     * Retrieves the block runtime ID at the specified coordinates.
     *
     * @param x The X coordinate of the block.
     * @param y The Y coordinate of the block.
     * @param z The Z coordinate of the block.
     * @return The block's Droplet runtime ID.
     */
    public int getState(int x, int y, int z) {
        var index = BlockStorageV2.getBedrockIndex(x, y, z);
        var paletteIndex = Null.or(this.blocks.get(index), 0);
        return Null.or(this.palette.get(paletteIndex), this.air);
    }

    /**
     * Sets the block at the specified coordinates.
     *
     * @param x The X coordinate of the block.
     * @param y The Y coordinate of the block.
     * @param z The Z coordinate of the block.
     * @param state The block's Droplet runtime ID.
     */
    public void setState(int x, int y, int z, int state) {
        var index = BlockStorageV2.getBedrockIndex(x, y, z);
        var paletteIndex = this.palette.indexOf(state);
        if (paletteIndex == -1) {
            paletteIndex = this.palette.size();
            this.palette.add(state);
        }
        this.blocks.set(index, paletteIndex);
    }

    /**
     * Serializes the block storage into the Bedrock format.
     * Taken from: `@serenityjs/world`
     *
     * @param storage The block storage to serialize.
     * @param buffer The buffer to write the data to.
     * @param remapper A function used to remap block states.
     */
    public static void serializeBedrock(
            BlockStorageV2 storage, ByteBuf buffer,
            Function<Integer, Integer> remapper
    ) {
        var bitsPerBlock = (int) Math.ceil(MathUtils.log2(storage.palette.size()));

        // Apply padding as needed.
        bitsPerBlock = switch (bitsPerBlock) {
            case 0 -> 1;
            case 1, 2, 3, 4, 5, 6 -> bitsPerBlock;
            case 7, 8 -> 8;
            default -> 16;
        };

        // Write bits per block.
        buffer.writeByte((bitsPerBlock << 1) | 1);

        // Calculate blocks per word & words per block.
        var blocksPerWord = (int) Math.floor(32f / bitsPerBlock);
        var wordsPerBlock = (int) Math.ceil(4096f / blocksPerWord);

        // Iterate over words.
        for (var w = 0; w < wordsPerBlock; w++) {
            var word = 0;

            // Iterate over blocks.
            for (var block = 0; block < blocksPerWord; block++) {
                // Calculate the block's index.
                var paletteIndex = Null.or(
                        storage.blocks.get(w * blocksPerWord + block),
                        0
                );
                word |=
                        (paletteIndex & ((1 << bitsPerBlock) - 1))
                                << (bitsPerBlock * block);
            }

            // Append the word.
            buffer.writeIntLE(word);
        }

        // Write the palette size.
        VarInts.writeInt(buffer, storage.palette.size());

        // Iterate over palette.
        for (var state : storage.palette) {
            var remapped = remapper.apply(state);
            VarInts.writeInt(buffer, remapped);
        }
    }
}
