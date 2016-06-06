package io.github.synapz1.warningmanager.storage;

import io.github.synapz1.warningmanager.SettingsManager;
import io.github.synapz1.warningmanager.storage.database.DatabaseManager;
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
        String path = "";

        for (String part : paths)
            path += "." + part;

        path = path.replaceFirst("\\.", "");

        return path;
    }

    // Abstract getting and setting values
    public void setValue(String path, Object object) {
        file.set(path, object);
        saveFile();
    }

    public Object getValue(String path) {
        return file.get(path);
    }
}
