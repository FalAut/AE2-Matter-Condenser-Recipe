package com.falaut.ae2mcr.config;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.falaut.ae2mcr.AE2MatterCondenserRecipe;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

@SuppressWarnings("removal")
@EventBusSubscriber(modid = AE2MatterCondenserRecipe.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public final class DefaultCatalystConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultCatalystConfig.class);
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    private static final List<String> DEFAULT_CATALYST_LINES = List.of(
            "ae2:cell_component_1k=8192",
            "ae2:cell_component_4k=32768",
            "ae2:cell_component_16k=131072",
            "ae2:cell_component_64k=524288",
            "ae2:cell_component_256k=2097152",
            "megacells:cell_component_1m=8388608",
            "megacells:cell_component_4m=33554432",
            "megacells:cell_component_16m=134217728",
            "megacells:cell_component_64m=536870912",
            "megacells:cell_component_256m=2147483647");
    private static final ModConfigSpec.ConfigValue<List<? extends String>> DEFAULT_CATALYSTS = BUILDER
            .comment(
                    "Default condenser catalysts used only when a recipe omits the catalyst field.",
                    "Format: modid:item=storage",
                    "Storage range: 1 to 2147483647.",
                    "Examples: ae2:cell_component_1k=8192, megacells:cell_component_1m=8388608")
            .defineListAllowEmpty(
                    "defaultCatalysts",
                    DEFAULT_CATALYST_LINES,
                    value -> value instanceof String);

    public static final ModConfigSpec SPEC = BUILDER.build();

    private static volatile List<DefaultCatalystEntry> entries = parseEntries(DEFAULT_CATALYST_LINES);

    private DefaultCatalystConfig() {
    }

    @SubscribeEvent
    static void onLoad(ModConfigEvent event) {
        entries = parseEntries(DEFAULT_CATALYSTS.get());
    }

    public static boolean matches(ItemStack stack) {
        return findEntry(stack) != null;
    }

    public static int storageFor(ItemStack stack) {
        var entry = findEntry(stack);
        return entry == null ? 0 : entry.storage();
    }

    public static List<ItemStack> previewStacks(int requiredStorage) {
        var out = new ArrayList<ItemStack>();
        for (var entry : entries) {
            if (entry.storage() < requiredStorage) {
                continue;
            }
            var item = BuiltInRegistries.ITEM.get(entry.itemId());
            if (item == null || item == net.minecraft.world.item.Items.AIR) {
                continue;
            }
            out.add(new ItemStack(item));
        }
        return out;
    }

    private static DefaultCatalystEntry findEntry(ItemStack stack) {
        if (stack.isEmpty()) {
            return null;
        }
        var itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        for (var entry : entries) {
            if (entry.itemId().equals(itemId)) {
                return entry;
            }
        }
        return null;
    }

    private static DefaultCatalystEntry parseEntry(String raw) {
        int separator = raw.lastIndexOf('=');
        if (separator <= 0 || separator >= raw.length() - 1) {
            LOGGER.warn("Ignoring invalid default catalyst entry '{}'", raw);
            return null;
        }

        var itemId = ResourceLocation.tryParse(raw.substring(0, separator).trim());
        if (itemId == null) {
            LOGGER.warn("Ignoring default catalyst with invalid item id '{}'", raw);
            return null;
        }

        int storage;
        try {
            storage = Integer.parseInt(raw.substring(separator + 1).trim());
        } catch (NumberFormatException ex) {
            LOGGER.warn("Ignoring default catalyst with invalid storage '{}'", raw);
            return null;
        }

        if (storage <= 0) {
            LOGGER.warn("Ignoring default catalyst with non-positive storage '{}'", raw);
            return null;
        }

        return new DefaultCatalystEntry(itemId, storage);
    }

    private static List<DefaultCatalystEntry> parseEntries(List<? extends String> rawEntries) {
        var parsed = new ArrayList<DefaultCatalystEntry>();
        for (var raw : rawEntries) {
            var entry = parseEntry(String.valueOf(raw));
            if (entry != null) {
                parsed.add(entry);
            }
        }
        return List.copyOf(parsed);
    }

    private record DefaultCatalystEntry(ResourceLocation itemId, int storage) {
    }
}
