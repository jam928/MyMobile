package com.mymobile.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.web.server.ResponseStatusException;

import com.mymobile.dto.request.PhoneSort;
import com.mymobile.dto.response.BrandCount;
import com.mymobile.dto.response.PhoneResponse;
import com.mymobile.entity.Phone;
import com.mymobile.mapper.PhoneMapper;
import com.mymobile.service.PhoneService;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/main")
@RequiredArgsConstructor
public class PhoneController {
	
	private static final int PHONES_PER_PAGE = 8;
	
	private final PhoneService phoneService;
	private final PhoneMapper phoneMapper;
	
	@RequestMapping("/list")
	public String listPhones(@RequestParam(name = "page", defaultValue = "1") int page,
			@RequestParam(name = "q", required = false) String q,
			@RequestParam(name = "brand", required = false) String brand,
			@RequestParam(name = "sort", required = false) String sort,
			Model theModel)
	{
		String search = StringUtils.hasText(q) ? q.trim() : null;
		String brandFilter = StringUtils.hasText(brand) ? brand : null;
		PhoneSort phoneSort = PhoneSort.fromParam(sort);
		
		// the list URL with the current filters, used for the page links
		String listUrl = UriComponentsBuilder.fromPath("/main/list")
				.queryParamIfPresent("q", Optional.ofNullable(search))
				.queryParamIfPresent("brand", Optional.ofNullable(brandFilter))
				.queryParamIfPresent("sort", Optional.ofNullable(phoneSort == PhoneSort.FEATURED ? null : phoneSort.getParam()))
				.encode().build().toUriString();
		
		// get one page of matching phones from the dao(db); pages are 1-based in the URL
		Page<PhoneResponse> phones = phoneService.searchPhones(search, brandFilter, phoneSort, PageRequest.of(Math.max(page, 1) - 1, PHONES_PER_PAGE))
				.map(phoneMapper::toResponse);
		
		// past the last page (e.g. an old link): go to the last page instead
		if(page > 1 && page > phones.getTotalPages())
			return "redirect:" + listUrl + (listUrl.contains("?") ? "&" : "?") + "page=" + Math.max(phones.getTotalPages(), 1);
		
		theModel.addAttribute("phones", phones);
		theModel.addAttribute("q", search);
		theModel.addAttribute("brand", brandFilter);
		theModel.addAttribute("sort", phoneSort);
		theModel.addAttribute("sorts", PhoneSort.values());
		// keep the selected brand's chip visible even when the search has no phones from it
		List<BrandCount> brands = new ArrayList<>(phoneService.countByBrand(search));
		if(brandFilter != null && brands.stream().noneMatch(b -> b.brand().equals(brandFilter)))
		{
			brands.add(new BrandCount(brandFilter, 0));
			brands.sort(Comparator.comparing(BrandCount::brand));
		}
		theModel.addAttribute("brands", brands);
		theModel.addAttribute("listUrl", listUrl);
		
		return "list-phones";
	}
	
	@GetMapping("/phones/{pid}")
	public String phoneDetails(@PathVariable int pid, Model theModel)
	{
		Phone phone = phoneService.getPhone(pid);
		if(phone == null)
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		
		theModel.addAttribute("phone", phoneMapper.toResponse(phone));
		
		return "phone-details";
	}
	
	@RequestMapping("/sell")
	public String sellPhones(Model theModel)
	{
		return "sell-phones";
	}
	
	@RequestMapping("/login")
	public String login()
	{
		return "redirect:/account/login";
	}
	
	@RequestMapping("/admin")
	public String admin()
	{
		return "redirect:/admin";
	}
}
