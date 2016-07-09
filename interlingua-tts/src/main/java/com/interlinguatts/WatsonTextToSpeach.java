package com.interlinguatts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.reflect.TypeToken;
import com.ibm.watson.developer_cloud.http.HttpMediaType;
import com.ibm.watson.developer_cloud.http.RequestBuilder;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.*;
import com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice;
import com.ibm.watson.developer_cloud.util.ResponseUtil;
import com.ibm.watson.developer_cloud.util.Validate;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WatsonTextToSpeach extends com.ibm.watson.developer_cloud.text_to_speech.v1.TextToSpeech {

    private static final String ACCEPT = "accept";
    private static final String VOICE = "voice";
    private static final String TEXT = "text";
    public static final String PATH_SYNTHESIZE = "/v1/synthesize";
    public final static Type listVoiceType = new TypeToken<List<Voice>>() {}.getType();
    public final static String URL = "https://stream.watsonplatform.net/text-to-speech/api";

    public InputStream synthesize(final String text, final com.ibm.watson.developer_cloud.text_to_speech.v1.model.Voice voice, final String outputFormat) {
        Validate.isTrue(text != null && !text.isEmpty(), "text cannot be null or empty");
        Validate.isTrue(voice != null, "voice cannot be null or empty");

        final RequestBuilder request = RequestBuilder.post(PATH_SYNTHESIZE);
        ObjectMapper objectMapper = new ObjectMapper();
        Map<String,String> data = new HashMap<String,String>();
        data.put("text",text);
        String dataString;
        try {
            dataString = objectMapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        request.withBodyContent(dataString, "application/json");
        request.withQuery(VOICE, voice.getName());

        if (outputFormat != null && !outputFormat.startsWith("audio/"))
            throw new IllegalArgumentException(
                    "format needs to be an audio mime type, for example: audio/wav or audio/ogg; codecs=opus");

        request.withQuery(ACCEPT, outputFormat != null ? outputFormat : HttpMediaType.AUDIO_WAV);

        final Response response = execute(request.build());
        return ResponseUtil.getInputStream(response);
    }
}
