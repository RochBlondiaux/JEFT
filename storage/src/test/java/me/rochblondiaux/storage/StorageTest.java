package me.rochblondiaux.storage;

import lombok.extern.slf4j.Slf4j;
import me.rochblondiaux.storage.flat.sqlite.SQLiteFactory;
import me.rochblondiaux.storage.queries.Column;
import me.rochblondiaux.storage.queries.DataTypes;
import me.rochblondiaux.storage.queries.QueryBuilder;
import org.apache.log4j.BasicConfigurator;
import org.junit.jupiter.api.*;

import java.io.File;
import java.sql.SQLException;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
@Slf4j(topic = "Storage Test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StorageTest {

    static final String table = "test";
    static SQLiteFactory factory;
    static StorageManager manager;

    @BeforeAll
    static void beforeAll() {
        BasicConfigurator.configure();

        File file = new File("C:\\Users\\rochb\\Desktop\\test\\test.db");
        file.deleteOnExit();

        factory = new SQLiteFactory(file.toPath());
        try {
            factory.initialize();
            manager = new StorageManager(factory);
        } catch (SQLException e) {
            log.error("Cannot initialize SQLite Connection factory!", e);
        }
    }

    @AfterAll
    static void afterAll() {

    }

    @Test
    @Order(0)
    void testTableCreation() {
        manager.executeUpdate(new QueryBuilder(table)
                .createTableIfNotExists()
                .column(Column.dataType("id", DataTypes.INT))
                .column(Column.dataType("text", DataTypes.Limit.VARCHAR, 10))
                .primaryKey("id")
                .build());
    }

    @Test
    @Order(1)
    void testInsertion() {
        manager.executeUpdate(new QueryBuilder(table)
                .insert()
                .insert("id", 1)
                .insert("text", "aaaaaaaaa")
                .build());
    }

    @Test
    @Order(2)
    void testAsyncInsertion() {
        manager.executeAsyncUpdate(new QueryBuilder(table)
                .insert()
                .insert("id", 2)
                .insert("text", "bbbbbbbbb")
                .build());
    }

    @Test
    @Order(3)
    void testUpdate() {
        manager.executeUpdate(new QueryBuilder(table)
                .update()
                .set("text", "ccccccccc")
                .toWhere()
                .where("id", 1)
                .build());
    }

    @Test
    @Order(4)
    void testAsyncUpdate() {
        manager.executeAsyncUpdate(new QueryBuilder(table)
                .update()
                .set("text", "ddddddddd")
                .toWhere()
                .where("id", 2)
                .build());
    }

    @Test
    @Order(5)
    void testSelection() {
        manager.executeQuery(new QueryBuilder(table)
                        .select()
                        .allColumns()
                        .where("id", 1)
                        .build(), rs -> rs.getInt("id") + " - " + rs.getString("text"))
                .ifPresentOrElse(o -> log.info("Returned value: " + o), () -> log.error("No value returned"));
    }

    @Test
    @Order(6)
    void testAsyncSelection() {
        manager.executeAsyncQuery(new QueryBuilder(table)
                        .select()
                        .allColumns()
                        .where("id", 2)
                        .build(), rs -> rs.getInt("id") + " - " + rs.getString("text"))
                .join()
                .ifPresentOrElse(o -> log.info("Returned value (async): " + o), () -> log.error("No value returned (async)"));
    }
}
