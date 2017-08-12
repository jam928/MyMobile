package com.mymobile.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mymobile.entity.Customer;
import com.mymobile.entity.Phone;
import com.mymobile.entity.PhoneLine;
import com.mymobile.service.PhoneService;
import com.mymobile.service.PhoneServiceImpl;

@Controller
@RequestMapping("/main")
public class PhoneController {
	
	// NEED to inject our phoneService
	@Autowired
	private PhoneService phoneService;
	
	@RequestMapping("/list")
	public String listPhones(Model theModel)
	{
		// get list of phones from the dao(db)
		List<Phone> phones = phoneService.getPhones();
		
		// add the phones to the model
		theModel.addAttribute("phones", phones);
		
		return "list-phones";
	}
	
	@RequestMapping("/sell")
	public String sellPhones(Model theModel)
	{
		return "sell-phones";
	}
	
	@RequestMapping("/login")
	public String login()
	{
		return "redirect:http://localhost:8080/MyMobile/account/login";
	}
}
