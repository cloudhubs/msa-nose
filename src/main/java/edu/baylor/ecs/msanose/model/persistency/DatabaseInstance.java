package edu.baylor.ecs.msanose.model.persistency;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DatabaseInstance {
    private String database;
    private Integer port;
    private String host;
    private DatabaseType type;

    public DatabaseInstance(Object database, Object port, Object host){
        this.database = (String) database;
        this.port = (Integer) port;
        this.host = (String) host;
        this.type = DatabaseType.GENERIC;
    }

    public DatabaseInstance(Object database, Object port, Object host, DatabaseType type){
        this.database = (String) database;
        this.port = (Integer) port;
        this.host = (String) host;
        this.type = type;
    }
}
