package com.raga.ecommerce.account.repository;

import com.raga.ecommerce.account.vo.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AccountRepository extends MongoRepository<Account, String> {

  Optional<Account> findByAccountId(String accountId);
}
