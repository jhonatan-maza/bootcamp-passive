package com.nttdata.bootcamp.repository;

import com.nttdata.bootcamp.entity.PassiveCurrentAccount;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface PassiveCurrentAccountRepository extends ReactiveCrudRepository<PassiveCurrentAccount, String> {
}
