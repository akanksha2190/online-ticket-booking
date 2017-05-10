package com.me.movieapp.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.me.movieapp.pojo.User;
import com.me.movieapp.validator.UserValidator;
import com.me.movieapp.exception.UserException;
import com.me.movieapp.dao.UserDao;
import com.me.movieapp.enums.Role;

@Controller
public class UserController {

	@Autowired
	@Qualifier("userDao")
	UserDao userDao;
	
	@Autowired
	@Qualifier("userValidator")
	UserValidator validator;

	@InitBinder
	private void initBinder(WebDataBinder binder) {
		binder.setValidator(validator);
	}

	@RequestMapping(value = "user/login", method = RequestMethod.GET)
	public String goToUserHome(HttpServletRequest request) throws UserException {
		return "index";
	}
	
	@RequestMapping(value = "/user/signup", method = RequestMethod.GET)
	public ModelAndView goToUserSignUp(HttpServletRequest request) throws Exception {
		System.out.print("signup");

		return new ModelAndView("signup", "user", new User());
	}
	


	@RequestMapping(value = "user/login", method = RequestMethod.POST)
	protected String loginUser(	HttpServletRequest request) throws Exception {

		HttpSession session = (HttpSession) request.getSession();

		
			System.out.println(request.getParameter("email"));
			System.out.print("loginUser");

			User user = userDao.get(request.getParameter("email"), request.getParameter("password"));

			if (user == null) {
				System.out.println("UserName/Password does not exist");
				session.setAttribute("errorMessage", "UserName/Password does not exist");
				return "index";
			}

			session.setAttribute("user", user);
			user.setIsAuthenticated(true);
			user.setRole(Role.valueOf(userDao.getUserRole(request.getParameter("email"))));
			return "index";

		} 

	@RequestMapping(value = "user/logout", method = RequestMethod.GET)
	public String logOut(HttpServletRequest request,@ModelAttribute("user") User user) {
		System.out.println("inside logout");
		user.setIsAuthenticated(false);
		request.getSession().invalidate();
		return "redirect:/";
	}

	
	
	@RequestMapping(value="/user/signup", method = RequestMethod.POST)
	protected ModelAndView registerNewUser(HttpServletRequest request,  @ModelAttribute("user") User user,BindingResult result) throws UserException {

		//validator.validate(user, result);
		validator.validate(user, result);

		if (result.hasErrors()) {
			return new ModelAndView("signup", "user", user);
		}
		
		try {

			System.out.print("registerNewUser");
			//user.setRole(Role.ROLE_USER);
			User u = userDao.register(user);
			
			request.getSession().setAttribute("user", u);
			
			return new ModelAndView("index", "user", u);

		} catch (UserException e) {
			System.out.println("Exception: " + e.getMessage());
			return new ModelAndView("error", "errorMessage", "error while signup");
		}
		
	}
	

}

