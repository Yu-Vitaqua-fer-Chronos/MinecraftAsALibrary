package com.foc.minecraftasalibrary;

/* TODO: Add mapping URLs here maybe? Issue with Quilt not having mappings older than 1.18,
 * But it shouldn't be an issue since we can fallback to Fabric mappings.
 */
public interface MinecraftVersion {
    String version();

    MinecraftVersion RELEASE_1_19_2 = () -> "1.19.2";
    MinecraftVersion RELEASE_1_19_1 = () -> "1.19.1";
    MinecraftVersion RELEASE_1_19 = () -> "1.19";

    MinecraftVersion RELEASE_1_18_2 = () -> "1.18.2";
    MinecraftVersion RELEASE_1_18_1 = () -> "1.18.1";
    MinecraftVersion RELEASE_1_18 = () -> "1.18";

    MinecraftVersion RELEASE_1_17_1 = () -> "1.17.1";
    MinecraftVersion RELEASE_1_17 = () -> "1.17";

    MinecraftVersion RELEASE_1_16_5 = () -> "1.16.5";
    MinecraftVersion RELEASE_1_16_4 = () -> "1.16.4";
    MinecraftVersion RELEASE_1_16_3 = () -> "1.16.3";
    MinecraftVersion RELEASE_1_16_2 = () -> "1.16.2";
    MinecraftVersion RELEASE_1_16_1 = () -> "1.16.1";
    MinecraftVersion RELEASE_1_16 = () -> "1.16";

    MinecraftVersion RELEASE_1_15_2 = () -> "1.15.2";
    MinecraftVersion RELEASE_1_15_1 = () -> "1.15.1";
    MinecraftVersion RELEASE_1_15 = () -> "1.15";

    MinecraftVersion RELEASE_1_14_4 = () -> "1.14.4";
    MinecraftVersion RELEASE_1_14_3 = () -> "1.14.3";
    MinecraftVersion RELEASE_1_14_2 = () -> "1.14.2";
    MinecraftVersion RELEASE_1_14_1 = () -> "1.14.1";
    MinecraftVersion RELEASE_1_14 = () -> "1.14";

    MinecraftVersion LATEST = RELEASE_1_19_2;

    static MinecraftVersion from(String version) {
        return switch (version.toLowerCase()) {
            case "latest" -> LATEST;

            case "1.19.2" -> RELEASE_1_19_2;
            case "1.19.1" -> RELEASE_1_19_1;
            case "1.19" -> RELEASE_1_19;

            case "1.18.2" -> RELEASE_1_18_2;
            case "1.18.1" -> RELEASE_1_18_1;
            case "1.18" -> RELEASE_1_18;

            case "1.17.1" -> RELEASE_1_17_1;
            case "1.17" -> RELEASE_1_17;

            case "1.16.5" -> RELEASE_1_16_5;
            case "1.16.4" -> RELEASE_1_16_4;
            case "1.16.3" -> RELEASE_1_16_3;
            case "1.16.2" -> RELEASE_1_16_2;
            case "1.16.1" -> RELEASE_1_16_1;
            case "1.16" -> RELEASE_1_16;

            case "1.15.2" -> RELEASE_1_15_2;
            case "1.15.1" -> RELEASE_1_15_1;
            case "1.15" -> RELEASE_1_15;

            case "1.14.4" -> RELEASE_1_14_4;
            case "1.14.3" -> RELEASE_1_14_3;
            case "1.14.2" -> RELEASE_1_14_2;
            case "1.14.1" -> RELEASE_1_14_1;
            case "1.14" -> RELEASE_1_14;

            default -> throw new IllegalStateException("Unexpected value: " + version);
        };
    }
}
