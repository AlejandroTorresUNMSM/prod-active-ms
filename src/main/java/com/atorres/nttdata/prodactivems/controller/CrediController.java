package com.atorres.nttdata.prodactivems.controller;

import com.atorres.nttdata.prodactivems.model.CreditDto;
import com.atorres.nttdata.prodactivems.model.RequestCredit;
import com.atorres.nttdata.prodactivems.model.RequestUpdate;
import com.atorres.nttdata.prodactivems.service.CreditService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/api/credit")
@Slf4j
public class CrediController {
    @Autowired
    CreditService creditService;

    /**
     * Endpoint para traer todos los creditos de un cliente
     * @param id id cliente
     * @return lista creditos
     */
    @GetMapping( value = "/client/{id}",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<CreditDto> getAllCreditClient(@PathVariable String id){
        return creditService.getAllCreditByClient(id)
                .doOnNext(v -> log.info("Credito encontrado: "+v.getId()));
    }

    /**
     * Endpoint para crear un credito
     * @param id id cliente
     * @param requestCredit request
     * @return clientproduct
     */
    @PostMapping(value = "/client/{id}",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<CreditDto> createCredit(@PathVariable String id, @RequestBody Mono<RequestCredit> requestCredit){
        return requestCredit.flatMap(credit -> creditService.createCredit(id,credit))
                .doOnSuccess(v -> log.info("Credito creado con exito"));
    }

    /**
     * Endpoint para actualizar un credito
     * @param id id credito
     * @param requestCredit request
     * @return creditdto
     */
    @PutMapping(value = "/update/{id}",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<CreditDto> updateCredit(@PathVariable String id, @RequestBody Mono<RequestUpdate> requestCredit){
        return requestCredit.flatMap(credit -> creditService.updateCredit(credit,id))
                .doOnSuccess(v -> log.info("Credito actualizado con exito"));
    }

    /**
     * Metodo para eliminar un credito
     * @param clientId request
     * @return vacio
     */
    @DeleteMapping(value = "/{clientId}",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<Void> deleteCredit(@PathVariable String clientId){
        return creditService.delete(clientId)
                .doOnSuccess(v -> log.info("Credito eliminado con exito"));
    }

    /**
     * Metodo que indica si el cliente tiene deudas vencidas
     * @param clientId id cliente
     * @return boolean
     */
    @GetMapping( value = "/deudas-vencidas/{clientId}",produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<Boolean> getDeuda(@PathVariable String clientId){
        return creditService.getDebt(clientId)
                .doOnSuccess(v -> {
                    if(Boolean.TRUE.equals(v))
                        log.info("Cliente tiene deudas vencidas");
                    else
                        log.info("Cliente no tiene deudas vencidas");
                });
    }
}
