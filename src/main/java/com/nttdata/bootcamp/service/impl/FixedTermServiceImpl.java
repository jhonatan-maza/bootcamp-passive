package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Passive;
import com.nttdata.bootcamp.repository.PassiveRepository;
import com.nttdata.bootcamp.service.FixedTermService;
import com.nttdata.bootcamp.service.PassiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Service implementation
@Service
public class FixedTermServiceImpl implements FixedTermService {

    @Autowired
    private PassiveRepository passiveRepository;
    @Autowired
    private PassiveService passiveService;

    @Override
    public Flux<Passive> findAllFixedTerm() {
        Flux<Passive> passives = passiveRepository
                .findAll()
                .filter(x -> x.getFixedTerm());
        return passives;
    }

    @Override
    public Flux<Passive> findFixedTermByCustomer(String dni) {
        Flux<Passive> passives = passiveRepository
                .findAll()
                .filter(x -> x.getFixedTerm() && x.getDni().equals(dni));
        return passives;
    }

    @Override
    public Mono<Passive> findFixedTermByAccountNumber(String accountNumber) {
        Mono<Passive> passiveMono = passiveRepository
                .findAll()
                .filter(x -> x.getFixedTerm() && x.getAccountNumber().equals(accountNumber))
                .next();
        return passiveMono;
    }

    @Override
    public Mono<Passive> saveFixedTerm(Passive dataFixedTerm) {
        Mono<Passive> passive = Mono.empty();
        dataFixedTerm.setFreeCommission(true);
        dataFixedTerm.setCommissionMaintenance(0);
        dataFixedTerm.setMovementsMonthly(true);
        dataFixedTerm.setLimitMovementsMonthly(1);
        dataFixedTerm.setSaving(false);
        dataFixedTerm.setCurrentAccount(false);
        dataFixedTerm.setFixedTerm(true);
        passive = passiveService.searchByPersonalCustomer(dataFixedTerm);
        return passive
                .flatMap(__ -> Mono.<Passive>error(new Error("El cliente con dni " + dataFixedTerm.getDni() + " YA TIENE UNA CUENTA")))
                .switchIfEmpty(passiveRepository.save(dataFixedTerm));
        //return passiveSavingRepository.save(dataPassiveSaving);
    }

    @Override
    public Mono<Passive> updateFixedTerm(Passive dataFixedTerm) {
        Mono<Passive> passiveMono = findFixedTermByAccountNumber(dataFixedTerm.getAccountNumber());
        try{
            Passive passive = passiveMono.block();
            passive.setModificationDate(dataFixedTerm.getModificationDate());
            return passiveRepository.save(passive);
        }catch (Exception e){
            return Mono.<Passive>error(new Error("El número de cuenta " + dataFixedTerm.getAccountNumber() + " NO EXISTE"));
        }
    }

    @Override
    public Mono<Void> deleteFixedTerm(String accountNumber) {
        Mono<Passive> passiveMono = findFixedTermByAccountNumber(accountNumber);
        try{
            return passiveRepository.delete(passiveMono.block());
        }catch (Exception e){
            return Mono.<Void>error(new Error("El número de cuenta " + accountNumber + " NO EXISTE"));
        }
    }

}
