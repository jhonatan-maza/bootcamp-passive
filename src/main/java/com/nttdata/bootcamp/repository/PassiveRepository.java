package com.nttdata.bootcamp.repository;

import com.nttdata.bootcamp.entity.Passive;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

//Mongodb Repository
public interface PassiveRepository extends ReactiveCrudRepository<Passive, String> {
}
