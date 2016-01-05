package com.lge.morpho.utils.multimedia;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.provider.MediaStore.Images.Media;
import android.provider.MediaStore.Images.Thumbnails;
import android.provider.MediaStore.Video;
import android.text.format.DateFormat;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.util.CamLog;
import com.lge.morpho.app.morphopanorama.PanoramaApplication;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class MediaProviderUtils {
    private static final String LOG_TAG = "MediaProviderUtils";
    public static final int ROTATION_180 = 180;
    public static final int ROTATION_270 = 270;
    public static final int ROTATION_90 = 90;
    public static final int ROTATION_NORMAL = 0;

    public static Uri getExternalImageContentUri(ContentResolver cr, String filePath) {
        return getImageContentUri(cr, filePath, Media.EXTERNAL_CONTENT_URI);
    }

    public static Uri getInternalImageContentUri(ContentResolver cr, String filePath) {
        return getImageContentUri(cr, filePath, Media.INTERNAL_CONTENT_URI);
    }

    private static Uri getImageContentUri(ContentResolver cr, String filePath, Uri storageUri) {
        if (cr == null || filePath == null) {
            return null;
        }
        ContentResolver contentResolver = cr;
        Uri uri = storageUri;
        Cursor cursor = contentResolver.query(uri, new String[]{"_id"}, "_data=?", new String[]{filePath}, "bucket_display_name");
        if (cursor == null) {
            return null;
        }
        if (cursor.getCount() <= 0) {
            return null;
        }
        cursor.moveToFirst();
        return Uri.parse(storageUri.toString() + File.separator + cursor.getString(cursor.getColumnIndex("_id")));
    }

    public static String getImageFilePath(ContentResolver cr, Uri uri) {
        return getContentFilePath(cr, uri, "_data");
    }

    public static String getVideoFilePath(ContentResolver cr, Uri uri) {
        return getContentFilePath(cr, uri, "_data");
    }

    private static String getContentFilePath(ContentResolver cr, Uri uri, String colunmName) {
        if (cr == null || uri == null) {
            return null;
        }
        Cursor cursor = cr.query(uri, new String[]{colunmName}, null, null, null);
        if (cursor == null || cursor.getCount() <= 0) {
            return null;
        }
        cursor.moveToFirst();
        return cursor.getString(cursor.getColumnIndex(colunmName));
    }

    public static Uri addImageExternal(ContentResolver cr, String filePath, String mime, int rotation) {
        return addImage(cr, filePath, mime, rotation, Media.EXTERNAL_CONTENT_URI);
    }

    public static Uri addImageExternal(ContentResolver cr, String filePath, String mime, int rotation, long dateTaken, Location location) {
        return addImage(cr, filePath, mime, rotation, dateTaken, location, Media.EXTERNAL_CONTENT_URI);
    }

    public static Uri addImageInternal(ContentResolver cr, String filePath, String mime, int rotation) {
        return addImage(cr, filePath, mime, rotation, Media.INTERNAL_CONTENT_URI);
    }

    private static Uri addImage(ContentResolver cr, String filePath, String mime, int rotation, Uri storageUri) {
        if (cr == null || filePath == null || mime == null) {
            return null;
        }
        File file = new File(filePath);
        String fileName = file.getName();
        ContentValues contentValues = new ContentValues(9);
        long time = System.currentTimeMillis();
        if (!(rotation == 0 || rotation == ROTATION_90 || rotation == ROTATION_180 || rotation == ROTATION_270)) {
            rotation = 0;
        }
        contentValues.put("title", fileName);
        contentValues.put("_display_name", fileName);
        contentValues.put("datetaken", Long.valueOf(time));
        contentValues.put("date_added", Long.valueOf(time / 1000));
        contentValues.put("date_modified", Long.valueOf(time / 1000));
        contentValues.put("mime_type", mime);
        contentValues.put("orientation", Integer.valueOf(rotation));
        contentValues.put("_data", filePath);
        contentValues.put("_size", Long.valueOf(file.length()));
        if (ProjectVariables.isAppliedBurstPlayer()) {
            contentValues.put("burst_id", fileName);
        }
        return cr.insert(storageUri, contentValues);
    }

    public static Uri addImage(ContentResolver cr, String filePath, String mime, int rotation, long dateTaken, Location location, Uri storageUri) {
        if (cr == null || filePath == null || mime == null) {
            return null;
        }
        File file = new File(filePath);
        String fileName = file.getName();
        ContentValues contentValues = new ContentValues(11);
        long time = System.currentTimeMillis();
        if (!(rotation == 0 || rotation == ROTATION_90 || rotation == ROTATION_180 || rotation == ROTATION_270)) {
            rotation = 0;
        }
        if (location != null) {
            contentValues.put("latitude", Double.valueOf(location.getLatitude()));
            contentValues.put("longitude", Double.valueOf(location.getLongitude()));
        }
        contentValues.put("title", fileName);
        contentValues.put("_display_name", fileName);
        contentValues.put("datetaken", Long.valueOf(dateTaken));
        contentValues.put("date_added", Long.valueOf(time / 1000));
        contentValues.put("date_modified", Long.valueOf(time / 1000));
        contentValues.put("mime_type", mime);
        contentValues.put("orientation", Integer.valueOf(rotation));
        contentValues.put("_data", filePath);
        contentValues.put("_size", Long.valueOf(file.length()));
        if (ProjectVariables.isAppliedBurstPlayer()) {
            contentValues.put("burst_id", fileName);
        }
        return cr.insert(storageUri, contentValues);
    }

    public static int deleteImageExternal(ContentResolver cr, String filePath) {
        return deleteImage(cr, filePath, Media.EXTERNAL_CONTENT_URI);
    }

    public static int deleteImageInternal(ContentResolver cr, String filePath) {
        return deleteImage(cr, filePath, Media.INTERNAL_CONTENT_URI);
    }

    private static int deleteImage(ContentResolver cr, String filePath, Uri storageUri) {
        int result = -1;
        if (cr == null || filePath == null) {
            return -1;
        }
        ContentResolver contentResolver = cr;
        Uri uri = storageUri;
        Cursor cursor = contentResolver.query(uri, new String[]{"_id"}, "_data=?", new String[]{filePath}, "bucket_display_name");
        if (cursor != null) {
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                result = cr.delete(ContentUris.appendId(storageUri.buildUpon(), cursor.getLong(cursor.getColumnIndex("_id"))).build(), null, null);
            }
            cursor.close();
        }
        return result;
    }

    public static Uri addVideoExternal(ContentResolver cr, String filePath, String mime, long duration) {
        return addVideo(cr, filePath, mime, duration, Video.Media.EXTERNAL_CONTENT_URI);
    }

    public static Uri addVideoInternal(ContentResolver cr, String filePath, String mime, long duration) {
        return addVideo(cr, filePath, mime, duration, Video.Media.INTERNAL_CONTENT_URI);
    }

    private static Uri addVideo(ContentResolver cr, String filePath, String mime, long duration, Uri storageUri) {
        if (cr == null || filePath == null || mime == null) {
            return null;
        }
        File file = new File(filePath);
        String fileName = file.getName();
        ContentValues contentValues = new ContentValues(9);
        long time = System.currentTimeMillis();
        contentValues.put("title", fileName);
        contentValues.put("_display_name", fileName);
        contentValues.put("datetaken", Long.valueOf(time));
        contentValues.put("date_added", Long.valueOf(time / 1000));
        contentValues.put("date_modified", Long.valueOf(time / 1000));
        contentValues.put("mime_type", mime);
        contentValues.put("_data", filePath);
        contentValues.put("_size", Long.valueOf(file.length()));
        if (duration > 0) {
            contentValues.put("duration", Long.toString(duration));
        }
        if (ProjectVariables.isAppliedBurstPlayer()) {
            contentValues.put("burst_id", fileName);
        }
        return cr.insert(storageUri, contentValues);
    }

    public static int deleteVideoExternal(ContentResolver cr, String filePath) {
        return deleteVideo(cr, filePath, Video.Media.EXTERNAL_CONTENT_URI);
    }

    public static int deleteVideoInternal(ContentResolver cr, String filePath) {
        return deleteVideo(cr, filePath, Video.Media.INTERNAL_CONTENT_URI);
    }

    private static int deleteVideo(ContentResolver cr, String filePath, Uri storageUri) {
        int result = -1;
        if (cr == null || filePath == null) {
            return -1;
        }
        ContentResolver contentResolver = cr;
        Uri uri = storageUri;
        Cursor cursor = contentResolver.query(uri, new String[]{"_id"}, "_data=?", new String[]{filePath}, "title");
        if (cursor != null) {
            if (cursor.getCount() != 0) {
                cursor.moveToFirst();
                result = cr.delete(ContentUris.appendId(storageUri.buildUpon(), cursor.getLong(cursor.getColumnIndex("_id"))).build(), null, null);
            }
            cursor.close();
        }
        return result;
    }

    public static Bitmap getImageThumbnailBitmapExternal(ContentResolver cr, String filePath, int size) {
        return getImageThumbnailBitmap(cr, filePath, size, Media.EXTERNAL_CONTENT_URI);
    }

    public static Bitmap getImageThumbnailBitmapInternal(ContentResolver cr, String filePath, int size) {
        return getImageThumbnailBitmap(cr, filePath, size, Media.INTERNAL_CONTENT_URI);
    }

    private static Bitmap getImageThumbnailBitmap(ContentResolver cr, String filePath, int size, Uri storageUri) {
        Bitmap thumbnail = null;
        if (cr == null || filePath == null) {
            return null;
        }
        if (!(size == 1 || size == 3)) {
            size = 1;
        }
        ContentResolver contentResolver = cr;
        Uri uri = storageUri;
        Cursor cursor = contentResolver.query(uri, new String[]{"_id"}, "_data=?", new String[]{filePath}, "bucket_display_name");
        if (cursor != null) {
            int count = cursor.getCount();
            int index = cursor.getColumnIndex("_id");
            int[] videoIds = new int[count];
            cursor.moveToFirst();
            for (int i = 0; i < count; i++) {
                videoIds[i] = cursor.getInt(index);
                thumbnail = Thumbnails.getThumbnail(cr, (long) videoIds[i], size, null);
                if (thumbnail != null) {
                    break;
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        return thumbnail;
    }

    public static Bitmap getVideoThumbnailBitmapExternal(ContentResolver cr, String filePath, int size) {
        return getVideoThumbnailBitmap(cr, filePath, size, Video.Media.EXTERNAL_CONTENT_URI);
    }

    public static Bitmap getVideoThumbnailBitmapInternal(ContentResolver cr, String filePath, int size) {
        return getVideoThumbnailBitmap(cr, filePath, size, Video.Media.INTERNAL_CONTENT_URI);
    }

    private static Bitmap getVideoThumbnailBitmap(ContentResolver cr, String filePath, int size, Uri storageUri) {
        Bitmap thumbnail = null;
        if (cr == null || filePath == null) {
            return null;
        }
        if (!(size == 1 || size == 3)) {
            size = 1;
        }
        ContentResolver contentResolver = cr;
        Uri uri = storageUri;
        Cursor cursor = contentResolver.query(uri, new String[]{"_id"}, "_data=?", new String[]{filePath}, "title");
        if (cursor != null) {
            int count = cursor.getCount();
            int index = cursor.getColumnIndex("_id");
            int[] videoIds = new int[count];
            cursor.moveToFirst();
            for (int i = 0; i < count; i++) {
                videoIds[i] = cursor.getInt(index);
                thumbnail = Video.Thumbnails.getThumbnail(cr, (long) videoIds[i], size, null);
                if (thumbnail != null) {
                    break;
                }
                cursor.moveToNext();
            }
            cursor.close();
        }
        return thumbnail;
    }

    public static String createDateStringForAppSeg(long dateTaken) {
        Date date = new Date(dateTaken);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        return sdf.format(date);
    }

    public static String createName(long dateTaken) {
        return DateFormat.format("yyyy-MM-dd_kk-mm-ss", dateTaken).toString();
    }

    public static String getSaveFileName(String dir_path, String filename) {
        File file = new File(dir_path, filename);
        int sequentialNo = 0;
        String orgFileName = filename;
        while (file.exists()) {
            sequentialNo++;
            String[] str = orgFileName.split("\\.");
            filename = str[0] + "-" + Integer.toString(sequentialNo) + "." + str[1];
            file = new File(dir_path, filename);
            CamLog.d(LOG_TAG, "NewFilename:" + filename);
            if (sequentialNo >= PanoramaApplication.SENSOR_CORRECTION_EXTRA_TIME) {
                CamLog.e(LOG_TAG, "NewFilename 1000 count over!!");
                return null;
            }
        }
        return filename;
    }
}
