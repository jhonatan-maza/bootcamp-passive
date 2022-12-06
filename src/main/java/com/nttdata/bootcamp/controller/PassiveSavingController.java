package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.PassiveSaving;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nttdata.bootcamp.service.PassiveSavingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.Date;
import javax.validation.Valid;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/saving")
public class PassiveSavingController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PassiveSavingController.class);
	@Autowired
	private PassiveSavingService passiveSavingService;

	//Passives search
	@GetMapping("/findAllPassivesSaving")
	public Flux<PassiveSaving> findAllPassivesSaving() {
		Flux<PassiveSaving> actives = passiveSavingService.findAllSaving();
		LOGGER.info("Registered Passives Products: " + actives);
		return actives;
	}

	//Passives search by customer
	@GetMapping("/findAllPassivesSavingByCustomer/{dni}")
	public Flux<PassiveSaving> findAllPassivesSavingByCustomer(@PathVariable("dni") String dni) {
		Flux<PassiveSaving> passives = passiveSavingService.findSavingByCustomer(dni);
		LOGGER.info("Registered Actives Products by customer of dni: "+dni +"-" + passives);
		return passives;
	}

	//Search for passive by AccountNumber
	@CircuitBreaker(name = "passive", fallbackMethod = "fallBackGetSaving")
	@GetMapping("/findBySavingAccountNumber/{accountNumber}")
	public Mono<PassiveSaving> findBySavingAccountNumber(@PathVariable("accountNumber") String accountNumber) {
		LOGGER.info("Searching passives product by accountNumber: " + accountNumber);
		return passiveSavingService.findSavingByAccountNumber(accountNumber);
	}

	//Save passive
	@CircuitBreaker(name = "passive", fallbackMethod = "fallBackGetSaving")
	@PostMapping(value = "/saveSaving")
	public Mono<PassiveSaving> saveSaving(@RequestBody PassiveSaving dataPassiveSaving){
		Mono.just(dataPassiveSaving).doOnNext(t -> {

					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataPassiveSaving).onErrorResume(e -> Mono.just(dataPassiveSaving))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<PassiveSaving> passiveMono = passiveSavingService.saveSaving(dataPassiveSaving);
		return passiveMono;
	}

	//Update passive
	@CircuitBreaker(name = "passive", fallbackMethod = "fallBackGetSaving")
	@PutMapping("/updateSaving/{accountNumber}")
	public ResponseEntity<Mono<?>> updateSaving(@PathVariable("accountNumber") String accountNumber,
												@Valid @RequestBody PassiveSaving dataPassiveSaving) {
		Mono.just(dataPassiveSaving).doOnNext(t -> {
					dataPassiveSaving.setAccountNumber(accountNumber);
					t.setModificationDate(new Date());

				}).onErrorReturn(dataPassiveSaving).onErrorResume(e -> Mono.just(dataPassiveSaving))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<PassiveSaving> passiveMono = passiveSavingService.updateSaving(dataPassiveSaving);

		if (passiveMono != null) {
			return new ResponseEntity<>(passiveMono, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(Mono.just(new PassiveSaving()), HttpStatus.I_AM_A_TEAPOT);
	}

	//Delete passive
	@CircuitBreaker(name = "passive", fallbackMethod = "fallBackGetSaving")
	@DeleteMapping("/deleteSaving/{accountNumber}")
	public ResponseEntity<Mono<Void>> deleteSaving(@PathVariable("accountNumber") String accountNumber) {
		LOGGER.info("Deleting passive product by accountNumber: " + accountNumber);
		Mono<Void> delete = passiveSavingService.deleteSaving(accountNumber);
		return ResponseEntity.noContent().build();
	}

	//get balance of an Passive Product
	@GetMapping("/balanceOfPassiveSaving/{accountNumber}")
		public Double balanceOfPassive(@PathVariable("accountNumber") String accountNumber) {
		//LOGGER.info("Balance of passiveAccount " + accountNumber +":");
		Mono<PassiveSaving> pasiveProductMono= findBySavingAccountNumber(accountNumber);
			return pasiveProductMono.block().getBalance();

	}

	//Save Personal Customer By Passive -savings-account
	@PostMapping(value = "/savePersonalCustomerByPassiveSaving")
	public Mono<PassiveSaving> savePersonalCustomerByPassiveSaving(@Valid @RequestBody PassiveSaving dataPassiveSaving){

		Mono.just(dataPassiveSaving).doOnNext(t -> {

					t.setTypeCustomer("PERSONAL");
					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataPassiveSaving).onErrorResume(e -> Mono.just(dataPassiveSaving))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<PassiveSaving> newCustomer = passiveSavingService.saveSavingPersonalCustomerByPassive(dataPassiveSaving);
		return newCustomer;
	}

	//Save Personal Customer By Passive -savings-account
	/*@PostMapping(value = "/savePersonalCustomerByPassive-current-account")
	public Mono<PassiveSaving> savePersonalCustomerByPassiveCurrentAccount(@Valid @RequestBody PassiveSaving dataPassiveSaving){

		Mono.just(dataPassiveSaving).doOnNext(t -> {

					t.setTypeCustomer("PERSONAL");
					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataPassiveSaving).onErrorResume(e -> Mono.just(dataPassiveSaving))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<PassiveSaving> newCustomer = passiveSavingService.saveSavingPersonalCustomerByPassive(dataPassiveSaving );
		return newCustomer;
	}

	//Save Personal Customer By Passive -savings-account
	@PostMapping(value = "/savePersonalCustomerByPassive-fixed-term")
	public Mono<PassiveSaving> savePersonalCustomerByPassiveFixedTerm(@Valid @RequestBody PassiveSaving dataPassiveSaving){

		Mono.just(dataPassiveSaving).doOnNext(t -> {

					t.setTypeCustomer("PERSONAL");
					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataPassiveSaving).onErrorResume(e -> Mono.just(dataPassiveSaving))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<PassiveSaving> newCustomer = passiveSavingService.saveSavingPersonalCustomerByPassive(dataPassiveSaving);
		return newCustomer;
	}*/

	//Save Business Customer By Passive -savings-account
	@PostMapping(value = "/saveBusinessCustomerByPassive-savings-account")
	public Mono<PassiveSaving> saveBusinessCustomerByPassiveSavingsAccount(@Valid @RequestBody PassiveSaving dataPassiveSaving){

		Mono.just(dataPassiveSaving).doOnNext(t -> {

					t.setTypeCustomer("EMPRESARIAL");
					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataPassiveSaving).onErrorResume(e -> Mono.just(dataPassiveSaving))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<PassiveSaving> newCustomer = passiveSavingService.saveSavingPersonalCustomerByPassive(dataPassiveSaving);
		return newCustomer;
	}

	//Save Business Customer By Passive -savings-account
	/*@PostMapping(value = "/saveBusinessCustomerByPassive-current-account")
	public Mono<PassiveSaving> saveBusinessCustomerByPassiveCurrentAccount(@Valid @RequestBody PassiveSaving dataPassiveSaving){

		Mono.just(dataPassiveSaving).doOnNext(t -> {

					t.setTypeCustomer("EMPRESARIAL");
					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataPassiveSaving).onErrorResume(e -> Mono.just(dataPassiveSaving))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<PassiveSaving> newCustomer = passiveSavingService.saveSavingPersonalCustomerByPassive(dataPassiveSaving);
		return newCustomer;
	}

	//Save Business Customer By Passive -savings-account
	@PostMapping(value = "/saveBusinessCustomerByPassive-fixed-term")
	public Mono<PassiveSaving> saveBusinessCustomerByPassiveFixedTerm(@Valid @RequestBody PassiveSaving dataPassiveSaving){

		Mono.just(dataPassiveSaving).doOnNext(t -> {

					t.setTypeCustomer("EMPRESARIAL");
					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataPassiveSaving).onErrorResume(e -> Mono.just(dataPassiveSaving))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<PassiveSaving> newCustomer = passiveSavingService.saveSavingPersonalCustomerByPassive(dataPassiveSaving);
		return newCustomer;
	}*/
	private Mono<PassiveSaving> fallBackGetCurrent(Exception e){
		PassiveSaving activeStaff= new PassiveSaving();
		Mono<PassiveSaving> staffMono= Mono.just(activeStaff);
		return staffMono;
	}
}
