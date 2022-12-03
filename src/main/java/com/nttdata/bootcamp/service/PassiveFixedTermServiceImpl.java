package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.PassiveFixedTerm;
import com.nttdata.bootcamp.repository.PassiveFixedTermRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Service implementation
@Service
public class PassiveFixedTermServiceImpl implements PassiveFixedTermService {
    @Autowired
    private PassiveFixedTermRepository passiveFixedTermRepository;

    @Override
    public Flux<PassiveFixedTerm> findAllFixedTerm() {
        Flux<PassiveFixedTerm> passives = passiveFixedTermRepository.findAll();
        return passives;
    }

    @Override
    public Mono<PassiveFixedTerm> findFixedTermByAccountNumber(String accountNumber) {
        Mono<PassiveFixedTerm> passiveMono = passiveFixedTermRepository
                .findAll()
                .filter(x -> x.getAccountNumber().equals(accountNumber))
                .next();
        return passiveMono;
    }
    @Override
    public Flux<PassiveFixedTerm> findFixedTermByCustomer(String dni) {
        Flux<PassiveFixedTerm> passives = passiveFixedTermRepository
                .findAll()
                .filter(x -> x.getDni().equals(dni));
        return passives;
    }
    @Override
    public Mono<PassiveFixedTerm> saveFixedTerm(PassiveFixedTerm dataPassiveFixedTerm) {
        return passiveFixedTermRepository.save(dataPassiveFixedTerm);
    }

    @Override
    public Mono<PassiveFixedTerm> updateFixedTerm(PassiveFixedTerm dataPassiveSaving) {
        Mono<PassiveFixedTerm> passiveMono = findFixedTermByAccountNumber(dataPassiveSaving.getDni());
        PassiveFixedTerm passiveSaving = passiveMono.block();
        passiveSaving.setStatus(dataPassiveSaving.getStatus());
        return passiveFixedTermRepository.save(passiveSaving);
    }

    @Override
    public Mono<Void> deleteFixedTerm(String dni) {
        Mono<PassiveFixedTerm> passiveMono = findFixedTermByAccountNumber(dni);
        PassiveFixedTerm passiveSaving = passiveMono.block();
        return passiveFixedTermRepository.delete(passiveSaving);
    }

    @Override
    public Mono<PassiveFixedTerm> saveFixedTermPersonalCustomerByPassive(PassiveFixedTerm dataPassiveFixedTerm) {
        Mono<PassiveFixedTerm> passive = Mono.empty();
        if(dataPassiveFixedTerm.getTypeCustomer().equals("PERSONAL")){
            passive = this.searchByFixedTermPersonalCustomer(dataPassiveFixedTerm);
        }else if(dataPassiveFixedTerm.getTypeCustomer().equals("EMPRESARIAL")){
            passive = this.searchByFixedTermBusinessCustomer(dataPassiveFixedTerm);
        }

        dataPassiveFixedTerm.setStatus("active");
        dataPassiveFixedTerm.setMovementCommission(0);
        dataPassiveFixedTerm.setSavingMovementsMonthly(1);

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
                .flatMap(__ -> Mono.<PassiveFixedTerm>error(new Error("El cliente con dni " + dataPassiveFixedTerm.getDni() + " YA TIENE UNA CUENTA")))
                .switchIfEmpty(passiveFixedTermRepository.save(dataPassiveFixedTerm));
    }

    public Mono<PassiveFixedTerm> searchByFixedTermPersonalCustomer(PassiveFixedTerm dataPassiveFixedTerm){
        Mono<PassiveFixedTerm> savingsAccount = passiveFixedTermRepository
                .findAll()
                .filter(x -> x.getDni().equals(dataPassiveFixedTerm.getDni()) && x.getTypeCustomer().equals(dataPassiveFixedTerm.getTypeCustomer()) )
                .next();
        return savingsAccount;
    }

    public Mono<PassiveFixedTerm> searchByFixedTermBusinessCustomer(PassiveFixedTerm dataPassiveFixedTerm){
        Mono<PassiveFixedTerm> savingsAccount = passiveFixedTermRepository
                .findAll()
                .filter(x -> x.getDni().equals(dataPassiveFixedTerm.getDni()) && x.getTypeCustomer().equals(dataPassiveFixedTerm.getTypeCustomer()) )
                .next();
        return savingsAccount;
    }

}
