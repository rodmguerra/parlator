package com.interlinguatts;

import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.ivona.services.tts.IvonaSpeechCloudClient;
import com.ivona.services.tts.model.*;

import java.io.*;
import java.util.List;

public class IvonaFacade {
    private IvonaSpeechCloudClient speechCloud;

    public IvonaFacade () {
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

    public List<Voice> getVoices() {
        ListVoicesRequest listVoicesRequest = new ListVoicesRequest();
        ListVoicesResult result = speechCloud.listVoices(listVoicesRequest);
        return result.getVoices();
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
}
