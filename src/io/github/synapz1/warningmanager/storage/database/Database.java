package io.github.synapz1.warningmanager.storage.database;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Created by Jeremy(Refrigerbater) on 5/9/2016.
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
        PreparedStatement statement = connection.prepareStatement("CREATE TABLE IF NOT EXISTS " + statsTable + ";");
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
    }

    public void deleteRow(String playername) throws SQLException
    {
        openConnection();
        PreparedStatement statement = connection.prepareStatement("DELETE FROM " + statsTable + " WHERE(playername = '" + playername + "');");
        statement.executeUpdate();
    }

    public boolean doesTableExist() throws SQLException
    {
        openConnection();
        PreparedStatement statement = connection.prepareStatement("SHOW TABLES LIKE " + statsTable + ";");
        return statement.executeQuery().next();
    }
}