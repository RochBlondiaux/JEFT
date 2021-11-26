package me.rochblondiaux.storage.queries;

import lombok.Getter;

/**
 * @author Roch Blondiaux
 * www.roch-blondiaux.com
 */
public class Column {

    @Getter
    private final transient String column;

    Column(String column) {
        this.column = column;
    }

    public static Column create(String column) {
        return new Column(column);
    }

    public static Column dataType(String name, DataTypes type) {
        return new Column(name + " " + type.name());
    }

    public static Column dataType(String name, DataTypes.Limit type, int limiter) {
        return new Column(name + " " + type.name() + "(" + limiter + ")");
    }

    public static Column dataType(String name, DataTypes.FloatingPoint type, int length, int decimals) {
        return new Column(name + " " + type.name() + "(" + length + ", " + decimals + ")");
    }
}
