package io.github.synapz1.warningmanager.storage;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

public class WarningManagerFile extends File {

    protected FileConfiguration file;
    protected Plugin wm;

    protected WarningManagerFile(Plugin wm, String name) {
        super(wm.getDataFolder(), name);

        this.wm = wm;

        if (!this.exists()) {
            try {
                createNewFile();
            } catch (IOException e) {
                wm.getLogger().log(Level.SEVERE, "Could not create " + getName() + ". Stack trace: ");
                e.printStackTrace();
            }
        }

        this.file = YamlConfiguration.loadConfiguration(this);

        this.saveFile();
    }

    public void saveFile() {
        try {
            file.save(this);
        } catch (Exception e) {
            wm.getLogger().log(Level.SEVERE, "Could not save " + getName() + ". Stack trace: ");
            e.printStackTrace();
        }
    }

    public FileConfiguration getFileConfig() {
        return this.file;
    }

    public void setFileConfiguration(FileConfiguration file) {
        this.file = file;
    }
}
