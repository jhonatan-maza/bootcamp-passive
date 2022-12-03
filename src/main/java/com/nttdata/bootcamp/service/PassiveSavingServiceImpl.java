package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.PassiveSaving;
import com.nttdata.bootcamp.repository.PassiveSavingRepository;
import com.nttdata.bootcamp.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Service implementation
@Service
public class PassiveSavingServiceImpl implements PassiveSavingService {
    @Autowired
    private PassiveSavingRepository passiveSavingRepository;

    @Override
    public Flux<PassiveSaving> findAllSaving() {
        Flux<PassiveSaving> passives = passiveSavingRepository.findAll();
        return passives;
    }

    @Override
    public Mono<PassiveSaving> findSavingByAccountNumber(String accountNumber) {
        Mono<PassiveSaving> passiveMono = passiveSavingRepository
                .findAll()
                .filter(x -> x.getAccountNumber().equals(accountNumber))
                .next();
        return passiveMono;
    }
    @Override
    public Flux<PassiveSaving> findSavingByCustomer(String dni) {
        Flux<PassiveSaving> passives = passiveSavingRepository
                .findAll()
                .filter(x -> x.getDni().equals(dni));
        return passives;
    }
    @Override
    public Mono<PassiveSaving> saveSaving(PassiveSaving dataPassiveSaving) {
        return passiveSavingRepository.save(dataPassiveSaving);
    }

    @Override
    public Mono<PassiveSaving> updateSaving(PassiveSaving dataPassiveSaving) {
        Mono<PassiveSaving> passiveMono = findSavingByAccountNumber(dataPassiveSaving.getDni());
        PassiveSaving passiveSaving = passiveMono.block();
        passiveSaving.setStatus(dataPassiveSaving.getStatus());
        return passiveSavingRepository.save(passiveSaving);
    }

    @Override
    public Mono<Void> deleteSaving(String dni) {
        Mono<PassiveSaving> passiveMono = findSavingByAccountNumber(dni);
        PassiveSaving passiveSaving = passiveMono.block();
        return passiveSavingRepository.delete(passiveSaving);
    }

    @Override
    public Mono<PassiveSaving> saveSavingPersonalCustomerByPassive(PassiveSaving dataPassiveSaving) {
        Mono<PassiveSaving> passive = Mono.empty();
        if(dataPassiveSaving.getTypeCustomer().equals("PERSONAL")){
            passive = this.searchBySavingPersonalCustomer(dataPassiveSaving);
        }else if(dataPassiveSaving.getTypeCustomer().equals("EMPRESARIAL")){
            passive = this.searchBySavingBusinessCustomer(dataPassiveSaving);
        }

        dataPassiveSaving.setStatus("active");
        dataPassiveSaving.setMovementCommission(0);
        dataPassiveSaving.setSavingMovementsMonthly(10);

        /*if(accountType.equals(Constant.SAVINGS_ACCOUNT)){
            dataPassiveSaving.setSaving(true);
            dataPassiveSaving.setMovementCommission(0);
            dataPassiveSaving.setSavingMovementsMonthly(10);
        }
        else if(accountType.equals(Constant.CURRENT_ACCOUNT)){
            dataPassiveSaving.setCurrentAccount(true);
            dataPassiveSaving.setMovementCommission(2);
            dataPassiveSaving.setSavingMovementsMonthly(0);
        }
        else if(accountType.equals(Constant.FIXED_TERM)){
            dataPassiveSaving.setFixedTerm(true);
            dataPassiveSaving.setMovementCommission(0);
            dataPassiveSaving.setSavingMovementsMonthly(1);
        }*/

        return passive
                .flatMap(__ -> Mono.<PassiveSaving>error(new Error("El cliente con dni " + dataPassiveSaving.getDni() + " YA TIENE UNA CUENTA")))
                .switchIfEmpty(passiveSavingRepository.save(dataPassiveSaving));
    }

    public Mono<PassiveSaving> searchBySavingPersonalCustomer(PassiveSaving dataPassiveSaving){
        Mono<PassiveSaving> savingsAccount = passiveSavingRepository
                .findAll()
                .filter(x -> x.getDni().equals(dataPassiveSaving.getDni()) && x.getTypeCustomer().equals(dataPassiveSaving.getTypeCustomer()) )
                .next();
        return savingsAccount;
    }

    public Mono<PassiveSaving> searchBySavingBusinessCustomer(PassiveSaving dataPassiveSaving){
        Mono<PassiveSaving> savingsAccount = passiveSavingRepository
                .findAll()
                .filter(x -> x.getDni().equals(dataPassiveSaving.getDni()) && x.getTypeCustomer().equals(dataPassiveSaving.getTypeCustomer()) )
                .next();
        return savingsAccount;
    }

}
