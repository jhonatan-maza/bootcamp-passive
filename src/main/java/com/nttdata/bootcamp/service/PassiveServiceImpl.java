package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Passive;
import com.nttdata.bootcamp.repository.PassiveRepository;
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

}
