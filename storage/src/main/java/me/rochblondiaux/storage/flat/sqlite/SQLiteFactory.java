package me.rochblondiaux.storage.flat.sqlite;

import lombok.extern.slf4j.Slf4j;
import me.rochblondiaux.storage.models.FlatFileConnectionFactory;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Slf4j(topic = "SQLite Factory")
public class SQLiteFactory extends FlatFileConnectionFactory {

    public SQLiteFactory(Path file) {
        super(file);
    }

    @Override
    protected Connection createConnection(Path file) throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:" + getWriteFile().toString());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
