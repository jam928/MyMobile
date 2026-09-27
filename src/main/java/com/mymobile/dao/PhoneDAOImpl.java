package com.mymobile.dao;

import java.util.List;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.mymobile.dto.request.PhoneSort;
import com.mymobile.dto.response.BrandCount;
import com.mymobile.entity.Phone;
import com.mymobile.entity.PhonePlan;

@Repository
public class PhoneDAOImpl implements PhoneDAO {

	// inject the JPA entity manager (replaces the Hibernate SessionFactory from the xml file)
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public List<Phone> getPhones() {
		
		// create query, then execute the query to get the list of phones
		return entityManager.createQuery("FROM Phone", Phone.class).getResultList();
		
	}

	@Override
	public Page<Phone> searchPhones(String search, String brand, PhoneSort sort, Pageable pageable) {
		
		String where = where(search, brand);
		
		// count the matching phones, then load just the requested page
		TypedQuery<Long> countQuery = entityManager.createQuery("SELECT COUNT(p) FROM Phone p" + where, Long.class);
		TypedQuery<Phone> query = entityManager.createQuery("FROM Phone p" + where + " ORDER BY " + sort.getOrderBy(), Phone.class);
		setFilterParameters(countQuery, search, brand);
		setFilterParameters(query, search, brand);
		
		List<Phone> phones = query
				.setFirstResult((int) pageable.getOffset())
				.setMaxResults(pageable.getPageSize())
				.getResultList();
		
		return new PageImpl<>(phones, pageable, countQuery.getSingleResult());
	}

	@Override
	public List<BrandCount> countByBrand(String search) {
		
		TypedQuery<BrandCount> query = entityManager.createQuery(
				"SELECT new com.mymobile.dto.response.BrandCount(p.brand, COUNT(p)) FROM Phone p"
						+ where(search, null) + " GROUP BY p.brand ORDER BY p.brand", BrandCount.class);
		setFilterParameters(query, search, null);
		
		return query.getResultList();
	}
	
	// search matches the name, brand or color (case-insensitive); brand must match exactly
	private static String where(String search, String brand) {
		StringBuilder where = new StringBuilder();
		if(search != null)
			where.append(" WHERE (LOWER(p.name) LIKE :pattern ESCAPE '!' OR LOWER(p.brand) LIKE :pattern ESCAPE '!' OR LOWER(p.color) LIKE :pattern ESCAPE '!')");
		if(brand != null)
			where.append(search != null ? " AND" : " WHERE").append(" p.brand = :brand");
		return where.toString();
	}
	
	private static void setFilterParameters(TypedQuery<?> query, String search, String brand) {
		if(search != null)
			query.setParameter("pattern", "%" + escapeLike(search.toLowerCase()) + "%");
		if(brand != null)
			query.setParameter("brand", brand);
	}
	
	// treat % and _ typed by the user as plain characters
	private static String escapeLike(String value) {
		return value.replace("!", "!!").replace("%", "!%").replace("_", "!_");
	}

	@Override
	public Phone getPhone(int pid) {

		// get the phone based on the pid(model id)
		return entityManager.find(Phone.class, pid);
		
	}

	@Override
	public void addPhone(Phone phone) {
		
		// save the new phone to the database
		entityManager.persist(phone);
	}

	@Override
	public List<PhonePlan> getPhonePlans() {

		// get the list of phone plans of the database
		return entityManager.createQuery("FROM PhonePlan", PhonePlan.class).getResultList();
	}

	@Override
	public float getRate(int planId) {
		
		// get the monthlyRate based on the plan id
		return entityManager.find(PhonePlan.class, planId).getMonthlyRate();
	}

	@Override
	public boolean deletePhoneLine(int plid, int cid) {
		
		// delete the phoneline only if it belongs to the customer; returns whether a line was deleted
		int deleted = entityManager.createQuery("DELETE FROM PhoneLine WHERE plid = :theId AND cid = :customerId")
				.setParameter("theId", plid)
				.setParameter("customerId", cid)
				.executeUpdate();
		
		return deleted > 0;
	}

}
