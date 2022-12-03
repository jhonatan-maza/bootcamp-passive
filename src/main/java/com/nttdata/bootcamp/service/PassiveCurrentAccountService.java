package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.PassiveCurrentAccount;
import com.nttdata.bootcamp.entity.PassiveSaving;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Interface Service
public interface PassiveCurrentAccountService {
    public Flux<PassiveCurrentAccount> findAllCurrentAccount();
    public Mono<PassiveCurrentAccount> findCurrentAccountByAccountNumber(String accountNumber);
    public Flux<PassiveCurrentAccount> findCurrentAccountByCustomer(String dni);
    public Mono<PassiveCurrentAccount> saveCurrentAccount(PassiveCurrentAccount active);
    public Mono<PassiveCurrentAccount> updateCurrentAccount(PassiveCurrentAccount dataPassiveSaving);
    public Mono<Void> deleteCurrentAccount(String accountNumber);
    public Mono<PassiveCurrentAccount> saveCurrentAccountPersonalCustomerByPassive(PassiveCurrentAccount passiveSaving);

}
