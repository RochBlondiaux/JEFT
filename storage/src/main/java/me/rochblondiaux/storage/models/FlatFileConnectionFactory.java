package me.rochblondiaux.storage.models;

import lombok.RequiredArgsConstructor;
import me.rochblondiaux.storage.flat.NonClosableConnection;

import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Objects;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@RequiredArgsConstructor
public abstract class FlatFileConnectionFactory implements SQLConnectionFactory {

    private final Path file;
    protected NonClosableConnection connection;

    protected abstract Connection createConnection(Path file) throws SQLException;

    @Override
    public void initialize() throws SQLException {
        this.connection = new NonClosableConnection(createConnection(getWriteFile()));
    }

    @Override
    public void shutdown() throws SQLException {
        if (Objects.nonNull(connection)) connection.shutdown();
    }

    @Override
    public Connection getConnection() throws SQLException {
        NonClosableConnection connection = this.connection;
        if (Objects.isNull(connection) || connection.isClosed()) {
            connection = new NonClosableConnection(createConnection(file));
            this.connection = connection;
        }
        return connection;
    }

    protected Path getWriteFile() {
        return file;
    }
}
