package me.rochblondiaux.storage.queries;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public enum DataTypes {
    TINYINT,
    SMALLINT,
    MEDIUMINT,
    INT,
    BIGINT,
    FLOAT,
    DOUBLE,
    DECIMAL,
    DATE,
    DATETIME,
    TIMESTAMP,
    TIME,
    YEAR,
    BOOLEAN,
    CHAR,
    VARCHAR,
    TINYTEXT,
    MEDIUMTEXT,
    TEXT,
    LONGTEXT,
    ENUM,
    BLOB,
    ;

    public enum FloatingPoint {
        FLOAT,
        DOUBLE,
        DECIMAL
    }

    public enum Limit {
        CHAR,
        TINYINT,
        VARCHAR,
        BIGINT,
    }
}