package io.fourfinanceit.homework.controller;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import io.fourfinanceit.homework.jpa.service.api.LoanService;
import io.fourfinanceit.homework.web.beans.Response;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {

	private static final Logger logger = Logger.getLogger(ApiController.class);
	@Autowired
	private LoanService loanService;

	@RequestMapping(value = "/create", method = GET)
	@ResponseBody
	public Response create(@RequestParam("name") String name,
			@RequestParam("amount") Double amount,
			@RequestParam("term") Integer term, HttpServletRequest request) {
		Response response = new Response();
		try {
			response.setResponse(loanService.create(name, amount, term,
					request.getRemoteAddr()));
			response.setResponseCode("200");
			response.setMessage("OK");
		} catch (RuntimeException e) {
			logger.error("Exception on create ", e);
			response.setResponseCode("500");
			response.setMessage(e.getMessage());
		}

		return response;
	}

}
