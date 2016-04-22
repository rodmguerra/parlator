package com.interlinguatts;

import com.interlinguatts.domain.Word;
import com.interlinguatts.repository.MemoryWordRepository;
import com.interlinguatts.repository.Repository;
import com.interlinguatts.repository.WordRepository;
import com.ivona.services.tts.model.Voice;
import org.apache.commons.dbcp.BasicDataSource;

import java.util.List;

public class App {
    public static void main(String[] args) {
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setDriverClassName("org.postgresql.Driver");
        dataSource.setUrl("jdbc:postgresql://pellefant.db.elephantsql.com:5432/xoyaewgn");
        dataSource.setUsername("xoyaewgn");
        dataSource.setPassword("t9Y27piepSw8YbhM5WzRT19f747J7uNU");
        dataSource.setDefaultAutoCommit(false);
        //String[] languages = {"pt-BR", "es-US", "pl-PL", "it-IT"};
        String text = "Le juvene senior reguarda le juvene dama. Illa es un senioretta belle, e ille la reguarda con interesse. Nostre amico es un senior elegante. Sed illa tamen le reguarda sin interesse. Nos debe constatar iste facto tragic jam nunc. Ille dice a illa: “Excusa me, senioretta! Esque vos permitte que io me sede?”. Illa non responde per parolas, sed face un signo con le capite.";

        InterlinguaTextToSpeach tts;
        InterlinguaNumberWriter numberWriter = new InterlinguaNumberWriter();
        WordRepository wordRepository = new WordRepository(dataSource);
        Repository<Word> repository = new MemoryWordRepository(wordRepository.findAll());
        IvonaFacade ivonaFacade = new IvonaFacade();
        InterlinguaIpaProvider provider = new InterlinguaIpaProvider(wordRepository, numberWriter);
        List<Voice> voices = ivonaFacade.getVoices();
        tts = new InterlinguaTextToSpeach(ivonaFacade, provider, numberWriter);

        for(Voice voice : voices) {
            tts.textToSpeech(text, "C:\\Users\\rodmguerra\\Projetos Pessoais\\intergrammar\\java\\interlingua-tts\\interlingua-tts\\src\\main\\resources\\newaudio\\" + voice.getLanguage() + "_" + voice.getGender() + "_" + voice.getName() + ".mp3", voice);
        }
    }
}
