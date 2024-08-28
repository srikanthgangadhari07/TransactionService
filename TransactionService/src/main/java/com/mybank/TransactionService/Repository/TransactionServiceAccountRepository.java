package com.mybank.TransactionService.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.mybank.TransactionService.Model.Account;

import jakarta.transaction.Transactional;
@Transactional
public interface TransactionServiceAccountRepository extends JpaRepository<Account,Long> {
	@Query("select a from Account a where a.accountId = :accountId")
    Account findByAccountId(@Param("accountId") Long accountId);

}
