package me.rochblondiaux.server.storage;

import me.rochblondiaux.commons.models.files.ClientFile;
import me.rochblondiaux.storage.SQLUtils;
import me.rochblondiaux.storage.models.SQLConnectionFactory;
import me.rochblondiaux.storage.queries.Column;
import me.rochblondiaux.storage.queries.DataTypes;
import me.rochblondiaux.storage.queries.QueryBuilder;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 * <p>
 * Reponsible for {@link ClientFile} persistence.
 * All methods (except {@link #createTable()}) are asynchronous.
 */
public class StorageManager extends SQLUtils {

    private static final String TABLE = "files";

    public StorageManager(SQLConnectionFactory connectionFactory) {
        super(connectionFactory);
        createTable();
    }

    /**
     * Create files table in database.
     */
    public void createTable() {
        execute(new QueryBuilder(TABLE)
                .createTableIfNotExists()
                .column(Column.dataType("id", DataTypes.Limit.VARCHAR, 100))
                .column(Column.dataType("client", DataTypes.Limit.VARCHAR, 100))
                .column(Column.dataType("path", DataTypes.TEXT))
                .column(Column.dataType("size", DataTypes.FLOAT))
                .column(Column.dataType("uploaded_at", DataTypes.TIMESTAMP))
                .primaryKey("id")
                .build());
    }

    /**
     * Insert {@link ClientFile} in database.
     *
     * @param file {@link ClientFile} to insert.
     */
    public void insert(ClientFile file) {
        executeAsyncUpdate(new QueryBuilder(TABLE)
                .insert()
                .insert("id", file.getUniqueId().toString())
                .insert("client", file.getAddress().getHostAddress())
                .insert("path", file.getFile().getPath())
                .insert("size", file.getSize())
                .insert("uploaded_at", file.getUploadDate().getTime())
                .build());
    }

    /**
     * Delete {@link ClientFile} from database.
     *
     * @param file {@link ClientFile} to delete.
     */
    public void delete(ClientFile file) {
        executeAsyncUpdate(new QueryBuilder(TABLE)
                .delete()
                .where("id", file.getUniqueId().toString())
                .build());
    }

    /**
     * Get client's {@link ClientFile} by its address.
     *
     * @param address {@link me.rochblondiaux.server.network.client.NettyClientConnection} address.
     * @return client's {@link ClientFile} as list.
     */
    public CompletableFuture<List<ClientFile>> get(InetAddress address) {
        return executeAsyncQuery(new QueryBuilder(TABLE)
                .select()
                .allColumns()
                .where("client", address.getHostAddress())
                .build(), rs -> {
            List<ClientFile> files = new ArrayList<>();
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("id"));
                InetAddress client;
                try {
                    client = InetAddress.getByName(rs.getString("client"));
                } catch (UnknownHostException e) {
                    continue;
                }
                File file = new File(rs.getString("path"));
                if (!file.exists()) continue;
                long size = rs.getLong("size");
                Date uploadDate = new Date(rs.getTimestamp("uploaded_at").getTime());
                files.add(new ClientFile(uuid, client, file, size, uploadDate));
            }
            return files;
        }).thenApply(o -> o.orElseGet(ArrayList::new));
    }

}
