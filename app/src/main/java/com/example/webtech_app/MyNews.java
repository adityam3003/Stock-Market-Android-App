package com.example.webtech_app;

import androidx.annotation.NonNull;

public class MyNews {
    String image;
    String source;
    String summary;
    String headline;
    String url;
    int datetime;

    public MyNews(String image, String source, String summary, String headline, String url, int datetime) {
        this.image = image;
        this.source = source;
        this.summary = summary;
        this.headline = headline;
        this.url = url;
        this.datetime = datetime;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDatetime() {
        return datetime;
    }

    public void setDatetime(int datetime) {
        this.datetime = datetime;
    }

}
