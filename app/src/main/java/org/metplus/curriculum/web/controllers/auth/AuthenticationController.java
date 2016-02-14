package org.metplus.curriculum.web.controllers.auth;

import org.metplus.curriculum.web.controllers.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


/**
 * Created by joaopereira on 2/14/2016.
 */
@RestController
public class AuthenticationController extends BaseController {

    @RequestMapping(value = authenticationUrl, method = RequestMethod.POST)
    public String authenticate() {
        return "This is just for in-code-documentation purposes and Rest API reference documentation." +
                "Servlet will never get to this point as Http requests are processed by AuthenticationFilter." +
                "Nonetheless to authenticate Domain User POST request with X-Auth-Username and X-Auth-Password headers " +
                "is mandatory to this URL. If username and password are correct valid token will be returned (just json string in response) " +
                "This token must be present in X-Auth-Token header in all requests for all other URLs, including logout." +
                "Authentication can be issued multiple times and each call results in new ticket.";
    }
}
