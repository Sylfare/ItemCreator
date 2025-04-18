package re.sylfa.itemcreator.items;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.kyori.adventure.key.Key;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.inventory.ItemStack;
import re.sylfa.itemcreator.ItemCreator;
import re.sylfa.itemcreator.util.Log;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ItemReader {

    static final ObjectMapper mapper = ItemCreator.getMapper();

    public static List<CustomItem> readAllItems() {
        File itemsFolder = new File(ItemCreator.getInstance().getDataFolder(), "items");
        if(!itemsFolder.exists()) {
            itemsFolder.mkdirs();
            // There was no items folder, so no need to parse it
            return List.of();
        }

        return Arrays.stream(itemsFolder.listFiles())
        .filter(file -> {
            if(!file.isDirectory()) {
                Log.warn("%s is not an item namespace", file.getName());
                return false;
            } else {
                return true;
            }
        })
        .flatMap(folder -> {
            String folderName = folder.getName();
            return Arrays.stream(Objects.requireNonNull(folder.listFiles()))
            .filter(file -> {
                if(!"json".equals(FilenameUtils.getExtension(file.getPath()))) {
                    Log.warn("%s is not an item file in %s", file.getName(), folderName);
                    return false;
                } else {
                    return true;
                }
            })
            .map(ItemReader::readItem)
            .filter(Objects::nonNull);
        })
        .toList();     
    }

    public static CustomItem readItem(File file) {
        String fileName = FilenameUtils.getBaseName(file.getPath());
        String parentName = file.getParentFile().getName();

        if("items".equals(parentName)) {
            Log.warn("%s item is not in a namespace", file.getName());
            return null;
        }

        if(!Key.parseableNamespace(parentName)) {
            Log.error("%s namespace should only have characters from [a-z0-9_\\-.]");
            return null;
        }
        if(!Key.parseableValue(fileName)) {
            Log.error("%s is not a valid item name", fileName);
        }

        Key key = Key.key(parentName, fileName);
//        try {
        try {
            ItemStack item = mapper.readValue(file, ItemStack.class);


            return CustomItem.builder(key)
                    .item(item)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
//            return readItemJson(readFile(file), parentName, fileName);
//            return null;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
    }

    public static CustomItem readItemJson(String config, String folderName, String fileName) {


        Key key = Key.key(folderName, fileName);
        Log.log("Reading item %s", key.asString());

        try {
            CustomItem item = mapper.readValue(config, CustomItem.class);
            Log.log(item.toString());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

//        CustomItem item = ItemCreator.getGson().fromJson(config, CustomItem.class);
//        item.key(key);
//        return item;
        return null;
    }

    // public static CustomItem readItem(YamlConfiguration config, String folderName, String fileName) {
    //     Key key = Key.key(folderName, fileName);
    //     Log.log("Reading item %s", key.asString());

    //     return CustomItem.Builder.builder(key)
    //         .customModelData(config.getInt("model"))
    //         .itemName(config.getString("name", ""))
    //         .material(Material.matchMaterial(config.getString("material", "diamond_pickaxe")))
    //         .lore(config.getStringList("lore"))
    //         .maxDamage(config.getInt("maxDamage"))
    //         .maxStackSize(config.getInt("maxStackSize", 1))
    //         .jukeboxPlayableComponent(config.getString("jukeboxSong.name"), config.getBoolean("jukeboxSong.showInTooltip", true))
    //         .enchantmentGlintOverride(config.getBoolean("enchantmentGlintOverride.set"), config.getBoolean("enchantmentGlintOverride.value"))
    //         .build();
    // }

    private static String readFile(File file) throws IOException {
        return FileUtils.readFileToString(file, Charset.defaultCharset());
    }
}
