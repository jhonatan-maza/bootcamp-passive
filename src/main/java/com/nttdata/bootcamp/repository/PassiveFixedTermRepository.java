package com.nttdata.bootcamp.repository;

import com.nttdata.bootcamp.entity.PassiveCurrentAccount;
import com.nttdata.bootcamp.entity.PassiveFixedTerm;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PassiveFixedTermRepository extends   ReactiveCrudRepository<PassiveFixedTerm, String>{
}
