package re.sylfa.itemcreator;

import org.bukkit.plugin.java.JavaPlugin;

import re.sylfa.itemcreator.items.CustomItem;
import re.sylfa.itemcreator.items.ItemReader;
import re.sylfa.itemcreator.items.ItemRegistry;
import re.sylfa.itemcreator.util.Log;

public class ItemCreator extends JavaPlugin {
    
    private static ItemCreator instance;
    private static ItemRegistry itemRegistry = new ItemRegistry();

    @Override
    public void onEnable() {
        instance = this;
        Log.log("Enabled!");
        init();
    }

    public static ItemCreator getInstance() {
        return instance;
    }

    public static ItemRegistry getItemRegistry() {
        return itemRegistry;
    }

    public void init(){
        itemRegistry.add(ItemReader.readAllItems().toArray(CustomItem[]::new));
    }
    
}
