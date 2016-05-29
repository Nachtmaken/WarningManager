package io.github.synapz1.warningmanager.storage.database;

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
    protected String statsTable;

    public Database(String host, String database, int port, String username, String password, String statsTable)
    {
        this.host = host;
        this.database = database;
        this.port = port;
        this.username = username;
        this.password = password;
        this.statsTable = statsTable;
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
        PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + statsTable
                    + " id int NOT NULL AUTO_INCREMENT"
                    + ",username VARCHAR(16) NOT NULL"
                    + ",type VARCHAR(16) NOT NULL"
                    + ",reason VARCHAR(255) NOT NULL"
                    + ",sender VARCHAR(16) NOT NULL"
                    + ",date DATETIME NOT NULL"
                    + ",PRIMARY KEY (id);");
        statement.executeUpdate();
    }

    public void dropTable() throws SQLException
    {
        if (!doesTableExist())
            return;
        openConnection();
        PreparedStatement statement = connection.prepareStatement("DROP TABLE " + statsTable + ";");
        statement.executeUpdate();
    }

    // TODO Go through config and add leaderboards from username.
    public void updateDatabase(FileConfiguration config) throws SQLException
    {
        openConnection();
        // Clear the warnings so that nothing gets added that is already in the database.
        PreparedStatement clear = connection.prepareStatement("TRUNCATE TABLE " + statsTable);
        clear.executeUpdate();
        for (String username : config.getKeys(false))
        {
            ConfigurationSection section = config.getConfigurationSection(username + ".Warning");
            for (String type : section.getKeys(false))
            {
                try
                {
                    SimpleDateFormat format = new SimpleDateFormat("MM-dd-YYYY HH:mm:ss");
                    java.util.Date date = format.parse(section.getString(type + ".Date"));
                    PreparedStatement statement = connection.prepareStatement("INSERT INTO " + statsTable
                                + " '" + username + "'"
                                + ",'" + type + "'"
                                + ",'" + section.getString(type + ".Reason")
                                + ",'" + section.getString(type + ".Sender")
                                + "'" + date + ";");
                } catch(ParseException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    public FileConfiguration buildConfig() throws SQLException
    {
        openConnection();
        FileConfiguration config = new YamlConfiguration();
        Set<String> players = new HashSet<String>();
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM " + statsTable);
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
                Date date = set.getDate("date");
                config.set(username + "." + c + ".Type", type);
                config.set(username + "." + c + ".Sender", sender);
                config.set(username + "." + c + ".Date", date);
                config.set(username + "." + c + ".Reason", reason);
            }
        }

        return config;
    }

    public boolean doesTableExist() throws SQLException
    {
        openConnection();
        PreparedStatement statement = connection.prepareStatement("SHOW TABLES LIKE " + statsTable + ";");
        return statement.executeQuery().next();
    }
}