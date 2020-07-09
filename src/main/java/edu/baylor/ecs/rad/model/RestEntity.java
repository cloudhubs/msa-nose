package edu.baylor.ecs.rad.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class defines a REST entity along with it's properties and metadata.
 * A rest entity can be a rest-endpoint(server) or, a rest-call(client).
 *
 * @author Dipta Das
 */

@Getter
@Setter
@ToString
public class RestEntity {
    private boolean isClient;

    private String url;

    private String applicationName; // used in eureka discovery
    private String ribbonServerName;

    private String resourcePath;
    private String className;
    private String methodName;
    private String returnType;

    private String path;
    private HttpMethod httpMethod;

    private List<Param> pathParams;
    private List<Param> queryParams;

    private String consumeType; // can be any mime type
    private String produceType; // can be any mime type

    public void addPathParam(Param pathParam) {
        if (pathParams == null) {
            pathParams = new ArrayList<>();
        }
        pathParams.add(pathParam);
    }

    public void addQueryParam(Param queryParam) {
        if (queryParams == null) {
            queryParams = new ArrayList<>();
        }
        queryParams.add(queryParam);
    }

    public void setUrl(String url){
        Pattern pattern = Pattern.compile("(/[a-zA-Z0-9]+)+/?$");
        Matcher matcher = pattern.matcher(url);
        String trimmed = url;
        if (matcher.find()) {
            trimmed = matcher.group(0);
        }
        this.url = trimmed;
    }
}
