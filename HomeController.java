package com.me.movieapp.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import com.me.movieapp.dao.MovieDao;
import com.me.movieapp.exception.MovieException;
import com.me.movieapp.pojo.Movie;
import com.me.movieapp.validator.MovieValidator;

import org.hibernate.Query;
import org.hibernate.Session;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {

	@Autowired
	@Qualifier("movieDao")
	MovieDao movieDao;

	/**
	 * Simply selects the home view to render by returning its name.
	 * 
	 * @throws MovieException
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String showHome(HttpServletRequest request) throws MovieException {
		HttpSession session = request.getSession();

		List<Movie> movieList = movieDao.getMovie();

		session.setAttribute("movieList", movieList);

		return "index";

	}

}
