package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.PassiveSaving;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Interface Service
public interface PassiveSavingService {
    public Flux<PassiveSaving> findAllSaving();
    public Mono<PassiveSaving> findSavingByAccountNumber(String accountNumber);
    public Flux<PassiveSaving> findSavingByCustomer(String dni);
    public Mono<PassiveSaving> saveSaving(PassiveSaving active);
    public Mono<PassiveSaving> updateSaving(PassiveSaving dataPassiveSaving);
    public Mono<Void> deleteSaving(String accountNumber);
    public Mono<PassiveSaving> saveSavingPersonalCustomerByPassive(PassiveSaving passiveSaving);

}
