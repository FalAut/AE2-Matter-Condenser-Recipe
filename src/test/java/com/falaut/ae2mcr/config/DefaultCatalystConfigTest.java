package com.falaut.ae2mcr.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

class DefaultCatalystConfigTest {
    @Test
    void parseEntryRejectsInvalidLines() {
        assertNull(DefaultCatalystConfig.parseEntry("missing_separator"));
        assertNull(DefaultCatalystConfig.parseEntry("bad id=32"));
        assertNull(DefaultCatalystConfig.parseEntry("minecraft:diamond=0"));
    }

    @Test
    void parseEntriesDeduplicatesByItemAndKeepsLaterValue() {
        var entries = DefaultCatalystConfig.parseEntries(List.of(
                "minecraft:diamond=64",
                "minecraft:iron_ingot=32",
                "minecraft:diamond=128"));

        assertEquals(2, entries.size());
        assertEquals("minecraft:iron_ingot", entries.get(0).itemId().toString());
        assertEquals(32, entries.get(0).storage());
        assertEquals("minecraft:diamond", entries.get(1).itemId().toString());
        assertEquals(128, entries.get(1).storage());
    }
}
