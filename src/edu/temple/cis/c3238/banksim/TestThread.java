package edu.temple.cis.c3238.banksim;

public class TestThread extends Thread {

    private final Bank myBank;

    public TestThread(Bank myBank) {
        this.myBank = myBank;
    }

    @Override
    public void run() {
        if (myBank.shouldTest()) {
            myBank.test();
        }
        try {
            sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
