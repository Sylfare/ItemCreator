package re.sylfa.itemcreator;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.Recipe;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.permissions.CommandPermissions;

import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.plugin.lifecycle.event.LifecycleEventManager;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import re.sylfa.itemcreator.commands.ItemCreatorCommand;
import re.sylfa.itemcreator.items.CustomItem;
import re.sylfa.itemcreator.items.ItemReader;
import re.sylfa.itemcreator.items.ItemRegistry;
import re.sylfa.itemcreator.items.RepairListener;
import re.sylfa.itemcreator.recipes.CustomRecipe;
import re.sylfa.itemcreator.recipes.RecipeReader;
import re.sylfa.itemcreator.recipes.RecipeRegistry;
import re.sylfa.itemcreator.util.Log;

public class ItemCreator extends JavaPlugin {
    
    private static ItemCreator instance;
    private static ItemRegistry itemRegistry = new ItemRegistry();
    private static RecipeRegistry recipeRegistry = new RecipeRegistry();

    @Override
    public void onEnable() {
        PluginManager pm = Bukkit.getPluginManager();
        instance = this;
        Log.log("Enabled!");
        init();
        registerPermissions();
        registerCommands();
        pm.registerEvents(new RepairListener(), this);
    }

    public static ItemCreator getInstance() {
        return instance;
    }

    public static ItemRegistry getItemRegistry() {
        return itemRegistry;
    }

    public static RecipeRegistry getRecipeRegistry() {
        return recipeRegistry;
    }

    public void init(){
        itemRegistry.removeAll();
        itemRegistry.add(ItemReader.readAllItems().toArray(CustomItem[]::new));

        List<CustomRecipe> readRecipes = RecipeReader.readAllRecipes();
        Map<NamespacedKey, Recipe> readRecipesMap = readRecipes.stream().collect(Collectors.toMap(r -> r.key(), r -> r.asRecipe()));
        recipeRegistry.getAll().keySet().forEach(key -> Log.log("Removing %s: %b", key.asString(), Bukkit.removeRecipe(key)));
        recipeRegistry.removeAll();

        readRecipes.forEach(recipeRegistry::add);
        readRecipesMap.values().forEach(r -> Bukkit.addRecipe(r, true));
    }

    public void reload() {
        init();
    }

    public void registerPermissions() {
        CommandPermissions.registerPermissions(new Permission("itemcreator.give", PermissionDefault.OP));
    }

    public void registerCommands() {
        LifecycleEventManager<Plugin> manager = this.getLifecycleManager();
        manager.registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            final Commands commands = event.registrar();
            List.of(
                new ItemCreatorCommand()
            ).forEach(command -> commands.register(command.command(), command.aliases()));            
        });
    }
}
