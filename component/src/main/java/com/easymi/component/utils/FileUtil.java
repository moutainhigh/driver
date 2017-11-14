package com.easymi.component.utils;


import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import java.io.File;
import java.io.IOException;
import java.util.List;

import okio.BufferedSink;
import okio.BufferedSource;
import okio.Okio;
import okio.Sink;
import okio.Source;

/**
 * Created by xyin on 2016/12/.
 * 文件操作的工具类.
 */

public class FileUtil {

    private static final String TAG = "FileUtil";

    /**
     * 复制文件.
     *
     * @param srcPath    源文件路径
     * @param targetPath 目标文路径
     * @return 是否复制成功
     */
    public static boolean copyFile(String srcPath, String targetPath) {
        LogUtil.d(TAG, "copy file begin");
        boolean isSucceed = false;  //操作文件是否成功
        File srcFile = new File(srcPath);
        if (!srcFile.exists() || !srcFile.isFile()) {
            LogUtil.e(TAG, "src file not exists or not be file");
            return false;
        }

        File targetFile = new File(targetPath);
        if (targetFile.exists()) {
            if (targetFile.delete()) {
                LogUtil.e(TAG, "delete targetFile fail");
                return false;
            }
        }

        BufferedSink bSink = null; //输出buffer
        BufferedSource bSource = null; //输入buffer
        try {
            Source source = Okio.source(srcFile);   //输入流
            bSource = Okio.buffer(source);
            Sink sink = Okio.sink(targetFile); //输出流
            bSink = Okio.buffer(sink);
            bSink.writeAll(bSource);    //写入数据
            bSink.flush();  //flush数据
            isSucceed = true;   //操作成功
        } catch (IOException e) {
            e.printStackTrace();
            isSucceed = false;  //操作失败
        } finally {
            try {
                if (bSink != null) {
                    bSink.close();
                }
                if (bSource != null) {
                    bSource.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                isSucceed = false;  //关闭流失败
            }
        }

        LogUtil.d(TAG, "copy file succeed ? -->" + isSucceed);

        return isSucceed;
    }


    public static String readJsonFromFile(String fileName) {
        File srcFile = new File(fileName);
        if (!srcFile.exists()) {
            return null;
        }

        String json = null;
        BufferedSource bSource = null; //输入buffer
        try {
            Source source = Okio.source(srcFile);   //输入流
            bSource = Okio.buffer(source);
            json = bSource.readUtf8();

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bSource != null) {
                    bSource.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return json;
    }

    /**
     * 通过通过uri删除该文件.
     *
     * @param path 文件绝对路径
     * @return 成功删除返回true, 否则返回false
     */
    public static boolean deleteFile(String path) {
        if (path != null) {
            File file = new File(path);
            return file.exists() && file.delete();
        }
        LogUtil.e(TAG, "path is null");
        return false;
    }

    /**
     * 通过绝对路径的方式批量删除文件.
     *
     * @param paths 绝对路径list
     */
    public static void deleteFiles(List<String> paths) {
        if (paths != null && !paths.isEmpty()) {
            for (String path : paths) {
                deleteFile(path);
            }
        }
    }

    /**
     * 删除目录下所有文件(下级目录不删除).
     *
     * @param dir 需要操作的目录
     */
    public static void clearDir(File dir) {
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return;
        }
        //获取有文件
        File[] files = dir.listFiles();
        if (files == null) {
            return;
        }

        //遍历并删除所有非目录
        for (File file : files) {
            if (file.isFile()) {
                //noinspection ResultOfMethodCallIgnored
                file.delete();
            }
        }
    }

    /**
     * 通过uri获取所表示的文件的绝对路径.
     *
     * @param context context对象
     * @param uri     需要操作的uri
     * @return 返回uri表示的实际路径, 如果获取失败则返回null
     */
    public static String getOriginalPath(Context context, final Uri uri) {
        if (context == null || uri == null) {
            LogUtil.e(TAG, "context is null or uri is null");
            return null;
        }

        //api 19 以上且是Document类型的uri 形式:content://com.android.providers.media.documents/document/image:3951
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }
                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };
                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }// MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    /**
     * 在Resolver在查询uri路径字段.
     *
     * @param context       context
     * @param uri           uri
     * @param selection     selection
     * @param selectionArgs selectionArgs
     * @return 返回路径字段
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {
        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };
        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

}
