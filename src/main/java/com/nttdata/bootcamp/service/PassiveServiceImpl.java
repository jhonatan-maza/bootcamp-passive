package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Passive;
import com.nttdata.bootcamp.repository.PassiveRepository;
import com.nttdata.bootcamp.util.Constant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Service implementation
@Service
public class PassiveServiceImpl implements PassiveService {
    @Autowired
    private PassiveRepository passiveRepository;

    @Override
    public Flux<Passive> findAll() {
        Flux<Passive> passives = passiveRepository.findAll();
        return passives;
    }

    @Override
    public Mono<Passive> findByAccountNumber(String accountNumber) {
        Mono<Passive> passiveMono = passiveRepository
                .findAll()
                .filter(x -> x.getAccountNumber().equals(accountNumber))
                .next();
        return passiveMono;
    }
    @Override
    public Flux<Passive> findByCustomer(String dni) {
        Flux<Passive> passives = passiveRepository
                .findAll()
                .filter(x -> x.getDni().equals(dni));
        return passives;
    }
    @Override
    public Mono<Passive> save(Passive dataPassive) {
        return passiveRepository.save(dataPassive);
    }

    @Override
    public Mono<Passive> update(Passive dataPassive) {
        Mono<Passive> passiveMono = findByAccountNumber(dataPassive.getDni());
        Passive passive = passiveMono.block();
        passive.setStatus(dataPassive.getStatus());
        return passiveRepository.save(passive);
    }

    @Override
    public Mono<Void> delete(String dni) {
        Mono<Passive> passiveMono = findByAccountNumber(dni);
        Passive passive = passiveMono.block();
        return passiveRepository.delete(passive);
    }

    @Override
    public Mono<Passive> savePersonalCustomerByPassive(Passive dataPassive, String accountType) {
        Mono<Passive> passive = Mono.empty();
        if(dataPassive.getTypeCustomer().equals("PERSONAL")){
            passive = this.searchByPersonalCustomer(dataPassive);
        }else if(dataPassive.getTypeCustomer().equals("EMPRESARIAL")){
            passive = this.searchByBusinessCustomer(dataPassive);
        }

        dataPassive.setStatus("active");
        if(accountType.equals(Constant.SAVINGS_ACCOUNT)){
            dataPassive.setSaving(true);
            dataPassive.setMovementCommission(0);
            dataPassive.setSavingMovementsMonthly(10);
        }
        else if(accountType.equals(Constant.CURRENT_ACCOUNT)){
            dataPassive.setCurrentAccount(true);
            dataPassive.setMovementCommission(2);
            dataPassive.setSavingMovementsMonthly(0);
        }
        else if(accountType.equals(Constant.FIXED_TERM)){
            dataPassive.setFixedTerm(true);
            dataPassive.setMovementCommission(0);
            dataPassive.setSavingMovementsMonthly(1);
        }

        return passive
                .flatMap(__ -> Mono.<Passive>error(new Error("El cliente con dni " + dataPassive.getDni() + " YA TIENE UNA CUENTA")))
                .switchIfEmpty(passiveRepository.save(dataPassive));
    }

    public Mono<Passive> searchByPersonalCustomer(Passive dataPassive){
        Mono<Passive> savingsAccount = passiveRepository
                .findAll()
                .filter(x -> x.getDni().equals(dataPassive.getDni()) && x.getTypeCustomer().equals(dataPassive.getTypeCustomer()) || x.getSaving() || x.getCurrentAccount() || x.getFixedTerm())
                .next();
        return savingsAccount;
    }

    public Mono<Passive> searchByBusinessCustomer(Passive dataPassive){
        Mono<Passive> savingsAccount = passiveRepository
                .findAll()
                .filter(x -> x.getDni().equals(dataPassive.getDni()) && x.getTypeCustomer().equals(dataPassive.getTypeCustomer()) || x.getSaving() || x.getFixedTerm())
                .next();
        return savingsAccount;
    }

}
