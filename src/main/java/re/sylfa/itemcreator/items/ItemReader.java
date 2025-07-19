package re.sylfa.itemcreator.items;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.kyori.adventure.key.Key;
import org.apache.commons.io.FilenameUtils;
import org.bukkit.inventory.ItemStack;
import re.sylfa.itemcreator.ItemCreator;
import re.sylfa.itemcreator.util.JavaUtils;
import re.sylfa.itemcreator.util.Log;

import java.io.File;
import java.io.IOException;
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

        return JavaUtils.arrayStream(itemsFolder.listFiles())
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
                return JavaUtils.arrayStream(Objects.requireNonNull(folder.listFiles()))
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
        try {
            ItemStack item = mapper.readValue(file, ItemStack.class);

            return CustomItem.builder(key)
                    .item(item)
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
