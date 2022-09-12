package com.foc.minecraftasalibrary;

import net.fabricmc.tinyremapper.NonClassCopyMode;
import net.fabricmc.tinyremapper.OutputConsumerPath;
import net.fabricmc.tinyremapper.TinyRemapper;
import net.fabricmc.tinyremapper.TinyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

class Utils {
    public static void remapMinecraftServerJar(final Path mappings, final Path serverJar, final Path outputJar) {
        Path[] classpath = new Path[0];

        TinyRemapper remapper = TinyRemapper.newRemapper()
                .withMappings(TinyUtils.createTinyMappingProvider(mappings, "official", "named")) // Just saying it's from official names to mapping names
                .build();

        try (OutputConsumerPath outputConsumer =
                     new OutputConsumerPath.Builder(outputJar).build()) {
            outputConsumer.addNonClassFiles(serverJar, NonClassCopyMode.FIX_META_INF, remapper);

            remapper.readInputs(serverJar);
            remapper.readClassPath(classpath);

            remapper.apply(outputConsumer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            remapper.finish();
        }
    }

    public static String readStringFromURL(String requestURL) throws IOException {
        try (Scanner scanner = new Scanner(new URL(requestURL).openStream(),
                StandardCharsets.UTF_8)) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

    static long downloadToFile(String url, String fileName) throws IOException {
        Path filePath = Path.of(fileName);

        Files.createDirectories(filePath.getParent()); // Ensure that the parent dir exists

        try (InputStream in = URI.create(url).toURL().openStream()) {
            return Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
