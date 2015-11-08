package io.fourfinanceit.homework.web.beans;

import static org.junit.Assert.assertEquals;
import io.fourfinanceit.homework.Application;
import io.fourfinanceit.homework.jpa.domain.Attempt;
import io.fourfinanceit.homework.jpa.repositorires.AttemptRepository;
import io.fourfinanceit.homework.jpa.service.api.LoanService;
import io.fourfinanceit.homework.jpa.service.impl.LoanServiceException;

import java.util.Calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class LoanServiceIntegrationTests {
	@Autowired
	AttemptRepository attemptRepository;

	@Autowired
	LoanService service;

	@Value("${maximum.possible.amount}")
	private Double maximumPossibleAmount;

	@Test
	public void testCount() {
		service.create("test1", 1.0, 1, "123.123.123.123");
		service.create("test2", 1.0, 1, "123.123.123.123");
		service.create("test3", 1.0, 1, "123.123.123.123");

		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.HOUR_OF_DAY, -24);

		Long cnt = attemptRepository.countByIpAndRequestDateAfter(
				"123.123.123.123", calendar.getTime());
		System.out.println("cnt :" + cnt);
		assertEquals(cnt.longValue(), 3l);
	}

	@Test(expected = LoanServiceException.class)
	public void testMaxAmount() throws LoanServiceException {
		Attempt attempt = new Attempt("test3", maximumPossibleAmount + 1, 1,
				"111.111.111.111");
		service.createLoan(attempt);
	}

	@Test(expected = LoanServiceException.class)
	public void testTime() throws LoanServiceException {
		Attempt attempt = new Attempt("test3", maximumPossibleAmount, 1,
				"222.222.222.222");
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 4);
		calendar.set(Calendar.MINUTE, 30);
		attempt.setRequestDate(calendar.getTime());
		service.createLoan(attempt);
	}
}
