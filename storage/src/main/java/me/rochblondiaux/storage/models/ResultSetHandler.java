package me.rochblondiaux.storage.models;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public interface ResultSetHandler<T> {

    /**
     * Handles a {@code ResultSet}. This method is called by
     * <p>
     * Implementations of this interface should not close the result set when finished. {@code StatementRunner} takes care of managing all JDBC objects.
     *
     * @param rs The {@code ResultSet}.
     * @return An object that represents the processed results.
     * @throws SQLException If a database access error occurs.
     */
    T handleResultSet(ResultSet rs) throws SQLException;

}
