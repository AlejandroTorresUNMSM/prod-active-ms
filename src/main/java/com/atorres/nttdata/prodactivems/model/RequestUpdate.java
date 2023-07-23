package com.atorres.nttdata.prodactivems.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class RequestUpdate {
	private BigDecimal balance;
	private BigDecimal debt;
}
