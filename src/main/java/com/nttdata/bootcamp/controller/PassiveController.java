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
@RequestMapping(value = "/customer")
public class PassiveController {

	private static final Logger LOGGER = LoggerFactory.getLogger(PassiveController.class);
	@Autowired
	private PassiveService passiveService;

	//Customer search
	@GetMapping("/")
	public Flux<Passive> findAllCustomers() {
		Flux<Passive> customers = passiveService.findAll();
		LOGGER.info("Registered Customers: " + customers);
		return customers;
	}

	//Search for clients by DNI
	@GetMapping("/findByClient/{dni}")
	public Mono<Passive> findByClientDNI(@PathVariable("dni") String dni) {
		LOGGER.info("Searching client by DNI: " + dni);
		return passiveService.findByDni(dni);
	}

	//Save customer
	@PostMapping(value = "/save")
	public Mono<Passive> saveCustomer(@RequestBody Passive dataPassive){
		Mono.just(dataPassive).doOnNext(t -> {

					t.setCreationDate(new Date());
					t.setModificationDate(new Date());

				}).onErrorReturn(dataPassive).onErrorResume(e -> Mono.just(dataPassive))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Passive> newCustomer = passiveService.save(dataPassive);
		return newCustomer;
	}

	//Update customer
	@PutMapping("/update/{dni}")
	public ResponseEntity<Mono<?>> updateCustomer(@PathVariable("dni") String dni,
													   @Valid @RequestBody Passive dataPassive) {
		Mono.just(dataPassive).doOnNext(t -> {
					dataPassive.setDni(dni);
					t.setModificationDate(new Date());

				}).onErrorReturn(dataPassive).onErrorResume(e -> Mono.just(dataPassive))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Passive> pAsset = passiveService.update(dataPassive);

		if (pAsset != null) {
			return new ResponseEntity<>(pAsset, HttpStatus.CREATED);
		}
		return new ResponseEntity<>(Mono.just(new Passive()), HttpStatus.I_AM_A_TEAPOT);
	}

	//Delete customer
	@DeleteMapping("/delete/{dni}")
	public ResponseEntity<Mono<Void>> deleteCustomer(@PathVariable("dni") String dni) {
		LOGGER.info("Deleting client by DNI: " + dni);
		Mono<Void> delete = passiveService.delete(dni);
		return ResponseEntity.noContent().build();
	}

}
