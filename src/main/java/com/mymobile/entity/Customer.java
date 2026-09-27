package com.mymobile.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class Customer {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "cid")
	private int cid;
	
	@Column
	@NotNull(message = "is required")
	private String name;
	
	@Column(unique = true)
	@NotNull(message = "is required")
	private String email;
	
	@Column
	@NotNull(message = "is required")
	@Size(min = 8, message ="at least 8 characters at the minimum")
	@ToString.Exclude
	private String password;
	
	@Column
	@NotNull
	private String birthday;
	
	@Column(name = "phone_lines")
	private int phoneLines;
	
	@Column
	private float balance;
	
	@Column
	private int planId;
	
	@Column
	@Enumerated(EnumType.STRING)
	@JdbcTypeCode(SqlTypes.VARCHAR)
	private Role role = Role.CUSTOMER;
}
