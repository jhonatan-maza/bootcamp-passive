package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Passive;
import com.nttdata.bootcamp.repository.PassiveRepository;
import com.nttdata.bootcamp.service.PassiveService;
import com.nttdata.bootcamp.service.SavingAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Service implementation
@Service
public class SavingAccountServiceImpl implements SavingAccountService {

    @Autowired
    private PassiveRepository passiveRepository;
    @Autowired
    private PassiveService passiveService;

    @Override
    public Flux<Passive> findAllSavingAccount() {
        Flux<Passive> passives = passiveRepository
                .findAll()
                .filter(x -> x.getSaving());
        return passives;
    }

    @Override
    public Flux<Passive> findSavingAccountByCustomer(String dni) {
        Flux<Passive> passives = passiveRepository
                .findAll()
                .filter(x -> x.getSaving() && x.getDni().equals(dni));
        return passives;
    }

    @Override
    public Mono<Passive> findSavingAccountByAccountNumber(String accountNumber) {
        Mono<Passive> passiveMono = passiveRepository
                .findAll()
                .filter(x -> x.getSaving() && x.getAccountNumber().equals(accountNumber))
                .next();
        return passiveMono;
    }

    @Override
    public Mono<Passive> saveSavingAccount(Passive dataSavingAccount) {
        Mono<Passive> passive = Mono.empty();
        dataSavingAccount.setFreeCommission(true);
        dataSavingAccount.setCommissionMaintenance(0);
        dataSavingAccount.setMovementsMonthly(true);
        dataSavingAccount.setSaving(true);
        dataSavingAccount.setCurrentAccount(false);
        dataSavingAccount.setFixedTerm(false);
        passive = passiveService.searchByPersonalCustomer(dataSavingAccount);
        return passive
                .flatMap(__ -> Mono.<Passive>error(new Error("El cliente con dni " + dataSavingAccount.getDni() + " YA TIENE UNA CUENTA")))
                .switchIfEmpty(passiveRepository.save(dataSavingAccount));
        //return passiveSavingRepository.save(dataPassiveSaving);
    }

    @Override
    public Mono<Passive> updateSavingAccount(Passive dataSavingAccount) {
        Mono<Passive> passiveMono = findSavingAccountByAccountNumber(dataSavingAccount.getAccountNumber());
        try{
            Passive passive = passiveMono.block();
            passive.setLimitMovementsMonthly(dataSavingAccount.getLimitMovementsMonthly());
            passive.setModificationDate(dataSavingAccount.getModificationDate());
            return passiveRepository.save(passive);
        }catch (Exception e){
            return Mono.<Passive>error(new Error("El número de cuenta " + dataSavingAccount.getAccountNumber() + " NO EXISTE"));
        }
    }

    @Override
    public Mono<Void> deleteSavingAccount(String accountNumber) {
        Mono<Passive> passiveMono = findSavingAccountByAccountNumber(accountNumber);
        try {
            return passiveRepository.delete(passiveMono.block());
        }catch (Exception e){
            return Mono.<Void>error(new Error("El número de cuenta " + accountNumber + " NO EXISTE"));
        }
    }

}
