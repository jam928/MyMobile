package com.mymobile.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.mymobile.entity.CreditCard;
import com.mymobile.entity.Customer;
import com.mymobile.entity.PhoneLine;
import com.mymobile.entity.PhonePlan;
import com.mymobile.entity.Transaction;

public interface CustomerDAO {
	public void addCustomer(Customer theCustomer);

	public Customer getCustomer(int cid);

	public Page<Customer> getCustomers(Pageable pageable);

	public void saveLine(PhoneLine phoneLine);

	public List<PhoneLine> getPhoneLines(Customer currentCustomer);

	public PhonePlan getPhonePlan(Customer currentCustomer);

	public void savePayment(Transaction transaction, CreditCard card, Customer currentCustomer);

	public Customer getCustomerByEmail(String email);

	public boolean isEmailTaken(String email, int excludedCid);

	public List<CreditCard> getCreditCards(Customer currentCustomer);

	public CreditCard getCreditCard(int crid);

}
