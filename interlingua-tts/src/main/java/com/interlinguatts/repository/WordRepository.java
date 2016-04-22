package com.interlinguatts.repository;

import com.interlinguatts.domain.Word;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.apache.commons.dbutils.DbUtils.closeQuietly;

public class WordRepository implements Repository<Word> {

    private final DataSource dataSource;

    public WordRepository(DataSource connection) {
        this.dataSource = connection;
    }

    public Word findByWord(String word) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement("SELECT * FROM words WHERE LOWER(word) = LOWER(?)");
            statement.setString(1, word);
            ResultSet results = statement.executeQuery();
            if(results.next()) {
                return new Word(results.getString(2), results.getString(3), results.getString(4));
            } else {
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeQuietly(connection);
        }
    }

    @Override
    public void deleteAll() {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement("DELETE FROM words");
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeQuietly(connection);
        }
    }

    public Map<String, Word> findAll() {
        Connection connection = null;
        PreparedStatement statement = null;
        Map<String, Word> words = new TreeMap<String, Word>(String.CASE_INSENSITIVE_ORDER);
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement("SELECT * FROM words");
            ResultSet results = statement.executeQuery();
            while (results.next()) {
                String word = results.getString(2);
                String respell = results.getString(3);
                String wordClass = results.getString(4);
                words.put(word, new Word(word, respell, wordClass));
            }
            return words;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeQuietly(connection);
        }
    }

    public void insert(List<Word> words) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = dataSource.getConnection();
            statement = connection.prepareStatement("INSERT INTO words (word, respell, word_class) values (?, ?, ?)");

            for (Word word : words) {
                statement.setString(1, word.getWord());
                if(!word.getRespell().equals(word.getWord())) {
                    statement.setString(2, word.getRespell());
                } else {
                    statement.setString(2, null);
                }
                statement.setString(3, word.getWordClass());

                statement.addBatch();
            }
            statement.executeBatch();
            connection.commit();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeQuietly(statement);
            closeQuietly(connection);
        }
    }
}
