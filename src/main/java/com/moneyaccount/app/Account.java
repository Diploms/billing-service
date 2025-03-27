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
import ua.karazin.moviesbaseevents.moneyaccount.revision1.*;

import java.math.BigDecimal;

import static ir.cafebabe.math.utils.BigDecimalUtils.is;
import static org.axonframework.modelling.command.AggregateLifecycle.apply;

@Aggregate
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @AggregateIdentifier
    private String profileId;

    private BigDecimal balance = BigDecimal.ZERO;

    @CommandHandler
    private void handle(ReplenishAccountCommand1 command) {
        if (is(command.amount()).isNegative()) {
            throw new CommandExecutionException("Positive amount is expected. Got: " + command.amount(), new IllegalStateException());
        }

        apply(new AccountReplenishedEvent1(command.profileId(), command.amount()));
    }

    @CommandHandler
    private void handle(DepleteAccountCommand1 command) {
        if (is(command.amount()).isNegative()) {
            throw new CommandExecutionException("Positive amount is expected. Got: " + command.amount(), new IllegalStateException());
        }

        if (is(command.amount()).gt(this.balance)) {
            throw new CommandExecutionException("Too much amount", new IllegalStateException());
        }

        apply(new AccountDepletedEvent1(command.profileId(), command.amount()));
    }

    @EventSourcingHandler
    private void on(AccountCreatedEvent1 event) {
        this.profileId = event.profileId();
    }

    @EventSourcingHandler
    private void on(AccountReplenishedEvent1 event) {
        this.balance = this.balance.add(event.amount());
    }

    @EventSourcingHandler
    private void on(AccountDepletedEvent1 event) {
        this.balance = this.balance.subtract(event.amount());
    }
}
