package com.db.quoters;

import lombok.Setter;

/**
 * Created by Evegeny on 30/08/2017.
 */
@Transaction
@Benchmark
@InjectRandomInt(min = 1, max = 1)
public class ShakespeareQuoter implements Quoter {
    @Setter
    private String message;

    @InjectRandomInt(min = 3, max = 6)
    private int repeat;

    @Override
    public void sayQuote() {
        for (int i = 0; i < repeat; i++) {
            System.out.println(message);
        }
    }

    @Override
    public String toString() {
        return "hui";
    }
}
