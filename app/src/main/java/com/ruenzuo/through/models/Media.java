package com.ruenzuo.through.models;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.Date;

/**
 * Created by renzocrisostomo on 14/06/14.
 */
@ParseClassName("Media")
public class Media extends ParseObject {

    public Date getMediaDate() {
        return getDate("mediaDate");
    }

    public void setMediaDate(Date mediaDate) {
        put("mediaDate", mediaDate);
    }

    public String getText() {
        return getString("text");
    }

    public void setText(String value) {
        put("text", value);
    }

    public String getUserName() {
        return getString("userName");
    }

    public void setUserName(String userName) {
        put("userName", userName);
    }

    public String getURL() {
        return getString("url");
    }

    public void setURL(String URL) {
        put("url", URL);
    }

    public MediaType getType() {
        return MediaType.values()[getInt("type")];
    }

    public void setType(MediaType type) {
        put("type", type.ordinal());
    }

    public static ParseQuery<Media> getQuery() {
        return ParseQuery.getQuery(Media.class);
    }

}
