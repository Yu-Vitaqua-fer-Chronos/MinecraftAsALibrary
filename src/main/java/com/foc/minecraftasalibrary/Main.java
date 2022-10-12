// /*
package com.foc.minecraftasalibrary;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

// This is just a test class
class Main {
    public static void main(String[] args) {

        deleteFileOrDir(Path.of(".temp"));
        deleteFileOrDir(Path.of("minecraft.1.19.2.deps.jar"));
        deleteFileOrDir(Path.of("minecraft.1.19.2.remapped.jar"));

        MAAL maal = MAAL.create();
        ClassLoader loader = maal.generateVanillaClassLoader(MinecraftVersion.RELEASE_1_19_2);

        try {
            Class<?> stackClazz = loader.loadClass("net.minecraft.item.ItemStack");
            Class<?> itemsClazz = loader.loadClass("net.minecraft.item.Items");
            Class<?> itemClazz = loader.loadClass("net.minecraft.item.ItemConvertible");
            Object stack = stackClazz.getConstructor(itemClazz).newInstance(itemsClazz.getField("STONE").get(null));
            int count = (int) stackClazz.getMethod("getMaxCount").invoke(stack);
            System.out.println("Count: " + count);
        } catch (ClassNotFoundException | NoSuchFieldException | InvocationTargetException | InstantiationException |
                 IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    static void deleteFileOrDir(Path directoryToBeDeleted) {
        if (Files.isDirectory(directoryToBeDeleted)) {
            try (Stream<Path> entries = Files.list(directoryToBeDeleted)) {
                for (Path entry : (Iterable<Path>) entries::iterator) {
                    deleteFileOrDir(entry);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else if (Files.isRegularFile(directoryToBeDeleted)) {
            try {
                Files.delete(directoryToBeDeleted);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
// */