package com.mymobile.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "phonelines")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class PhoneLine {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "plid")
	private int plid;
	
	@Column
	private int pid;
	
	@Column(name = "phone_number")
	@Size(min = 0, max = 20, message = "0<X<20")
	private String phoneNumber;
	
	@Column
	private int cid;
	
	@Column(name = "phone_name")
	private String phoneName;
	
	@Column(name = "img_src")
	private String imgSrc;
	
	@Column
	private String alt;
	
	@Column
	private String color;
}
