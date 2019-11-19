package edu.baylor.ecs.msanose.controller;

import edu.baylor.ecs.msanose.service.APIService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/api/v1")
public class NoseController {

    private final APIService apiService;

    public NoseController(APIService apiService){
        this.apiService = apiService;
    }

    @GetMapping("/")
    public String getHandshake(){
        return "Hello from [NoseController]";
    }

    @GetMapping("/apis")
    public List<String> getApis(){
        return apiService.getAPIs();
    }
}
