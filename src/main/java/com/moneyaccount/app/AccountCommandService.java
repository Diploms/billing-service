package com.moneyaccount.app;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.springframework.stereotype.Service;
import ua.karazin.moviesbaseevents.moneyaccount.revision1.AccountDto1;
import ua.karazin.moviesbaseevents.moneyaccount.revision1.CreateAccountCommand1;
import ua.karazin.moviesbaseevents.moneyaccount.revision1.DepleteAccountCommand1;
import ua.karazin.moviesbaseevents.moneyaccount.revision1.ReplenishAccountCommand1;

import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AccountCommandService {
    private final CommandGateway commandGateway;

    public void createAccount(String profileId) {
        commandGateway.send(new CreateAccountCommand1(profileId));
    }

    public CompletableFuture<Account> replenishAccount(AccountDto1 dto) {
        return commandGateway.send(new ReplenishAccountCommand1(dto.profileId(), dto.amount()));
    }

    public CompletableFuture<Account> depleteAccount(AccountDto1 dto) {
        return commandGateway.send(new DepleteAccountCommand1(dto.profileId(), dto.amount()));
    }
}
