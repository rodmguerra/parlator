package com.intergrammar.parlator;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

public class SessionHandler {

    public Map<String, String> getErrorMessageMap(HttpSession session) {
        Map<String, String> errorMessageMap;
        errorMessageMap = (Map) session.getAttribute("errorMessage");
        if(errorMessageMap == null) {
            errorMessageMap = new HashMap<String,String>();
            session.setAttribute("errorMessage", errorMessageMap);
        }
        return errorMessageMap;
    }

    public Map<String,byte[]> getAudioMap(HttpSession session) {
        Map<String, byte[]> audioMap;
        audioMap = (Map) session.getAttribute("audio");
        if(audioMap == null) {
            audioMap = new HashMap<String,byte[]>();
            session.setAttribute("audio", audioMap);
        }
        return audioMap;
    }
}
