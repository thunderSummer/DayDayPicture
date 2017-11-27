package com.oureda.thunder.daydaypicture.util;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.NonNull;

import com.oureda.thunder.daydaypicture.base.MyApplication;

import java.io.File;
import java.io.IOException;

/**
 * Created by thunder on 17-5-26.
 */

public class FileUtil {
    public static String getPictureHome(){
        return getRootPath(MyApplication.getContext())+"/picture/";
    }
    public static String getPicturePath(String pictureId){
        return getPictureHome()+pictureId;
    }
    public static File getPictureFile(String pictureId){
        File file= new File(getPicturePath(pictureId));
        if(file.exists()){
            return null;
        }else{
            createFile(file);
        }
        return file;

    }

    public static  String getRootPath(Context context){
        String rootPath ="";
        if(isSdCardAvailable()){
            rootPath=context.getExternalCacheDir().getPath();
        }else{
            rootPath=context.getCacheDir().getPath();
        }
        return rootPath;
    }

    private static boolean isSdCardAvailable(){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
    private static boolean createFile(File file){
        try{
            if(file.getParentFile().exists()){
                return file.createNewFile();
            }else{
                createDir(file.getParentFile().getAbsolutePath());
                return file.createNewFile();
            }
        }catch (IOException i){
            i.printStackTrace();
        }
        return false;

    }
    public static boolean makeFile(String path){
        File file = new File(path);
        if(file.exists()){
            return true;
        }else{
            if(file.isDirectory()){
                createDir(path);
            }else{
                createFile(file);
            }
            return true;
        }
    }
    private static boolean createDir(String path){
        File file =new File(path);
        if(file.getParentFile().exists()){
            return file.mkdir();
        }else{
            createDir(file.getParentFile().getAbsolutePath());
            return file.mkdir();
        }
    }
    /**
     * 删除指定文件
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static boolean deleteFile(File file) throws IOException {
        return deleteFileOrDirectory(file);
    }

    /**
     * 删除指定文件，如果是文件夹，则递归删除
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static boolean deleteFileOrDirectory(File file) {
        try {
            if (file != null && file.isFile()) {
                return file.delete();
            }
            if (file != null && file.isDirectory()) {
                File[] childFiles = file.listFiles();
                // 删除空文件夹
                if (childFiles == null || childFiles.length == 0) {
                    return file.delete();
                }
                // 递归删除文件夹下的子文件
                for (int i = 0; i < childFiles.length; i++) {
                    deleteFileOrDirectory(childFiles[i]);
                }
                return file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
