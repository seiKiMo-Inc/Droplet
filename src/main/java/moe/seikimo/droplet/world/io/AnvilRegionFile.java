package moe.seikimo.droplet.world.io;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.BitSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPInputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;
import java.util.zip.ZipException;

import static moe.seikimo.droplet.utils.constants.WorldConstants.*;

/**
 * Sourced from: <a href="https://github.com/GlowstoneMC/Glowstone/blob/dev/src/main/java/net/glowstone/io/anvil/RegionFile.java#">...</a>
 */
public final class AnvilRegionFile {
    /**
     * Unknown.
     */
    private final AtomicInteger sizeDelta = new AtomicInteger();

    /**
     * A handle to the region file.
     */
    private final RandomAccessFile handle;

    /** Chunk offsets. */
    private final int[] offsets = new int[SECTOR_INTS];
    /** Chunk sectors used by the region. */
    private final BitSet sectorsUsed;

    public AnvilRegionFile(File regionFile)
            throws IOException {
        this.handle = new RandomAccessFile(regionFile, "rw");

        // Check if the file needs to grow.
        var lastModified = regionFile.lastModified();
        var initialLength = (int) this.handle.length();
        if (lastModified == 0 || initialLength < 4096) {
            // fast path for new or region files under 4K
            this.handle.write(emptySector);
            this.handle.write(emptySector);
            this.sizeDelta.set(2 * SECTOR_BYTES);
        } else {
            // seek to the end to prepare for grows
            this.handle.seek(initialLength);
            if (initialLength < 2 * SECTOR_BYTES) {
                // if the file size is under 8KB, grow it
                this.sizeDelta.set(2 * SECTOR_BYTES - initialLength);

                for (long i = 0; i < this.sizeDelta.get(); ++i) {
                    this.handle.write(0);
                }
            } else if ((initialLength & (SECTOR_BYTES - 1)) != 0) {
                // if the file size is not a multiple of 4KB, grow it
                this.sizeDelta.set(initialLength & (SECTOR_BYTES - 1));
                for (long i = 0; i < this.sizeDelta.get(); ++i) {
                    this.handle.write(0);
                }
            }
        }

        // Read the sectors.
        var totalSectors = (int) Math.ceil(this.handle.length() / (double) SECTOR_BYTES);
        this.sectorsUsed = new BitSet(totalSectors);

        // Prepare for offset reading.
        this.sectorsUsed.set(0, 2);
        this.handle.seek(0);

        // Read the offset table.
        var header = ByteBuffer.allocate(2 * SECTOR_BYTES);
        while (header.hasRemaining()) {
            if (this.handle.getChannel().read(header) == -1) {
                throw new EOFException("Unexpected end of file.");
            }
        }
        header.flip();

        // Attempt to read the chunk offsets.
        var headerAsInts = header.asIntBuffer();
        for (var i = 0; i < SECTOR_INTS; ++i) {
            var offset = headerAsInts.get();
            this.offsets[i] = offset;

            var startSector = offset >> 8;
            var numSectors = offset & 255;

            if (offset != 0 && startSector >= 0 && startSector + numSectors <= totalSectors) {
                this.sectorsUsed.set(startSector, startSector + numSectors + 1);
            }
        }
    }

    /**
     * Fetches the chunk data from the region.
     *
     * @param x The chunk's X coordinate.
     * @param z The chunk's Z coordinate.
     * @return The chunk data.
     * @throws IOException If an I/O error occurs.
     */
    public DataInputStream getChunkData(int x, int z)
        throws IOException {
        var offset = this.getOffset(x, z);
        if (offset == 0) throw new IOException("Offset does not exist.");

        // Sanity check the offset against the sectors.
        var totalSectors = sectorsUsed.length();
        var sectorNumber = offset >> 8;
        var numSectors = offset & 0xFF;
        if (sectorNumber + numSectors > totalSectors) {
            throw new IOException(
                    "Invalid sector: " + sectorNumber + "+" + numSectors + " > " + totalSectors);
        }

        this.handle.seek((long) sectorNumber * SECTOR_BYTES);
        var length = this.handle.readInt();
        if (length > SECTOR_BYTES * numSectors) {
            throw new IOException("Invalid length: " + length + " > " + SECTOR_BYTES * numSectors);
        } else if (length <= 0) {
            throw new IOException("Invalid length: " + length + " <= 0 ");
        }

        var version = this.handle.readByte();
        if (version == VERSION_GZIP) {
            var data = new byte[length - 1];
            this.handle.read(data);
            try {
                return new DataInputStream(new BufferedInputStream(
                        new GZIPInputStream(new ByteArrayInputStream(data), 2048)));
            } catch (ZipException exception) {
                if (exception.getMessage().equals("Not in GZIP format")) {
                    this.handle.seek(((long) sectorNumber * SECTOR_BYTES) + Integer.BYTES);
                    this.handle.write(VERSION_DEFLATE);
                    return AnvilRegionFile.asStream(data);
                }
            }
        } else if (version == VERSION_DEFLATE) {
            var data = new byte[length - 1];
            this.handle.read(data);
            return AnvilRegionFile.asStream(data);
        }

        throw new IOException("Unknown version: " + version);
    }

    /**
     * @param x The chunk's X coordinate.
     * @param z The chunk's Z coordinate.
     * @return The offset of the chunk.
     */
    public int getOffset(int x, int z) {
        return this.offsets[x + (z << 5)];
    }

    /**
     * @param data The chunk data.
     * @return The data as a stream.
     */
    private static DataInputStream asStream(byte[] data) {
        return new DataInputStream(new BufferedInputStream(new InflaterInputStream(
                new ByteArrayInputStream(data), new Inflater(), 2048)));
    }
}
