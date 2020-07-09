package edu.baylor.ecs.rad.service;

import edu.baylor.ecs.rad.analyzer.Helper;
import edu.baylor.ecs.rad.analyzer.JaxRsAnalyzer;
import edu.baylor.ecs.rad.analyzer.SpringAnalyzer;
import edu.baylor.ecs.rad.analyzer.SpringClientAnalyzer;
import edu.baylor.ecs.rad.context.RestEntityContext;
import edu.baylor.ecs.rad.model.HttpMethod;
import edu.baylor.ecs.rad.model.RestEntity;
import javassist.CtClass;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;

/**
 * This class constructs a {@link RestEntityContext}.
 * It takes a list of JavaAssist CtClass as input.
 *
 * @author Dipta Das
 */

@Service
@AllArgsConstructor
public class RestEntityService {
    private final JaxRsAnalyzer jaxRsAnalyzer;
    private final SpringAnalyzer springAnalyzer;
    private final SpringClientAnalyzer springClientAnalyzer;
    // private final SpringClientWrapperAnalyzer springClientWrapperAnalyzer;


    public RestEntityService() {
        this.jaxRsAnalyzer = new JaxRsAnalyzer();
        this.springAnalyzer = new SpringAnalyzer();
        this.springClientAnalyzer = new SpringClientAnalyzer();
    }

    public RestEntityContext getRestEntityContext(List<CtClass> allClasses, String path, String serviceDNS, Properties properties) {
        RestEntityContext restEntityContext = new RestEntityContext();
        restEntityContext.setResourcePath(path);

        for (CtClass ctClass : allClasses) {
            restEntityContext.getRestEntities().addAll(jaxRsAnalyzer.getRestEntity(ctClass));
            restEntityContext.getRestEntities().addAll(springAnalyzer.getRestEntity(ctClass));
            restEntityContext.getRestEntities().addAll(springClientAnalyzer.getRestEntity(ctClass, properties));
            // restEntityContext.getRestEntities().addAll(springClientWrapperAnalyzer.getRestEntity(ctClass));
        }

        for (RestEntity restEntity : restEntityContext.getRestEntities()) {
            populateDefaultProperties(restEntity, path, serviceDNS, properties);
        }

        return restEntityContext;
    }

    private void populateDefaultProperties(RestEntity restEntity, String path, String serviceDNS, Properties properties) {
        restEntity.setResourcePath(path);
        if (restEntity.getPath() == null) {
            restEntity.setPath("/");
        }
        if (restEntity.getHttpMethod() == null) {
            restEntity.setHttpMethod(HttpMethod.GET);
        }

        // find application name, used in eureka discovery
        restEntity.setApplicationName(findApplicationNameProperties(properties));

        // find serverIP and port
        // priority order: serviceDNS > application name > localhost
        String serverIP = "http://localhost";
        if (serviceDNS != null) {
            serverIP = "http://" + serviceDNS; // kubernetes
        } else if (restEntity.getApplicationName() != null) {
            serverIP = "http://" + restEntity.getApplicationName(); // ribbon
        }
        String serverPort = findPortFromProperties(properties);

        if (!restEntity.isClient()) { // set server ip and port
            restEntity.setUrl(Helper.mergeUrlPath(serverIP + ":" + serverPort, restEntity.getPath()));
        } else if (restEntity.getUrl() == null) { // set client ip and port from MicroProfile config
            String mpRestUrl = properties.getProperty(restEntity.getClassName() + "/mp-rest/url");
            if (mpRestUrl != null) {
                restEntity.setUrl(Helper.mergeUrlPath(mpRestUrl, restEntity.getPath()));
            }
        }
    }

    private String findPortFromProperties(Properties properties) {
        if (properties == null) return "";

        String port = properties.getProperty("port");
        if (port == null) {
            port = properties.getProperty("server.port");
        }
        return port;
    }

    private String findApplicationNameProperties(Properties properties) {
        if (properties == null) return null;
        return properties.getProperty("spring.application.name");
    }
}
