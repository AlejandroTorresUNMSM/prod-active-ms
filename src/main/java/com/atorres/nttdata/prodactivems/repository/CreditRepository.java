package com.atorres.nttdata.prodactivems.repository;

import com.atorres.nttdata.prodactivems.model.dao.CreditDao;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CreditRepository extends ReactiveMongoRepository<CreditDao,String> {
}
