package com.anotherdeveloper.raidtimer.Utils;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public class Color {

    /**
     * Color a string
     *
     * @param s String to color
     * @return The colored string
     */
    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    /**
     * Color a list of strings
     *
     * @param s String list to color
     * @return Same list, colored
     */
    public static List<String> color(List<String> s) {
        return s.stream().map(Color::color).collect(Collectors.toList());
    }

    /**
     * Color and then uncolor a string
     * Useful to get raw item names
     *
     * @param s String to uncolor
     * @return S, but with no colors
     */
    public static String colorUnColor(String s) {
        s = ChatColor.translateAlternateColorCodes('&', s);
        s = ChatColor.stripColor(s);
        return s;
    }

}
