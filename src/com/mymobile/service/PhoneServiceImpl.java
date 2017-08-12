package com.mymobile.service;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mymobile.dao.CustomerDAO;
import com.mymobile.dao.PhoneDAO;
import com.mymobile.entity.CreditCard;
import com.mymobile.entity.Customer;
import com.mymobile.entity.Phone;
import com.mymobile.entity.PhoneLine;
import com.mymobile.entity.PhonePlan;
import com.mymobile.entity.Transaction;

@Service
public class PhoneServiceImpl implements PhoneService {
	
	// inject the phonesDAO 
	@Autowired
	private PhoneDAO phoneDAO;
	
	// inject the customerDAO
	@Autowired
	private CustomerDAO customerDAO;
	
	@Override
	@Transactional
	public List<Phone> getPhones() {
		// TODO Auto-generated method stub
		return phoneDAO.getPhones();
	}

	@Override
	@Transactional
	public void addCustomer(Customer theCustomer) {
		// TODO Auto-generated method stub
		
		customerDAO.addCustomer(theCustomer);
	}

	@Override
	@Transactional
	public Customer login(String email, String password, HttpServletRequest request) {
		// TODO Auto-generated method stub
		return customerDAO.login(email, password, request);
	}

	@Override
	@Transactional
	public void saveLine(PhoneLine phoneLine) {
		customerDAO.saveLine(phoneLine);
	}

	@Override
	@Transactional
	public List<PhoneLine> getPhoneLines(Customer currentCustomer) {
		// TODO Auto-generated method stub
		return customerDAO.getPhoneLines(currentCustomer);
	}

	@Override
	@Transactional
	public float getPhonePrice(int pid) {
		// TODO Auto-generated method stub
		return phoneDAO.getPhonePrice(pid);
	}

	@Override
	@Transactional
	public List<PhonePlan> getPhonePlans() {
		// TODO Auto-generated method stub
		return phoneDAO.getPhonePlans();
	}

	@Override
	@Transactional
	public PhonePlan getPhonePlan(Customer currentCustomer) {
		// TODO Auto-generated method stub
		return customerDAO.getPhonePlan(currentCustomer);
	}

	@Override
	@Transactional
	public float getRate(int planId) {
		// TODO Auto-generated method stub
		return phoneDAO.getRate(planId);
	}

	@Override
	@Transactional
	public void deletePhoneLine(int plid) {

	 phoneDAO.deletePhoneLine(plid);
		
	}

	@Override
	@Transactional
	public void savePayment(Transaction transaction, CreditCard card, Customer currentCustomer) {
		customerDAO.savePayment(transaction, card, currentCustomer);
		
	}

	@Override
	@Transactional
	public Customer reset(String email) {
		// TODO Auto-generated method stub
		return customerDAO.reset(email);
	}

	@Override
	@Transactional
	public List<CreditCard> getCreditCards(Customer currentCustomer) {
		
		return customerDAO.getCreditCards(currentCustomer);
	}

	@Override
	@Transactional
	public CreditCard getCreditCard(int crid) {
		// TODO Auto-generated method stub
		return customerDAO.getCreditCard(crid);
	}


}
