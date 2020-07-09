package edu.baylor.ecs.jparser.model;

public enum AccessorType {

    PRIVATE("private"),
    PUBLIC("public"),
    PROTECTED("protected"),
    DEFAULT;

    String type;

    AccessorType() {
        this.type = null;
    }

    AccessorType(String type) {
        this.type = type;
    }

    public static AccessorType fromString(String type) {
        for (AccessorType a : AccessorType.values()) {
            if (a.type != null && a.type.equalsIgnoreCase(type))
                return a;
        }
        return null;
    }

    public String getTypeAsString() {
        return this.type;
    }
}
