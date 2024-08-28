package com.mybank.TransactionService.Service;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.mybank.TransactionService.Exception.BadRequestException;
import com.mybank.TransactionService.Exception.DataNotFoundException;
import com.mybank.TransactionService.Model.Transaction;
import com.mybank.TransactionService.Repository.TransactionServiceAccountRepository;
import com.mybank.TransactionService.Repository.TransactionServiceRepository;
import com.mybank.TransactionService.dto.AccountDto;
import com.mybank.TransactionService.dto.TransactionDto;

@Service
public class TransactionServiceService implements TransactionserviceIn {
	
	@Autowired
	TransactionServiceRepository transactionRepository;
	
	@Autowired
	RestTemplate restTemplate;

	@Autowired
	TransactionServiceAccountRepository accountRepository;
	public static final Logger LOGGER = LoggerFactory.getLogger(TransactionServiceService.class);

	@Override
	@Transactional
	public TransactionDto  deposit(Long accountId , Double amount,String jwtToken) throws Exception {
		LOGGER.info("In TransactionServiceService.deposite()");
		TransactionDto transactionDto = new TransactionDto();
		try {
			AccountDto account = checkAccountExist(accountId,jwtToken);
			account.setAmount(account.getAmount() +amount); 
			account = updateAccount(account,jwtToken);
			Transaction transaction=new Transaction();
		    transaction = saveTransaction(transaction,account,"DEPOSIT",amount);
		    sendEmailNotification(account.getEmail(),amount, transaction,jwtToken);
			BeanUtils.copyProperties(transaction, transactionDto);
			LOGGER.info("Out TransactionServiceService.deposite()");
		}catch(Exception e) {
			LOGGER.info("Exception occured during deposit");
			throw e;
		}

		return transactionDto;

	}

	@Override
	@Transactional
	public TransactionDto  withdraw(Long accountId , Double amount,String jwtToken) throws Exception {
		LOGGER.info("In TransactionServiceService.withdraw()");
		TransactionDto transactionDto = new TransactionDto();
		try {
			AccountDto account = checkAccountExist(accountId,jwtToken);
			balanceValidation(account,amount);
			account.setAmount(account.getAmount()-amount); 
			account = updateAccount(account,jwtToken);
			Transaction transaction=new Transaction();
			transaction = saveTransaction(transaction , account , "WITHDRAW", amount);
//			account.setAmount(transaction.getAmount()); 
//			accountRepository.save(account);
			sendEmailNotification(account.getEmail(),amount, transaction,jwtToken);
			BeanUtils.copyProperties(transaction, transactionDto);
//			transactionDto.setAmount(amount);
			LOGGER.info("Out TransactionServiceService.withdraw");
		}catch(Exception e) {
			LOGGER.info("Exception occured during withdraw", e.getMessage());
			throw e;

		}
		return transactionDto;
	}
	@Override
	@Transactional
	public TransactionDto transfer(Long sourceAccountId, Long targetAccountId, Double amount,String jwtToken) throws Exception {
		LOGGER.info("In TransactionServiceService.depositeAmount()");
		TransactionDto transactionDto = new TransactionDto();
		try {
			AccountDto sourceAccount = checkAccountExist(sourceAccountId,jwtToken);
			AccountDto targetaccount = checkAccountExist(targetAccountId,jwtToken);
			balanceValidation(sourceAccount,amount);
			sourceAccount.setAmount(sourceAccount.getAmount()-amount);
			sourceAccount = updateAccount(sourceAccount,jwtToken);
//			sourceAccount = accountRepository.save(sourceAccount);
			targetaccount.setAmount(targetaccount.getAmount()+amount);
			targetaccount = updateAccount(targetaccount,jwtToken);
//			accountRepository.save(targetaccount);
			Transaction debitTransaction =new Transaction();
			debitTransaction.setFromAccountId(sourceAccountId);
			debitTransaction.setToAccountId(targetAccountId);
			debitTransaction = saveTransaction(debitTransaction,sourceAccount,"TRANSFER_TO",amount);
			sendEmailNotification(sourceAccount.getEmail(),amount, debitTransaction,jwtToken);
			Transaction creditTransaction =new Transaction();
			creditTransaction.setFromAccountId(sourceAccountId);
			creditTransaction.setToAccountId(targetAccountId);
			creditTransaction = saveTransaction(creditTransaction,targetaccount,"TRANSFER_FROM",amount);
			sendEmailNotification(targetaccount.getEmail(), amount,creditTransaction,jwtToken);
			transactionDto.setFromAccountId(sourceAccountId);
			transactionDto.setToAccountId(targetAccountId);
			transactionDto.setAmount(creditTransaction.getAmount());
		}catch(Exception e) {
			LOGGER.info("Exception occured during transfer",e.getMessage());
			throw e;	
		}
		LOGGER.info("Out TransactionServiceService.transfer()");
		return transactionDto;
	}
	public List<Transaction> gettransactions(Long accountId) {
		LOGGER.info("In TransactionServiceService.gettransactions()");
		List<Transaction> transactionsList = new ArrayList<>();
		try {
		    transactionsList = transactionRepository.findAll(accountId);
			
		}catch(Exception e) {
			LOGGER.info("Exception occured during transfer", e.getMessage());
			throw e;	
		}
		LOGGER.info("Out TransactionServiceService.gettransactions()");
		return transactionsList;
	}
	public List<Transaction> gettransactionsBetweenDates(Long accountId,LocalDate startDate , LocalDate endDate) {
		LOGGER.info("In TransactionServiceService.gettransactionsBetweenDates()");
		List<Transaction> transactionsList = new ArrayList<>();
		try {
			 LocalDateTime startDateTime = startDate.atStartOfDay();
		     LocalDateTime endDateTime = endDate.atTime(23, 59, 59, 999999999);
		    transactionsList = transactionRepository.findAllTransactionsBetweenDates(accountId,startDateTime,endDateTime);
			
		}catch(Exception e) {
			LOGGER.info("Exception occured during transfer", e.getMessage());
			throw e;	
		}
		LOGGER.info("Out TransactionServiceService.gettransactionsBetweenDates()");
		return transactionsList;
	}
	public Transaction saveTransaction(Transaction transaction, AccountDto account ,String transactionType,Double amount) {
		LOGGER.info("In TransactionServiceService.saveTransaction()");
		transaction.setAccountId(account.getAccountId());
		transaction.setAmount(amount);
		transaction.setTimeStamp(LocalDateTime.now());
		transaction.setTransactionType(transactionType);
		transactionRepository.save(transaction);
		LOGGER.info("In TransactionServiceService.saveTransaction()");
		return transactionRepository.save(transaction);
	}


