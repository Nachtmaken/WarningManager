package io.github.synapz1.warningmanager.storage.database;

import io.github.synapz1.warningmanager.SettingsManager;

import java.sql.SQLException;

/**
 * Created by Jeremy Lugo(Refrigerbater) on 5/29/2016.
 */
public class DatabaseManager
{
    private static DatabaseManager instance = new DatabaseManager();

    private Database database;

    public DatabaseManager()
    {
        this.database = new Database(SettingsManager.HOST, SettingsManager.DATABASE, SettingsManager.PORT, SettingsManager.USERNAME, SettingsManager.PASSWORD, SettingsManager.WARNINGS_TABLE);
    }

    public static DatabaseManager getManager()
    {
        return instance;
    }

    public void init()
    {
        if (!SettingsManager.MYSQL_ENABLED) return;
        try
        {
            database.openConnection();
            database.createDatabase();
            database.createTable();
        } catch (SQLException e)
        {
            e.printStackTrace();
        }
    }

    public Database getDatabase()
    {
        return database;
    }
}
