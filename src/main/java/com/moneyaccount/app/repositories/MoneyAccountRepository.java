package com.moneyaccount.app.repositories;

import com.moneyaccount.app.models.MoneyAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface MoneyAccountRepository extends JpaRepository<MoneyAccount, UUID> {
    Optional<MoneyAccount> findByUserId(UUID userId);
}