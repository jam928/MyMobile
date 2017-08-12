package com.mymobile.dao;

import java.util.List;

import com.mymobile.entity.Phone;
import com.mymobile.entity.PhonePlan;

public interface PhoneDAO {
	public List<Phone> getPhones();

	public float getPhonePrice(int pid);
	
	public List<PhonePlan> getPhonePlans();

	public float getRate(int planId);

	public void deletePhoneLine(int plid);
}
