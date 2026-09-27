package com.mymobile.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mymobile.dto.request.AddPhoneLineRequest;
import com.mymobile.dto.request.PaymentRequest;
import com.mymobile.dto.request.RegisterCustomerRequest;
import com.mymobile.dto.request.SelectPhonePlanRequest;
import com.mymobile.entity.CreditCard;
import com.mymobile.entity.Customer;
import com.mymobile.entity.Phone;
import com.mymobile.entity.PhonePlan;
import com.mymobile.entity.Transaction;
import com.mymobile.mapper.CreditCardMapper;
import com.mymobile.mapper.CustomerMapper;
import com.mymobile.mapper.PhoneLineMapper;
import com.mymobile.mapper.PhoneMapper;
import com.mymobile.mapper.PhonePlanMapper;
import com.mymobile.security.CurrentUser;
import com.mymobile.service.PhoneService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
	
	// cid of the customer whose credentials are being reset
	private static final String RESET_USER_ID = "resetUserId";
	
	private static final DateTimeFormatter TRANSACTION_DATE_FORMAT = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
	
	private final PhoneService phoneService;
	private final CustomerMapper customerMapper;
	private final PhoneMapper phoneMapper;
	private final PhoneLineMapper phoneLineMapper;
	private final PhonePlanMapper phonePlanMapper;
	private final CreditCardMapper creditCardMapper;
	private final PasswordEncoder passwordEncoder;
	
	// the login form is submitted to /account/loggedIn, which Spring Security handles
	@GetMapping("/login")
	public String login()
	{
		return "login";
	}
	
	@RequestMapping("/register")
	public String register(Model theModel)
	{
		theModel.addAttribute("customer", new RegisterCustomerRequest());
		
		return "register";
	}
	
	@RequestMapping("/addCustomer")
	public String addCustomer(@Valid @ModelAttribute("customer") RegisterCustomerRequest request, BindingResult theBindingResult, RedirectAttributes redirectAttributes)
	{
		if(!theBindingResult.hasErrors() && phoneService.isEmailTaken(request.getEmail(), 0))
			rejectDuplicateEmail(theBindingResult);
		if(theBindingResult.hasErrors())
			return "register";
		// save the customer using the phoneService, with the password hashed
		Customer customer = customerMapper.toEntity(request);
		customer.setPassword(passwordEncoder.encode(request.getPassword()));
		try
		{
			phoneService.addCustomer(customer);
		}
		catch(DataIntegrityViolationException e)
		{
			// another registration with the same email got in first
			rejectDuplicateEmail(theBindingResult);
			return "register";
		}
		
		redirectAttributes.addFlashAttribute("message", "Your account was created. Sign in to continue.");
		return "redirect:/account/login";
	}
	
	@RequestMapping("/myAccount")
	public String myAccount(Model model)
	{
		// get the logged in customer
		Customer currentCustomer = currentCustomer();
		if(currentCustomer == null)
			return "redirect:/account/login";
		
		// add the customer, phone lines and phone plan to the model
		model.addAttribute("currentCustomer", customerMapper.toResponse(currentCustomer));
		model.addAttribute("phoneLines", phoneLineMapper.toResponses(phoneService.getPhoneLines(currentCustomer)));
		model.addAttribute("phonePlan", phonePlanMapper.toResponse(phoneService.getPhonePlan(currentCustomer)));
		
		return "account";
	}
	@RequestMapping("/addALine")
	public String addALine(@RequestParam(name = "pid", required = false) Integer pid, Model theModel, RedirectAttributes redirectAttributes)
	{
		Customer currentUser = currentCustomer();
		if(currentUser == null)
			return "redirect:/account/login";
		PhonePlan plan = phoneService.getPhonePlan(currentUser);
		if(plan != null)
		{
			if(currentUser.getPhoneLines() > plan.getNumberOfLines() - 1)
			{
				redirectAttributes.addFlashAttribute("lineError", "Your plan allows " + plan.getNumberOfLines() + " line(s). Choose a bigger plan to add more.");
				return "redirect:/account/myAccount";
			}
		}
		// pre-select the phone when coming from its details page
		AddPhoneLineRequest request = new AddPhoneLineRequest();
		if(pid != null)
			request.setPid(pid);
		theModel.addAttribute("phoneLine", request);
		theModel.addAttribute("phones", phoneMapper.toResponses(phoneService.getPhones()));
		return "addLine";
	}
	
	@RequestMapping("/saveLine")
	public String saveLine(@Valid @ModelAttribute("phoneLine") AddPhoneLineRequest request, BindingResult theBindingResult, Model theModel, RedirectAttributes redirectAttributes)
	{
		// get the customer from the session
		Customer currentUser = currentCustomer();
		if(currentUser == null)
			return "redirect:/account/login";
		
		Phone phone = phoneService.getPhone(request.getPid());
		if(phone == null)
			theBindingResult.rejectValue("pid", "required", "Please choose a phone.");
		if(theBindingResult.hasErrors())
		{
			theModel.addAttribute("phones", phoneMapper.toResponses(phoneService.getPhones()));
			return "addLine";
		}
		
		// save the line and add the fee to the customer cid $10 plus the phone price
		phoneService.saveLine(phoneLineMapper.toEntity(request, phone, currentUser.getCid()));
		
		currentUser.setBalance(10 + currentUser.getBalance() + phone.getPrice());
		currentUser.setPhoneLines(currentUser.getPhoneLines() + 1);
		
		// save the customer with the new balance
		phoneService.addCustomer(currentUser);
		
		redirectAttributes.addFlashAttribute("message", phone.getName() + " line added.");
		return "redirect:/account/myAccount";
	}
	
	@RequestMapping("/selectPhonePlan")
	public String selectPhonePlan(Model theModel)
	{
		Customer currentUser = currentCustomer();
		if(currentUser == null)
			return "redirect:/account/login";
		
		// pre-select the customer's current plan
		SelectPhonePlanRequest request = new SelectPhonePlanRequest();
		request.setPlanId(currentUser.getPlanId());
		theModel.addAttribute("plan", request);
		theModel.addAttribute("currentPlanId", currentUser.getPlanId());
		theModel.addAttribute("phonePlans", phonePlanMapper.toResponses(phoneService.getPhonePlans()));
		return "phone_plans";
	}
	
	@RequestMapping("/savePhonePlan")
	public String savePhonePlan(@ModelAttribute("plan") SelectPhonePlanRequest request, RedirectAttributes redirectAttributes)
	{
		Customer currentUser = currentCustomer();
		if(currentUser == null)
			return "redirect:/account/login";
		currentUser.setPlanId(request.getPlanId());
		
		float newBalance = phoneService.getRate(request.getPlanId()) + currentUser.getBalance();
		
		currentUser.setBalance(newBalance);
		phoneService.addCustomer(currentUser);
		redirectAttributes.addFlashAttribute("message", "Your plan was updated.");
		return "redirect:/account/myAccount";
	}
	@RequestMapping("/pay")
	public String pay(Model model)
	{
		Customer currentCustomer = currentCustomer();
		if(currentCustomer == null)
			return "redirect:/account/login";
		
		// bind the payment form
		model.addAttribute("creditCard", new PaymentRequest());
		addPaymentPageData(model, currentCustomer);
		
		return "payment";
	}
	@RequestMapping("/savePayment")
	public String savePayment(@Valid @ModelAttribute("creditCard") PaymentRequest request, BindingResult theBindingResult, Model model, RedirectAttributes redirectAttributes)
	{ 
		Customer currentCustomer = currentCustomer();
		if(currentCustomer == null)
			return "redirect:/account/login";
		if(theBindingResult.hasErrors())
		{
			addPaymentPageData(model, currentCustomer);
			return "payment";
		}
		
		CreditCard card = creditCardMapper.toEntity(request);
		card.setCid(currentCustomer.getCid());
		
		// save the transaction and creditcard (the crid is set once the new card is saved)
		Transaction transaction = new Transaction(currentCustomer.getBalance(), now(), card.getCrid(), currentCustomer.getCid());
		currentCustomer.setBalance(0.0f);
		phoneService.savePayment(transaction, card, currentCustomer);
		
		redirectAttributes.addFlashAttribute("message", "Payment received. Thank you!");
		return "redirect:/account/myAccount";
	}
	@RequestMapping("/savePaymentS")
	public String savePaymentS(@RequestParam("crid") int crid, RedirectAttributes redirectAttributes)
	{
		Customer currentCustomer = currentCustomer();
		if(currentCustomer == null)
			return "redirect:/account/login";
		
		// only allow paying with one of the customer's own saved cards
		CreditCard creditCard = phoneService.getCreditCard(crid);
		if(creditCard == null || creditCard.getCid() != currentCustomer.getCid())
			return "redirect:/account/pay";
		
		Transaction transaction = new Transaction(currentCustomer.getBalance(), now(), creditCard.getCrid(), currentCustomer.getCid());
		currentCustomer.setBalance(0.0f);
		phoneService.savePayment(transaction, creditCard, currentCustomer);
		
		redirectAttributes.addFlashAttribute("message", "Payment received. Thank you!");
		return "redirect:/account/myAccount";
	}
	@PostMapping("/deletePhoneLine")
	public String deletePhoneLine(@RequestParam("phoneLineId") int plid)
	{
		Customer currentCustomer = currentCustomer();
		if(currentCustomer == null)
			return "redirect:/account/login";
		// delete the phone line if it belongs to the current customer
		phoneService.deletePhoneLine(plid, currentCustomer);
		
		return "redirect:/account/myAccount";
	}
	@RequestMapping("/forgotPW")
	public String forgotPW()
	{
		return "verifyReset";
	}
	@RequestMapping("/viewPhones")
	public String viewPhones()
	{
		return "redirect:/main/list";
	}
	@RequestMapping("/reset")
	public String reset(@RequestParam("email") String email, Model model, HttpSession session)
	{
		Customer customer = phoneService.getCustomerByEmail(email);
		
		if(customer == null)
		{
			model.addAttribute("email", email);
			model.addAttribute("error", "We couldn't find an account with that email.");
			return "verifyReset";
		}
		
		// remember which customer is being reset, so the form can't be used to change another account
		session.setAttribute(RESET_USER_ID, customer.getCid());
		
		// pre-fill the reset form with the customer's details
		model.addAttribute("customer", customerMapper.toRegisterRequest(customer));
		
		return "reset";
	}
	@RequestMapping("/updateCredentials")
	public String updateCredentials(@Valid @ModelAttribute("customer") RegisterCustomerRequest request, BindingResult theBindingResult, HttpSession session, RedirectAttributes redirectAttributes)
	{
		Integer cid = (Integer) session.getAttribute(RESET_USER_ID);
		Customer customer = cid == null ? null : phoneService.getCustomer(cid);
		if(customer == null)
			return "redirect:/account/forgotPW";
		
		if(!theBindingResult.hasErrors() && phoneService.isEmailTaken(request.getEmail(), customer.getCid()))
			rejectDuplicateEmail(theBindingResult);
		if(theBindingResult.hasErrors())
			return "reset";
		
		// update the existing customer instead of creating a new one
		customerMapper.updateEntity(request, customer);
		customer.setPassword(passwordEncoder.encode(request.getPassword()));
		try
		{
			phoneService.addCustomer(customer);
		}
		catch(DataIntegrityViolationException e)
		{
			rejectDuplicateEmail(theBindingResult);
			return "reset";
		}
		session.removeAttribute(RESET_USER_ID);
		
		redirectAttributes.addFlashAttribute("message", "Your details were updated. Sign in with your new password.");
		return "redirect:/account/login";
	}
	
	// loads the logged in customer, or returns null if nobody is logged in
	private Customer currentCustomer()
	{
		Integer cid = CurrentUser.cid();
		return cid == null ? null : phoneService.getCustomer(cid);
	}
	
	private static void rejectDuplicateEmail(BindingResult theBindingResult)
	{
		theBindingResult.rejectValue("email", "duplicate", "is already registered");
	}
	
	private void addPaymentPageData(Model model, Customer currentCustomer)
	{
		model.addAttribute("currentCustomer", customerMapper.toResponse(currentCustomer));
		model.addAttribute("savedCards", creditCardMapper.toResponses(phoneService.getCreditCards(currentCustomer)));
	}
	
	private static String now()
	{
		return LocalDateTime.now().format(TRANSACTION_DATE_FORMAT);
	}
}
