package re.sylfa.itemcreator;

import org.bukkit.plugin.java.JavaPlugin;

import re.sylfa.itemcreator.items.ItemsReader;
import re.sylfa.itemcreator.util.Log;

public class ItemCreator extends JavaPlugin {
    
    private static ItemCreator instance;

    @Override
    public void onEnable() {
        instance = this;
        Log.log("Enabled!");
        ItemsReader.readAllItems();
    }

    public static ItemCreator getInstance() {
        return instance;
    }

}
