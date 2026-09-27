package com.mymobile.controller;

import java.io.IOException;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mymobile.dto.request.CreatePhoneRequest;
import com.mymobile.dto.request.PhoneSort;
import com.mymobile.dto.response.BrandCount;
import com.mymobile.dto.response.PhonePlanResponse;
import com.mymobile.entity.Customer;
import com.mymobile.entity.Phone;
import com.mymobile.entity.Role;
import com.mymobile.mapper.CustomerMapper;
import com.mymobile.mapper.PhoneMapper;
import com.mymobile.mapper.PhonePlanMapper;
import com.mymobile.service.PhoneService;
import com.mymobile.security.CurrentUser;
import com.mymobile.service.PhotoService;
import com.mymobile.service.UserAdminService;
import com.mymobile.service.PhotoService.ImageType;

import lombok.RequiredArgsConstructor;

/**
 * Admin pages. Access is checked by AdminInterceptor for everything under /admin.
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {
	
	private static final int ROWS_PER_PAGE = 20;
	
	private final PhoneService phoneService;
	private final PhotoService photoService;
	private final PhoneMapper phoneMapper;
	private final CustomerMapper customerMapper;
	private final PhonePlanMapper phonePlanMapper;
	private final UserAdminService userAdminService;
	
	@GetMapping("/phones")
	public String phones(@RequestParam(name = "page", defaultValue = "1") int page, Model model)
	{
		// newest first, so a phone that was just added is at the top
		model.addAttribute("phones", phoneService
				.searchPhones(null, null, PhoneSort.NEWEST, PageRequest.of(Math.max(page, 1) - 1, ROWS_PER_PAGE))
				.map(phoneMapper::toResponse));
		return "admin-phones";
	}
	
	@GetMapping("/phones/new")
	public String newPhone(Model model)
	{
		model.addAttribute("phone", new CreatePhoneRequest());
		addFormData(model);
		return "admin-phone-form";
	}
	
	@PostMapping("/phones")
	public String createPhone(@Valid @ModelAttribute("phone") CreatePhoneRequest request, BindingResult theBindingResult,
			Model model, RedirectAttributes redirectAttributes) throws IOException
	{
		// check the photo: it must be a real JPEG, PNG or WebP image
		MultipartFile photo = request.getPhoto();
		byte[] photoData = null;
		ImageType imageType = null;
		if(photo == null || photo.isEmpty())
			theBindingResult.rejectValue("photo", "required", "Please choose a photo.");
		else
		{
			photoData = photo.getBytes();
			imageType = ImageType.detect(photoData);
			if(imageType == null)
				theBindingResult.rejectValue("photo", "type", "must be a JPEG, PNG or WebP image");
		}
		
		if(theBindingResult.hasErrors())
		{
			addFormData(model);
			return "admin-phone-form";
		}
		
		// store the photo in MinIO, then the phone in the database
		String key = photoService.uploadPhoto(photoData, imageType);
		Phone phone = phoneMapper.toEntity(request);
		phone.setImgSrc(key);
		phone.setAlt(phone.getColor() + " " + phone.getName());
		try
		{
			phoneService.addPhone(phone);
		}
		catch(RuntimeException e)
		{
			// don't leave an unused photo behind
			photoService.deletePhoto(key);
			throw e;
		}
		
		redirectAttributes.addFlashAttribute("message", phone.getName() + " was added.");
		return "redirect:/admin/phones";
	}
	
	@GetMapping("/users")
	public String users(@RequestParam(name = "page", defaultValue = "1") int page, Model model)
	{
		model.addAttribute("users", phoneService
				.getCustomers(PageRequest.of(Math.max(page, 1) - 1, ROWS_PER_PAGE))
				.map(customerMapper::toResponse));
		
		// plans by id, to show each customer's plan
		Map<Integer, PhonePlanResponse> plans = phonePlanMapper.toResponses(phoneService.getPhonePlans()).stream()
				.collect(Collectors.toMap(PhonePlanResponse::planId, Function.identity()));
		model.addAttribute("plans", plans);
		model.addAttribute("currentUserId", CurrentUser.cid());
		
		return "admin-users";
	}
	
	@PostMapping("/users/{cid}/role")
	public String changeRole(@PathVariable int cid, @RequestParam("role") Role role,
			@RequestParam(name = "page", defaultValue = "1") int page, RedirectAttributes redirectAttributes)
	{
		try
		{
			Customer customer = userAdminService.changeRole(CurrentUser.cid(), cid, role);
			redirectAttributes.addFlashAttribute("message", role == Role.ADMIN
					? customer.getName() + " is now an admin. They get admin access the next time they sign in."
					: customer.getName() + " is no longer an admin.");
		}
		catch(IllegalArgumentException | NoSuchElementException e)
		{
			redirectAttributes.addFlashAttribute("error", e.getMessage());
		}
		return "redirect:/admin/users?page=" + Math.max(page, 1);
	}
	
	// existing brands, suggested in the brand field
	private void addFormData(Model model)
	{
		model.addAttribute("brands", phoneService.countByBrand(null).stream().map(BrandCount::brand).toList());
	}
}
