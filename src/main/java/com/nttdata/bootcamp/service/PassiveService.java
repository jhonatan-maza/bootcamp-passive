package com.nttdata.bootcamp.service;

import com.nttdata.bootcamp.entity.Passive;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

//Interface Service
public interface PassiveService {

    public Flux<Passive> findAll();
    public Mono<Passive> findByDni(String dni);
    public Mono<Passive> save(Passive passive);
    public Mono<Passive> update(Passive dataPassive);
    public Mono<Void> delete(String dni);

}
