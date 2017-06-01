package com.oureda.thunder.daydaypicture.base;

/**
 * Created by thunder on 17-5-26.
 */

public class User {
    public Data data;
    public class Data{
     private int screen_id;
     private String username;
     private String version;

        public int getScreen_id() {
            return screen_id;
        }

        public void setScreen_id(int screen_id) {
            this.screen_id = screen_id;
        }

        public String  getUsername() {
            return username;
        }


        public String getVersion() {
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }
    }
}
