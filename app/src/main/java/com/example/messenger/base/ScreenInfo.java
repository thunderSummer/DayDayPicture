package com.example.messenger.base;

/**
 * Created by thunder on 17-7-9.
 */

public class ScreenInfo {
    public  Data data;

    public static class Data{
        private String url;
        private String md5;
        private String time;
        private String json_url;

        public String getUrl() {
            return url;
        }


        public String getJson_url() {
            return json_url;
        }
        private int is_user;

        public int getIs_user() {
            return is_user;
        }

        public void setIs_user(int is_user) {
            this.is_user = is_user;
        }

        public String getTime() {
            return time;
        }

        public String getMd5() {
            return md5;
        }
    }


}
