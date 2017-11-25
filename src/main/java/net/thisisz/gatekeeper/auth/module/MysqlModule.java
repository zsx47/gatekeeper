package net.thisisz.gatekeeper.auth.module;

import net.thisisz.gatekeeper.GateKeeper;
import net.thisisz.gatekeeper.auth.AuthLevel;

import java.sql.*;
import java.util.UUID;

public class MysqlModule implements AuthModule {


    private AuthLevel authLevel;
    private String host, port, username, password, database, table, column;
    private boolean uuidMode;
    private Connection connection;

    public MysqlModule(String host, String port, String username, String password, String database, String table, String column, boolean uuid) {
        this.authLevel = AuthLevel.NORMAL;
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
        this.table = table;
        this.column = column;
        uuidMode = uuid;
        if (this.port == null || this.port == "") {
            this.port = "3306";
        }
        try {
            openConnection();
        } catch (Exception e) {
            getPlugin().getLogger().info("Faild to open connection to database.");
        }
    }

    public MysqlModule(String host, String port, String username, String password, String database, String table, String column, boolean uuid, AuthLevel authLevel) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
        this.table = table;
        this.column = column;
        uuidMode = uuid;
        this.authLevel = authLevel;
        if (this.port == null || this.port == "") {
            this.port = "3306";
        }
        try {
            openConnection();
        } catch (Exception e) {

            getPlugin().getLogger().info("Failed to open connection to database.");
            getPlugin().getLogger().info(e.getMessage());
        }
    }

    private GateKeeper getPlugin() {
        return GateKeeper.getPlugin();
    }

    public AuthLevel getAuthLevel() {
        return authLevel;
    }

    public void openConnection() throws SQLException, ClassNotFoundException {
        if (connection != null && !connection.isClosed()) {
            return;
        }

        synchronized (this) {
            if (connection != null && !connection.isClosed()) {
                return;
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + this.host+ ":" + this.port + "/" + this.database, this.username, this.password);
        }
    }

    private Statement getNewStatement() {
        try {
            openConnection();
            return connection.createStatement();
        } catch (Exception e) {
            if (getPlugin().debugMode()) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private PreparedStatement getNewPreparedStatement(String statement) {
        try {
            openConnection();
            return connection.prepareStatement(statement);
        } catch (Exception e) {
            if (getPlugin().debugMode()) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private ResultSet executeQuery(String query) {
        Statement statement = getNewStatement();
        try {
            openConnection();
            return statement.executeQuery(query);
        } catch (Exception e) {
            if (getPlugin().debugMode()) {
                e.printStackTrace();
            }
            return null;
        }
    }

    @Override
    public boolean checkAuthUUID(UUID user) {
        if (uuidMode) {
            try {
                ResultSet rs = executeQuery("SELECT " + column + " FROM " + table + "WHERE " + column + "='" + user.toString() + "';");
                rs.last();
                if (rs.getRow() > 1) {
                    return true;
                }
                rs = executeQuery("SELECT " + column + " FROM " + table + "WHERE " + column + "='" + user.toString().replace("-", "") + "';");
                rs.last();
                if (rs.getRow() > 1) {
                    return true;
                }
            } catch (Exception e) {
                if (getPlugin().debugMode()) {
                    getPlugin().getLogger().warning(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    @Override
    public boolean checkAuthUsername(String name) {
        if (!uuidMode) {
            try {
                ResultSet rs = executeQuery("SELECT " + column + " FROM " + table + " WHERE " + column + " LIKE '" + name + "';");
                rs.last();
                if (rs.getRow() >= 1) {
                    return true;
                }
            } catch (Exception e) {
                if (getPlugin().debugMode()) {
                    getPlugin().getLogger().warning(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}
