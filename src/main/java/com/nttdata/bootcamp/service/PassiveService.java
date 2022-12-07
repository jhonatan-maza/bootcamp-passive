package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Passive;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

//Interface Service
@Service
public interface PassiveService {
    Mono<Passive> searchByPersonalCustomer(Passive dataPersonalCustomer);
}
