package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.PassiveCurrentAccount;
import com.nttdata.bootcamp.repository.PassiveCurrentAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Service implementation
@Service
public class PassiveCurrentAccountServiceImpl implements PassiveCurrentAccountService {
    @Autowired
    private PassiveCurrentAccountRepository passiveCurrentAccountRepository;

    @Override
    public Flux<PassiveCurrentAccount> findAllCurrentAccount() {
        Flux<PassiveCurrentAccount> passives = passiveCurrentAccountRepository.findAll();
        return passives;
    }

    @Override
    public Mono<PassiveCurrentAccount> findCurrentAccountByAccountNumber(String accountNumber) {
        Mono<PassiveCurrentAccount> passiveMono = passiveCurrentAccountRepository
                .findAll()
                .filter(x -> x.getAccountNumber().equals(accountNumber))
                .next();
        return passiveMono;
    }
    @Override
    public Flux<PassiveCurrentAccount> findCurrentAccountByCustomer(String dni) {
        Flux<PassiveCurrentAccount> passives = passiveCurrentAccountRepository
                .findAll()
                .filter(x -> x.getDni().equals(dni));
        return passives;
    }
    @Override
    public Mono<PassiveCurrentAccount> saveCurrentAccount(PassiveCurrentAccount dataPassiveSaving) {
        return passiveCurrentAccountRepository.save(dataPassiveSaving);
    }

    @Override
    public Mono<PassiveCurrentAccount> updateCurrentAccount(PassiveCurrentAccount dataPassiveCurrentAccount) {
        Mono<PassiveCurrentAccount> passiveMono = findCurrentAccountByAccountNumber(dataPassiveCurrentAccount.getDni());
        PassiveCurrentAccount currentAccount = passiveMono.block();
        currentAccount.setStatus (dataPassiveCurrentAccount.getStatus());
        return passiveCurrentAccountRepository.save(currentAccount);
    }

    @Override
    public Mono<Void> deleteCurrentAccount(String accountNumber) {
        Mono<PassiveCurrentAccount> passiveMono = findCurrentAccountByAccountNumber(accountNumber);
        PassiveCurrentAccount passiveSaving = passiveMono.block();
        return passiveCurrentAccountRepository.delete(passiveSaving);
    }

    @Override
    public Mono<PassiveCurrentAccount> saveCurrentAccountPersonalCustomerByPassive(PassiveCurrentAccount dataPassiveSaving) {
        Mono<PassiveCurrentAccount> passive = Mono.empty();
        if(dataPassiveSaving.getTypeCustomer().equals("PERSONAL")){
            passive = this.searchBySavingPersonalCustomer(dataPassiveSaving);
        }else if(dataPassiveSaving.getTypeCustomer().equals("EMPRESARIAL")){
            passive = this.searchBySavingBusinessCustomer(dataPassiveSaving);
        }

        dataPassiveSaving.setStatus("active");
        dataPassiveSaving.setMovementCommission(2);
        dataPassiveSaving.setSavingMovementsMonthly(0);

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
                .flatMap(__ -> Mono.<PassiveCurrentAccount>error(new Error("El cliente con dni " + dataPassiveSaving.getDni() + " YA TIENE UNA CUENTA")))
                .switchIfEmpty(passiveCurrentAccountRepository.save(dataPassiveSaving));
    }

    public Mono<PassiveCurrentAccount> searchBySavingPersonalCustomer(PassiveCurrentAccount dataPassiveSaving){
        Mono<PassiveCurrentAccount> savingsAccount = passiveCurrentAccountRepository
                .findAll()
                .filter(x -> x.getDni().equals(dataPassiveSaving.getDni()) && x.getTypeCustomer().equals(dataPassiveSaving.getTypeCustomer()) )
                .next();
        return savingsAccount;
    }

    public Mono<PassiveCurrentAccount> searchBySavingBusinessCustomer(PassiveCurrentAccount dataPassiveSaving){
        Mono<PassiveCurrentAccount> savingsAccount = passiveCurrentAccountRepository
                .findAll()
                .filter(x -> x.getDni().equals(dataPassiveSaving.getDni()) && x.getTypeCustomer().equals(dataPassiveSaving.getTypeCustomer()) )
                .next();
        return savingsAccount;
    }

}
