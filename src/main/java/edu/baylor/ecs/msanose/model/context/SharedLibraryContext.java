package edu.baylor.ecs.msanose.model.context;

import edu.baylor.ecs.msanose.model.SharedLibrary;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@AllArgsConstructor
@Data public class SharedLibraryContext {
    Map<String, SharedLibrary> sharedLibraries;

    public SharedLibraryContext(){
        this.sharedLibraries = new HashMap<>();
    }

    public void addSharedLibrary(SharedLibrary sharedLibrary){
        this.sharedLibraries.put(sharedLibrary.getLibrary(), sharedLibrary);
    }

    public SharedLibrary getOrDefault(String library){
        return this.sharedLibraries.getOrDefault(library, new SharedLibrary(library));
    }
}
