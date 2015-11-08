package io.fourfinanceit.homework.jpa.service.api;

import io.fourfinanceit.homework.jpa.domain.Attempt;
import io.fourfinanceit.homework.jpa.service.impl.LoanServiceException;

public interface LoanService {

	Attempt create(String name, Double amount, Integer term,
			String string);

	void createLoan(Attempt attempt) throws LoanServiceException;

}
