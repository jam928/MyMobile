package com.mymobile;

import com.mymobile.entity.Customer;
import com.mymobile.entity.Phone;
import com.mymobile.entity.PhonePlan;
import com.mymobile.entity.Role;

/**
 * Builders for entities used by the unit tests.
 */
public final class TestData {

	private TestData() {
	}

	public static Customer customer(int cid, Role role) {
		Customer customer = new Customer();
		customer.setCid(cid);
		customer.setName("Customer " + cid);
		customer.setEmail("customer" + cid + "@example.com");
		customer.setPassword("{bcrypt}hash");
		customer.setBirthday("1990-01-01");
		customer.setRole(role);
		return customer;
	}

	public static Phone phone(int pid, String name, float price) {
		Phone phone = new Phone();
		phone.setPid(pid);
		phone.setName(name);
		phone.setBrand("Google");
		phone.setCondition("New");
		phone.setColor("Black");
		phone.setPrice(price);
		phone.setRating(5);
		phone.setQuantity(3);
		phone.setImgSrc(pid + ".jpg");
		phone.setAlt("Black " + name);
		return phone;
	}

	public static PhonePlan plan(int planId, int lines, float rate) {
		PhonePlan plan = new PhonePlan();
		plan.setPlanId(planId);
		plan.setNumberOfLines(lines);
		plan.setMonthlyRate(rate);
		plan.setDescription(lines + " lines");
		return plan;
	}
}
