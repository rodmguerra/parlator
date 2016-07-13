package com.interlinguatts;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class MediaType {

    private final String contentType;
    private final String extension;

    @JsonCreator
    public MediaType(@JsonProperty("contentType") String contentType, @JsonProperty("extension") String extension) {
        this.contentType = contentType;
        this.extension = extension;
    }

    public String getContentType() {
        return contentType;
    }

    public String getExtension() {
        return extension;
    }

}
