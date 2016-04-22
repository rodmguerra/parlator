package com.interlinguatts.repository;

import com.interlinguatts.domain.Word;
import com.interlinguatts.repository.Repository;

import java.util.HashMap;
import java.util.Map;

public class MemoryWordRepository implements Repository<Word>{

    private Map<String,Word> map;

    public MemoryWordRepository(Map<String, Word> map) {
        this.map = map;
    }

    @Override
    public Word findByWord(String word) {
        return map.get(word);
    }

    @Override
    public void deleteAll() {
        map = new HashMap<String, Word>();
    }
}
