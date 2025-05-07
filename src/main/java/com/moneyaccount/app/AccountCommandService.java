package com.moneyaccount.app;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;
import ua.karazin.moviesbaseevents.moneyaccount.revision1.AccountDto1;
import ua.karazin.moviesbaseevents.moneyaccount.revision1.ReplenishAccountCommand1;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AccountCommandService {
    private final CommandGateway commandGateway;

    public CompletableFuture<Account> replenishAccount(AccountDto1 dto) {
        return commandGateway.send(new ReplenishAccountCommand1(dto.profileId(), dto.amount()));
    }
}
