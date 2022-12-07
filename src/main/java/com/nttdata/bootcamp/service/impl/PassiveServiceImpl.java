package com.nttdata.bootcamp.service.impl;

import com.nttdata.bootcamp.entity.Passive;
import com.nttdata.bootcamp.repository.PassiveRepository;
import com.nttdata.bootcamp.service.PassiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PassiveServiceImpl implements PassiveService {

    @Autowired
    private PassiveRepository passiveRepository;

    public Mono<Passive> searchByPersonalCustomer(Passive dataPersonalCustomer){
        Mono<Passive> savingsAccount = passiveRepository
                .findAll()
                .filter(x -> x.getDni().equals(dataPersonalCustomer.getDni()) &&
                        x.getTypeCustomer().equals(dataPersonalCustomer.getTypeCustomer())
                )
                .next();
        return savingsAccount;
    }

}
