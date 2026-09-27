package com.mymobile.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mymobile.dto.request.PhoneSort;
import com.mymobile.dto.response.BrandCount;
import com.mymobile.entity.CreditCard;
import com.mymobile.entity.Customer;
import com.mymobile.entity.Phone;
import com.mymobile.entity.PhoneLine;
import com.mymobile.entity.PhonePlan;
import com.mymobile.entity.Transaction;

public interface PhoneService {
	public List<Phone> getPhones();

	// search and brand may be null (no filter)
	public Page<Phone> searchPhones(String search, String brand, PhoneSort sort, Pageable pageable);

	public List<BrandCount> countByBrand(String search);

	public void addCustomer(Customer theCustomer);

	public Customer getCustomer(int cid);

	public Page<Customer> getCustomers(Pageable pageable);

	public void saveLine(PhoneLine phoneLine);

	public List<PhoneLine> getPhoneLines(Customer currentCustomer);

	public Phone getPhone(int pid);

	public void addPhone(Phone phone);

	public List<PhonePlan> getPhonePlans();

	public PhonePlan getPhonePlan(Customer currentCustomer);
	
	public float getRate(int planId);

	public void deletePhoneLine(int plid, Customer currentCustomer);

	public void savePayment(Transaction transaction, CreditCard card, Customer currentCustomer);

	public Customer getCustomerByEmail(String email);

	// true if a customer other than excludedCid already uses the email (pass 0 for a new customer)
	public boolean isEmailTaken(String email, int excludedCid);

	public List<CreditCard> getCreditCards(Customer currentCustomer);

	public CreditCard getCreditCard(int crid);

	
}
