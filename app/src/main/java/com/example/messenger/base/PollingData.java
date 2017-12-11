package com.example.messenger.base;

/**
 * Created by thunder on 17-7-11.
 */

public class PollingData {
    public Data data;
    public static class Data{
        private String md5;
        private String time;

        public String getMd5() {
            return md5;
        }

        public String getTime() {
            return time;
        }

    }
}
