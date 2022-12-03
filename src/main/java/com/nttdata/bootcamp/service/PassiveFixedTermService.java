package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.PassiveFixedTerm;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Interface Service
public interface PassiveFixedTermService {
    public Flux<PassiveFixedTerm> findAllFixedTerm();
    public Mono<PassiveFixedTerm> findFixedTermByAccountNumber(String accountNumber);
    public Flux<PassiveFixedTerm> findFixedTermByCustomer(String dni);
    public Mono<PassiveFixedTerm> saveFixedTerm(PassiveFixedTerm active);
    public Mono<PassiveFixedTerm> updateFixedTerm(PassiveFixedTerm dataPassiveFixedTerm);
    public Mono<Void> deleteFixedTerm(String accountNumber);
    public Mono<PassiveFixedTerm> saveFixedTermPersonalCustomerByPassive(PassiveFixedTerm passiveFixedTerm);

}
