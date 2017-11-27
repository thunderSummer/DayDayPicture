package com.oureda.thunder.daydaypicture.base;

import java.util.List;

/**
 * Created by thunder on 17-8-13.
 */

public class PictureData {
    public List<Picture> picture;

    public class Picture{
        private String name;
        private String md5;
        private String url;
        private int time;

        public int getTime() {
            return time;
        }

        public String getName() {
            return name;
        }

        public String getMd5() {
            return md5;
        }

        public String getUrl() {
            return url;
        }
    }
}
