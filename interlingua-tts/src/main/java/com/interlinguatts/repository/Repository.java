package com.interlinguatts.repository;

public interface Repository<T> {
    T findByWord(String word);

    void deleteAll();
}
