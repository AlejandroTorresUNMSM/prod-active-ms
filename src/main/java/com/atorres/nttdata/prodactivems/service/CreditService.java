package com.atorres.nttdata.prodactivems.service;

import com.atorres.nttdata.prodactivems.client.FeignApiClient;
import com.atorres.nttdata.prodactivems.exception.CustomException;
import com.atorres.nttdata.prodactivems.model.CreditDto;
import com.atorres.nttdata.prodactivems.model.RequestCredit;
import com.atorres.nttdata.prodactivems.model.RequestUpdate;
import com.atorres.nttdata.prodactivems.model.clientms.ClientDto;
import com.atorres.nttdata.prodactivems.repository.CreditRepository;
import com.atorres.nttdata.prodactivems.service.creditstrategy.CreditStrategy;
import com.atorres.nttdata.prodactivems.service.creditstrategy.CreditStrategyFactory;
import com.atorres.nttdata.prodactivems.utils.RequestMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Date;

@Service
@Slf4j
public class CreditService {
	/**
	 *Repositorio creditos
	 */
	@Autowired
	private CreditRepository creditRepository;
	/**
	 * Cliente conecta cliente-microservice
	 */
	@Autowired
	private FeignApiClient feignApiClient;
	@Autowired
	private CreditStrategyFactory strategy;
	/**
	 * Mapper de creditos
	 */
	@Autowired
	private RequestMapper requestMapper;

	/**
	 * Metodo que crea un credito
	 * @param clientId id cliente
	 * @param requestCredit request
	 * @return clientproduct
	 */
	public Mono<CreditDto> createCredit(String clientId, RequestCredit requestCredit) {
		//obtenemos el cliente
		return verificandoDeudas(clientId)
						.switchIfEmpty(Mono.error(new CustomException(HttpStatus.NOT_FOUND, "El cliente no existe")))
						.flatMap(clientdao -> {
							CreditDto cr = requestMapper.toDto(requestCredit,clientId);
							//obtenemos todas las cuentas agregando la nueva
							Flux<CreditDto> creditAll = this.getAllCreditByClient(clientId).concatWith(Flux.just(cr));
							//seleccionamos la estrategia para el tipo de cliente
							CreditStrategy strategyCredit = strategy.getStrategy(clientdao.getTypeClient());
							return strategyCredit.verifyCredit(creditAll).flatMap(exist -> Boolean.FALSE.equals(exist) ? Mono.error(new CustomException(HttpStatus.BAD_REQUEST, "El credito no cumplen los requisitos"))
											: creditRepository.save(requestMapper.toDao(cr)));
						})
						.map(requestMapper::toDto);
	}

	private Mono<ClientDto> verificandoDeudas(String clientId){
		return this.getDebt(clientId)
						.flatMap(value -> {
							if(Boolean.TRUE.equals(value))
								return Mono.error(new CustomException(HttpStatus.CONFLICT, "Cliente tiene deudas vencidas"));
							else
								return feignApiClient.getClient(clientId).single();
						});
	}

	/**
	 * Metodo que trae todos los credito de un cliente
	 * @param clientId id cliente
	 * @return credito
	 */
	public Flux<CreditDto> getAllCreditByClient(String clientId) {
		return creditRepository.findAll()
						.filter(creditDao -> creditDao.getClient().equals(clientId))
						.map(requestMapper::toDto)
						.switchIfEmpty(Flux.empty());
	}

	/**
	 * Meotodo que indica true si tiene deudas vencidas
	 * @param clientId id cliente
	 * @return boolean
	 */
	public Mono<Boolean> getDebt(String clientId){
		return creditRepository.findAll()
						.filter(creditDao -> creditDao.getClient().equals(clientId))
						.any(creditDao -> creditDao.getExpirationDate().before(new Date()))
						.switchIfEmpty(Mono.just(false));
	}

	/**
	 * Metodo que actualiza el credito
	 * @param request request update
	 * @param creditId id del credito
	 * @return creditdto
	 */
	public Mono<CreditDto> updateCredit(RequestUpdate request, String creditId){
		return creditRepository.findById(creditId)
						.switchIfEmpty(Mono.error(new CustomException(HttpStatus.NOT_FOUND, "No se encontro el credito")))
						.map(creditDao -> requestMapper.toDao(request, creditDao))
						.flatMap(cr -> {
							if (cr.getBalance().doubleValue()==0 && cr.getDebt().doubleValue()==0) {
								log.info("Credito eliminado por balance y debt igual a 0");
								return creditRepository.deleteById(cr.getId())
												.then(Mono.empty());
							} else {
								return creditRepository.save(cr)
												.map(requestMapper::toDto);
							}
						});
	}

	/**
	 * Metodo que elimina un credito
	 * @param creditId id cliente
	 * @return vacio
	 */
	public Mono<Void> delete(String creditId) {
		return creditRepository.findById(creditId)
						.switchIfEmpty(Mono.error(new CustomException(HttpStatus.NOT_FOUND, "No se encontro el credito")))
						.flatMap(creditDao -> creditRepository.delete(creditDao));
	}

}
