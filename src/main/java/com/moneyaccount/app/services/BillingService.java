package com.moneyaccount.app.services;


import com.moneyaccount.app.models.MoneyAccount;
import com.moneyaccount.app.models.Movie;
import com.moneyaccount.app.models.Purchase;
import com.moneyaccount.app.models.Replenish;
import com.moneyaccount.app.repositories.MoneyAccountRepository;
import com.moneyaccount.app.repositories.MovieRepository;
import com.moneyaccount.app.repositories.PurchaseRepository;
import com.moneyaccount.app.repositories.ReplenishRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;


@Service
@Transactional(readOnly = true)
public class BillingService {
    private final MoneyAccountRepository moneyAccountRepository;
    private final ReplenishRepository replenishRepository;

    private final MovieRepository movieRepository;
    private final PurchaseRepository purchaseRepository;

    @Autowired
    public BillingService(MoneyAccountRepository moneyAccountRepository,
                          ReplenishRepository replenishRepository, MovieRepository movieRepository, PurchaseRepository purchaseRepository) {
        this.moneyAccountRepository = moneyAccountRepository;
        this.replenishRepository = replenishRepository;
        this.movieRepository = movieRepository;
        this.purchaseRepository = purchaseRepository;
    }

    /**
     * Getting user balance
     */
    public double getUserBalance(UUID userId) {
        MoneyAccount account = getMoneyAccountByUserId(userId);
        return account.getBalance();
    }

    /**
     * Replenishment of the user account
     */
    @Transactional
    public void replenishAccount(UUID userId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("The replenishment amount must be greater than zero");
        }

        // Find user account
        MoneyAccount account = getMoneyAccountByUserId(userId);

        // Update balance
        account.setBalance(account.getBalance() + amount);
        moneyAccountRepository.save(account);

        // Save replenishment record
        Replenish replenish = Replenish.builder()
                .userId(userId)
                .replenishment_amount(amount)
                .build();
        replenishRepository.save(replenish);
    }


    /**
     * Don't know exactly what can be more appropriate ->
     * method withdrawForPurchase() or purchaseMovie()
     */

    /**
     * Withdrawing money (buying a movie) -> just logic
     */
    @Transactional
    public void withdrawForPurchase(UUID userId, double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("The withdrawal amount must be greater than zero");
        }

        // Find user account
        MoneyAccount account = getMoneyAccountByUserId(userId);

        if (account.getBalance() < amount) {
            throw new IllegalStateException("Insufficient funds on balance");
        }

        // Update balance
        account.setBalance(account.getBalance() - amount);
        moneyAccountRepository.save(account);
    }

    /**
     * Buying a movie
     */
    @Transactional
    public void purchaseMovie(UUID userId, UUID movieId, double moviePrice) {
        if (moviePrice <= 0) {
            throw new IllegalArgumentException("The price of the movie must be greater than zero");
        }

        // Find user account
        MoneyAccount account = getMoneyAccountByUserId(userId);

        // Check balance
        if (account.getBalance() < moviePrice) {
            throw new IllegalStateException("Not enough funds to purchase the movies");
        }

        // Write off money from an account
        account.setBalance(account.getBalance() - moviePrice);
        moneyAccountRepository.save(account);

        // Find a movie
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new EntityNotFoundException("Movie not found"));

        // Create a purchase record
        Purchase purchase = Purchase.builder()
                .userId(userId)
                .total_amount(moviePrice)
                .movie(movie)
                .build();
        purchaseRepository.save(purchase);
    }

    private MoneyAccount getMoneyAccountByUserId(UUID userId) {
        return moneyAccountRepository.findByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("User account not found"));
    }
}
