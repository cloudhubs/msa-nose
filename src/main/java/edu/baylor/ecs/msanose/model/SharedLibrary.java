package edu.baylor.ecs.msanose.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@NoArgsConstructor @AllArgsConstructor
@Data public class SharedLibrary {
    String library;
    Set<String> microservices;
    int count;

    public SharedLibrary(String library){
        this.library = library;
        this.count = 0;
        this.microservices = new HashSet<>();
    }

    public void add(String msaA, String msaB){
        this.microservices.add(msaA);
        this.microservices.add(msaB);
        this.count = this.microservices.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SharedLibrary that = (SharedLibrary) o;

        return library != null ? library.equals(that.library) : that.library == null;
    }

    @Override
    public int hashCode() {
        return library != null ? library.hashCode() : 0;
    }
}
