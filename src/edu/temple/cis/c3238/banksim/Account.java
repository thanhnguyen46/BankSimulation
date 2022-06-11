package edu.temple.cis.c3238.banksim;

/**
 * @author Cay Horstmann
 * @author Modified by Paul Wolfgang
 * @author Modified by Charles Wang
 * @author Modified by Alexa Delacenserie
 * @author Modified by Tarek Elseify
 */
public class Account {

    private volatile int balance;
    private final int id;
    private final Bank myBank;

    public Account(Bank myBank, int id, int initialBalance) {
        this.myBank = myBank;
        this.id = id;
        this.balance = initialBalance;
    }

    public int getBalance() {
        return balance;
    }

    public synchronized boolean withdraw(int amount) {
        if (amount <= balance) {
            int currentBalance = balance;
            //Thread.yield(); // Try to force collision
            int newBalance = currentBalance - amount;
            balance = newBalance;
            return true;
        } else {
            return false;
        }
    }

    public synchronized void deposit(int amount) {
        int currentBalance = balance;
        Thread.yield();   // Try to force collision
        int newBalance = currentBalance + amount;
        balance = newBalance;
        notifyAll();
    }

    @Override
    public String toString() {
        return String.format("Account[%d] balance %d", id, balance);
    }

    public synchronized void waitForAvailableFunds(int amount){
        while(myBank.isopen() && balance <= amount){
            try{
                System.out.println("Account: " + id + " is waiting for funds");
                // Wait until some other thread invoke notify() or notifyAll()
                wait();
            }
            catch(InterruptedException e){
                e.printStackTrace();
            } finally {
                if (myBank.isopen())
                    System.out.println("Account: " + id + " has enough funds, transferring now");
                else // Bank close
                    System.out.println("Account: " + id + " was waiting for funds when the bank closed");
            }
        }
    }
}