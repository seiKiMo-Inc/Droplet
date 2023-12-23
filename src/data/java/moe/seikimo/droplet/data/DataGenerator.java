package moe.seikimo.droplet.data;

import com.google.gson.Gson;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public final class DataGenerator {
    @Getter private static final long startTime = System.currentTimeMillis();

    @Getter private static final File baseDir
            = new File("data");
    @Getter private static final Gson gson
            = new Gson();
    @Getter private static final Logger logger
            = LoggerFactory.getLogger("Data Generator");

    public static void main(String[] args) {
        DataGenerator.getLogger().info("Data generation finished in {}ms.",
                System.currentTimeMillis() - DataGenerator.getStartTime());
    }
}
