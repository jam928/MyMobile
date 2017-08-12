package com.mymobile.dao;

import java.util.List;

import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.mymobile.entity.Phone;
import com.mymobile.entity.PhoneLine;
import com.mymobile.entity.PhonePlan;

@Repository
public class PhoneDAOImpl implements PhoneDAO {

	// inject session factory from the xml file
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public List<Phone> getPhones() {
		
		// get the current hibernate session
		Session session = sessionFactory.getCurrentSession();
		
		// create query, then execute the query to get the result list
		List<Phone> phones = session.createQuery("from Phone", Phone.class).getResultList();
		
		// return the list of phones
		return phones;
		
	}

	@Override
	public float getPhonePrice(int pid) {

		// get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// get the phone price based on the pid(model id)
		Phone phone = currentSession.get(Phone.class, pid);
		
		// return the phone price based on the pid(model id)
		return phone.getPrice();
		
	}

	@Override
	public List<PhonePlan> getPhonePlans() {

		// get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// get the list of phone plans of the database
		List<PhonePlan> phonePlans = currentSession.createQuery("FROM PhonePlan", PhonePlan.class).getResultList();
		
		return phonePlans;
	}

	@Override
	public float getRate(int planId) {
		
		// get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		
		// get the monthlyRate based on the plan id
		PhonePlan plan = currentSession.get(PhonePlan.class, planId);
		
		return plan.getMonthlyRate();
	}

	@Override
	public void deletePhoneLine(int plid) {
		
		// get the current hibernate session
		Session currentSession = sessionFactory.getCurrentSession();
		
		// delete phoneline object 
		Query theQuery = 
				currentSession.createQuery("delete from PhoneLine where plid =:theId");
		theQuery.setParameter("theId", plid);
		
		theQuery.executeUpdate();
		
	}

}
