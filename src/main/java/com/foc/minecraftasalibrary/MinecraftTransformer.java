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
import java.nio.file.StandardCopyOption;
import java.util.*;

class MinecraftTransformer {
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

    @SuppressWarnings("resource")
    public static void flattenAndRemapJar(final Path officialJar, final Path mappings, final Path depsJar, final Path remappedJar) throws IOException {
        Path bundleDir = Path.of(".temp/minecraftBundle").toAbsolutePath();

        new ZipFile(officialJar.toAbsolutePath().toString()).extractAll(bundleDir.toString());

        final String mcVersion = new JSONObject(Files.readString(Path.of(bundleDir.toString(), "version.json"))).getString("id");

        // Get all jars within the bundled jar
        String[] jars = Files.readString(Path.of(bundleDir.toString(), "META-INF/classpath-joined")).split(";");

        Path serverJar = Path.of(bundleDir.toString(), "META-INF/versions", mcVersion, "server-"+mcVersion+".jar").toAbsolutePath();

        new ZipFile(serverJar.toString()).extractAll(".temp/minecraftServerJar");

        Utils.remapMinecraftServerJar(mappings, serverJar, remappedJar);

        for (String jar : jars) {
            Path path = Path.of(bundleDir.toString(), "META-INF", jar).toAbsolutePath();

            if (!path.equals(serverJar)) {
                new ZipFile(path.toString()).extractAll(".temp/outputtedjars");
            }
        }

        // NOTE: This jar isn't runnable, it's sole purpose is to make it loadable by a classloader.
        ZipFile depsZip = new ZipFile(depsJar.toAbsolutePath().toString());
        for (File file : Objects.requireNonNull(new File(".temp/outputtedjars").listFiles())) {
            if (file.isFile()) {
                depsZip.addFile(file);
            } else if (file.isDirectory()) {
                depsZip.addFolder(file);
            }
        }
        depsZip.close();
    }

    // Allows you to use a custom parent classloader
    public static ClassLoader createClassLoader(ClassLoader parentClassloader, URL... otherUrls) throws MalformedURLException {
        URLClassLoader cls = new URLClassLoader(Arrays.copyOf(otherUrls, otherUrls.length), parentClassloader);
        return new RemappingClassLoader(cls);
    }
}