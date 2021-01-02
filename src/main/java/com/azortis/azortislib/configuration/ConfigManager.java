/*
 * An open source utilities library used for Azortis plugins.
 *     Copyright (C) 2019  Azortis
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package com.azortis.azortislib.configuration;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.plugin.java.JavaPlugin;

@SuppressWarnings("all")
public class ConfigManager {

    private final Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final JavaPlugin plugin;

    /**
     * @param plugin The plugin to use when loading in configuration files.
     */
    public ConfigManager(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Loads and creates a configuration file.
     *
     * @param name     The name/path of the configuration file.
     * @param defaults The default values of the configuration file
     * @param <T>      The type/class of the configuration file.
     * @return {@link Config<T>} the config object
     */
    public <T> Config<T> loadConfig(String name, T defaults) {
        return new Config<T>(name, gson, plugin, defaults);
    }

    /**
     * @return The Gson object used to load in configuration data.
     */
    public Gson getGson() {
        return gson;
    }
}


