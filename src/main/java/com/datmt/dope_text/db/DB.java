package com.datmt.dope_text.db;

import com.datmt.dope_text.db.model.File;
import jdk.jshell.spi.SPIResolutionException;

import javax.swing.plaf.nimbus.State;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DB {
    private static final String DB_CONNECTION = "jdbc:sqlite:dope_text.db";
    private static final String FILE_TABLE = "Files";
    private static final String KEY_VALUE_TABLE = "KeysValues";

    private Connection connection;

    public static void main(String[] args) throws SQLException {
        DB db = new DB();
    }

    private void initConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(DB_CONNECTION);
        }
    }

    public DB() throws SQLException {
        createTables();
    }


    public List<File> getAllFiles() throws SQLException {
        List<File> files = new ArrayList<>();

        Connection connection = getConnection();

        String query = "SELECT * FROM " + FILE_TABLE;

        Statement statement = connection.createStatement();
        ResultSet set = statement.executeQuery(query);

        while (set.next()) {
            files.add(new File(
                    set.getLong("id"),
                    set.getString("file_hash"),
                    set.getString("local_path"),
                    set.getString("file_name"),
                    set.getString("content"),
                    set.getLong("created_time"),
                    set.getLong("updated_time"),
                    set.getInt("is_open")

            ));
        }
        closeConnection(connection);

        return files;
    }

    public List<File> getAllOpenedFiles() throws SQLException {
        List<File> files = new ArrayList<>();

        Connection connection = getConnection();

        String query = "SELECT * FROM " + FILE_TABLE + " WHERE is_open = 1";

        Statement statement = connection.createStatement();
        ResultSet set = statement.executeQuery(query);

        while (set.next()) {
            files.add(new File(
                    set.getLong("id"),
                    set.getString("file_hash"),
                    set.getString("local_path"),
                    set.getString("file_name"),
                    set.getString("content"),
                    set.getLong("created_time"),
                    set.getLong("updated_time"),
                    1

            ));
        }
        closeConnection(connection);

        return files;
    }

    private void createTables() throws SQLException {
        String createFilesTable = "CREATE TABLE IF NOT EXISTS " + FILE_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "file_hash text," +
                "local_path text," +
                "file_name text," +
                "content text," +
                "is_open INTEGER DEFAULT  1," +
                "created_time INTEGER," +
                "updated_time INTEGER" +
                " );";


        String createKeyValueTable = "CREATE TABLE IF NOT EXISTS " + KEY_VALUE_TABLE + " (id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "i_key text," +
                "i_value text" +
                " );";

        Connection connection = getConnection();

        Statement statement = connection.createStatement();

        statement.executeUpdate(createFilesTable);
        statement.executeUpdate(createKeyValueTable);

        closeConnection(connection);
    }

    public File createFile(String content, String fileName) throws SQLException {
        String sql = "INSERT INTO " + FILE_TABLE + " (file_hash, file_name, content, created_time, updated_time) VALUES (?, ?, ?, ?, ?)";
        Connection connection = getConnection();

        long createdTime = Instant.now().getEpochSecond();
        PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
        statement.setString(1, generateUUID());
        statement.setString(2, fileName);
        statement.setString(3, content);
        statement.setLong(4, createdTime);
        statement.setLong(5, createdTime);

        int affectedRows = statement.executeUpdate();

        if (affectedRows == 0) {
            throw new SQLException("Failed to save file");
        }

        File f = null;

        try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
            if (generatedKeys.next()) {
                f = getFileById(generatedKeys.getLong(1));
            }
            else {
                throw new SQLException("Failed to save file");
            }
        }

        closeConnection(connection);

        return f;


    }

    private String generateUUID() {
        return UUID.randomUUID().toString().replace("-", "");
    }


    public void updateFile(File file) throws SQLException {
        String sql = "UPDATE " + FILE_TABLE + " SET file_hash = ?, file_name = ?, content = ?, updated_time = ? WHERE id = ?";
        Connection connection = getConnection();

        long createdTime = Instant.now().getEpochSecond();
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, file.getFileHash());
        statement.setString(2, file.getFileName());
        statement.setString(3, file.getContent());
        statement.setLong(4, createdTime);
        statement.setLong(5, file.getId());

        statement.execute();
        closeConnection(connection);
    }

    public void updateFile(Long fileId, String fileContent) throws SQLException {
        String sql = "UPDATE " + FILE_TABLE + " SET content = ?, updated_time = ? WHERE id = ?";
        Connection connection = getConnection();

        long createdTime = Instant.now().getEpochSecond();
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setString(1, fileContent);
        statement.setLong(2, createdTime);
        statement.setLong(3, fileId);

        statement.execute();
        closeConnection(connection);
    }

    public void updateFileName(Long fileId, String fileName) throws SQLException {
        String sql = "UPDATE " + FILE_TABLE + " SET file_name = ?, updated_time = ? WHERE id = ?";
        Connection connection = getConnection();

        long createdTime = Instant.now().getEpochSecond();
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setString(1, fileName);
        statement.setLong(2, createdTime);
        statement.setLong(3, fileId);

        statement.execute();
        closeConnection(connection);
    }

    public void updateFileOpenStatus(Long fileId, int openStatus) throws SQLException {
        String sql = "UPDATE " + FILE_TABLE + " SET is_open = ?, updated_time = ? WHERE id = ?";
        Connection connection = getConnection();

        long updatedTime = Instant.now().getEpochSecond();
        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setInt(1, openStatus);
        statement.setLong(2, updatedTime);
        statement.setLong(3, fileId);

        statement.execute();
        closeConnection(connection);
    }


    public Connection getConnection() throws SQLException {
        initConnection();
        return connection;
    }

    private void closeConnection(Connection connection) throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void close() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void updateLastOpenedFile(Long fileId) throws SQLException {
        upsertKV(CommonKeys.LAST_OPENED_FILE, fileId);
    }

    private void upsertKV(String key, Object value) throws SQLException {
        Connection connection = getConnection();

        String checkQuery = "SELECT * FROM " + KEY_VALUE_TABLE + " WHERE i_key='" + key + "'";

        Statement checkStatement = connection.createStatement();
        ResultSet checkSet = checkStatement.executeQuery(checkQuery);

        if (checkSet.next()) {

            String updateQuery = "UPDATE " + KEY_VALUE_TABLE + " SET i_value = ? WHERE i_key = ?";
            PreparedStatement updateStatement = connection.prepareStatement(updateQuery);
            updateStatement.setString(1, String.valueOf(value));
            updateStatement.setString(2, key);
            updateStatement.execute();

        } else {
            String query = "INSERT INTO " + KEY_VALUE_TABLE + " (i_key, i_value) VALUES (?, ?)";

            PreparedStatement statement = connection.prepareStatement(query);
            statement.setString(1, key);
            statement.setString(2, String.valueOf(value));
            statement.execute();
        }

        closeConnection(connection);
    }

    private String getValueFromKey(String key) throws SQLException {
        String getValueQuery = "SELECT * FROM " + KEY_VALUE_TABLE + " WHERE i_key = ?";
        Connection connection = getConnection();
        PreparedStatement statement = connection.prepareStatement(getValueQuery);

        statement.setString(1, key);


        ResultSet set = statement.executeQuery();

        String value = null;

        if (set.next()) {
            value = set.getString("i_value");
        }

        closeConnection(connection);

        return value;

    }

    public File getFileById(Long id) throws SQLException {
        String query = "SELECT * FROM " + FILE_TABLE + " WHERE id = ?";
        Connection connection = getConnection();

        PreparedStatement statement = connection.prepareStatement(query);
        statement.setLong(1, id);

        ResultSet set = statement.executeQuery();

        File f = null;

        if (set.next()) {

            f = new File(
                    set.getLong("id"),
                    set.getString("file_hash"),
                    set.getString("local_path"),
                    set.getString("file_name"),
                    set.getString("content"),
                    set.getLong("created_time"),
                    set.getLong("updated_time"),
                    set.getInt("is_open")

            );
        }

        closeConnection(connection);

        return f;
    }

    public File getLastOpenedFile() throws SQLException {
        String fileId = getValueFromKey(CommonKeys.LAST_OPENED_FILE);

        if (fileId == null) {
            return null;
        }

        Long id = Long.valueOf(fileId);

        return getFileById(id);

    }
}
