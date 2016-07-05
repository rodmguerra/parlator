package com.interlinguatts.ivona;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.interlinguatts.VoiceProvider;
import com.ivona.services.tts.IvonaSpeechCloudClient;
import com.ivona.services.tts.model.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IvonaConnector implements VoiceProvider {
    private IvonaSpeechCloudClient speechCloud;

    public IvonaConnector() {
        speechCloud = new IvonaSpeechCloudClient(
                new ClasspathPropertiesFileCredentialsProvider("ivona.properties"));
        speechCloud.setEndpoint("https://tts.us-east-1.ivonacloud.com");
    }

    public void putLexicon(String name, String contents) {
        Lexicon lexicon = new Lexicon().withName(name).withContents(contents);
        PutLexiconRequest putLexiconRequest = new PutLexiconRequest().withLexicon(lexicon);
        speechCloud.putLexicon(putLexiconRequest);
    }

    public List<String> getLexiconNames() {
        ListLexiconsResult result = speechCloud.listLexicons();
        return result.getLexiconNames();
    }

    public List<com.interlinguatts.Voice> getVoices() {
        ListVoicesRequest listVoicesRequest = new ListVoicesRequest();
        ListVoicesResult result = speechCloud.listVoices(listVoicesRequest);
        List<com.interlinguatts.Voice> apiVoices = new ArrayList<com.interlinguatts.Voice>();
        for (Voice voice : result.getVoices()) {
           apiVoices.add(toApiVoice(voice));
        }
        return apiVoices;
    }

    public List<com.interlinguatts.Voice> getGoodVoicesForInterlingua() {
        List<com.interlinguatts.Voice> voices = getVoices();
        List<com.interlinguatts.Voice> goodVoices = new ArrayList<com.interlinguatts.Voice>();
        List<String> goodVoiceNames = Arrays.asList("Carla", "Giorgio");
        for (com.interlinguatts.Voice voice : voices) {
            if (goodVoiceNames.contains(voice.getName())) {
                goodVoices.add(voice);
            }
        }
        return goodVoices;
    }

    public void textToSpeech(Voice voice, String text, OutputStream outputStream, List<String> lexiconNames) {
        CreateSpeechRequest request = new CreateSpeechRequest();
        Input input = new Input();

        OutputFormat format = new OutputFormat();
        format.setCodec("MP3");
        input.setData(text);
        request.setLexiconNames(lexiconNames);
        request.setInput(input);
        request.setVoice(voice);
        request.setOutputFormat(format);

        Parameters parameters = new Parameters();
        parameters.setRate("slow");
        request.setParameters(parameters);

        InputStream in = null;
        try {

            CreateSpeechResult createSpeechResult = speechCloud.createSpeech(request);
            in = createSpeechResult.getBody();
            //
            byte[] buffer = new byte[2 * 1024];
            int readBytes;

            while ((readBytes = in.read(buffer)) > 0) {
                outputStream.write(buffer, 0, readBytes);
            }

            //System.out.println("\nFile saved: " + outputFileName);
        } catch (Exception e) {
            throw new RuntimeException("TTS fail", e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e) {
                throw new RuntimeException("Close input fail", e);
            }

            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                throw new RuntimeException("Close output fail", e);
            }
        }
    }

    public void deleteLexicon(String lexiconName) {
        DeleteLexiconRequest request = new DeleteLexiconRequest().withLexiconName(lexiconName);
        speechCloud.deleteLexicon(request);
    }

    private com.interlinguatts.Voice toApiVoice(Voice ivonaVoice) {
        com.interlinguatts.Voice apiVoice = new com.interlinguatts.Voice();
        apiVoice.setGender(ivonaVoice.getGender());
        apiVoice.setName(ivonaVoice.getName());
        apiVoice.setLanguage(ivonaVoice.getLanguage());
        return apiVoice;
    }
}
