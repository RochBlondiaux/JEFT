package me.rochblondiaux.storage;

import lombok.RequiredArgsConstructor;
import me.rochblondiaux.commons.utils.AsyncUtils;
import me.rochblondiaux.storage.models.ResultSetHandler;
import me.rochblondiaux.storage.models.SQLConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@RequiredArgsConstructor
public class SQLUtils {

    private final SQLConnectionFactory connectionFactory;

    public void execute(String query) {
        try (Connection c = connectionFactory.getConnection()) {
            try (PreparedStatement statement = c.prepareStatement(query)) {
                statement.execute();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    public void executeAsync(String query) {
        AsyncUtils.runAsync(() -> execute(query));
    }

    public <T> Optional<T> executeQuery(String query, ResultSetHandler<T> handler) {
        Optional<T> result = Optional.empty();
        try (Connection c = connectionFactory.getConnection()) {
            try (PreparedStatement statement = c.prepareStatement(query)) {
                try (ResultSet rs = statement.executeQuery()) {
                    result = Optional.ofNullable(handler.handleResultSet(rs));
                } catch (SQLException throwable) {
                    throwable.printStackTrace();
                }
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
        return result;
    }

    public <T> CompletableFuture<Optional<T>> executeAsyncQuery(String query, ResultSetHandler<T> handler) {
        CompletableFuture<Optional<T>> result = new CompletableFuture<>();
        AsyncUtils.runAsync(() -> {
            try (Connection c = connectionFactory.getConnection()) {
                try (PreparedStatement statement = c.prepareStatement(query)) {
                    try (ResultSet rs = statement.executeQuery()) {
                        result.complete(Optional.ofNullable(handler.handleResultSet(rs)));
                    } catch (SQLException throwable) {
                        throwable.printStackTrace();
                    }
                } catch (SQLException throwable) {
                    throwable.printStackTrace();
                }
            } catch (SQLException throwable) {
                result.completeExceptionally(throwable);
                throwable.printStackTrace();
            }
        });
        return result;
    }

    public void executeUpdate(String query) {
        try (Connection c = connectionFactory.getConnection()) {
            try (PreparedStatement statement = c.prepareStatement(query)) {
                statement.executeUpdate();
            } catch (SQLException throwable) {
                throwable.printStackTrace();
            }
        } catch (SQLException throwable) {
            throwable.printStackTrace();
        }
    }

    public void executeAsyncUpdate(String query) {
        AsyncUtils.runAsync(() -> executeUpdate(query));
    }

    public void dropTable(String table) {
        execute("DROP TABLE `" + table + "`");
    }

    public void dropTableAsync(String table) {
        executeAsync("DROP TABLE `" + table + "`");
    }

    public void truncateTable(String table) {
        execute("TRUNCATE TABLE `" + table + "`");
    }

    public void truncateTableAsync(String table) {
        executeAsync("TRUNCATE TABLE `" + table + "`");
    }
}
