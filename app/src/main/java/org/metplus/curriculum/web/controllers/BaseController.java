package org.metplus.curriculum.web.controllers;

/**
 * Created by Joao Pereira on 14/11/2015.
 */
public class BaseController {
    final static public String baseUrl = "/api/v1/";
    final static public String authenticationUrl = baseUrl + "authenticate";

    final static public String baseUrlApi = "/api/v";

    public static final String AUTOCONFIG_ENDPOINT = "/autoconfig";
    public static final String BEANS_ENDPOINT = "/beans";
    public static final String CONFIGPROPS_ENDPOINT = "/configprops";
    public static final String ENV_ENDPOINT = "/env";
    public static final String MAPPINGS_ENDPOINT = "/mappings";
    public static final String METRICS_ENDPOINT = "/metrics";
    public static final String SHUTDOWN_ENDPOINT = "/shutdown";
}
