package io.fourfinanceit.homework.jpa.service.impl;

import java.util.Calendar;

import io.fourfinanceit.homework.jpa.domain.Attempt;
import io.fourfinanceit.homework.jpa.domain.Loan;
import io.fourfinanceit.homework.jpa.repositorires.AttemptRepository;
import io.fourfinanceit.homework.jpa.service.api.LoanService;
import io.fourfinanceit.homework.jpa.service.impl.LoanServiceException.LoanServiceExceptionType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class LoanServiceImpl implements LoanService {
	@Autowired
	private AttemptRepository attemptRepository;

	@Value("${maximum.possible.amount}")
	private Double maximumPossibleAmount;

	@Value("${maximum.possible.term}")
	private Integer maximumPossibleTerm;

	@Value("${attempts.maximum.count}")
	private Long attemptsMaximumCount;

	@Value("${attempts.disabled.from}")
	private int from;

	@Value("${attempts.disabled.to}")
	private int to;

	@Value("${interest.rate}")
	private double interestRate;

	public Attempt create(String name, Double amount, Integer term, String ip) {

		Attempt attempt = new Attempt(name, amount, term, ip);
		boolean rejected = false;
		String rejectedReason = null;

		try {
			createLoan(attempt);
		} catch (LoanServiceException exception) {
			rejected = true;
			rejectedReason = exception.getMessage();
		}

		attempt.setRejected(rejected);
		attempt.setRejectedReason(rejectedReason);
		return attemptRepository.save(attempt);
	}

	public void createLoan(Attempt attempt) throws LoanServiceException {
		if (attempt.getAmount() > maximumPossibleAmount
				|| attempt.getAmount() <= 0)
			throw LoanServiceExceptionType.exceeds_maximum_possible_amount
					.getException();
		if (attempt.getTerm() > maximumPossibleTerm || attempt.getTerm() <= 0)
			throw LoanServiceExceptionType.exceeds_maximum_possible_term
					.getException();

		// count attempts in last 24 hours
		// TODO: count not rejected?
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(attempt.getRequestDate());
		// time HHMM format between 0 and 2400
		int time = calendar.get(Calendar.HOUR_OF_DAY) * 100
				+ calendar.get(Calendar.MINUTE);

		calendar.add(Calendar.HOUR_OF_DAY, -24);

		Long prevAttempts = attemptRepository.countByIpAndRequestDateAfter(
				attempt.getIp(), calendar.getTime());

		if (prevAttempts > attemptsMaximumCount)
			throw LoanServiceExceptionType.exceeds_maximum_attempts_count
					.getException();

		// attemt is made between 00:00 to 6:00 AM with maximum possible amount.
		// TODO: have to use the server or the client time

		if (time >= from && time <= to
				&& attempt.getAmount().equals(maximumPossibleAmount))
			throw LoanServiceExceptionType.disabled_time.getException();

		Loan loan = new Loan();
		calendar.add(Calendar.HOUR_OF_DAY, 24);
		loan.setFrom(calendar.getTime());

		calendar.add(Calendar.MONTH, attempt.getTerm());
		loan.setTo(calendar.getTime());

		loan.setMonthlyPayment(getMonthlyPayment(attempt));

		attempt.setLoan(loan);

	}

	private Double getMonthlyPayment(Attempt attempt) {
		// annuity payment
		double monthInterestRate = interestRate / 12.0;
		return monthInterestRate * attempt.getAmount()
				/ (1.0 - Math.pow(1.0 + monthInterestRate, -attempt.getTerm()));
	}
}
