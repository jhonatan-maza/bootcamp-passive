package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Passive;
import com.nttdata.bootcamp.entity.dto.SavingAccountDto;
import com.nttdata.bootcamp.entity.dto.UpdateSavingAccountDto;
import com.nttdata.bootcamp.util.Constant;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nttdata.bootcamp.service.SavingAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Date;
import javax.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/saving")
public class SavingAccountController {

	private static final Logger LOGGER = LoggerFactory.getLogger(SavingAccountController.class);
	@Autowired
	private SavingAccountService savingAccountService;

	//All Savings Account Products Registered
	@GetMapping("/findAllSavingsAccounts")
	public Flux<Passive> findAllSavingsAccounts() {
		Flux<Passive> passives = savingAccountService.findAllSavingAccount();
		LOGGER.info("All Savings Account Products Registered: " + passives);
		return passives;
	}

	//Saving Account Products Registered by customer dni
	@GetMapping("/findSavingAccountByDni/{dni}")
	public Flux<Passive> findSavingAccountByDni(@PathVariable("dni") String dni) {
		Flux<Passive> passives = savingAccountService.findSavingAccountByCustomer(dni);
		LOGGER.info("Saving Account Products Registered by customer dni: "+dni +"-" + passives);
		return passives;
	}

	//Search Saving Account by AccountNumber
	//@CircuitBreaker(name = "passive", fallbackMethod = "fallBackGetSaving")
	@GetMapping("/findSavingAccountByAccountNumber/{accountNumber}")
	public Mono<Passive> findSavingAccountByAccountNumber(@PathVariable("accountNumber") String accountNumber) {
		LOGGER.info("Searching Saving Account Products by customer accountNumber: " + accountNumber);
		return savingAccountService.findSavingAccountByAccountNumber(accountNumber);
	}

	//Save Saving Account
	//@CircuitBreaker(name = "passive", fallbackMethod = "fallBackGetSaving")
	@PostMapping(value = "/saveSavingAccount")
	public Mono<Passive> saveSavingAccount(@RequestBody SavingAccountDto account){

		Passive dataPassiveSaving = new Passive();
		Mono.just(dataPassiveSaving).doOnNext(t -> {
			t.setDni(account.getDni());
			t.setTypeCustomer(account.getTypeCustomer());
			t.setAccountNumber(account.getAccountNumber());
			t.setLimitMovementsMonthly(account.getLimitMovementsMonthly());
			t.setStatus(Constant.PASSIVE_ACTIVE);
			t.setCreationDate(new Date());
			t.setModificationDate(new Date());
		}).onErrorReturn(dataPassiveSaving).onErrorResume(e -> Mono.just(dataPassiveSaving))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Passive> passiveMono = savingAccountService.saveSavingAccount(dataPassiveSaving);
		return passiveMono;
	}

	//Update Saving Account
	//@CircuitBreaker(name = "passive", fallbackMethod = "fallBackGetSaving")
	@PutMapping("/updateSavingAccount/{accountNumber}")
	public Mono<Passive> updateSavingAccount(@PathVariable("accountNumber") String accountNumber,
											 @Valid @RequestBody UpdateSavingAccountDto account) {

		Passive dataSavingAccount = new Passive();
		Mono.just(dataSavingAccount).doOnNext(t -> {
					t.setAccountNumber(accountNumber);
					t.setLimitMovementsMonthly(account.getLimitMovementsMonthly());
					t.setModificationDate(new Date());
			t.setModificationDate(new Date());
		}).onErrorReturn(dataSavingAccount).onErrorResume(e -> Mono.just(dataSavingAccount))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Passive> updatePassive = savingAccountService.updateSavingAccount(dataSavingAccount);
		return updatePassive;
	}

	//Delete Saving Account
	//@CircuitBreaker(name = "passive", fallbackMethod = "fallBackGetSaving")
	@DeleteMapping("/deleteSavingAccount/{accountNumber}")
	public ResponseEntity<Mono<Void>> deleteSavingAccount(@PathVariable("accountNumber") String accountNumber) {
		LOGGER.info("Deleting Saving Account product by accountNumber: " + accountNumber);
		Mono<Void> delete = savingAccountService.deleteSavingAccount(accountNumber);
		return ResponseEntity.noContent().build();
	}

	private Mono<Passive> fallBackGetSaving(Exception e){
		Passive passive= new Passive();
		Mono<Passive> staffMono= Mono.just(passive);
		return staffMono;
	}
}
