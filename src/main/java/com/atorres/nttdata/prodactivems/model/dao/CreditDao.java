package com.atorres.nttdata.prodactivems.model.dao;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Builder
@Document("credit")
public class CreditDao {
    @Id
    private String id;
    private BigDecimal balance;
    private BigDecimal debt;
    private Date expirationDate;
}
