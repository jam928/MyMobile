package com.mymobile.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import com.mymobile.entity.CreditCard;
import com.mymobile.entity.Customer;
import com.mymobile.entity.Phone;
import com.mymobile.entity.PhoneLine;
import com.mymobile.entity.PhonePlan;
import com.mymobile.entity.Transaction;

public interface PhoneService {
	public List<Phone> getPhones();

	public void addCustomer(Customer theCustomer);

	public Customer login(String email, String password, HttpServletRequest request);

	public void saveLine(PhoneLine phoneLine);

	public List<PhoneLine> getPhoneLines(Customer currentCustomer);

	public float getPhonePrice(int pid);

	public List<PhonePlan> getPhonePlans();

	public PhonePlan getPhonePlan(Customer currentCustomer);
	
	public float getRate(int planId);

	public void deletePhoneLine(int plid);

	public void savePayment(Transaction transaction, CreditCard card, Customer currentCustomer);

	public Customer reset(String email);

	public List<CreditCard> getCreditCards(Customer currentCustomer);

	public CreditCard getCreditCard(int crid);

	
}
