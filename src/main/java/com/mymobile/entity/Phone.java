package com.mymobile.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "phones")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Phone {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pid")
	private int pid;
	
	@Column(name = "`condition`") // reserved word in MySQL, must be quoted
	@NotNull(message = "is required")
	private String condition;
	
	@Column(name = "name")
	@NotNull(message = "is required")
	private String name;
	
	@Column
	@NotNull(message = "is required")
	private String brand;
	
	@Column(name = "rating")
	@Min(value = 0, message = "must be greater than or equal to zero")
	@Max(value = 5, message = "must be less than or equal to five")
	private int rating;
	
	@Column(name = "price")
	@Min(value = 0, message = "price must be greater than or equal to zero")
	private Float price;
	
	@Column(name = "color")
	@NotNull(message = "is required")
	private String color;
	
	@Column(name = "img_src")
	@NotNull(message = "is required")
	private String imgSrc;
	
	@Column(name = "alt")
	@NotNull(message = "is required")
	private String alt;
	
	@Column
	private int quantity;
	
	@Column
	private String description;
	
	@Column
	private String storage;
	
	@Column
	private String screen;
	
	@Column
	private String camera;
	
	@Column
	private String battery;
}
