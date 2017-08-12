package com.mymobile.dao;

import java.util.List;

import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mymobile.entity.CreditCard;
import com.mymobile.entity.Customer;
import com.mymobile.entity.PhoneLine;
import com.mymobile.entity.PhonePlan;
import com.mymobile.entity.Transaction;

@Repository
public class CustomerDAOImpl implements CustomerDAO {
	
	// inject the session factory(db) from the xml file
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public void addCustomer(Customer theCustomer) {
		
		// get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// add the customer to the database
		currentSession.saveOrUpdate(theCustomer);
		
	}

	@Override
	public Customer login(String email, String password, HttpServletRequest request) {
		
		// get the current hibenate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// get the list of customers from the database
		List<Customer> listOfCustomers = currentSession.createQuery("from Customer", Customer.class).getResultList();
		
		// Loop over the customers from the list and check if the customer email's && password from the method matches one from the list
		for(Customer tempCustomer: listOfCustomers)
		{
			if(tempCustomer.getEmail().equals(email) && tempCustomer.getPassword().equals(password))
			{
				return tempCustomer;
			}
		}
		// return null if the no match is found
		return null;
	}

	@Override
	public void saveLine(PhoneLine phoneLine) {
		
		// get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// save the customer to the database
		currentSession.save(phoneLine);
		
	}

	@Override
	public List<PhoneLine> getPhoneLines(Customer currentCustomer) {
		
		// get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// get the list of phone lines from the database based on currentCustomer cid
		int cid = currentCustomer.getCid();
		List<PhoneLine> phoneLines = currentSession.createQuery("FROM PhoneLine pl WHERE pl.cid = :customerId").setParameter("customerId", cid).getResultList();
		
		return phoneLines;
	}

	@Override
	public PhonePlan getPhonePlan(Customer currentCustomer) {

		// get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// get the phoneplan based on the customer cid
		int cid = currentCustomer.getPlanId();
		PhonePlan plan = currentSession.get(PhonePlan.class, cid);
		
		// return the phone plan
		return plan;
	}

	@Override
	public void savePayment(Transaction transaction, CreditCard card, Customer currentCustomer) {

		// get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// save the transaction and credit card and set the customers balance to 0
		currentSession.saveOrUpdate(card);
		currentSession.saveOrUpdate(transaction);
		currentSession.saveOrUpdate(currentCustomer);
		
		
	}

	@Override
	public Customer reset(String email) {
		// get the current hibenate session
		Session currentSession = sessionFactory.getCurrentSession();
				
		// get the list of customers from the database
		List<Customer> listOfCustomers = currentSession.createQuery("from Customer", Customer.class).getResultList();
				
		// Loop over the customers from the list and check if the customer email's && password from the method matches one from the list
		for(Customer tempCustomer: listOfCustomers)
		{
			if(tempCustomer.getEmail().equals(email))
			{
				return tempCustomer;
			}
		}
		// return null if the no match is found
		return null;
	}

	@Override
	public List<CreditCard> getCreditCards(Customer currentCustomer) {
		// get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
				
		// get the list of credit cards from the database based on currentCustomer cid
		int cid = currentCustomer.getCid();
		List<CreditCard> creditCards = currentSession.createQuery("FROM CreditCard cc WHERE cc.cid = :customerId").setParameter("customerId", cid).getResultList();
				
		return creditCards;
	}

	@Override
	public CreditCard getCreditCard(int crid) {
		// get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
				
		// get the creditcard based on the crid
		CreditCard card = currentSession.get(CreditCard.class, crid);
				
		// return the card
		return card;
	}


}
