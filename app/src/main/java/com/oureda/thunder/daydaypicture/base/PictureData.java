package com.oureda.thunder.daydaypicture.base;

import java.util.List;

/**
 * Created by thunder on 17-5-27.
 */

public class PictureData {
    public Data data;
    public static class Data{
        public List<Picture> picture;

        public static class Picture{
            public String getName() {
                return name;
            }

            public String getUrl() {
                return url;
            }

            private String name;
            private String url;
            private String picture_id;

            public String getPicture_id() {
                return picture_id;
            }
        }
    }

}
