package com.mybank.TransactionService.Repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mybank.TransactionService.Model.Transaction;

import jakarta.transaction.Transactional;

@Transactional
public interface TransactionServiceRepository extends JpaRepository<Transaction,Long> {
	@Query("select t from Transaction t where t.transactionId = :transactionId")
    Transaction findByTransactionId(@Param("transactionId") Long transactionId);
	
	@Query("select t from Transaction t where t.accountId = :accountId")
    List<Transaction> findAll(@Param("accountId") Long accountId);
	
	@Query("SELECT t FROM Transaction t WHERE t.accountId = :accountId AND t.timeStamp BETWEEN :startDate AND :endDate")
	List<Transaction> findAllTransactionsBetweenDates(@Param("accountId") Long accountId,
	                                                   @Param("startDate") LocalDateTime startDate,
	                                                   @Param("endDate") LocalDateTime endDate);
	@Modifying
	@Query("delete from Transaction t where t.accountId = :accountId")
	int  deleteByAccountId(@Param("accountId") Long accountId);

	
 
}
