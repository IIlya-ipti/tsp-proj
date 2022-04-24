package com.example.superproject;

public record Triple<T1, T2, T3>(T1 one, T2 two, T3 three) {

    public T1 getOne() {
        return one;
    }

    public T2 getTwo() {
        return two;
    }

    public T3 getThree() {
        return three;
    }
}
