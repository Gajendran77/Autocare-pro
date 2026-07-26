package com.autocarepro.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public ModelAndView handleBusinessException(BusinessException ex, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("error/business-error");
        mav.addObject("message", ex.getMessage());
        mav.addObject("path", request.getRequestURI());
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneralException(Exception ex, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView("error/business-error");
        mav.addObject("message", "Something went wrong. Please try again.");
        mav.addObject("path", request.getRequestURI());
        return mav;
    }
}