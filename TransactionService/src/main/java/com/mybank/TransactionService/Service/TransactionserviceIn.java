package com.mybank.TransactionService.Service;

import com.mybank.TransactionService.dto.TransactionDto;

public interface TransactionserviceIn {

	TransactionDto deposit(Long accountId, Double amount,String jwtToken) throws Exception;
	TransactionDto withdraw(Long accountId, Double amount,String jwtToken) throws Exception;
	TransactionDto transfer(Long sourceAccountId, Long targetAccountId, Double amount,String jwtToken) throws Exception;
//    List<TransactionDto> getTransactionHistory(Long accountId, int page, int size, String sort);
}
