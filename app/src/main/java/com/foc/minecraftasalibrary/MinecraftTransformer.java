package com.foc.minecraftasalibrary;

import net.fabricmc.tinyremapper.NonClassCopyMode;
import net.fabricmc.tinyremapper.OutputConsumerPath;
import net.fabricmc.tinyremapper.TinyRemapper;
import net.fabricmc.tinyremapper.TinyUtils;
import net.lingala.zip4j.ZipFile;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Objects;

public class MinecraftTransformer {
    public static void downloadMinecraft(final String mcVersion, final Path downloadPath) throws IOException {
        final JSONObject mcMeta = new JSONObject(Utils.readStringFromURL("https://launchermeta.mojang.com/mc/game/version_manifest.json"));
        final JSONArray mcVersions = mcMeta.getJSONArray("versions");

        String MCURl = null;

        for (int i = 0; i < mcVersions.length(); i++) {
            JSONObject version = mcVersions.getJSONObject(i);
            if (version.getString("id").equals(mcVersion)) {
                JSONObject data = new JSONObject(Utils.readStringFromURL(version.getString("url")));
                MCURl = data.getJSONObject("downloads").getJSONObject("server").getString("url");
                break;
            }
        }

        if (MCURl == null) {
            throw new RuntimeException("The MC version you requested isn't here!");
        }

        Utils.downloadToFile(MCURl, downloadPath.toString());
    }

    public static void downloadMappings(final String mappingsUrl, final Path mappingsFile) throws IOException {
        Utils.downloadToFile(mappingsUrl, mappingsFile.toString());
    }

    public static void flattenMCJar(final Path officialJar, final Path outputJar) throws IOException {
        Path bundleDir = Path.of(".minecraftBundle").toAbsolutePath();

        new ZipFile(officialJar.toAbsolutePath().toString()).extractAll(bundleDir.toString());

        // Get all jars within the bundled jar
        String[] jars = Files.readString(Path.of(bundleDir.toString(), "META-INF/classpath-joined")).split(";");

        for (String jar : jars) {
            Path path = Path.of(bundleDir.toString(), "META-INF", jar).toAbsolutePath();

            new ZipFile(path.toString()).extractAll(".outputtedjars");
        }


        // NOTE: This jar isn't runnable, it's only made so it can be loaded by a classloader.
        new ZipFile(outputJar.toString()).addFiles(Arrays.asList(Objects.requireNonNull(new File(".outputtedjars").listFiles())));
    }

    public static void remapMinecraftJar(final Path mappings, final Path unpackedJar, final Path outputJar) {
        Path[] classpath = new Path[0];

        TinyRemapper remapper = TinyRemapper.newRemapper()
                .withMappings(TinyUtils.createTinyMappingProvider(mappings, "official", "named")) // Just saying it's from official names to mapping names
                .build();

        try (OutputConsumerPath outputConsumer =
                     new OutputConsumerPath.Builder(outputJar).build()) {
            outputConsumer.addNonClassFiles(unpackedJar, NonClassCopyMode.FIX_META_INF, remapper);

            remapper.readInputs(unpackedJar);
            remapper.readClassPath(classpath);

            remapper.apply(outputConsumer);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            remapper.finish();
        }
    }

    public static URLClassLoader createClassLoader(final Path remappedJar) throws MalformedURLException {
        return (new URLClassLoader(new URL[] {remappedJar.toAbsolutePath().toUri().toURL()}));
    }

    // Allows you to use a custom parent classloader
    public static URLClassLoader createClassLoader(final Path remappedJar, ClassLoader parentClassloader) throws MalformedURLException {
        return new URLClassLoader(new URL[] {remappedJar.toAbsolutePath().toUri().toURL()}, parentClassloader);
    }
}