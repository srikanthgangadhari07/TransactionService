package com.mybank.TransactionService.Controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.mybank.TransactionService.Model.Transaction;
import com.mybank.TransactionService.Service.TransactionServiceService;
import com.mybank.TransactionService.dto.TransactionDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/transaction")
public class TransactionServiceController {

	@Autowired
	TransactionServiceService transactionServiceService;

	@PostMapping("/deposit")
	@Operation(
	        summary = "Deposit amount",
	        description = "Deposit amount to a specific account.",
	        security = @SecurityRequirement(name = "bearerAuth")
	    )
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Deposit successful"),
	    @ApiResponse(responseCode = "400", description = "Bad request, invalid input"),
	    @ApiResponse(responseCode = "404", description = "Account not found"),
	    @ApiResponse(responseCode = "500", description = "Internal server error")
	})
	public ResponseEntity<TransactionDto> depositsAmount(@RequestBody TransactionDto transactionDto, @RequestHeader(value = "Authorization", required = false) String authHeader) throws Exception {
		String token = authHeader.replace("Bearer ", "");
		return ResponseEntity.ok(transactionServiceService.deposit(transactionDto.getAccountId(), transactionDto.getAmount(), token));
	}

	@PostMapping("/withdraw")
	@Operation(
	        summary = "Withdraw amount",
	        description = "Withdraw amount from a specific account.",
	        security = @SecurityRequirement(name = "bearerAuth")
	    )
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Withdrawal successful"),
	    @ApiResponse(responseCode = "400", description = "Bad request, invalid input"),
	    @ApiResponse(responseCode = "404", description = "Account not found"),
	    @ApiResponse(responseCode = "500", description = "Internal server error")
	})
	public ResponseEntity<TransactionDto> withdraAmount(@RequestBody TransactionDto transactionDto, @RequestHeader(value = "Authorization", required = false) String authHeader) throws Exception {
		String token = authHeader.replace("Bearer ", "");
		return ResponseEntity.ok(transactionServiceService.withdraw(transactionDto.getAccountId(), transactionDto.getAmount(), token));
	}

	@PostMapping("/transfer")
	@Operation(
	        summary = "Transfer amount",
	        description = "Transfer amount to a specific account.",
	        security = @SecurityRequirement(name = "bearerAuth")
	    )
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Transfer successful"),
	    @ApiResponse(responseCode = "400", description = "Bad request, invalid input"),
	    @ApiResponse(responseCode = "404", description = "Account not found"),
	    @ApiResponse(responseCode = "500", description = "Internal server error")
	})
	public ResponseEntity<TransactionDto> transferAmount(@RequestBody TransactionDto transactionDto, @RequestHeader(value = "Authorization", required = false) String authHeader) throws Exception {
		String token = authHeader.replace("Bearer ", "");
		return ResponseEntity.ok(transactionServiceService.transfer(transactionDto.getFromAccountId(), transactionDto.getToAccountId(), transactionDto.getAmount(), token));
	}

	@GetMapping("/{accountId}/getTransactions")
	@Operation(summary = "Get Transactions for Account")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
	    @ApiResponse(responseCode = "404", description = "Account not found"),
	    @ApiResponse(responseCode = "500", description = "Internal server error")
	})
	public ResponseEntity<List<Transaction>> getTransactions(@PathVariable Long accountId) {
		return ResponseEntity.ok(transactionServiceService.gettransactions(accountId));
	}

	@GetMapping("/{accountId}/transactions")
	@Operation(summary = "Get Transactions Between Dates")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
	    @ApiResponse(responseCode = "400", description = "Bad request, invalid date format"),
	    @ApiResponse(responseCode = "404", description = "Account not found"),
	    @ApiResponse(responseCode = "500", description = "Internal server error")
	})
	public ResponseEntity<List<Transaction>> getTransactions(@PathVariable Long accountId, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate, @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
		return ResponseEntity.ok(transactionServiceService.gettransactionsBetweenDates(accountId, startDate, endDate));
	}

	@DeleteMapping("/{accountId}")
	@Operation(summary = "Delete Transactions for Account")
	@ApiResponses(value = {
	    @ApiResponse(responseCode = "204", description = "Transactions deleted successfully"),
	    @ApiResponse(responseCode = "404", description = "Account not found"),
	    @ApiResponse(responseCode = "500", description = "Internal server error")
	})
	public ResponseEntity<Boolean> deleteTransactions(@PathVariable Long accountId) {
		transactionServiceService.deleteTransactions(accountId);
		return ResponseEntity.noContent().build();
	}
}