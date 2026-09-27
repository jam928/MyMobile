package com.mymobile.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mymobile.dto.request.PhoneSort;
import com.mymobile.dto.response.BrandCount;
import com.mymobile.entity.Phone;
import com.mymobile.entity.PhonePlan;

public interface PhoneDAO {
	public List<Phone> getPhones();

	// search and brand may be null (no filter)
	public Page<Phone> searchPhones(String search, String brand, PhoneSort sort, Pageable pageable);

	// number of phones per brand that match the search (null = all phones)
	public List<BrandCount> countByBrand(String search);

	public Phone getPhone(int pid);

	public void addPhone(Phone phone);
	
	public List<PhonePlan> getPhonePlans();

	public float getRate(int planId);

	public boolean deletePhoneLine(int plid, int cid);
}
