package com.mymobile.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.mymobile.entity.CreditCard;
import com.mymobile.entity.Customer;
import com.mymobile.entity.Phone;
import com.mymobile.entity.PhoneLine;
import com.mymobile.entity.PhonePlan;
import com.mymobile.entity.Transaction;
import com.mymobile.service.PhoneService;
import com.mymobile.service.PhoneServiceImpl;

@Controller
@RequestMapping("/account")
public class AccountController {
	
	// NEED to inject our phoneService
	@Autowired
	private PhoneService phoneService;
	
	@RequestMapping("/login")
	public String login(Model theModel)
	{
		theModel.addAttribute("theCustomer", new Customer());
		return "login";
	}
	
	@RequestMapping("/register")
	public String register(Model theModel)
	{
		Customer customer = new Customer();
		
		theModel.addAttribute("customer", customer);
		
		return "register";
	}
	
	@RequestMapping("/addCustomer")
	public String addCustomer(@Valid @ModelAttribute("customer") Customer theCustomer, BindingResult theBindingResult)
	{
		if(theBindingResult.hasErrors())
			return "register";
		// save the customer using the phoneService
		phoneService.addCustomer(theCustomer);
		
		return "redirect:/account/login";
	}
	@RequestMapping("/loggedIn")
	public String loggedIn(@ModelAttribute("theCustomer") Customer theCustomer, Model model, HttpSession session, HttpServletRequest request)
	{
		
		Customer customer = phoneService.login(theCustomer.getEmail(), theCustomer.getPassword(), request);
		
		if(customer == null)
		{
			model.addAttribute("theCustomer", new Customer());
			model.addAttribute("error", "Invalid Credientials!");
			return "login";
		}
		
		// add current user to the session
		session.setAttribute("currentUser", customer);
		
		return "redirect:/account/myAccount";
	}
	
	@RequestMapping("/myAccount")
	public String myAccount(Model model, HttpSession session)
	{
		// get current user from the logged session
		Customer currentCustomer = (Customer) session.getAttribute("currentUser");
		if(currentCustomer == null)
			return "redirect:/account/login";
		
		// get phone lines based on customer cid
		List<PhoneLine> phoneLines = phoneService.getPhoneLines(currentCustomer);
		
		// get phone plan based on the customer cid
		PhonePlan plan = phoneService.getPhonePlan(currentCustomer);
		
		// add the objects to the model
		model.addAttribute("currentCustomer", currentCustomer);
		model.addAttribute("phoneLines", phoneLines);
		model.addAttribute("phonePlan", plan);
		
		
		
		return "account";
	}
	@RequestMapping("/addALine")
	public String addALine(Model theModel, HttpSession session)
	{
		Customer currentUser = (Customer) session.getAttribute("currentUser");
		if(currentUser == null)
			return "redirect:/account/login";
		PhonePlan plan = phoneService.getPhonePlan(currentUser);
		if(plan != null)
		{
			if(currentUser.getPhoneLines() > plan.getNumberOfLines() - 1)
			{
				theModel.addAttribute("lineError", "Exceeding Phone Lines");
				return "redirect:/account/myAccount";
			}
		}
		theModel.addAttribute("phoneLine", new PhoneLine());
		theModel.addAttribute("phones", phoneService.getPhones());
		return "addLine";
	}
	
	@RequestMapping("/saveLine")
	public String saveLine(@ModelAttribute("phoneLine")PhoneLine phoneLine, HttpSession session)
	{
		
		// get name of phone
		for(Phone tempPhone: phoneService.getPhones())
		{
			
			if(tempPhone.getPid() == phoneLine.getPid())
			{
				phoneLine.setPhoneName(tempPhone.getName());
				phoneLine.setImgSrc(tempPhone.getImgSrc());
				phoneLine.setAlt(tempPhone.getAlt());
				phoneLine.setColor(tempPhone.getColor());
			}
		}
		
		// get cid of customer from the session
		Customer currentUser = (Customer) session.getAttribute("currentUser");
		if(currentUser == null)
			return "redirect:/account/login";
		phoneLine.setCid(currentUser.getCid());
		
		
		// print out object 
		// System.out.println(phoneLine.toString());
		
		// save the line and add the fee to the customer cid $10 plus the phone price
		phoneService.saveLine(phoneLine);
		
		float newBalance = 10 + currentUser.getBalance() + phoneService.getPhonePrice(phoneLine.getPid());
		
		currentUser.setBalance(newBalance);
		int phoneLines = currentUser.getPhoneLines() + 1;
		currentUser.setPhoneLines(phoneLines);
		
		
		// save the customer with the new balance
		phoneService.addCustomer(currentUser);
		
		return "redirect:/account/myAccount";
	}
	
