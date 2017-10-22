package com.karens.coding.server.stats.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller("HomeController")
public class HomeController {

	
	/**
	 * 
	 * @param modelAndView
	 * @param request
	 * @return
	 */
	@RequestMapping("/")
    public ModelAndView index(ModelAndView modelAndView, HttpServletRequest request) {
		modelAndView.setViewName("index");
        return modelAndView;
    }
	
	
	
}
