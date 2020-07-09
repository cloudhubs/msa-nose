package edu.baylor.ecs.jparser.model;

import lombok.Data;

@Data
public class AnnotationValuePair {

    private String key;
    private String value;

    public AnnotationValuePair(String key, String value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return key + "->" + value;
    }

}
