package com.aninfo;

import com.aninfo.model.Account;
import com.aninfo.model.Transaction;
import com.aninfo.model.Deposit;
import com.aninfo.model.Withdraw;
import com.aninfo.service.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@SpringBootApplication
@EnableSwagger2
public class Memo1BankApp {

	@Autowired
	private AccountService accountService;

	public static void main(String[] args) {
		SpringApplication.run(Memo1BankApp.class, args);
	}

	@PostMapping("/accounts")
	@ResponseStatus(HttpStatus.CREATED)
	public Account createAccount(@RequestBody Account account) {
		return accountService.createAccount(account);
	}

	@GetMapping("/accounts")
	public Collection<Account> getAccounts() {
		return accountService.getAccounts();
	}

	@GetMapping("/accounts/{cbu}")
	public ResponseEntity<Account> getAccount(@PathVariable Long cbu) {
		Optional<Account> accountOptional = accountService.findById(cbu);
		return ResponseEntity.of(accountOptional);
	}

	@PutMapping("/accounts/{cbu}")
	public ResponseEntity<Account> updateAccount(@RequestBody Account account, @PathVariable Long cbu) {
		Optional<Account> accountOptional = accountService.findById(cbu);

		if (!accountOptional.isPresent()) {
			return ResponseEntity.notFound().build();
		}
		account.setCbu(cbu);
		accountService.save(account);
		return ResponseEntity.ok().build();
	}

	@DeleteMapping("/accounts/{cbu}")
	public void deleteAccount(@PathVariable Long cbu) {
		accountService.deleteById(cbu);
	}

	@PutMapping("/accounts/{cbu}/withdraw")
	public Account withdraw(@PathVariable Long cbu, @RequestParam Double sum) {
		return accountService.withdraw(cbu, sum);
	}

	@PutMapping("/accounts/{cbu}/deposit")
	public Account deposit(@PathVariable Long cbu, @RequestParam Double sum) {
		return accountService.deposit(cbu, sum);
	}

	@Bean
	public Docket apiDocket() {
		return new Docket(DocumentationType.SWAGGER_2)
			.select()
			.apis(RequestHandlerSelectors.any())
			.paths(PathSelectors.any())
			.build();
	}

	@GetMapping("/accounts/{cbu}/transactions")
	public ResponseEntity<List<String>> getAccountTransactions(@PathVariable Long cbu) {
		Optional<Account> accountOptional = accountService.findById(cbu);

		if (accountOptional.isPresent()) {
			List<String> transactionDetails = new ArrayList<>();

			Account account = accountOptional.get();
			List<Transaction> transactions = account.getTransactions();

			for (Transaction transaction : transactions) {
				String transactionType = "";
				if (transaction instanceof Deposit) {
					transactionType = "Deposit";
				} else if (transaction instanceof Withdraw) {
					transactionType = "Withdraw";
				}

				transactionDetails.add(transactionType + " - ID: " + transaction.getTransactionId() + ", Amount: " + transaction.getAmount());
			}

			return ResponseEntity.ok(transactionDetails);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping("/{accountId}/transactions/{transactionId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteTransaction(
			@PathVariable Long accountId,
			@PathVariable Long transactionId
	) {
		accountService.deleteTransaction(accountId, transactionId);
	}

}
