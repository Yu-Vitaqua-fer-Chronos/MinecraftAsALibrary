package com.foc.minecraftasalibrary;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.logging.Logger;

final class MAALImpl implements MAAL {

    // TODO: Remove hardcoded file url
    public static final String MappingsURL = "https://maven.quiltmc.org/repository/release/org/quiltmc/quilt-mappings/1.19.2+build.14/quilt-mappings-1.19.2+build.14-tiny.gz";
    @Override
    public ClassLoader generateVanillaClassLoader(MinecraftVersion version) {
        if (!version.version().equals(MinecraftVersion.RELEASE_1_19_2.version())) {
            throw new UnsupportedOperationException("Only 1.19.2 is supported at the moment");
        }

        final String mcVersion = version.version();
        Logger logger = Logger.getLogger("MAAL");

        final Path MappingsFile = Path.of(".temp/"+mcVersion+".mappings.tiny.gz");
        final Path officialJar = Path.of(".temp/minecraft."+mcVersion+".official.jar");
        final Path depsJar = Path.of("minecraft."+mcVersion+".deps.jar");
        final Path remappedJar = Path.of("minecraft."+mcVersion+".remapped.jar");

        logger.info("Checking if remapped jar exists...");

        if (!Files.exists(remappedJar)) {
            if (!Files.exists(officialJar)) {
                logger.info("Downloading official Minecraft jar...");
                try {
                    MinecraftTransformer.downloadMinecraft(mcVersion, officialJar);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                logger.info("The official minecraft jar already exists, skipping download!");
            }

            if (!Files.exists(MappingsFile)) {
                logger.info("Downloading mappings file from Quilt...");
                try {
                    MinecraftTransformer.downloadMappings(MappingsURL, MappingsFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                logger.info("The mappings file already exists, skipping download!");
            }

            Path unpackedBundle = Path.of(".temp/minecraftBundle");
            if (Files.exists(unpackedBundle)) {
                try {
                    Files.walk(unpackedBundle)
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            Path unpackedServerJar = Path.of(".temp/minecraftServerJar");
            if (Files.exists(unpackedServerJar)) {
                try {
                    Files.walk(unpackedServerJar)
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            Path outputtedJars = Path.of(".temp/outputtedjars");
            if (Files.exists(outputtedJars)) {
                try {
                    Files.walk(outputtedJars)
                            .sorted(Comparator.reverseOrder())
                            .map(Path::toFile)
                            .forEach(File::delete);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

            logger.info("Flattening and remapping jar using Quilt mappings...");
            try {
                MinecraftTransformer.flattenAndRemapJar(officialJar, MappingsFile, depsJar, remappedJar);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            logger.info("Finished all tasks.");
        } else {
            logger.info("The remapped Minecraft server jar is already present! Delete it and relating files to redownload it!");
        }

        try {
            return MinecraftTransformer.createClassLoader(ClassLoader.getSystemClassLoader(), remappedJar.toUri().toURL(), depsJar.toUri().toURL());
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