	@RequestMapping("/selectPhonePlan")
	public String selectPhonePlan(Model theModel)
	{
		theModel.addAttribute("plan", new PhonePlan());
		theModel.addAttribute("phonePlans", phoneService.getPhonePlans());
		return "phone_plans";
	}
	
	@RequestMapping("/savePhonePlan")
	public String savePhonePlan(@ModelAttribute("plan")PhonePlan plan, HttpSession session)
	{
		Customer currentUser = (Customer) session.getAttribute("currentUser");
		if(currentUser == null)
			return "redirect:/account/login";
		currentUser.setPlanId(plan.getPlanId());
		System.out.println(plan.toString());
		
		float newBalance = phoneService.getRate(plan.getPlanId()) + currentUser.getBalance();
		
		currentUser.setBalance(newBalance);
		phoneService.addCustomer(currentUser);
		return "redirect:/account/myAccount";
	}
	@RequestMapping("/pay")
	public String pay(Model model,HttpSession session)
	{
		// bind the creditcard object
		model.addAttribute("creditCard", new CreditCard());
		
		// get saved credit cards
		Customer currentCustomer = (Customer) session.getAttribute("currentUser");
		if(currentCustomer == null)
			return "redirect:/account/login";
		List<CreditCard> savedCards = phoneService.getCreditCards(currentCustomer);
		model.addAttribute("savedCards", savedCards);
		
		return "payment";
	}
	@RequestMapping("/savePayment")
	public String savePayment(@Valid @ModelAttribute("creditCard")CreditCard card, HttpSession session, BindingResult theBindingResult)
	{ 
		if(theBindingResult.hasErrors())
			return "payment";
		System.out.println(card.toString());
		Customer currentCustomer = (Customer) session.getAttribute("currentUser");
		card.setCid(currentCustomer.getCid());
		
		// save the date of the transaction
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date today = Calendar.getInstance().getTime();        
	    String reportDate = df.format(today);
	    
		Transaction transaction = new Transaction(currentCustomer.getBalance(), reportDate , card.getCrid(), currentCustomer.getCid());
		
		// save transaction and creditcard
		currentCustomer.setBalance(0.0f);
		phoneService.savePayment(transaction, card, currentCustomer);
		
		return "redirect:/account/myAccount";
	}
	@RequestMapping("/savePaymentS")
	public String savePaymentS(@RequestParam("crid") int crid, HttpSession session)
	{
		// get CurrentCustomer 
		Customer currentCustomer = (Customer) session.getAttribute("currentUser");
		
		// get current card
		CreditCard creditCard = phoneService.getCreditCard(crid);
		creditCard.setCid(currentCustomer.getCid());
		
		// get current Transaction
		// save the date of the transaction
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		Date today = Calendar.getInstance().getTime();        
	    String reportDate = df.format(today);
	    
		Transaction transaction = new Transaction(currentCustomer.getBalance(), reportDate, creditCard.getCrid(), currentCustomer.getCid());
		currentCustomer.setBalance(0.0f);
		System.out.println(creditCard.toString());
		phoneService.savePayment(transaction, creditCard, currentCustomer);
		
		return "redirect:/account/myAccount";
	}
	@GetMapping("/deletePhoneLine")
	public String deletePhoneLine(@RequestParam("phoneLineId") int plid, HttpSession session)
	{
		// get CurrentCustomer 
		Customer currentCustomer = (Customer) session.getAttribute("currentUser");
		int ph = currentCustomer.getPhoneLines()-1;
		currentCustomer.setPhoneLines(ph);
		// delete the phone line id
		phoneService.deletePhoneLine(plid);
		phoneService.addCustomer(currentCustomer);
		
		return "redirect:/account/myAccount";
	}
	@RequestMapping("/logout")
	public String logout(Model theModel, HttpSession session, HttpServletRequest request)
	{
		session = request.getSession();
		// log out
		session.invalidate();
		
		// return to the login page
		return "redirect:/account/login";
	}
	@RequestMapping("/forgotPW")
	public String forgotPW()
	{
		return "verifyReset";
	}
	@RequestMapping("/viewPhones")
	public String viewPhones()
	{
		return "redirect:http://localhost:8080/MyMobile/main/list";
	}
	@RequestMapping("/reset")
	public String reset(@RequestParam("email") String email, Model model, HttpServletRequest request)
	{
		Customer customer = phoneService.reset(email);
		
		if(customer == null)
		{
			System.out.println("Customer not found");
			return "verifyReset";
		}
		
		// add current user to the session
		model.addAttribute("customer", customer);
		
		return "reset";
	}
	
	
}
