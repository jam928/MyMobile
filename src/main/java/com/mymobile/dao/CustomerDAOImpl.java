package com.mymobile.dao;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.mymobile.entity.CreditCard;
import com.mymobile.entity.Customer;
import com.mymobile.entity.PhoneLine;
import com.mymobile.entity.PhonePlan;
import com.mymobile.entity.Transaction;

@Repository
public class CustomerDAOImpl implements CustomerDAO {
	
	// inject the JPA entity manager (replaces the Hibernate SessionFactory from the xml file)
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public void addCustomer(Customer theCustomer) {
		
		// insert a new customer or update an existing one
		if(theCustomer.getCid() == 0)
			entityManager.persist(theCustomer);
		else
			entityManager.merge(theCustomer);
		
	}

	@Override
	public Customer getCustomer(int cid) {
		
		// get the customer based on the cid
		return entityManager.find(Customer.class, cid);
	}

	@Override
	public Page<Customer> getCustomers(Pageable pageable) {
		
		// count all customers, then load just the requested page (oldest first)
		long total = entityManager.createQuery("SELECT COUNT(c) FROM Customer c", Long.class).getSingleResult();
		List<Customer> customers = entityManager.createQuery("FROM Customer c ORDER BY c.cid", Customer.class)
				.setFirstResult((int) pageable.getOffset())
				.setMaxResults(pageable.getPageSize())
				.getResultList();
		
		return new PageImpl<>(customers, pageable, total);
	}

	@Override
	public void saveLine(PhoneLine phoneLine) {
		
		// save the phone line to the database
		entityManager.persist(phoneLine);
		
	}

	@Override
	public List<PhoneLine> getPhoneLines(Customer currentCustomer) {
		
		// get the list of phone lines from the database based on currentCustomer cid
		return entityManager
				.createQuery("FROM PhoneLine pl WHERE pl.cid = :customerId", PhoneLine.class)
				.setParameter("customerId", currentCustomer.getCid())
				.getResultList();
	}

	@Override
	public PhonePlan getPhonePlan(Customer currentCustomer) {

		// get the phoneplan based on the customer planId
		return entityManager.find(PhonePlan.class, currentCustomer.getPlanId());
	}

	@Override
	public void savePayment(Transaction transaction, CreditCard card, Customer currentCustomer) {

		// save the credit card first so a new card has its generated crid
		if(card.getCrid() == 0)
			entityManager.persist(card);
		else
			card = entityManager.merge(card);
		transaction.setCrid(card.getCrid());
		
		// save the transaction and the customer (whose balance was set to 0)
		entityManager.persist(transaction);
		entityManager.merge(currentCustomer);
		
	}

	@Override
	public Customer getCustomerByEmail(String email) {
		
		// find the customer with the matching email
		List<Customer> customers = entityManager
				.createQuery("FROM Customer c WHERE c.email = :email", Customer.class)
				.setParameter("email", email)
				.setMaxResults(1)
				.getResultList();
		
		// return null if the no match is found
		return customers.isEmpty() ? null : customers.get(0);
	}

	@Override
	public boolean isEmailTaken(String email, int excludedCid) {
		
		// check if another customer already uses this email
		return entityManager
				.createQuery("SELECT COUNT(c) FROM Customer c WHERE c.email = :email AND c.cid <> :cid", Long.class)
				.setParameter("email", email)
				.setParameter("cid", excludedCid)
				.getSingleResult() > 0;
	}

	@Override
	public List<CreditCard> getCreditCards(Customer currentCustomer) {
				
		// get the list of credit cards from the database based on currentCustomer cid
		return entityManager
				.createQuery("FROM CreditCard cc WHERE cc.cid = :customerId", CreditCard.class)
				.setParameter("customerId", currentCustomer.getCid())
				.getResultList();
	}

	@Override
	public CreditCard getCreditCard(int crid) {
				
		// get the creditcard based on the crid
		return entityManager.find(CreditCard.class, crid);
	}

}
