package ltd.nft.mall.controller.common;

import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.ModelAndView;

import ltd.nft.mall.common.marketException;
import ltd.nft.mall.util.Result;

import javax.servlet.http.HttpServletRequest;

/**
 * Global exception handling for market
 */
@RestControllerAdvice
public class marketExceptionHandler {

    @ExceptionHandler(marketException.class)
    public Object handleException(marketException e, HttpServletRequest req) {
        Result result = new Result();
        result.setResultCode(500);
        // Distinguish if it is a custom exception
        if (e instanceof marketException) {
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
            modelAndView.addObject("author", "Senti");
            modelAndView.addObject("ltd", "NFTmarket");
            modelAndView.setViewName("error/error");
            return modelAndView;
        }
    }
}
