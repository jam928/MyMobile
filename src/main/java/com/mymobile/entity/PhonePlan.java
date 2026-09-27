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
@Table(name = "phoneplans")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class PhonePlan {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "planId")
	private int planId;
	
	@Column
	private int numberOfLines;
	
	@Column
	private float monthlyRate;
	
	@Column
	private String description;
}
