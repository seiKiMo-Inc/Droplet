package moe.seikimo.droplet.utils.constants;

public interface WorldConstants {
    byte VERSION_GZIP = 1;
    byte VERSION_DEFLATE = 2;

    int SECTOR_BYTES = 4096;
    int SECTOR_INTS = SECTOR_BYTES / 4;

    int CHUNK_HEADER_SIZE = 5;

    byte[] emptySector = new byte[SECTOR_BYTES];
}
