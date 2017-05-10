package com.me.movieapp.controller;

import java.util.ArrayList;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
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
import com.me.movieapp.pojo.Ticket;
import com.me.movieapp.pojo.User;
import com.me.movieapp.validator.MovieValidator;

import org.hibernate.Query;
import org.hibernate.Session;

@Controller
public class MovieController {

	@Autowired
	@Qualifier("movieScreeningDao")
	MovieScreeningDao movieScreenDao;

	@Autowired
	@Qualifier("movieDao")
	MovieDao movieDao;

	@Autowired
	@Qualifier("orderDao")
	OrderDao orderDao;
	
	@Autowired
	@Qualifier("userDao")
	UserDao userDao;
	
	

	@RequestMapping(value = "movie/booking/{id}", method = RequestMethod.GET)
	public ModelAndView getMovieInfo(HttpServletRequest request, @PathVariable("id") String movieId) throws Exception {
		System.out.println(movieId);
		Movie movie = movieDao.getMovieById(movieId);

		return new ModelAndView("movie", "movie", movie);
	}

	@RequestMapping(value = "movie/booking/{id}", method = RequestMethod.POST)
	public ModelAndView getMovieInfoTime(HttpServletRequest request, @PathVariable("id") String movieId,
			@ModelAttribute("movie") Movie movie) throws MovieException {

		try {

			HttpSession session = request.getSession();

			String movieScreeningID = request.getParameter("movieInfoId");

			MovieScreening movieScreening = movieScreenDao.getMovieScreening(movieScreeningID);
			List<Ticket> tickets = new ArrayList<Ticket>();
			Ticket t1 = new Ticket();
			t1.setType(TicketType.ADULT);
			t1.setQuantity(0);
			t1.setMovieScreening(movieScreening);
			Ticket t2 = new Ticket();
			t2.setType(TicketType.CHILD);
			t2.setQuantity(0);
			t2.setMovieScreening(movieScreening);
			tickets.add(t1);
			tickets.add(t2);
			return new ModelAndView("booktickets", "ticket", tickets);
		} catch (MovieException e) {
			System.out.println(e.getMessage());
			return new ModelAndView("error", "errorMessage", "error while booking tickets");
		}
	}

	@RequestMapping(value = "movie/ticket/confirm", method = RequestMethod.POST)
	public ModelAndView selectTicktes(HttpServletRequest request, @ModelAttribute("tickets") Ticket tickets)
			throws Exception {
		HttpSession session = request.getSession();
		System.out.println("inside ticket");
		Order order = new Order();
		String[] ticketCount = request.getParameterValues("ticketCount");
		int qty = 0;

		System.out.println(ticketCount[0]);
		List<Ticket> listTickets = new ArrayList<Ticket>();
		double adultPrice = 0;
		double childPrice = 0;
	
		if (!ticketCount[0].equalsIgnoreCase("")) {
			Ticket t1 = new Ticket();
			t1.setType(TicketType.ADULT);
			t1.setQuantity(Integer.parseInt(ticketCount[0]));
			adultPrice = t1.getQuantity() * t1.getType().price();
			System.out.println(adultPrice);
			session.setAttribute("adultPrice", adultPrice);
			listTickets.add(t1);
		}

		if (!ticketCount[1].equalsIgnoreCase("")) {
			Ticket t2 = new Ticket();
			t2.setType(TicketType.CHILD);
			t2.setQuantity(Integer.parseInt(ticketCount[1]));
			childPrice = t2.getQuantity() * t2.getType().price();
			session.setAttribute("childPrice", childPrice);
			listTickets.add(t2);
		}
	
		User user=(User)session.getAttribute("user");
		order.setUser(user);
		order.setTicketOrder(listTickets);
		order.setOrderTotal((adultPrice) + (childPrice));
		order = orderDao.create(order);
		return new ModelAndView("orderconfirm", "order", order);
	}
	
}
