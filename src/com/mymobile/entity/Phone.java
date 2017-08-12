package com.mymobile.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "phones")
public class Phone {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "pid")
	private int pid;
	
	@Column(name = "condition")
	@NotNull(message = "is required")
	private String condition;
	
	@Column(name = "name")
	@NotNull(message = "is required")
	private String name;
	
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
	
	public String getCondition() {
		return condition;
	}
	public void setCondition(String condition) {
		this.condition = condition;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getRating() {
		return rating;
	}
	public void setRating(int rating) {
		this.rating = rating;
	}
	public Float getPrice() {
		return price;
	}
	public void setPrice(Float price) {
		this.price = price;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getImgSrc() {
		return imgSrc;
	}
	public void setImgSrc(String imgSrc) {
		this.imgSrc = imgSrc;
	}
	public String getAlt() {
		return alt;
	}
	public void setAlt(String alt) {
		this.alt = alt;
	}
	public int getPid() {
		return pid;
	}
	public void setPid(int pid) {
		this.pid = pid;
	}
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
}
