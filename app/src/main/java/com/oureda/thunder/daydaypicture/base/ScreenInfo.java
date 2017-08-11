package com.oureda.thunder.daydaypicture.base;

import android.graphics.Paint;

/**
 * Created by thunder on 17-7-9.
 */

public class ScreenInfo {
    private int screen_time;
    private String screen_md5;
    public static Pack pack;

    public Pack getPack() {
        return pack;
    }

    public static class Pack{
        private String resource_url;
        private String json_url;

        public String getResource_url() {
            return resource_url;
        }

        public String getJson_url() {
            return json_url;
        }
    }

    public int getScreen_time() {
        return screen_time;
    }

    public String  getScreen_md5() {
        return screen_md5;
    }
}
