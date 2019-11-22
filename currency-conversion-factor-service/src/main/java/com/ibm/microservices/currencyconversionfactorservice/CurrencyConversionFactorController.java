package com.ibm.microservices.currencyconversionfactorservice;

import java.net.URI;
import java.util.Optional;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

@RestController
public class CurrencyConversionFactorController {

	@Autowired
	private Environment environment;
	
	@Autowired
	private ExchangeValueRepository repository;
	
	@PostMapping("/currency-conversion-factor")
	public ResponseEntity<Object> addConversionFactor(@RequestBody ExchangeValue exchangeValue) {		
		Optional<ExchangeValue> existingIdExchangeValue = repository.findById(exchangeValue.getId());
		System.out.println(" Found ------ "+existingIdExchangeValue);
		if(existingIdExchangeValue.isPresent()) {
			throw new ConversionFactorExistsException("Id already exists !! ");
		}
		ExchangeValue existingExchangeValue = repository.findByFromAndTo(exchangeValue.getFrom(), exchangeValue.getTo());		
		if(existingExchangeValue==null) {
			repository.save(exchangeValue);
			URI location =  ServletUriComponentsBuilder
					.fromCurrentRequest()
					.path("/from/{from}/to/{to}")
					.buildAndExpand(exchangeValue.getFrom(),exchangeValue.getTo()).toUri();
				
				return ResponseEntity.created(location).build();
		}
		throw new ConversionFactorExistsException("Conversion factor from "+exchangeValue.getFrom()+" to "+exchangeValue.getTo()+" already exists !!");
		
	}
	
	@PostMapping("/update-currency-conversion-factor")
	public ExchangeValue updateConversionFactor(@RequestBody ExchangeValue exchangeValue) {		
		ExchangeValue existingExchangeValue = repository.findByFromAndTo(exchangeValue.getFrom(), exchangeValue.getTo());		
		if(existingExchangeValue==null) {
			throw new ConversionFactorNotFoundException("Conversion factor doenst exists !!");
		}
		if(existingExchangeValue.getId().longValue()!=exchangeValue.getId().longValue()) {
			throw new ConversionFactorNotFoundException("Conversion factor Id mismatch !!");
		}
		repository.save(exchangeValue);
		return exchangeValue;
	}
	
	
	@GetMapping("/currency-conversion-factor/from/{from}/to/{to}")
	public ExchangeValue getConversionFactor(@PathVariable String from, @PathVariable String to) {
		ExchangeValue exchangeValue = repository.findByFromAndTo(from, to);
		exchangeValue.setPort(Integer.parseInt(environment.getProperty("local.server.port")));
		return exchangeValue;
	}
	
	@GetMapping("/test")
	@HystrixCommand(fallbackMethod = "myFallbackMethod")
	public @ResponseBody String test() {
		if (new Random().nextBoolean()) {
			return "Everything Working Fine";
		} else {
			throw new RuntimeException();
		}
	}
	
	public String myFallbackMethod() {
		return "Fallback Enabled. Normal flow selected !!";
	}
	
	
}
