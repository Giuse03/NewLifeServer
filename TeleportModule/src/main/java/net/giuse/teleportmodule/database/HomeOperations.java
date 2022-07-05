package net.giuse.teleportmodule.database;


import net.giuse.mainmodule.MainModule;
import net.giuse.mainmodule.databases.DBOperations;

import javax.inject.Inject;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class HomeOperations implements DBOperations {

    @Inject
    private MainModule mainModule;

    @Override
    public ArrayList<String> getAllString() {
        ArrayList<String> allStrings = new ArrayList<>();
        try (PreparedStatement st = mainModule.getConnectorSQLite().getConnection().prepareStatement("SELECT * FROM Home")) {
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                allStrings.add(rs.getString(1));
            }
        } catch (SQLException ignored) {
            mainModule.getLogger().info("Empty Database");
        }
        return allStrings;
    }

    @Override
    public void dropTable() {
        try (PreparedStatement stmt = mainModule.getConnectorSQLite().getConnection().prepareStatement("DROP TABLE Home;");) {
            stmt.execute();
        } catch (SQLException ignored) {
            mainModule.getLogger().info("Empty Database");
        }
    }

    @Override
    public void insert(final String str) {
        try (PreparedStatement insert = mainModule.getConnectorSQLite().getConnection().prepareStatement("INSERT INTO Home VALUES(?)");) {
            insert.setString(1, str);
            insert.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createTable() {
        try (PreparedStatement stmt = mainModule.getConnectorSQLite().getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS Home (name TEXT);");) {
            stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isPresent(String string) {
        try (PreparedStatement stmt = mainModule.getConnectorSQLite().getConnection().prepareStatement("SELECT name FROM Home where name = '" + string + "';")) {
            ResultSet resultSet = stmt.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void update(String string) {
        try (PreparedStatement stmt = mainModule.getConnectorSQLite().getConnection().prepareStatement("UPDATE Home SET name = '" + string + "' WHERE name = '" + string + "'")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void remove(String string) {
        try (PreparedStatement stmt = mainModule.getConnectorSQLite().getConnection().prepareStatement("DELETE FROM Home WHERE name = '" + string + "'")) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}