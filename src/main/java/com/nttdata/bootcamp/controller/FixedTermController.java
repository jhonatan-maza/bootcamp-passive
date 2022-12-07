package com.nttdata.bootcamp.controller;

import com.nttdata.bootcamp.entity.Passive;
import com.nttdata.bootcamp.entity.dto.FixedTermDto;
import com.nttdata.bootcamp.entity.dto.UpdateFixedTermDto;
import com.nttdata.bootcamp.service.FixedTermService;
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
@RequestMapping(value = "/fixedTerm")
public class FixedTermController {

	private static final Logger LOGGER = LoggerFactory.getLogger(FixedTermController.class);
	@Autowired
	private FixedTermService fixedTermService;

	//All FixedTerm Products Registered
	@GetMapping("/findAllFixedTerm")
	public Flux<Passive> findAllFixedTerm() {
		Flux<Passive> passives = fixedTermService.findAllFixedTerm();
		LOGGER.info("All FixedTerm Account Products Registered: " + passives);
		return passives;
	}

	//FixedTerm Products Registered by customer dni
	@GetMapping("/findFixedTermByDni/{dni}")
	public Flux<Passive> findFixedTermByDni(@PathVariable("dni") String dni) {
		Flux<Passive> passives = fixedTermService.findFixedTermByCustomer(dni);
		LOGGER.info("FixedTerm Products Registered by customer dni: "+dni +"-" + passives);
		return passives;
	}

	//Search FixedTerm by AccountNumber
	//@CircuitBreaker(name = "passive", fallbackMethod = "fallBackGetFixedTerm")
	@GetMapping("/findFixedTermByAccountNumber/{accountNumber}")
	public Mono<Passive> findFixedTermByAccountNumber(@PathVariable("accountNumber") String accountNumber) {
		LOGGER.info("Searching FixedTerm Products by customer accountNumber: " + accountNumber);
		return fixedTermService.findFixedTermByAccountNumber(accountNumber);
	}

	//Save FixedTerm
	//@CircuitBreaker(name = "passive", fallbackMethod = "fallBackGetFixedTerm")
	@PostMapping(value = "/saveFixedTerm")
	public Mono<Passive> saveFixedTerm(@RequestBody FixedTermDto account){

		Passive dataFixedTerm = new Passive();
		Mono.just(dataFixedTerm).doOnNext(t -> {
					t.setDni(account.getDni());
					t.setTypeCustomer(account.getTypeCustomer());
					t.setAccountNumber(account.getAccountNumber());
					t.setStatus(Constant.PASSIVE_ACTIVE);
					t.setCreationDate(new Date());
					t.setModificationDate(new Date());
				}).onErrorReturn(dataFixedTerm).onErrorResume(e -> Mono.just(dataFixedTerm))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Passive> passiveMono = fixedTermService.saveFixedTerm(dataFixedTerm);
		return passiveMono;
	}

	//Update FixedTerm
	//@CircuitBreaker(name = "passive", fallbackMethod = "fallBackGetFixedTerm")
	@PutMapping("/updateFixedTerm/{accountNumber}")
	public Mono<Passive> updateFixedTerm(@PathVariable("accountNumber") String accountNumber,
											 @Valid @RequestBody UpdateFixedTermDto account) {

		Passive dataFixedTerm = new Passive();
		Mono.just(dataFixedTerm).doOnNext(t -> {
					t.setAccountNumber(accountNumber);
					t.setModificationDate(new Date());
				}).onErrorReturn(dataFixedTerm).onErrorResume(e -> Mono.just(dataFixedTerm))
				.onErrorMap(f -> new InterruptedException(f.getMessage())).subscribe(x -> LOGGER.info(x.toString()));

		Mono<Passive> updatePassive = fixedTermService.updateFixedTerm(dataFixedTerm);
		return updatePassive;
	}

	//Delete FixedTerm
	//@CircuitBreaker(name = "passive", fallbackMethod = "fallBackGetFixedTerm")
	@DeleteMapping("/deleteFixedTerm/{accountNumber}")
	public ResponseEntity<Mono<Void>> deleteFixedTerm(@PathVariable("accountNumber") String accountNumber) {
		LOGGER.info("Deleting FixedTerm product by accountNumber: " + accountNumber);
		Mono<Void> delete = fixedTermService.deleteFixedTerm(accountNumber);
		return ResponseEntity.noContent().build();
	}

	private Mono<Passive> fallBackGetFixedTerm(Exception e){
		Passive passive= new Passive();
		Mono<Passive> staffMono= Mono.just(passive);
		return staffMono;
	}

}
