package com.ibm.microservices.currencyconversionfactorservice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ConversionFactorNotFoundException  extends RuntimeException {
	public ConversionFactorNotFoundException(String messsage) {
		super(messsage);
	}
}
