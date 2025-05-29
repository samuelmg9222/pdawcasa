package pdaw.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {
    @RequestMapping("/access-denied")
    public String accessDenied() {
        return "/access-denied";
    }
    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("jakarta.servlet.error.status_code");

        if (statusCode != null) {
            switch (statusCode) {
                case 400:
                    return "error/400";
                case 404:
                    return "error/404";
                case 405:
                    return "error/405";
                case 500:
                    return "error/500";
                default:
                    return "error/error";
            }
        }
        return "error/error";
    }
}
