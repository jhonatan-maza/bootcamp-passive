package com.nttdata.bootcamp.repository;

import com.nttdata.bootcamp.entity.PassiveSaving;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

//Mongodb Repository
public interface PassiveSavingRepository extends ReactiveCrudRepository<PassiveSaving, String> {
}
