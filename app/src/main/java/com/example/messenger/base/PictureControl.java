package com.oureda.thunder.daydaypicture.base;

import android.util.Log;

import com.oureda.thunder.daydaypicture.util.FileUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by thunder on 17-8-12.
 */

public class PictureControl {
    public static List<String> setPicture(Map<String, String> originMap) {
        File file = new File(FileUtil.getPictureHome() + "images");
        List<String> downLoadString = new ArrayList<>();
        if (!file.exists() || file.listFiles() == null || file.listFiles().length == 0) {

            //代表没有图片资源
            for (Map.Entry<String, String> entry : originMap.entrySet()) {
                String fileURL = entry.getKey();
                downLoadString.add(fileURL);

            }
            return downLoadString;
        }

        File[] files = file.listFiles();
        List<File> existList = new ArrayList<>();
        existList.addAll(Arrays.asList(files));

        try {

            for (Map.Entry<String, String> entry : originMap.entrySet()) {
                boolean exist = false;
                String fileURL = entry.getKey();
                String fileMD5 = entry.getValue();
                for (File file1 : files) {
                    if (MD5.getFileMD5String(file1).equalsIgnoreCase(fileMD5)) {
                        exist = true;
                        existList.remove(file1);
                    }
                }
                if (!exist) {
                    downLoadString.add(fileURL);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (File file1 : existList) {
            FileUtil.deleteFileOrDirectory(file1);
        }
        return downLoadString;
    }
}
