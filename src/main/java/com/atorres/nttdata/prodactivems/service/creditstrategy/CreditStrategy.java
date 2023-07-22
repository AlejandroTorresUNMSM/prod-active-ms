package com.atorres.nttdata.prodactivems.service.creditstrategy;

import com.atorres.nttdata.prodactivems.model.dao.CreditDao;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditStrategy {
    Mono<Boolean> verifyCredit(Flux<CreditDao> listCredit);
}
