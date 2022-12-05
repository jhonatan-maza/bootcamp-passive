package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.PassiveCurrentAccount;
import com.nttdata.bootcamp.entity.PassiveSaving;
import com.nttdata.bootcamp.service.PassiveCurrentAccountService;
import com.nttdata.bootcamp.service.PassiveSavingService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import java.util.Date;

@RestController
@RequestMapping(value = "/currentAccount")
public class PassiveCurrentAccountController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PassiveCurrentAccountController.class);
	@Autowired
	private PassiveCurrentAccountService passiveCurrentAccountService;

	//Passives search
	@GetMapping("/findAllPassivesSaving")
	public Flux<PassiveCurrentAccount> findAllPassivesSaving() {
		Flux<PassiveCurrentAccount> actives = passiveCurrentAccountService.findAllCurrentAccount();
		LOGGER.info("Registered Passives Products: " + actives);
		return actives;
	}

	//Passives search by customer
	@GetMapping("/findAllPassivesSavingByCustomer/{dni}")
	public Flux<PassiveCurrentAccount> findAllPassivesSavingByCustomer(@PathVariable("dni") String dni) {
		Flux<PassiveCurrentAccount> passives = passiveCurrentAccountService.findCurrentAccountByCustomer(dni);
		LOGGER.info("Registered Actives Products by customer of dni: "+dni +"-" + passives);
		return passives;
	}

	//Search for passive by AccountNumber
	@CircuitBreaker(name = "passive", fallbackMethod = "fallBackGetCurrent")
	@GetMapping("/findBySavingAccountNumber/{accountNumber}")
	public Mono<PassiveCurrentAccount> findByCurrentAccountNumber(@PathVariable("accountNumber") String accountNumber) {
		LOGGER.info("Searching passives product by accountNumber: " + accountNumber);
		return passiveCurrentAccountService.findCurrentAccountByAccountNumber(accountNumber);
	}

	//Save passive
	@CircuitBreaker(name = "passive", fallbackMethod = "fallBackGetCurrent")
	@PostMapping(value = "/saveSaving")
	public Mono<PassiveCurrentAccount> saveSaving(@RequestBody PassiveCurrentAccount dataPassiveSaving){
		Mono.just(dataPassiveSaving).doOnNext(t -> {

					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataPassiveSaving).onErrorResume(e -> Mono.just(dataPassiveSaving))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<PassiveCurrentAccount> passiveMono = passiveCurrentAccountService.saveCurrentAccount(dataPassiveSaving);
		return passiveMono;
	}

	//Update passive
	@CircuitBreaker(name = "passive", fallbackMethod = "fallBackGetCurrent")
	@PutMapping("/updateSaving/{accountNumber}")
	public ResponseEntity<Mono<?>> updateSaving(@PathVariable("accountNumber") String accountNumber,
												@Valid @RequestBody PassiveCurrentAccount dataPassiveSaving) {
		Mono.just(dataPassiveSaving).doOnNext(t -> {
					dataPassiveSaving.setAccountNumber(accountNumber);
					t.setModificationDate(new Date());

				}).onErrorReturn(dataPassiveSaving).onErrorResume(e -> Mono.just(dataPassiveSaving))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<PassiveCurrentAccount> passiveMono = passiveCurrentAccountService.updateCurrentAccount(dataPassiveSaving);

		if (passiveMono != null) {
			return new ResponseEntity<>(passiveMono, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(Mono.just(new PassiveSaving()), HttpStatus.I_AM_A_TEAPOT);
	}

	//Delete passive
	@CircuitBreaker(name = "passive", fallbackMethod = "fallBackGetCurrent")
	@DeleteMapping("/deleteSaving/{accountNumber}")
	public ResponseEntity<Mono<Void>> deleteSaving(@PathVariable("accountNumber") String accountNumber) {
		LOGGER.info("Deleting passive product by accountNumber: " + accountNumber);
		Mono<Void> delete = passiveCurrentAccountService.deleteCurrentAccount(accountNumber);
		return ResponseEntity.noContent().build();
	}

	//get balance of an Passive Product
	@GetMapping("/balanceOfPassiveSaving/{accountNumber}")
		public Double balanceOfPassive(@PathVariable("accountNumber") String accountNumber) {
		//LOGGER.info("Balance of passiveAccount " + accountNumber +":");
		Mono<PassiveCurrentAccount> pasiveProductMono= findByCurrentAccountNumber(accountNumber);
			return pasiveProductMono.block().getBalance();

	}

	//Save Personal Customer By Passive -savings-account
	@PostMapping(value = "/savePersonalCustomerByPassiveCurrentAccount")
	public Mono<PassiveCurrentAccount> savePersonalCustomerByPassiveCurrentAccount(@Valid @RequestBody PassiveCurrentAccount dataPassiveSaving){

		Mono.just(dataPassiveSaving).doOnNext(t -> {

					t.setTypeCustomer("PERSONAL");
					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataPassiveSaving).onErrorResume(e -> Mono.just(dataPassiveSaving))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<PassiveCurrentAccount> newCustomer = passiveCurrentAccountService.saveCurrentAccountPersonalCustomerByPassive(dataPassiveSaving);
		return newCustomer;
	}


	//Save Business Customer By Passive -savings-account
	@PostMapping(value = "/saveBusinessCustomerByPassive-savings-account")
	public Mono<PassiveCurrentAccount> saveBusinessCustomerByPassiveCurrentAccount(@Valid @RequestBody PassiveCurrentAccount dataPassiveSaving){

		Mono.just(dataPassiveSaving).doOnNext(t -> {

					t.setTypeCustomer("EMPRESARIAL");
					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataPassiveSaving).onErrorResume(e -> Mono.just(dataPassiveSaving))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<PassiveCurrentAccount> newCurrent = passiveCurrentAccountService.saveCurrentAccountPersonalCustomerByPassive(dataPassiveSaving);
		return newCurrent;
	}
	private Mono<PassiveCurrentAccount> fallBackGetCurrent(Exception e){
		PassiveCurrentAccount activeStaff= new PassiveCurrentAccount();
		Mono<PassiveCurrentAccount> staffMono= Mono.just(activeStaff);
		return staffMono;
	}



}
