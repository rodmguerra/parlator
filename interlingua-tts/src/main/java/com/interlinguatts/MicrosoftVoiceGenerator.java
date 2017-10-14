package com.interlinguatts;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MicrosoftVoiceGenerator implements VoiceGenerator {
    @Override
    public Voice getDefaultVoice() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Voice> getVoices() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public List<Voice> getGoodVoicesForInterlingua() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InputStream textAndLexiconToAudio(Voice voice, String text, Map<String, String> graphemePhonemeMap, MediaType mediaType) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public InputStream ssmlToAudio(Voice voice, String text, MediaType mediaType) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    /*
   @Override
   public InputStream ssmlToAudio(Voice voice, String text, MediaType mediaType) {
       String url = "https://speech.platform.bing.com/synthesize";

       HttpClient client = HttpClientBuilder.create().build();
       HttpPost post = new HttpPost(url);

// add header
       post.setHeader("Content-Type", "application/ssml+xml");
       post.setHeader("X-Microsoft-OutputFormat", "application/ssml+xml");
       post.setHeader("X-Search-AppId", "application/ssml+xml");
       post.setHeader("X-Search-ClientID", "application/ssml+xml");
       post.setHeader("User-Agent", "application/ssml+xml");
       post.setHeader("X-Search-PartnerEventID", "application/ssml+xml");
       post.setEntity(HttpEntity);

       List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
       urlParameters.add(new BasicNameValuePair("sn", "C02G8416DRJM"));
       urlParameters.add(new BasicNameValuePair("cn", ""));
       urlParameters.add(new BasicNameValuePair("locale", ""));
       urlParameters.add(new BasicNameValuePair("caller", ""));
       urlParameters.add(new BasicNameValuePair("num", "12345"));

       post.setEntity(new UrlEncodedFormEntity(urlParameters));

       HttpResponse response = client.execute(post);
       System.out.println("Response Code : "
               + response.getStatusLine().getStatusCode());

       BufferedReader rd = new BufferedReader(
               new InputStreamReader(response.getEntity().getContent()));

       StringBuffer result = new StringBuffer();
       String line = "";
       while ((line = rd.readLine()) != null) {
           result.append(line);
       }
   }
       */
    @Override
    public List<MediaType> getAvailableMediaTypes() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

            /*
    private static byte[] Synthesize(String textToSynthesize, String outputFormat, String locale, String genderName, String voiceName) throws Exception {

        // Note: Sign up at http://www.projectoxford.ai for the client credentials.
        OxfordAuthentication auth = new OxfordAuthentication("Your ClientId goes here", "Your Client Secret goes here");
        OxfordAccessToken token = auth.GetAccessToken();

        HttpsURLConnection webRequest = HttpsConnection.getHttpsConnection(ttsServiceUri);
        webRequest.setDoInput(true);
        webRequest.setDoOutput(true);
        webRequest.setConnectTimeout(5000);
        webRequest.setReadTimeout(15000);
        webRequest.setRequestMethod("POST");

        webRequest.setRequestProperty("Content-Type", "application/ssml+xml");
        webRequest.setRequestProperty("X-Microsoft-OutputFormat", outputFormat);
        webRequest.setRequestProperty("Authorization", "Bearer " + token.access_token);
        webRequest.setRequestProperty("X-Search-AppId", "07D3234E49CE426DAA29772419F436CA");
        webRequest.setRequestProperty("X-Search-ClientID", "1ECFAE91408841A480F00935DC390960");
        webRequest.setRequestProperty("User-Agent", "TTSAndroid");
        */
        //webRequest.setRequestProperty("Accept", "*/*");
        /*
        String SsmlTemplate = "<speak version='1.0' xml:lang='en-us'><voice xml:lang='%s' xml:gender='%s' name='%s'>%s</voice></speak>";
        String body = String.format(SsmlTemplate, locale, genderName, voiceName, textToSynthesize);
        byte[] bytes = body.getBytes();
        webRequest.setRequestProperty("content-length", String.valueOf(bytes.length));
        webRequest.connect();

        DataOutputStream dop = new DataOutputStream(webRequest.getOutputStream());
        dop.write(bytes);
        dop.flush();
        dop.close();

        InputStream inSt = webRequest.getInputStream();
        ByteArray ba = new ByteArray();

        int rn2 = 0;
        int bufferLength = 4096;
        byte[] buf2 = new byte[bufferLength];
        while ((rn2 = inSt.read(buf2, 0, bufferLength)) > 0) {
            ba.cat(buf2, 0, rn2);
        }

        inSt.close();
        webRequest.disconnect();

        return ba.getArray();
    }
*/
}
