package ltd.newbee.mall.controller.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import ltd.newbee.mall.common.NewBeeMallException;
import ltd.newbee.mall.util.Result;

import javax.servlet.http.HttpServletRequest;

/**
 * Global exception handling for newbee-mall
 */
@RestControllerAdvice
public class NewBeeMallExceptionHandler {

    @ExceptionHandler(Exception.class)
    public Object handleException(Exception e, HttpServletRequest req) {
        Result result = new Result();
        result.setResultCode(500);
        // Distinguish if it is a custom exception
        if (e instanceof NewBeeMallException) {
            result.setMessage(e.getMessage());
        } else {
            e.printStackTrace();
            result.setMessage("Unknown exception");
        }
        // Check if the request is an AJAX request. If it is, return a Result JSON
        // string. If not, return the error view.
        String contentTypeHeader = req.getHeader("Content-Type");
        String acceptHeader = req.getHeader("Accept");
        String xRequestedWith = req.getHeader("X-Requested-With");
        if ((contentTypeHeader != null && contentTypeHeader.contains("application/json"))
                || (acceptHeader != null && acceptHeader.contains("application/json"))
                || "XMLHttpRequest".equalsIgnoreCase(xRequestedWith)) {
            return result;
        } else {
            ModelAndView modelAndView = new ModelAndView();
            modelAndView.addObject("message", e.getMessage());
            modelAndView.addObject("url", req.getRequestURL());
            modelAndView.addObject("stackTrace", e.getStackTrace());
            modelAndView.addObject("author", "Shisan");
            modelAndView.addObject("ltd", "NewBee Mall");
            modelAndView.setViewName("error/error");
            return modelAndView;
        }
    }
}
