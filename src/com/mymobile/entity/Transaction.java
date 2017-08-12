package com.mymobile.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "transaction")
public class Transaction {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tid")
	private int tid;
	
	@Column
	private float amount;
	
	@Column
	private String date;
	
	@Column
	private int crid;
	
	@Column
	private int cid;
	
	public Transaction()
	{
		
	}
	
	
	public Transaction(float amount, String date, int crid, int cid) {
		this.amount = amount;
		this.date = date;
		this.crid = crid;
		this.cid = cid;
	}


	public int getTid() {
		return tid;
	}

	public void setTid(int tid) {
		this.tid = tid;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public int getCrid() {
		return crid;
	}

	public void setCrid(int crid) {
		this.crid = crid;
	}
	
	
}
