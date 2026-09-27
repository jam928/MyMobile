package com.mymobile.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "creditcard")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class CreditCard {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "crid")
	private int crid;
	
	@NotNull
	@Size(min=8,max = 16)
	@Column(name = "credit_card_number")
	@ToString.Exclude
	private String creditCardNumber;
	
	@Column(name = "expiration_date")
	private String expirationDate;
	
	@Column
	@ToString.Exclude
	private int csc;
	
	@Column
	private int cid;
	
	@Column
	private String vendor;
}
