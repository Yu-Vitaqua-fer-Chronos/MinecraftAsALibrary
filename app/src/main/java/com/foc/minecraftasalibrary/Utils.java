package com.foc.minecraftasalibrary;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Scanner;

public class Utils {
    public static String readStringFromURL(String requestURL) throws IOException {
        try (Scanner scanner = new Scanner(new URL(requestURL).openStream(),
                StandardCharsets.UTF_8)) {
            scanner.useDelimiter("\\A");
            return scanner.hasNext() ? scanner.next() : "";
        }
    }

    static long downloadToFile(String url, String fileName) throws IOException {
        try (InputStream in = URI.create(url).toURL().openStream()) {
            return Files.copy(in, Paths.get(fileName), StandardCopyOption.REPLACE_EXISTING);
        }
    }
}
