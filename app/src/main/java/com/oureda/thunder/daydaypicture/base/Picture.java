package com.oureda.thunder.daydaypicture.base;

import org.litepal.crud.DataSupport;

/**
 * Created by thunder on 17-5-27.
 */

public class Picture extends DataSupport {
    private String url;
    private String pictureId;
    private String  time;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPictureId() {
        return pictureId;
    }

    public void setPictureId(String pictureId) {
        this.pictureId = pictureId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
