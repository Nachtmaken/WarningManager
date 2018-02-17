package io.github.synapz1.warningmanager.storage;

import io.github.synapz1.warningmanager.SettingsManager;
import io.github.synapz1.warningmanager.storage.database.DatabaseManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.sql.SQLException;

public class WarningsFile extends WarningManagerFile {

    public WarningsFile(Plugin wm) {
        super(wm, "warnings.yml");

        if (SettingsManager.MYSQL_ENABLED) {
            try
            {
                this.setFileConfiguration(DatabaseManager.getManager().getDatabase().buildConfig());
                saveFile();
            } catch (SQLException e)
            {
                e.printStackTrace();
            }
        }
    }

    // Ways of getting paths
    public String getPath(String... paths) {
        StringBuilder path = new StringBuilder();

        for (String part : paths)
            path.append(".").append(part);

        path = new StringBuilder(path.toString().replaceFirst("\\.", ""));

        return path.toString();
    }

    // Abstract getting and setting values
    public void setValue(String path, Object object) {
        file.set(path, object);
        saveFile();
    }

    public Object getValue(String path) {
        return file.get(path);
    }

    @Override
    public FileConfiguration getFileConfig() {
        return super.getFileConfig();
    }
}
