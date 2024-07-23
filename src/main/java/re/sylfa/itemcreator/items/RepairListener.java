package re.sylfa.itemcreator.items;

import java.util.Arrays;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareItemCraftEvent;

import re.sylfa.itemcreator.ItemCreator;

public class RepairListener implements Listener {
    @EventHandler
    public void onPrepareRepareEvent(PrepareItemCraftEvent event){
        if(event.isRepair() && Arrays.stream(event.getInventory().getMatrix()).filter(item -> item != null).anyMatch(item -> ItemCreator.getItemRegistry().matchCustomItem(item) != null)) {
            event.getInventory().getResult().setAmount(0);
        }
    }
}
