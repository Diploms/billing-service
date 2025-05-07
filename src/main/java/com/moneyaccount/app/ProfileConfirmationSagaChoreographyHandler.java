package com.moneyaccount.app;

import lombok.RequiredArgsConstructor;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.stereotype.Component;
import ua.karazin.moviesbaseevents.moneyaccount.revision1.CreateAccountCommand1;
import ua.karazin.moviesbaseevents.profiles.revision1.ProfileConfirmedEvent1;

@Component
@RequiredArgsConstructor
public class ProfileConfirmationSagaChoreographyHandler {
  private final CommandGateway commandGateway;

  @EventHandler
  private void on(ProfileConfirmedEvent1 event) {
    commandGateway.send(new CreateAccountCommand1(event.profileId()));
  }
}
