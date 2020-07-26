package com.github.nibroc.healthcheckdemo.controller;

import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private enum Health {
		OK, FAILURE, TIMEOUT
	}

	private AtomicInteger count = new AtomicInteger(0);
	private Health health = Health.OK;

	@GetMapping(path = "/health", produces = "application/json")
	public ResponseEntity<String> health() throws InterruptedException {
		final int cnt = count.incrementAndGet();

		logger.info("Processing health check number {} with status {}", cnt, health);

		if (health == Health.TIMEOUT) {
			Thread.sleep(30000);
		}

		if (health == Health.FAILURE) {
			return new ResponseEntity<>("{\"status\": \"fail\", \"count\": \"" + cnt + "\"}",
					HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return new ResponseEntity<>("{\"status\": \"ok\", \"count\": \"" + cnt + "\"}", HttpStatus.OK);
	}

	@GetMapping(path = "/health/fail")
	public void fail() {
		health = Health.FAILURE;
	}

	@GetMapping(path = "/health/timeout")
	public void timeout() {
		health = Health.TIMEOUT;
	}

	@GetMapping(path = "/health/ok")
	public void ok() {
		health = Health.OK;
	}
}
