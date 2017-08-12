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
@Table(name = "phonelines")
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

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public int getPlid() {
		return plid;
	}

	public void setPlid(int plid) {
		this.plid = plid;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public int getCid() {
		return cid;
	}

	public void setCid(int cid) {
		this.cid = cid;
	}

	public String getPhoneName() {
		return phoneName;
	}

	public void setPhoneName(String phoneName) {
		this.phoneName = phoneName;
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

	@Override
	public String toString() {
		return "PhoneLine [plid=" + plid + ", pid=" + pid + ", phoneNumber=" + phoneNumber + ", cid=" + cid
				+ ", phoneName=" + phoneName + ", imgSrc=" + imgSrc + ", alt=" + alt + ", color=" + color + "]";
	}
}
