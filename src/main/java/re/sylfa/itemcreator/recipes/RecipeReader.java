package re.sylfa.itemcreator.recipes;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.bukkit.configuration.file.YamlConfiguration;

import re.sylfa.itemcreator.ItemCreator;
import re.sylfa.itemcreator.util.Log;

public class RecipeReader {

    public static List<CustomRecipe> readAllRecipes() {
        File recipesFolder = new File(ItemCreator.getInstance().getDataFolder(), "recipes");
        if(!recipesFolder.exists()) {
            recipesFolder.mkdirs();
            // There was no recipes folder, so no need to parse it
            return List.of();
        }

        return Arrays.stream(recipesFolder.listFiles())
        .map(file -> {
            if(!List.of("yml","yaml").contains(FilenameUtils.getExtension(file.getPath()))) {
                Log.warn("%s is not an recipe file", file.getName());
                return null;
            } else {
                return RecipeReader.readRecipe(file);
            }
        })
        .filter(file -> file != null)
        .toList();     
    }

    public static CustomRecipe readRecipe(File file) {
        String fileName = FilenameUtils.getBaseName(file.getPath());    
        return readRecipe(YamlConfiguration.loadConfiguration(file), fileName);
    }

    public static CustomRecipe readRecipe(YamlConfiguration config, String fileName) {
        Log.log("Reading recipe %s", fileName);
        var shapedIngredients = config.getConfigurationSection("shapedIngredients").getValues(false)
            .entrySet().stream().collect(Collectors.toMap(
            entry -> entry.getKey().charAt(0),
            entry -> entry.getValue() instanceof String str ? str : ""
        ));

        return CustomRecipe.Builder.builder(fileName, config.getString("type"))
            .shape(config.getStringList("shape").toArray(String[]::new))
            .shapedIngredients(shapedIngredients)
            .result(config.getString("result.item"), config.getInt("result.item", 1))
            .build();
    }
}
