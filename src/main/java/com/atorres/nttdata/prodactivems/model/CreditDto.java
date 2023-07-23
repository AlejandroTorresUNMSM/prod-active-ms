package com.atorres.nttdata.prodactivems.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class CreditDto {
	private String id;
	private BigDecimal balance;
	private BigDecimal debt;
	private Date expirationDate;
	private String client;
}
