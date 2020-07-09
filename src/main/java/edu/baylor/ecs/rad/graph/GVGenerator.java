package edu.baylor.ecs.rad.graph;

import edu.baylor.ecs.rad.context.ResponseContext;
import edu.baylor.ecs.rad.model.RestEntity;
import edu.baylor.ecs.rad.model.RestFlow;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class generates a Graphviz file from {@link ResponseContext}.
 * The Graphviz file visualizes the rest flows among the microservices.
 *
 * @author Dipta Das
 */

public class GVGenerator {
    public static void generate(ResponseContext responseContext) {
        StringBuilder graph = new StringBuilder();
        graph.append("digraph cil_rad {").append("\n");
        graph.append("rankdir = LR;").append("\n");
        graph.append("node [shape=box];").append("\n");

        int clusterIndex = 0;

        Map<String, Set<String>> clusters = getClusters(responseContext);

        for (String key : clusters.keySet()) {
            StringBuilder cluster = new StringBuilder();

            cluster.append("subgraph cluster_").append(clusterIndex++).append(" {").append("\n")
                    .append("label = ").append(key).append(";").append("\n")
                    .append("color=blue;").append("\n")
                    .append("rank = same;");

            Set<String> entities = clusters.get(key);

            for (String entity : entities) {
                cluster.append(" ").append(entity).append(";");
            }

            cluster.append("\n").append("}").append("\n");
            graph.append(cluster);
        }

        for (RestFlow restFlow : responseContext.getRestFlowContext().getRestFlows()) {
            String nodeFrom = getFullMethodName(restFlow);

            for (RestEntity server : restFlow.getServers()) {
                String nodeTo = getFullMethodName(server);
                String label = getLinkLabel(server);

                String link = String.format("%s  -> %s [ label = %s ];", nodeFrom, nodeTo, label);
                graph.append(link).append("\n");
            }
        }

        graph.append("}");

        try (PrintWriter out = new PrintWriter(responseContext.getRequest().getOutputPath())) {
            out.println(graph);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Map<String, Set<String>> getClusters(ResponseContext responseContext) {
        Map<String, Set<String>> clusters = new HashMap<>();

        for (RestFlow restFlow : responseContext.getRestFlowContext().getRestFlows()) {
            String nodeFrom = getFullMethodName(restFlow);
            addToMap(clusters, addDoubleQuotations(restFlow.getResourcePath()), nodeFrom);

            for (RestEntity server : restFlow.getServers()) {
                String nodeTo = getFullMethodName(server);
                addToMap(clusters, addDoubleQuotations(server.getResourcePath()), nodeTo);
            }
        }

        return clusters;
    }

    private static String addDoubleQuotations(String str) {
        return "\"" + str + "\"";
    }

    private static void addToMap(Map<String, Set<String>> m, String key, String value) {
        if (!m.containsKey(key)) {
            m.put(key, new HashSet<>());
        }
        m.get(key).add(value);
    }

    private static String getLinkLabel(RestEntity restEntity) {
        return addDoubleQuotations(restEntity.getHttpMethod() + " " + restEntity.getUrl());
    }

    private static String getFullMethodName(RestEntity restEntity) {
        return addDoubleQuotations(restEntity.getClassName() + "." + restEntity.getMethodName());
    }

    private static String getFullMethodName(RestFlow restFlow) {
        return addDoubleQuotations(restFlow.getClassName() + "." + restFlow.getMethodName());
    }
}
