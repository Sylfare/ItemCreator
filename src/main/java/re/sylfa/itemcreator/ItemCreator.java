package re.sylfa.itemcreator;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import net.kyori.adventure.text.Component;

public class ItemCreator extends JavaPlugin {

    private CommandSender cs = Bukkit.getConsoleSender();
    
    @Override
    public void onEnable() {
        cs.sendMessage(Component.text("[ItemCreator] Enabled!"));
    }
    
}
