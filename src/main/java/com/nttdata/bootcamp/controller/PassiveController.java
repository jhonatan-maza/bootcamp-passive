package com.nttdata.bootcamp.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.nttdata.bootcamp.service.PassiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.nttdata.bootcamp.entity.Passive;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Date;
import javax.validation.Valid;

@RestController
@RequestMapping(value = "/passive")
public class PassiveController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PassiveController.class);
	@Autowired
	private PassiveService passiveService;

	//Passives search
	@GetMapping("/")
	public Flux<Passive> findAllPassives() {
		Flux<Passive> actives = passiveService.findAll();
		LOGGER.info("Registered Passives Products: " + actives);
		return actives;
	}

	//Passives search by customer
	@GetMapping("/findAllPassivesByCustomer/{dni}")
	public Flux<Passive> findAllPassivesByCustomer(@PathVariable("dni") String dni) {
		Flux<Passive> passives = passiveService.findByCustomer(dni);
		LOGGER.info("Registered Actives Products by customer of dni: "+dni +"-" + passives);
		return passives;
	}

	//Search for passive by AccountNumber
	@GetMapping("/findByAccountNumber/{accountNumber}")
	public Mono<Passive> findByAccountNumber(@PathVariable("accountNumber") String accountNumber) {
		LOGGER.info("Searching passives product by accountNumber: " + accountNumber);
		return passiveService.findByAccountNumber(accountNumber);
	}

	//Save passive
	@PostMapping(value = "/save")
	public Mono<Passive> saveActive(@RequestBody Passive dataPassive){
		Mono.just(dataPassive).doOnNext(t -> {

					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataPassive).onErrorResume(e -> Mono.just(dataPassive))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Passive> passiveMono = passiveService.save(dataPassive);
		return passiveMono;
	}

	//Update passive
	@PutMapping("/update/{accountNumber}")
	public ResponseEntity<Mono<?>> updateActive(@PathVariable("accountNumber") String accountNumber,
												@Valid @RequestBody Passive dataPassive) {
		Mono.just(dataPassive).doOnNext(t -> {
					dataPassive.setAccountNumber(accountNumber);
					t.setModificationDate(new Date());

				}).onErrorReturn(dataPassive).onErrorResume(e -> Mono.just(dataPassive))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Passive> passiveMono = passiveService.update(dataPassive);

		if (passiveMono != null) {
			return new ResponseEntity<>(passiveMono, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(Mono.just(new Passive()), HttpStatus.I_AM_A_TEAPOT);
	}

	//Delete passive
	@DeleteMapping("/delete/{accountNumber}")
	public ResponseEntity<Mono<Void>> deleteCustomer(@PathVariable("accountNumber") String accountNumber) {
		LOGGER.info("Deleting passive product by accountNumber: " + accountNumber);
		Mono<Void> delete = passiveService.delete(accountNumber);
		return ResponseEntity.noContent().build();
	}

	//get balance of an Passive Product
	@GetMapping("/balanceOfPassive/{accountNumber}")
		public Double balanceOfPassive(@PathVariable("accountNumber") String accountNumber) {
		//LOGGER.info("Balance of passiveAccount " + accountNumber +":");
		Mono<Passive> pasiveProductMono= findByAccountNumber(accountNumber);
			return pasiveProductMono.block().getBalance();

	}

	//Save Personal Customer By Passive -savings-account
	@PostMapping(value = "/savePersonalCustomerByPassive-savings-account")
	public Mono<Passive> savePersonalCustomerByPassiveSavingsAccount(@Valid @RequestBody Passive dataPassive){

		Mono.just(dataPassive).doOnNext(t -> {

					t.setTypeCustomer("PERSONAL");
					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataPassive).onErrorResume(e -> Mono.just(dataPassive))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Passive> newCustomer = passiveService.savePersonalCustomerByPassive(dataPassive, "savings-account");
		return newCustomer;
	}

	//Save Personal Customer By Passive -savings-account
	@PostMapping(value = "/savePersonalCustomerByPassive-current-account")
	public Mono<Passive> savePersonalCustomerByPassiveCurrentAccount(@Valid @RequestBody Passive dataPassive){

		Mono.just(dataPassive).doOnNext(t -> {

					t.setTypeCustomer("PERSONAL");
					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataPassive).onErrorResume(e -> Mono.just(dataPassive))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Passive> newCustomer = passiveService.savePersonalCustomerByPassive(dataPassive, "current-account");
		return newCustomer;
	}

	//Save Personal Customer By Passive -savings-account
	@PostMapping(value = "/savePersonalCustomerByPassive-fixed-term")
	public Mono<Passive> savePersonalCustomerByPassiveFixedTerm(@Valid @RequestBody Passive dataPassive){

		Mono.just(dataPassive).doOnNext(t -> {

					t.setTypeCustomer("PERSONAL");
					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataPassive).onErrorResume(e -> Mono.just(dataPassive))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Passive> newCustomer = passiveService.savePersonalCustomerByPassive(dataPassive, "fixed-term");
		return newCustomer;
	}

	//Save Business Customer By Passive -savings-account
	@PostMapping(value = "/saveBusinessCustomerByPassive-savings-account")
	public Mono<Passive> saveBusinessCustomerByPassiveSavingsAccount(@Valid @RequestBody Passive dataPassive){

		Mono.just(dataPassive).doOnNext(t -> {

					t.setTypeCustomer("EMPRESARIAL");
					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataPassive).onErrorResume(e -> Mono.just(dataPassive))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Passive> newCustomer = passiveService.savePersonalCustomerByPassive(dataPassive, "savings-account");
		return newCustomer;
	}

	//Save Business Customer By Passive -savings-account
	@PostMapping(value = "/saveBusinessCustomerByPassive-current-account")
	public Mono<Passive> saveBusinessCustomerByPassiveCurrentAccount(@Valid @RequestBody Passive dataPassive){

		Mono.just(dataPassive).doOnNext(t -> {

					t.setTypeCustomer("EMPRESARIAL");
					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataPassive).onErrorResume(e -> Mono.just(dataPassive))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Passive> newCustomer = passiveService.savePersonalCustomerByPassive(dataPassive, "current-account");
		return newCustomer;
	}

	//Save Business Customer By Passive -savings-account
	@PostMapping(value = "/saveBusinessCustomerByPassive-fixed-term")
	public Mono<Passive> saveBusinessCustomerByPassiveFixedTerm(@Valid @RequestBody Passive dataPassive){

		Mono.just(dataPassive).doOnNext(t -> {

					t.setTypeCustomer("EMPRESARIAL");
					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataPassive).onErrorResume(e -> Mono.just(dataPassive))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Passive> newCustomer = passiveService.savePersonalCustomerByPassive(dataPassive, "fixed-term");
		return newCustomer;
	}

}
