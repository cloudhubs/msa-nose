package edu.baylor.ecs.jparser.model;

public enum LanguageFileType {
    UNKNOWN("N/A"),
    JAVA("Java");

    private String lang;

    LanguageFileType(String lang) {
        this.lang = lang;
    }

    public static LanguageFileType fromString(String lang) {
        for (LanguageFileType type : values()) {
            if (lang.equalsIgnoreCase(type.lang))
                return type;
        }
        return UNKNOWN;
    }

    public String asString() {
        return this.lang;
    }

}
