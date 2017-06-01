package com.oureda.thunder.daydaypicture.base;

import org.litepal.crud.DataSupport;

/**
 * Created by thunder on 17-5-30.
 */

public class PictureOrderSave extends DataSupport {
    private int order;

    public PictureOrderSave(int order) {
        this.order = order;
    }

    public int getOrder() {
        return order;
    }
}
