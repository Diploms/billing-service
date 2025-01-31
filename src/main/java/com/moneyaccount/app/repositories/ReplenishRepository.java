package com.moneyaccount.app.repositories;

import com.moneyaccount.app.models.Replenish;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;


@Repository
public interface ReplenishRepository extends JpaRepository<Replenish, UUID> {
}