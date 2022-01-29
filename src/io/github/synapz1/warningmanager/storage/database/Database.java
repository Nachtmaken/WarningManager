package io.github.synapz1.warningmanager.storage.database;

import io.github.synapz1.warningmanager.SettingsManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Jeremy Lugo(Refrigerbater) on 5/9/2016.
 */
public class Database
{
    protected Connection connection;
    protected String host;
    protected String database;
    protected int port;
    protected String username;
    protected String password;
    protected String warningsTable;

    public Database(String host, String database, int port, String username, String password, String warningsTable)
    {
        this.host = host;
        this.database = database;
        this.port = port;
        this.username = username;
        this.password = password;
        this.warningsTable = warningsTable;
    }

    public void openConnection() throws SQLException
    {
        synchronized (this)
        {
            try
            {
                if (connection != null && !connection.isClosed())
                    return;

                Class.forName("com.mysql.jdbc.Driver");
                connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database, username, password);
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&a[WarningManager] Connection to MySQL database successful!"));
            } catch (ClassNotFoundException e)
            {
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&c[WarningManager] Could not connect to the MySQL database! Reverting to flat file storage..."));
            }
        }
    }

    public void closeConnection() throws SQLException
    {
        if (!connection.isClosed())
            connection.close();
    }

    public void createDatabase() throws SQLException
    {
        openConnection();
        PreparedStatement statement = connection.prepareStatement("CREATE DATABASE IF NOT EXISTS " + database + ";");
        statement.executeUpdate();
    }

    public void createTable() throws SQLException
    {
        openConnection();
        PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + warningsTable
                    + " (id int NOT NULL AUTO_INCREMENT"
                    + ",username VARCHAR(255) NOT NULL"
                    + ",type VARCHAR(16) NOT NULL"
                    + ",reason VARCHAR(255) NOT NULL"
                    + ",sender VARCHAR(255) NOT NULL"
                    + ",date VARCHAR(255) NOT NULL"
                    + ",PRIMARY KEY (id));");
        statement.executeUpdate();
    }

    public void dropTable() throws SQLException
    {
        if (!doesTableExist())
            return;
        openConnection();
        PreparedStatement statement = connection.prepareStatement("DROP TABLE " + warningsTable + ";");
        statement.executeUpdate();
    }

    public void updateDatabase(FileConfiguration config) throws SQLException
    {
        openConnection();
        // Clear the warnings so that nothing gets added that is already in the database.
        PreparedStatement clear = connection.prepareStatement("TRUNCATE TABLE " + warningsTable);
        clear.executeUpdate();
        for (String username : config.getKeys(false))
        {
            ConfigurationSection section = config.getConfigurationSection(username);
            for (String id : section.getKeys(false))
            {
                if (id.equals("Total-Warnings")) continue;
                PreparedStatement statement = connection.prepareStatement("INSERT INTO " + warningsTable
                            + "(username,type,reason,sender,date)"
                            + " VALUES("
                            + "'" + username
                            + "','" + section.getString(id + ".Type")
                            + "','" + section.getString(id + ".Reason")
                            + "','" + section.getString(id + ".Sender")
                            + "','" + section.getString(id + ".Date")
                            + "');");
                statement.executeUpdate();
            }
        }
    }

    public FileConfiguration buildConfig() throws SQLException
    {
        openConnection();
        FileConfiguration config = new YamlConfiguration();
        Set<String> players = new HashSet<String>();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + warningsTable);
        ResultSet set = statement.executeQuery();
        while (set.next())
        {
            String username = set.getString("username");
            players.add(username);
        }

        for (String username : players)
        {
            statement = connection.prepareStatement("SELECT * FROM " + statement + " WHERE username = '" + username + "'");
            set = statement.executeQuery();
            int c = 0;
            while (set.next())
            {
                c++;
                config.set(username + ".TotalWarnings", c);
                String type = set.getString("type");
                String reason = set.getString("reason");
                String sender = set.getString("sender");
                String date = set.getString("date");

                config.set(username + "." + c + ".Type", type);
                config.set(username + "." + c + ".Sender", sender);
                config.set(username + "." + c + ".Date", date);
                config.set(username + "." + c + ".Reason", reason);
            }
        }

        return config;
    }

    private boolean doesTableExist() throws SQLException
    {
        openConnection();
        PreparedStatement statement = connection.prepareStatement("SHOW TABLES LIKE " + warningsTable + ";");
        return statement.executeQuery().next();
    }
}
