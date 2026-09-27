package com.mymobile.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "transaction")
@Getter
@Setter
@NoArgsConstructor
@ToString
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
	
	public Transaction(float amount, String date, int crid, int cid) {
		this.amount = amount;
		this.date = date;
		this.crid = crid;
		this.cid = cid;
	}
}
