package re.sylfa.itemcreator.items;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import net.kyori.adventure.key.Key;
import re.sylfa.itemcreator.ItemCreator;
import re.sylfa.itemcreator.util.Log;

public class ItemReader {
    
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
                Log.warn(String.format("%s is not an item namespace", file.getName()));
                return false;
            } else {
                return true;
            }
        })
        .map(folder -> {
            String folderName = folder.getName();
            return Arrays.stream(folder.listFiles())
            .filter(file -> {
                if(!List.of("yml","yaml").contains(FilenameUtils.getExtension(file.getPath()))) {
                    Log.warn(String.format("%s is not an item file in %s", file.getName(), folderName));
                    return false;
                } else {
                    return true;
                }
            })
            .map(ItemReader::readItem)
            .toList();

        })
        .flatMap(Collection::stream)
        .toList();     
    }

    public static CustomItem readItem(File file) {
        String fileName = FilenameUtils.getBaseName(file.getPath());
        
        String parentName = file.getParentFile().getName();

        if(parentName == "items") {
            Log.warn(String.format("%s is not in a namespace", file.getName()));
        }
    
        return readItem(YamlConfiguration.loadConfiguration(file), parentName, fileName);
    }

    public static CustomItem readItem(YamlConfiguration config, String folderName, String fileName) {
        Log.log(String.format("Reading %s:%s", folderName, fileName));
        Key key = Key.key(folderName, fileName);
        return CustomItem.Builder.builder()
            .key(key)
            .customModelData(config.getInt("model"))
            .itemName(config.getString("itemName", ""))
            .material(Material.matchMaterial(config.getString("material", "diamond_pickaxe")))
            .lore(config.getStringList("lore"))
            .build();
    }
}
