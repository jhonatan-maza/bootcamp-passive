package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.PassiveFixedTerm;
import com.nttdata.bootcamp.service.PassiveFixedTermService;
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

@CrossOrigin(origins = "*")
@RestController
@RequestMapping(value = "/fixedTerm")
public class PassiveFixedTermController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PassiveFixedTermController.class);
	@Autowired
	private PassiveFixedTermService passiveFixedTermService;

	//Passives search
	@GetMapping("/findAllPassivesSaving")
	public Flux<PassiveFixedTerm> findAllPassivesFixedTerm() {
		Flux<PassiveFixedTerm> actives = passiveFixedTermService.findAllFixedTerm();
		LOGGER.info("Registered Passives Products: " + actives);
		return actives;
	}

	//Passives search by customer
	@GetMapping("/findAllPassivesSavingByCustomer/{dni}")
	public Flux<PassiveFixedTerm> findAllPassivesFixedTermByCustomer(@PathVariable("dni") String dni) {
		Flux<PassiveFixedTerm> passives = passiveFixedTermService.findFixedTermByCustomer(dni);
		LOGGER.info("Registered Actives Products by customer of dni: "+dni +"-" + passives);
		return passives;
	}

	//Search for passive by AccountNumber
	@CircuitBreaker(name = "passive", fallbackMethod = "fallBackGetFixedTerm")
	@GetMapping("/findBySavingAccountNumber/{accountNumber}")
	public Mono<PassiveFixedTerm> findByFixedTermAccountNumber(@PathVariable("accountNumber") String accountNumber) {
		LOGGER.info("Searching passives product by accountNumber: " + accountNumber);
		return passiveFixedTermService.findFixedTermByAccountNumber(accountNumber);
	}

	//Save passive
	@CircuitBreaker(name = "passive", fallbackMethod = "fallBackGetFixedTerm")
	@PostMapping(value = "/saveSaving")
	public Mono<PassiveFixedTerm> saveFixedTerm(@RequestBody PassiveFixedTerm dataPassiveSaving){
		Mono.just(dataPassiveSaving).doOnNext(t -> {

					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataPassiveSaving).onErrorResume(e -> Mono.just(dataPassiveSaving))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<PassiveFixedTerm> passiveMono = passiveFixedTermService.saveFixedTerm(dataPassiveSaving);
		return passiveMono;
	}

	//Update passive
	@CircuitBreaker(name = "passive", fallbackMethod = "fallBackGetFixedTerm")
	@PutMapping("/updateFixedTerm/{accountNumber}")
	public ResponseEntity<Mono<?>> updateFixedTerm(@PathVariable("accountNumber") String accountNumber,
												@Valid @RequestBody PassiveFixedTerm dataPassiveSaving) {
		Mono.just(dataPassiveSaving).doOnNext(t -> {
					dataPassiveSaving.setAccountNumber(accountNumber);
					t.setModificationDate(new Date());

				}).onErrorReturn(dataPassiveSaving).onErrorResume(e -> Mono.just(dataPassiveSaving))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<PassiveFixedTerm> passiveMono = passiveFixedTermService.updateFixedTerm(dataPassiveSaving);

		if (passiveMono != null) {
			return new ResponseEntity<>(passiveMono, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(Mono.just(new PassiveFixedTerm()), HttpStatus.I_AM_A_TEAPOT);
	}

	//Delete passive
	@CircuitBreaker(name = "passive", fallbackMethod = "fallBackGetFixedTerm")
	@DeleteMapping("/deleteFixedTerm/{accountNumber}")
	public ResponseEntity<Mono<Void>> deleteFixedTerm(@PathVariable("accountNumber") String accountNumber) {
		LOGGER.info("Deleting passive product by accountNumber: " + accountNumber);
		Mono<Void> delete = passiveFixedTermService.deleteFixedTerm(accountNumber);
		return ResponseEntity.noContent().build();
	}

	//get balance of an Passive Product
	@GetMapping("/balanceOfPassiveFixedTerm/{accountNumber}")
		public Double balanceOfPassive(@PathVariable("accountNumber") String accountNumber) {
		//LOGGER.info("Balance of passiveAccount " + accountNumber +":");
		Mono<PassiveFixedTerm> pasiveProductMono= findByFixedTermAccountNumber(accountNumber);
			return pasiveProductMono.block().getBalance();

	}

	//Save Personal Customer By Passive -savings-account
	@PostMapping(value = "/savePersonalCustomerByPassiveFixedTerm")
	public Mono<PassiveFixedTerm> savePersonalCustomerByPassiveFixedTerm(@Valid @RequestBody PassiveFixedTerm dataPassiveSaving){

		Mono.just(dataPassiveSaving).doOnNext(t -> {

					t.setTypeCustomer("PERSONAL");
					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataPassiveSaving).onErrorResume(e -> Mono.just(dataPassiveSaving))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<PassiveFixedTerm> newCustomer = passiveFixedTermService.saveFixedTermPersonalCustomerByPassive(dataPassiveSaving);
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
	@PostMapping(value = "/saveBusinessCustomerByPassiveFixedTerm")
	public Mono<PassiveFixedTerm> saveBusinessCustomerByPassiveFixedTerm(@Valid @RequestBody PassiveFixedTerm dataPassiveSaving){

		Mono.just(dataPassiveSaving).doOnNext(t -> {

					t.setTypeCustomer("EMPRESARIAL");
					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataPassiveSaving).onErrorResume(e -> Mono.just(dataPassiveSaving))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<PassiveFixedTerm> newCustomer = passiveFixedTermService.saveFixedTermPersonalCustomerByPassive(dataPassiveSaving);
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

	private Mono<PassiveFixedTerm> fallBackGetCurrent(Exception e){
		PassiveFixedTerm activeStaff= new PassiveFixedTerm();
		Mono<PassiveFixedTerm> staffMono= Mono.just(activeStaff);
		return staffMono;
	}

}
