package uz.jesko.anvildm.utils;

import net.md_5.bungee.api.ChatColor;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ColorUtils {
    
    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    
    public static String colorize(String text) {
        if (text == null) return "";
        
        //hex
        Matcher matcher = HEX_PATTERN.matcher(text);
        while (matcher.find()) {
            String hexCode = matcher.group(1);
            String replacement = ChatColor.of("#" + hexCode).toString();
            text = text.replace("&#" + hexCode, replacement);
        }
        
        //legacy
        text = ChatColor.translateAlternateColorCodes('&', text);
        
        return text;
    }
    
    public static String stripColor(String text) {
        if (text == null) return "";
        
        //hex-first
        text = HEX_PATTERN.matcher(text).replaceAll("");
        
        //then legacy
        return ChatColor.stripColor(text);
    }
}