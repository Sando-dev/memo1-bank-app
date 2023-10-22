package com.aninfo.service;

import com.aninfo.exceptions.DepositNegativeSumException;
import com.aninfo.exceptions.InsufficientFundsException;
import com.aninfo.model.Account;
import com.aninfo.model.Transaction;
import com.aninfo.model.Deposit;
import com.aninfo.model.Withdraw;
import com.aninfo.repository.AccountRepository;
import com.aninfo.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    public Account createAccount(Account account) {
        account.setTransactions(new ArrayList<>());
        return accountRepository.save(account);
    }

    public Collection<Account> getAccounts() {
        return accountRepository.findAll();
    }

    public Optional<Account> findById(Long cbu) {
        return accountRepository.findById(cbu);
    }

    public void save(Account account) {
        accountRepository.save(account);
    }

    public void deleteById(Long cbu) {
        Account account = accountRepository.findAccountByCbu(cbu);

        List<Transaction> transactions = account.getTransactions();
        transactions.forEach(transaction -> transactionRepository.delete(transaction));

        accountRepository.deleteById(cbu);
    }

    @Transactional
    public Account withdraw(Long cbu, Double sum) {
        Account account = accountRepository.findAccountByCbu(cbu);

        if (account.getBalance() < sum) {
            throw new InsufficientFundsException("Insufficient funds");
        }

        Withdraw withdraw = new Withdraw();
        withdraw.setAmount(sum);

        account.addTransaction(withdraw);

        account.setBalance(account.getBalance() - sum);
        accountRepository.save(account);

        return account;
    }

    @Transactional
    public Account deposit(Long cbu, Double sum) {
        if (sum <= 0) {
            throw new DepositNegativeSumException("Cannot deposit negative sums");
        }

        if (sum >= 2000) {
            double bonus = sum * 0.10;
            if (bonus > 500) {
                bonus = 500;
            }
            sum += bonus;
        }

        Account account = accountRepository.findAccountByCbu(cbu);

        Deposit deposit = new Deposit();
        deposit.setAmount(sum);

        account.addTransaction(deposit);

        account.setBalance(account.getBalance() + sum);
        accountRepository.save(account);

        return account;
    }

    @Transactional
    public void deleteTransaction(Long accountId, Long transactionId) {
        Optional<Account> accountOptional = accountRepository.findById(accountId);

        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();
            Optional<Transaction> transactionOptional = transactionRepository.findById(transactionId);

            if (transactionOptional.isPresent()) {
                Transaction transaction = transactionOptional.get();

                if (transaction instanceof Deposit) {
                    account.setBalance(account.getBalance() - transaction.getAmount());
                } else if (transaction instanceof Withdraw) {
                    account.setBalance(account.getBalance() + transaction.getAmount());
                }

                transactionRepository.deleteById(transactionId);

                accountRepository.save(account);
            }
        }
    }

}
