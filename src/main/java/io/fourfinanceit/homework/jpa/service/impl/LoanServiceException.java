package io.fourfinanceit.homework.jpa.service.impl;

public class LoanServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	public enum LoanServiceExceptionType {
		exceeds_maximum_possible_amount(
				"Attempted amount exceeds maximum possible amount"), exceeds_maximum_possible_term(
				"Attempted amount exceeds maximum possible term"), exceeds_maximum_attempts_count(
				"Client exceeds maximum possible attempts count"), disabled_time(
				"Attemt is made between 00:00 to 6:00 AM with maximum possible amount");

		private String msg = "";

		LoanServiceExceptionType(String msg) {
			this.msg = msg;
		}

		public LoanServiceException getException() {
			return new LoanServiceException(msg);
		}
	}

	public LoanServiceException(String msg) {
		super(msg);
	}

	public LoanServiceException(Exception e) {
		super(e);
	}
}
