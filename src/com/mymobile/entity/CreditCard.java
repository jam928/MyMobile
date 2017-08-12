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
@Table(name = "creditcard")
public class CreditCard {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "crid")
	private int crid;
	
	@NotNull
	@Size(min=8,max = 16)
	@Column(name = "credit_card_number")
	private String creditCardNumber;
	
	@Column(name = "expiration_date")
	private String expirationDate;
	
	@Column
	private int csc;
	
	@Column
	private int cid;
	
	@Column
	private String vendor;

	public int getCrid() {
		return crid;
	}

	public void setCrid(int crid) {
		this.crid = crid;
	}

	public String getExpirationDate() {
		return expirationDate;
	}

	public void setExpirationDate(String expirationDate) {
		this.expirationDate = expirationDate;
	}

	public int getCsc() {
		return csc;
	}

	public void setCsc(int csc) {
		this.csc = csc;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getCreditCardNumber() {
		return creditCardNumber;
	}

	public void setCreditCardNumber(String creditCardNumber) {
		this.creditCardNumber = creditCardNumber;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	@Override
	public String toString() {
		return "CreditCard [crid=" + crid + ", creditCardNumber=" + creditCardNumber + ", expirationDate="
				+ expirationDate + ", csc=" + csc + ", cid=" + cid + ", vendor=" + vendor + "]";
	}
	
	
}
