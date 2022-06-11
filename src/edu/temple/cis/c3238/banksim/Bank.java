package edu.temple.cis.c3238.banksim;

import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 * @author Modified by Charles Wang
 * @author Modified by Alexa Delacenserie
 * @author Modified by Tarek Elseify
 */

public class Bank {

    public static final int NTEST = 10;
    private final Account[] accounts;
    private long numTransactions = 0;
    private final int initialBalance;
    private final int numAccounts;
    public ReentrantLock lock;
    private final Semaphore semaphore;
    private boolean isopen;

    public Bank(int numAccounts, int initialBalance) {
        this.initialBalance = initialBalance;
        this.numAccounts = numAccounts;
        isopen = true;
        accounts = new Account[numAccounts];
        for (int i = 0; i < accounts.length; i++) {
            accounts[i] = new Account(this, i, initialBalance);
        }
        numTransactions = 0;
        lock = new ReentrantLock();
        semaphore = new Semaphore(numAccounts, true);
    }

    public void transfer(int from, int to, int amount) {
        accounts[from].waitForAvailableFunds(amount);
        if(!isopen) return;

        lock.lock();
        try {
            semaphore.acquire();
            if (accounts[from].withdraw(amount)) {
                accounts[to].deposit(amount);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            semaphore.release();
            lock.unlock();
        }
        // Uncomment line when ready to start Task 3.
        //if (shouldTest()) test();
    }

    public void test() {
        lock.lock();
        try {
            int totalBalance = 0;
            for (Account account : accounts) {
                System.out.printf("%-30s %s%n",
                        Thread.currentThread().toString(), account.toString());
                totalBalance += account.getBalance();
            }
            System.out.printf("%-30s Total balance: %d\n", Thread.currentThread().toString(), totalBalance);
            if (totalBalance != numAccounts * initialBalance) {
                System.out.printf("%-30s Total balance changed!\n", Thread.currentThread().toString());
                System.exit(0);
            } else {
                System.out.printf("%-30s Total balance unchanged.\n", Thread.currentThread().toString());
            }
        } finally {
            lock.unlock();
        }
    }

    public int getNumAccounts() {
        return numAccounts;
    }
    
    public boolean shouldTest() {
        return ++numTransactions % NTEST == 0;
    }

    public synchronized boolean isopen(){
        return isopen;
    }

    public void close(){
        synchronized (getClass()){
            isopen = false;
        }
        for(Account account: accounts){
            synchronized (account){
                account.notifyAll();
            }
        }
    }
}
