package com.foc.minecraftasalibrary;

public sealed interface MAAL permits MAALImpl {

    static MAAL create() {
        return new MAALImpl();
    }

    ClassLoader generateVanillaClassLoader(MinecraftVersion version);
}
