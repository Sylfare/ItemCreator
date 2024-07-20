package re.sylfa.itemcreator.util;

import org.bukkit.Bukkit;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class Log {
    private static String PREFIX = "[ItemCreator] ";

    public static void log(Component message){
        Bukkit.getConsoleSender().sendMessage(Component.text(PREFIX).append(message));
    }

    public static void log(String message, Object... args){
        Log.log(Component.text(String.format(message, args)));
    }


    public static void warn(String message, Object... args){
        Log.warn(Component.text(String.format(message, args)));
    }

    public static void warn(Component message){
        log(message.color(NamedTextColor.RED));
    }
}
