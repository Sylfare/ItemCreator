package re.sylfa.itemcreator.recipes;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import net.kyori.adventure.key.Key;
import re.sylfa.itemcreator.ItemCreator;
import re.sylfa.itemcreator.util.Log;

public class RecipeReader {

    public static List<CustomRecipe> readAllItems() {
        File recipesFolder = new File(ItemCreator.getInstance().getDataFolder(), "recipes");
        if(!recipesFolder.exists()) {
            recipesFolder.mkdirs();
            // There was no items folder, so no need to parse it
            return List.of();
        }

        return Arrays.stream(recipesFolder.listFiles())
        .filter(file -> {
            if(!file.isDirectory()) {
                Log.warn("%s is not an recipe namespace", file.getName());
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
                    Log.warn("%s is not an recipe file in %s", file.getName(), folderName);
                    return false;
                } else {
                    return true;
                }
            })
            .map(RecipeReader::readRecipe)
            .filter(file -> file != null);
        })
        .flatMap(a -> a)
        .toList();     
    }

    public static CustomRecipe readRecipe(File file) {
        String fileName = FilenameUtils.getBaseName(file.getPath());
        String parentName = file.getParentFile().getName();

        if(parentName == "recipes") {
            Log.warn("%s recipe is not in a namespace", file.getName());
            return null;
        }
    
        return readRecipe(YamlConfiguration.loadConfiguration(file), parentName, fileName);
    }

    public static CustomRecipe readRecipe(YamlConfiguration config, String folderName, String fileName) {
        Key key = Key.key(folderName, fileName);
        Log.log("Reading recipe %s", key.asString());
        return CustomRecipe.Builder.builder(key)
            .build();
    }
}
