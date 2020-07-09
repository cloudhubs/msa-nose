package edu.baylor.ecs.msanose.service;

import edu.baylor.ecs.rad.context.RequestContext;
import edu.baylor.ecs.rad.context.ResponseContext;
import edu.baylor.ecs.rad.service.RestDiscoveryService;
import org.springframework.stereotype.Service;

@Service
public class RestService {

    private final RestDiscoveryService restDiscoveryService;

    private ResponseContext cache = null;

    public RestService(RestDiscoveryService restDiscoveryService){
        this.restDiscoveryService = restDiscoveryService;
    }

    public ResponseContext generateResponseContext(RequestContext requestContext){
        if(this.cache == null){
            this.cache = restDiscoveryService.generateResponseContext(requestContext);
        }

        return this.cache;
    }

}
