package com.moneyaccount.app;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ua.karazin.moviesbaseevents.moneyaccount.revision1.AccountDto1;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class AccountController {
    private final AccountCommandService accountCommandService;

    @PostMapping("/{id}/replenish")
    public CompletableFuture<Account> replenishById(@Valid AccountDto1 dto) {
        return accountCommandService.replenishAccount(dto);
    }

    @PostMapping("/{id}/deplete")
    public CompletableFuture<Account> depleteById(@Valid AccountDto1 dto) {
        return accountCommandService.depleteAccount(dto);
    }
}
