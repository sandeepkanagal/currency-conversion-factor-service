package com.ibm.microservices.currencyconversionfactorservice;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FOUND)
public class ConversionFactorExistsException  extends RuntimeException {
	public ConversionFactorExistsException(String messsage) {
		super(messsage);
	}
}
