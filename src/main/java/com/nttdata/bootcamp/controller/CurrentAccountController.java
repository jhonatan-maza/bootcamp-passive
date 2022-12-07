package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Passive;
import com.nttdata.bootcamp.entity.dto.CurrentAccountDto;
import com.nttdata.bootcamp.entity.dto.UpdateCurrentAccountDto;
import com.nttdata.bootcamp.service.CurrentAccountService;
import com.nttdata.bootcamp.util.Constant;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import javax.validation.Valid;
import java.util.Date;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/currentAccount")
public class CurrentAccountController {

	private static final Logger LOGGER = LoggerFactory.getLogger(CurrentAccountController.class);
	@Autowired
	private CurrentAccountService currentAccountService;

	//All Currents Account Products Registered
	@GetMapping("/findAllCurrentAccounts")
	public Flux<Passive> findAllCurrentAccounts() {
		Flux<Passive> passives = currentAccountService.findAllCurrentAccount();
		LOGGER.info("All Currents Account Products Registered: " + passives);
		return passives;
	}

	//Current Account Products Registered by customer dni
	@GetMapping("/findCurrentAccountByDni/{dni}")
	public Flux<Passive> findCurrentAccountByDni(@PathVariable("dni") String dni) {
		Flux<Passive> passives = currentAccountService.findCurrentAccountByCustomer(dni);
		LOGGER.info("Current Account Products Registered by customer dni: "+dni +"-" + passives);
		return passives;
	}

	//Search Current Account by AccountNumber
	//@CircuitBreaker(name = "passive", fallbackMethod = "fallBackGetCurrent")
	@GetMapping("/findCurrentAccountByAccountNumber/{accountNumber}")
	public Mono<Passive> findCurrentAccountByAccountNumber(@PathVariable("accountNumber") String accountNumber) {
		LOGGER.info("Searching Current Account Products by customer accountNumber: " + accountNumber);
		return currentAccountService.findCurrentAccountByAccountNumber(accountNumber);
	}

	//Save Current Account Personal
	//@CircuitBreaker(name = "passive", fallbackMethod = "fallBackGetCurrent")
	@PostMapping(value = "/saveCurrentAccountPersonal")
	public Mono<Passive> saveCurrentAccountPersonal(@RequestBody CurrentAccountDto account){

		Passive dataCurrentAccount = new Passive();
		Mono.just(dataCurrentAccount).doOnNext(t -> {
					t.setDni(account.getDni());
					t.setTypeCustomer(Constant.PERSONAL_CUSTOMER);
					t.setAccountNumber(account.getAccountNumber());
					t.setCommissionMaintenance(account.getCommissionMaintenance());
					t.setStatus(Constant.PASSIVE_ACTIVE);
					t.setCreationDate(new Date());
					t.setModificationDate(new Date());
				}).onErrorReturn(dataCurrentAccount).onErrorResume(e -> Mono.just(dataCurrentAccount))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Passive> passiveMono = currentAccountService.saveCurrentAccount(dataCurrentAccount);
		return passiveMono;
	}

	//Save Current Account Business
	//@CircuitBreaker(name = "passive", fallbackMethod = "fallBackGetCurrent")
	@PostMapping(value = "/saveCurrentAccountBusiness")
	public Mono<Passive> saveCurrentAccountBusiness(@RequestBody CurrentAccountDto account){

		Passive dataCurrentAccount = new Passive();
		Mono.just(dataCurrentAccount).doOnNext(t -> {
					t.setDni(account.getDni());
					t.setTypeCustomer(Constant.BUSINESS_CUSTOMER);
					t.setAccountNumber(account.getAccountNumber());
					t.setCommissionMaintenance(account.getCommissionMaintenance());
					t.setStatus(Constant.PASSIVE_ACTIVE);
					t.setCreationDate(new Date());
					t.setModificationDate(new Date());
				}).onErrorReturn(dataCurrentAccount).onErrorResume(e -> Mono.just(dataCurrentAccount))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Passive> passiveMono = currentAccountService.saveCurrentAccount(dataCurrentAccount);
		return passiveMono;
	}

	//Update Current Account
	//@CircuitBreaker(name = "passive", fallbackMethod = "fallBackGetCurrent")
	@PutMapping("/updateCurrentAccount/{accountNumber}")
	public Mono<Passive> updateCurrentAccount(@PathVariable("accountNumber") String accountNumber,
											  @Valid @RequestBody UpdateCurrentAccountDto account) {

		Passive dataCurrentAccount = new Passive();
		Mono.just(dataCurrentAccount).doOnNext(t -> {
					t.setAccountNumber(accountNumber);
					t.setCommissionMaintenance(account.getCommissionMaintenance());
					t.setModificationDate(new Date());
				}).onErrorReturn(dataCurrentAccount).onErrorResume(e -> Mono.just(dataCurrentAccount))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Passive> updatePassive = currentAccountService.updateCurrentAccount(dataCurrentAccount);
		return updatePassive;
	}

	//Delete Current Account
	//@CircuitBreaker(name = "passive", fallbackMethod = "fallBackGetCurrent")
	@DeleteMapping("/deleteCurrentAccount/{accountNumber}")
	public ResponseEntity<Mono<Void>> deleteCurrentAccount(@PathVariable("accountNumber") String accountNumber) {
		LOGGER.info("Deleting Current Account product by accountNumber: " + accountNumber);
		Mono<Void> delete = currentAccountService.deleteCurrentAccount(accountNumber);
		return ResponseEntity.noContent().build();
	}

	private Mono<Passive> fallBackGetCurrent(Exception e){
		Passive passive = new Passive();
		Mono<Passive> staffMono= Mono.just(passive);
		return staffMono;
	}

}
