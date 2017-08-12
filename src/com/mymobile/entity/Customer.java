package com.mymobile.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "customer")
public class Customer {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cid")
	private int cid;
	
	@Column
	@NotNull(message = "is required")
	private String name;
	
	@Column
	@NotNull(message = "is required")
	private String email;
	
	@Column
	@NotNull(message = "is required")
	@Size(min = 8, message ="at least 8 characters at the minimum")
	private String password;
	
	@Column
	@NotNull
	private String birthday;
	
	@Column(name = "phone_lines")
	private int phoneLines;
	
	@Column
	private float balance;
	
	@Column
	private int planId;
	
	// default constructor
	public Customer()
	{
		
	}
	
	public Customer(String name, String email, String password, String birthday) {
		this.name = name;
		this.email = email;
		this.password = password;
		this.birthday = birthday;
	}



	public int getCid() {
		return cid;
	}



	public void setCid(int cid) {
		this.cid = cid;
	}



	public String getName() {
		return name;
	}



	public void setName(String name) {
		this.name = name;
	}



	public String getEmail() {
		return email;
	}



	public void setEmail(String email) {
		this.email = email;
	}



	public String getPassword() {
		return password;
	}



	public void setPassword(String password) {
		this.password = password;
	}



	public String getBirthday() {
		return birthday;
	}



	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}



	public int getPhoneLines() {
		return phoneLines;
	}



	public void setPhoneLines(int phoneLines) {
		this.phoneLines = phoneLines;
	}



	public float getBalance() {
		return balance;
	}



	public void setBalance(float balance) {
		this.balance = balance;
	}

	public int getPlanId() {
		return planId;
	}

	public void setPlanId(int planId) {
		this.planId = planId;
	}

	@Override
	public String toString() {
		return "Customer [cid=" + cid + ", name=" + name + ", email=" + email + ", password=" + password + ", birthday="
				+ birthday + ", phoneLines=" + phoneLines + ", balance=" + balance + ", planId=" + planId + "]";
	}

}
