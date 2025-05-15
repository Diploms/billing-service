package com.moneyaccount.app;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.axonframework.commandhandling.CommandExecutionException;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import ua.karazin.moviesbaseevents.moneyaccount.revision1.AccountCreatedEvent1;
import ua.karazin.moviesbaseevents.moneyaccount.revision1.AccountDepleteRejectedOnOrderEvent1;
import ua.karazin.moviesbaseevents.moneyaccount.revision1.AccountDepleteRejectedOnSubscriptionRenewEvent1;
import ua.karazin.moviesbaseevents.moneyaccount.revision1.AccountDepleteRejectedOnSubscriptionStartEvent1;
import ua.karazin.moviesbaseevents.moneyaccount.revision1.AccountDepletedOnOrderEvent1;
import ua.karazin.moviesbaseevents.moneyaccount.revision1.AccountDepletedOnSubscriptionRenewEvent1;
import ua.karazin.moviesbaseevents.moneyaccount.revision1.AccountDepletedOnSubscriptionStartEvent1;
import ua.karazin.moviesbaseevents.moneyaccount.revision1.AccountReplenishedEvent1;
import ua.karazin.moviesbaseevents.moneyaccount.revision1.CreateAccountCommand1;
import ua.karazin.moviesbaseevents.moneyaccount.revision1.DepleteAccountOnOrderCommand1;
import ua.karazin.moviesbaseevents.moneyaccount.revision1.DepleteAccountOnSubscriptionRenewCommand1;
import ua.karazin.moviesbaseevents.moneyaccount.revision1.DepleteAccountOnSubscriptionStartCommand1;
import ua.karazin.moviesbaseevents.moneyaccount.revision1.ReplenishAccountCommand1;

import java.math.BigDecimal;
import java.util.UUID;

import static ir.cafebabe.math.utils.BigDecimalUtils.is;
import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {
  @AggregateIdentifier
  private String accountId;

  private String profileId;

  private BigDecimal balance = BigDecimal.ZERO;

  @CommandHandler
  private Account(CreateAccountCommand1 command) {
    apply(new AccountCreatedEvent1(UUID.randomUUID().toString(), command.profileId()));
  }

  @CommandHandler
  private void handle(ReplenishAccountCommand1 command) {
    if (is(command.amount()).isNegative()) {
      throw new CommandExecutionException("Positive amount is expected. Got: " + command.amount(),
          new IllegalStateException());
    }

    apply(new AccountReplenishedEvent1(command.accountId(), command.amount()));
  }

  @CommandHandler
  private void handle(DepleteAccountOnOrderCommand1 command) {
    if (is(command.amount()).isNegative() || is(command.amount()).gt(this.balance)) {
      apply(new AccountDepleteRejectedOnOrderEvent1(command.accountId(), command.orderId()));
    }

    apply(
        new AccountDepletedOnOrderEvent1(command.accountId(), command.orderId(), command.amount()));
  }

  @CommandHandler
  private void handle(DepleteAccountOnSubscriptionStartCommand1 command) {
    if (is(command.price()).isNegative() || is(command.price()).gt(this.balance)) {
      apply(new AccountDepleteRejectedOnSubscriptionStartEvent1(command.accountId(),
          command.profileId(), command.transactionId(), command.price()));
    }

    apply(new AccountDepletedOnSubscriptionStartEvent1(command.accountId(), command.profileId(),
        command.transactionId(), command.price()));
  }

  @CommandHandler
  private void handle(DepleteAccountOnSubscriptionRenewCommand1 command) {
    if (is(command.price()).isNegative() || is(command.price()).gt(this.balance)) {
      apply(new AccountDepleteRejectedOnSubscriptionRenewEvent1(command.accountId(),
          command.profileId(), command.transactionId(), command.price()));
    }

    apply(new AccountDepletedOnSubscriptionRenewEvent1(command.accountId(), command.profileId(),
        command.transactionId(), command.price()));
  }

  @EventSourcingHandler
  private void on(AccountCreatedEvent1 event) {
    this.accountId = event.accountId();
    this.profileId = event.profileId();
  }

  @EventSourcingHandler
  private void on(AccountReplenishedEvent1 event) {
    this.balance = this.balance.add(event.amount());
  }

  @EventSourcingHandler
  private void on(AccountDepletedOnOrderEvent1 event) {
    this.balance = this.balance.subtract(event.amount());
  }

  @EventSourcingHandler
  private void on(AccountDepletedOnSubscriptionStartEvent1 event) {
    this.balance = this.balance.subtract(event.amount());
  }

  @EventSourcingHandler
  private void on(AccountDepletedOnSubscriptionRenewEvent1 event) {
    this.balance = this.balance.subtract(event.amount());
  }
}
