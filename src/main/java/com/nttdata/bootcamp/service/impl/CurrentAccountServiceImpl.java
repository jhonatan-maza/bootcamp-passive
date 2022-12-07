package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Passive;
import com.nttdata.bootcamp.repository.PassiveRepository;
import com.nttdata.bootcamp.service.CurrentAccountService;
import com.nttdata.bootcamp.service.PassiveService;
import com.nttdata.bootcamp.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Service implementation
@Service
public class CurrentAccountServiceImpl implements CurrentAccountService {

    @Autowired
    private PassiveRepository passiveRepository;
    @Autowired
    private PassiveService passiveService;

    @Override
    public Flux<Passive> findAllCurrentAccount() {
        Flux<Passive> passives = passiveRepository
                .findAll()
                .filter(x -> x.getCurrentAccount());
        return passives;
    }

    @Override
    public Flux<Passive> findCurrentAccountByCustomer(String dni) {
        Flux<Passive> passives = passiveRepository
                .findAll()
                .filter(x -> x.getCurrentAccount() && x.getDni().equals(dni));
        return passives;
    }

    @Override
    public Mono<Passive> findCurrentAccountByAccountNumber(String accountNumber) {
        Mono<Passive> passiveMono = passiveRepository
                .findAll()
                .filter(x -> x.getCurrentAccount() && x.getAccountNumber().equals(accountNumber))
                .next();
        return passiveMono;
    }

    @Override
    public Mono<Passive> saveCurrentAccount(Passive dataCurrentAccount) {
        Mono<Passive> passive = Mono.empty();
        dataCurrentAccount.setFreeCommission(false);
        dataCurrentAccount.setMovementsMonthly(false);
        dataCurrentAccount.setLimitMovementsMonthly(0);
        dataCurrentAccount.setSaving(false);
        dataCurrentAccount.setCurrentAccount(true);
        dataCurrentAccount.setFixedTerm(false);
        if(dataCurrentAccount.getTypeCustomer().equals(Constant.PERSONAL_CUSTOMER)){
            passive = passiveService.searchByPersonalCustomer(dataCurrentAccount);
        }
        return passive
                .flatMap(__ -> Mono.<Passive>error(new Error("El cliente con dni " + dataCurrentAccount.getDni() + " YA TIENE UNA CUENTA")))
                .switchIfEmpty(passiveRepository.save(dataCurrentAccount));
    }

    @Override
    public Mono<Passive> updateCurrentAccount(Passive dataCurrentAccount) {
        Mono<Passive> passiveMono = findCurrentAccountByAccountNumber(dataCurrentAccount.getAccountNumber());
        try{
            Passive passive = passiveMono.block();
            passive.setCommissionMaintenance(dataCurrentAccount.getCommissionMaintenance());
            passive.setModificationDate(dataCurrentAccount.getModificationDate());
            return passiveRepository.save(passive);
        }catch (Exception e){
            return Mono.<Passive>error(new Error("El número de cuenta " + dataCurrentAccount.getAccountNumber() + " NO EXISTE"));
        }
    }

    @Override
    public Mono<Void> deleteCurrentAccount(String accountNumber) {
        Mono<Passive> passiveMono = findCurrentAccountByAccountNumber(accountNumber);
        try {
            return passiveRepository.delete(passiveMono.block());
        }catch (Exception e){
            return Mono.<Void>error(new Error("El número de cuenta " + accountNumber + " NO EXISTE"));
        }
    }

}
