package com.me.movieapp.controller;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.ModelAndView;
import com.me.movieapp.dao.MovieDao;
import com.me.movieapp.dao.MovieScreeningDao;
import com.me.movieapp.dao.OrderDao;
import com.me.movieapp.dao.UserDao;
import com.me.movieapp.enums.TicketType;
import com.me.movieapp.exception.MovieException;
import com.me.movieapp.pojo.Movie;
import com.me.movieapp.pojo.MovieScreening;
import com.me.movieapp.pojo.Order;
import com.me.movieapp.pojo.Screen;
import com.me.movieapp.pojo.Ticket;
import com.me.movieapp.validator.MovieValidator;

import org.hibernate.Query;
import org.hibernate.Session;

@Controller
public class AdminController {

	@Autowired
	@Qualifier("movieDao")
	MovieDao movieDao;

	@Autowired
	@Qualifier("movieScreeningDao")
	MovieScreeningDao movieScreenDao;
	
	@Autowired
	@Qualifier("movieValidator")
	MovieValidator movieValidator;

	@Autowired
	ServletContext servletContext;
	
	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(movieValidator);
	}
	
	private static String UPLOAD_LOCATION="C:/SpringProjects/images";

	@RequestMapping(value = "/admin/addmovie", method = RequestMethod.GET)
	protected ModelAndView addBooksForm() throws Exception {
		return new ModelAndView("add-movie", "movie", new Movie());
	}

	@RequestMapping(value = "/admin/addmovie", method = RequestMethod.POST)
	public String handleUpload(HttpServletRequest request, @ModelAttribute("movie") Movie movie,BindingResult result) {

	
		movieValidator.validate(movie, result);
		 
		 
		 if (result.hasErrors()) { return "add-movie"; }
		System.out.println("inside admin controller");

		HttpSession session = request.getSession();

		try {

			CommonsMultipartFile photoInMemory = movie.getPhoto();
			String fileName = photoInMemory.getOriginalFilename();
			File localFile = new File(UPLOAD_LOCATION + File.separator +  fileName);
			photoInMemory.transferTo(localFile);
			movie.setPoster(File.separator + "images" + File.separator + fileName);
			System.out.println(movie.getPoster());
			System.out.println("File is stored at" + localFile.getPath());
			System.out.print("added new Book");
			List<MovieScreening> ms = new ArrayList<MovieScreening>();

			int screenid = Integer.parseInt(request.getParameter("screen"));
			Screen sc = movieScreenDao.getScreen(screenid);
			String d1=request.getParameter("movieFromDate");
			System.out.println(d1);
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
			final LocalDate fromDate = LocalDate.parse(request.getParameter("movieFromDate"), formatter);
			System.out.println(fromDate);
			final LocalDate toDate = LocalDate.parse(request.getParameter("movieToDate"), formatter);
			final long days = ChronoUnit.DAYS.between(fromDate, toDate);
	
			for (LocalDate date = fromDate; date.isBefore(toDate); date = date.plusDays(1)) {
				System.out.println("inside date loop");
				MovieScreening ms1 = new MovieScreening();
				ms1.setMovie(movie);
				ms1.setMovieDate(date);
				System.err.println(ms1.getMovieDate());
				ms1.setShowTime("12:00");
				ms1.setScreen(sc);
				ms.add(ms1);
				
				MovieScreening ms2 = new MovieScreening();
				ms2.setMovie(movie);
				ms2.setMovieDate(date);
				ms2.setShowTime("15:00");
				ms2.setScreen(sc);
				ms.add(ms2);
				
				MovieScreening ms3 = new MovieScreening();
				ms3.setMovie(movie);
				ms3.setMovieDate(date);
				ms3.setShowTime("18:00");
				ms3.setScreen(sc);
				ms.add(ms3);
			}
			
			movie.setMovieScreening(ms);
			movieDao.create(movie);
			session.setAttribute("movie", movie);
			
			List<Movie> movieList = movieDao.getMovie();

			session.setAttribute("movieList", movieList);


		} catch (IllegalStateException e) {
			System.out.println("*** IllegalStateException: " + e.getMessage());
		} catch (IOException e) {
			System.out.println("*** IOException: " + e.getMessage());
		} catch (MovieException e) {
			e.printStackTrace();
		}

		return "index";
	}

}