	public void  balanceValidation(AccountDto account, Double amount) {
		LOGGER.info("In TransactionServiceService.balanceValidation()");
		if(account.getAmount()!=null  &&  (account.getAmount()<=0 || amount > account.getAmount())) {
			throw new BadRequestException("Insufficient Balance", "Account: " +account.getAccountId()+ " does not have sufficient balance to do this transaction");
		}
		LOGGER.info("Out TransactionServiceService.balanceValidation()");
	}
	public AccountDto checkAccountExist(Long accountId , String jwtToken) {
		LOGGER.info("In TransactionServiceService.checkAccountExist()");
		AccountDto account = null;
		try {
			HttpHeaders httpHeaders=new HttpHeaders() ;
			httpHeaders.set("Authorization", "Bearer " + jwtToken);
			HttpEntity<?> entity =new HttpEntity<>(httpHeaders);
			String url ="http://gatewayservice/accountservice/account/"+accountId+"/getAccountDetails";
			LOGGER.info("URL: "+url);
			ResponseEntity<AccountDto> resp =null;
			resp =  restTemplate.exchange(url,HttpMethod.GET ,entity,AccountDto.class);
		    account=resp.getBody();
			if(account.getAccountId()==null ) {
				LOGGER.info("account id deos not exit ");
				throw new DataNotFoundException( "Account is not found","Account Id: " + accountId + " is does not exist");		
			}
			LOGGER.info("Out TransactionServiceService.checkAccountExist()");
		}catch(HttpClientErrorException e) {
			LOGGER.info("HttpClientErrorException occured while fetching account details",e.getMessage());
            throw e;
		}catch(Exception e) {
			LOGGER.info("exception occured while fetching account details",e.getMessage());
            throw e;
		}
		return account;
	}
	@SuppressWarnings("null")
	public AccountDto updateAccount(AccountDto account,String jwtToken) {
		LOGGER.info("In TransactionServiceService.updateAccount()");
		try {
			HttpHeaders httpHeaders=new HttpHeaders() ;
			httpHeaders.set("Authorization", "Bearer " + jwtToken);
			HttpEntity<?> entity =new HttpEntity<>(account, httpHeaders);
			String url ="http://gatewayservice/accountservice/account/updateAccount";
			ResponseEntity<AccountDto> resp =null;
			resp =  restTemplate.exchange(url,HttpMethod.PUT ,entity,AccountDto.class);
		    account=resp.getBody();
			if(account==null) {
				LOGGER.info("account id deos not exit ");
				throw new DataNotFoundException( "Account is not found","Account Id: " + account.getAccountId() + " is does not exist");		
			}
			LOGGER.info("Out TransactionServiceService.updateAccount()");
		}catch(HttpClientErrorException e) {
			LOGGER.info("HttpClientErrorException occured while fetching account details",e.getMessage());
            throw e;
		}catch(Exception e) {
			LOGGER.info("exception occured while updating account details",e.getMessage());
            throw e;
		}
		return account;
	}
	
	@SuppressWarnings("null")
	public AccountDto sendEmailNotification(String email,Double amount,Transaction transaction,String jwtToken) {
		LOGGER.info("In TransactionServiceService.sendEmailNotification()");
		AccountDto account = new AccountDto();
		try {
			HttpHeaders httpHeaders=new HttpHeaders() ;
			httpHeaders.set("Authorization", "Bearer " + jwtToken);
			HttpEntity<?> entity =new HttpEntity<>(transaction, httpHeaders);
			String url ="http://gatewayservice/notificationservice/notification/notify/"+email+"/"+amount;
			ResponseEntity<Boolean> resp =null;
			resp =  restTemplate.exchange(url,HttpMethod.POST ,entity,Boolean.class);
			Boolean status= resp.getBody();
			if(!status) {
				LOGGER.info("account id deos not exit ");
				throw new DataNotFoundException( "not able send email notification","failed");		
			}
			LOGGER.info("Out TransactionServiceService.sendEmailNotification()");
		}catch(HttpClientErrorException e) {
			LOGGER.info("HttpClientErrorException occured while fetching sending Email Notification details",e.getMessage());
            throw e;
		}catch(Exception e) {
			LOGGER.info("exception occured while  sending Email Notification",e.getMessage());
            throw e;
		}
		return account;
	}
	
	public boolean deleteTransactions(Long accountId) {
		return 	transactionRepository.deleteByAccountId(accountId)>0;
	}
}
