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
        Flux<Passive> customers = passiveRepository.findAll();
        return customers;
    }

    @Override
    public Mono<Passive> findByDni(String dni) {
        Mono<Passive> customer = passiveRepository
                .findAll()
                .filter(x -> x.getDni().equals(dni))
                .next();
        return customer;
    }

    @Override
    public Mono<Passive> save(Passive dataPassive) {
        return passiveRepository.save(dataPassive);
    }

    @Override
    public Mono<Passive> update(Passive dataPassive) {
        Mono<Passive> customerMono = findByDni(dataPassive.getDni());
        Passive passive = customerMono.block();
        passive.setStatus(dataPassive.getStatus());
        return passiveRepository.save(passive);
    }

    @Override
    public Mono<Void> delete(String dni) {
        Mono<Passive> customerMono = findByDni(dni);
        Passive passive = customerMono.block();
        return passiveRepository.delete(passive);
    }

}
