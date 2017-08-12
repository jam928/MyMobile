package com.mymobile.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "phoneplans")
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

	public int getPlanId() {
		return planId;
	}

	public void setPlanId(int planId) {
		this.planId = planId;
	}

	public int getNumberOfLines() {
		return numberOfLines;
	}

	public void setNumberOfLines(int numberOfLines) {
		this.numberOfLines = numberOfLines;
	}

	public float getMonthlyRate() {
		return monthlyRate;
	}

	public void setMonthlyRate(float monthlyRate) {
		this.monthlyRate = monthlyRate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "PhonePlan [planId=" + planId + ", numberOfLines=" + numberOfLines + ", monthlyRate=" + monthlyRate
				+ ", description=" + description + "]";
	}
	
	
}
