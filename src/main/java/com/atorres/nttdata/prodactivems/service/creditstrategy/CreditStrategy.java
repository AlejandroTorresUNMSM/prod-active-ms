package com.atorres.nttdata.prodactivems.service.creditstrategy;

import com.atorres.nttdata.prodactivems.model.CreditDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CreditStrategy {
    Mono<Boolean> verifyCredit(Flux<CreditDto> listCredit);
}
