package edu.baylor.ecs.msanose.service;

import edu.baylor.ecs.msanose.model.context.CyclicDependencyContext;
import edu.baylor.ecs.rad.context.RequestContext;
import edu.baylor.ecs.rad.context.ResponseContext;
import edu.baylor.ecs.rad.context.RestEntityContext;
import edu.baylor.ecs.rad.context.RestFlowContext;
import edu.baylor.ecs.rad.model.RestEntity;
import edu.baylor.ecs.rad.model.RestFlow;
import edu.baylor.ecs.rad.service.RestDiscoveryService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CyclicDependencyService {

    private final RestService restDiscoveryService;

    private Map<String, Integer> vertexMap = new HashMap<>();
    private List<List<Integer>> adjList;
    private int V;

    public CyclicDependencyService(RestService restDiscoveryService){
        this.restDiscoveryService = restDiscoveryService;
    }

    // This function is a variation of DFSUtil() in
    // https://www.geeksforgeeks.org/archives/18212
    private boolean isCyclicUtil(int i, boolean[] visited, boolean[] recStack) {

        // Mark the current node as visited and
        // part of recursion stack
        if (recStack[i])
            return true;

        if (visited[i])
            return false;

        visited[i] = true;

        recStack[i] = true;
        List<Integer> children = adjList.get(i);

        for (Integer c: children)
            if (isCyclicUtil(c, visited, recStack))
                return true;

        recStack[i] = false;

        return false;
    }

    // Returns true if the graph contains a
    // cycle, else false.
    // This function is a variation of DFS() in
    // https://www.geeksforgeeks.org/archives/18212
    private boolean isCyclic() {

        // Mark all the vertices as not visited and
        // not part of recursion stack
        boolean[] visited = new boolean[V];
        boolean[] recStack = new boolean[V];

        // Call the recursive helper function to
        // detect cycle in different DFS trees
        for (int i = 0; i < V; i++)
            if (isCyclicUtil(i, visited, recStack))
                return true;

        return false;
    }

    public boolean getCyclicDependencies(RequestContext request){
        CyclicDependencyContext context = new CyclicDependencyContext();
        ResponseContext responseContext = restDiscoveryService.generateResponseContext(request);

        // Map all entities to indexes
        int ndx = 0;
        for(RestEntityContext entity : responseContext.getRestEntityContexts()){
            vertexMap.put(entity.getResourcePath(), ndx);
            ndx++;
        }

        // Construct edges in adjacency list
        V = responseContext.getRestEntityContexts().size();
        adjList = new LinkedList<>();

        for(int i = 0; i < V; i++){
            adjList.add(new LinkedList<>());
        }

        for(RestFlow flow : responseContext.getRestFlowContext().getRestFlows()){
            int keyA = vertexMap.get(flow.getResourcePath());

            for(RestEntity server : flow.getServers()){
                int keyB = vertexMap.get(server.getResourcePath());

                adjList.get(keyA).add(keyB);
            }
        }

        return isCyclic();
    }
}
