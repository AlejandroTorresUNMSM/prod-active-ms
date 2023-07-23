package com.atorres.nttdata.prodactivems.utils;

import com.atorres.nttdata.prodactivems.model.CreditDto;
import com.atorres.nttdata.prodactivems.model.RequestCredit;
import com.atorres.nttdata.prodactivems.model.RequestUpdate;
import com.atorres.nttdata.prodactivems.model.dao.CreditDao;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

@Component
public class RequestMapper {

    public CreditDao toDao(RequestCredit requestCredit,String clientId){
        return CreditDao.builder()
                .id(generateId())
                .balance(requestCredit.getBalance())
                .debt(requestCredit.getBalance())
                .expirationDate(generateExpirationDay())
                .client(clientId)
                .build();
    }

    public CreditDao toDao(RequestUpdate request, CreditDao creditDao){
        return CreditDao.builder()
                .id(creditDao.getId())
                .balance(request.getBalance())
                .debt(request.getDebt())
                .expirationDate(creditDao.getExpirationDate())
                .client(creditDao.getClient())
                .build();
    }

    public CreditDao toDao(CreditDto creditDto){
        return CreditDao.builder()
                .id(creditDto.getId())
                .balance(creditDto.getBalance())
                .debt(creditDto.getDebt())
                .expirationDate(creditDto.getExpirationDate())
                .client(creditDto.getClient())
                .build();
    }

    public CreditDto toDto(CreditDao creditDao){
        CreditDto creditDto= new CreditDto();
        creditDto.setId(creditDao.getId());
        creditDto.setBalance(creditDao.getBalance());
        creditDto.setDebt(creditDao.getDebt());
        creditDto.setExpirationDate(creditDao.getExpirationDate());
        creditDto.setClient(creditDao.getClient());
        return creditDto;
    }

    public CreditDto toDto(RequestCredit creditDao,String clientId){
        CreditDto creditDto= new CreditDto();
        creditDto.setId(generateId());
        creditDto.setBalance(creditDao.getBalance());
        creditDto.setDebt(creditDao.getBalance());
        creditDto.setExpirationDate(generateExpirationDay());
        creditDto.setClient(clientId);
        return creditDto;
    }

    private Date generateExpirationDay(){
        LocalDate today = LocalDate.now();
        LocalDate expirationLocalDate = today.plusDays(30);
        return Date.from(expirationLocalDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }
    private String generateId(){
        return java.util.UUID.randomUUID().toString().replace("-","");
    }
}
