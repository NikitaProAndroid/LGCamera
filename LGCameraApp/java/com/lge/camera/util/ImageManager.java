package com.lge.camera.util;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.location.Location;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.FunctionProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.properties.ProjectVariables;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.StringTokenizer;

public class ImageManager {
    private static final Uri STORAGE_URI;
    private static final String TAG = "CameraApp";

    static {
        STORAGE_URI = Media.EXTERNAL_CONTENT_URI;
    }

    public static int roundOrientation(int orientationInput) {
        if (orientationInput < 0) {
            orientationInput = 0;
        }
        return (((orientationInput + 45) / 90) * 90) % CameraConstants.DEGREE_360;
    }

    public static void changeImageTitle(ContentResolver cr, Uri uri, String path, String filename) {
        String title = new StringTokenizer(filename, CameraConstants.TIME_MACHINE_TEMPFILE_EXT).nextToken();
        CamLog.d(TAG, "change image uri title to = " + title);
        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("_data", path + filename);
        values.put("_display_name", filename);
        try {
            cr.update(uri, values, null, null);
        } catch (Exception ex) {
            CamLog.e(TAG, "changeImageTitle() Exception during update ContentResolver : " + ex.toString());
        }
    }

    public static void deleteImage(ContentResolver cr, Uri uri) {
        try {
            CamLog.d(TAG, "ImageManager delete uri = " + uri + " / result = " + cr.delete(uri, null, null));
        } catch (Exception e) {
            CamLog.e(TAG, "database delete error", e);
        }
    }

    public static Intent createSetAsIntent(Uri uri) {
        Intent intent = new Intent("android.intent.action.ATTACH_DATA");
        intent.setDataAndType(uri, MultimediaProperties.IMAGE_MIME_TYPE);
        intent.putExtra("mimeType", MultimediaProperties.IMAGE_MIME_TYPE);
        return intent;
    }

    public static Uri insertToContentResolver(ContentResolver cr, String title, long dateTaken, Location location, String directory, String filename, int degree, boolean isBurst) {
        double latitude = 0.0d;
        double longitude = 0.0d;
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        }
        return insertToContentResolver(cr, title, dateTaken, latitude, longitude, directory, filename, degree, isBurst);
    }

    public static Uri insertToContentResolver(ContentResolver cr, String title, long dateTaken, double latitude, double longitude, String directory, String filename, int degree, boolean isBurst) {
        int contentCount;
        String filePath = directory + filename;
        if (Double.compare(latitude, 0.0d) == 0 || Double.compare(longitude, 0.0d) == 0) {
            contentCount = 10;
        } else {
            contentCount = 12;
        }
        File file = new File(directory + filename);
        long size = 0;
        if (file != null) {
            size = file.length();
        }
        Uri resultUri = null;
        boolean bExist = false;
        Cursor cursor = null;
        try {
            ContentResolver contentResolver = cr;
            cursor = contentResolver.query(STORAGE_URI, new String[]{"_display_name", "_data"}, "_display_name=? and _data=? and mime_type='image/jpeg'", new String[]{filename, filePath}, null);
            if (cursor != null && cursor.getCount() > 0) {
                bExist = true;
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            CamLog.e(TAG, "error insert database", e);
            if (cursor != null) {
                cursor.close();
            }
        } catch (Throwable th) {
            if (cursor != null) {
                cursor.close();
            }
        }
        int width = ExifUtil.getExifWidth(directory + filename);
        int height = ExifUtil.getExifHeight(directory + filename);
        try {
            ContentValues contentValues = new ContentValues(contentCount);
            contentValues.put("title", title);
            CamLog.i(TAG, "set uri TITLE = " + title);
            contentValues.put("_display_name", filename);
            CamLog.i(TAG, "set uri DISPLAY_NAME = " + filename);
            contentValues.put("datetaken", Long.valueOf(dateTaken));
            CamLog.i(TAG, "set uri DATE_TAKEN = " + dateTaken);
            contentValues.put("date_modified", Long.valueOf(dateTaken / 1000));
            contentValues.put("mime_type", MultimediaProperties.IMAGE_MIME_TYPE);
            contentValues.put("orientation", Integer.valueOf(degree));
            CamLog.i(TAG, "set uri orientation = " + degree);
            contentValues.put("_data", filePath);
            CamLog.i(TAG, "set uri DATA = " + filePath);
            contentValues.put("_size", Long.valueOf(size));
            CamLog.i(TAG, "set uri SIZE = " + size);
            contentValues.put("width", Integer.valueOf(width));
            contentValues.put("height", Integer.valueOf(height));
            CamLog.d(TAG, "set uri WIDTH = " + width + ", HEIGHT = " + height);
            if (ProjectVariables.isAppliedBurstPlayer()) {
                if (isBurst) {
                    String burstID = getBurstID(filename);
                    contentValues.put("burst_id", burstID);
                    CamLog.d(TAG, "set burst_id = " + burstID);
                } else {
                    contentValues.put("burst_id", filename);
                    CamLog.d(TAG, "set burst_id = " + filename);
                }
            }
            if (!(Double.compare(latitude, 0.0d) == 0 || Double.compare(longitude, 0.0d) == 0)) {
                contentValues.put("latitude", Double.valueOf(latitude));
                contentValues.put("longitude", Double.valueOf(longitude));
            }
            if (bExist) {
                ContentResolver contentResolver2 = cr;
                ContentValues contentValues2 = contentValues;
                contentResolver2.update(STORAGE_URI, contentValues2, "_display_name=? and _data=? and mime_type='image/jpeg'", new String[]{filename, filePath});
            } else {
                CamLog.d(TAG, "##DEV:pre insert cr");
                resultUri = cr.insert(STORAGE_URI, contentValues);
                CamLog.d(TAG, "##DEV:after insert cr");
            }
        } catch (Exception e2) {
            CamLog.e(TAG, "error insert database", e2);
        }
        CamLog.i(TAG, "return resultUri = " + resultUri);
        return resultUri;
    }

    public static Uri addImage(ContentResolver cr, String title, long dateTaken, Location location, String directory, String filename, Bitmap source, byte[] jpegData, int degree, boolean isBurst) {
        FileNotFoundException ex;
        Throwable th;
        IOException ex2;
        CamLog.i(TAG, "addImage-start:" + filename);
        OutputStream outputStream = null;
        try {
            File dir = new File(directory);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            OutputStream fileOutputStream = new FileOutputStream(new File(directory, filename));
            if (source != null) {
                try {
                    source.compress(CompressFormat.JPEG, 95, fileOutputStream);
                } catch (FileNotFoundException e) {
                    ex = e;
                    outputStream = fileOutputStream;
                    try {
                        CamLog.w(TAG, ex.toString(), ex);
                        if (outputStream != null) {
                            try {
                                outputStream.close();
                            } catch (IOException e2) {
                                e2.printStackTrace();
                            }
                        }
                        return null;
                    } catch (Throwable th2) {
                        th = th2;
                        if (outputStream != null) {
                            try {
                                outputStream.close();
                            } catch (IOException e22) {
                                e22.printStackTrace();
                            }
                        }
                        throw th;
                    }
                } catch (IOException e3) {
                    ex2 = e3;
                    outputStream = fileOutputStream;
                    CamLog.w(TAG, ex2.toString(), ex2);
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e222) {
                            e222.printStackTrace();
                        }
                    }
                    return null;
                } catch (Throwable th3) {
                    th = th3;
                    outputStream = fileOutputStream;
                    if (outputStream != null) {
                        outputStream.close();
                    }
                    throw th;
                }
            }
            fileOutputStream.write(jpegData);
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e2222) {
                    e2222.printStackTrace();
                }
            } else {
                outputStream = fileOutputStream;
            }
            CamLog.d(TAG, "addImage-end");
            if (cr == null) {
                return null;
            }
            if (FunctionProperties.isSupportRotateSaveImage()) {
                degree = ExifUtil.getExifOrientationDegree(directory + filename);
            }
            return insertToContentResolver(cr, title, dateTaken, location, directory, filename, degree, isBurst);
        } catch (FileNotFoundException e4) {
            ex = e4;
            CamLog.w(TAG, ex.toString(), ex);
            if (outputStream != null) {
                outputStream.close();
            }
            return null;
        } catch (IOException e5) {
            ex2 = e5;
            CamLog.w(TAG, ex2.toString(), ex2);
            if (outputStream != null) {
                outputStream.close();
            }
            return null;
        }
    }

    public static Uri addImage(ContentResolver cr, String title, long dateTaken, Location location, String directory, String filename, int degree, boolean isBurst) {
        if (cr != null) {
            return insertToContentResolver(cr, title, dateTaken, location, directory, filename, degree, isBurst);
        }
        return null;
    }

    public static Uri addJpegImage(ContentResolver cr, String title, long dateTaken, byte[] jpegData, Location location, String directory, String filename, int degree, boolean isBurst) {
        FileNotFoundException ex;
        Throwable th;
        IOException ex2;
        CamLog.d(TAG, "addJpegImage : " + title);
        OutputStream outputStream = null;
        String filePath = directory + filename;
        try {
            File dir = new File(directory);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            OutputStream outputStream2 = new FileOutputStream(new File(directory, filename));
            try {
                int contentCount;
                outputStream2.write(jpegData);
                if (outputStream2 != null) {
                    try {
                        outputStream2.close();
                    } catch (Throwable t) {
                        CamLog.e(TAG, "addJpegImage : " + t);
                        outputStream = outputStream2;
                    }
                }
                if (location == null) {
                    contentCount = 10;
                } else {
                    contentCount = 12;
                }
                int width = ExifUtil.getExifWidth(directory + filename);
                int height = ExifUtil.getExifHeight(directory + filename);
                long size = new File(directory + filename).length();
                CamLog.d(TAG, "davidsnam directory : " + directory + filename);
                try {
                    ContentValues contentValues = new ContentValues(contentCount);
                    contentValues.put("title", title);
                    CamLog.d(TAG, "set uri TITLE = " + title);
                    contentValues.put("_display_name", filename);
                    CamLog.d(TAG, "set uri DISPLAY_NAME = " + filename);
                    contentValues.put("datetaken", Long.valueOf(dateTaken));
                    CamLog.d(TAG, "set uri DATE_TAKEN = " + dateTaken);
                    contentValues.put("date_modified", Long.valueOf(dateTaken / 1000));
                    contentValues.put("mime_type", MultimediaProperties.IMAGE_MIME_TYPE);
                    contentValues.put("orientation", Integer.valueOf(degree));
                    contentValues.put("_data", filePath);
                    CamLog.d(TAG, "set uri DATA = " + filePath);
                    contentValues.put("_size", Long.valueOf(size));
                    CamLog.d(TAG, "set uri SIZE = " + size);
                    contentValues.put("width", Integer.valueOf(width));
                    contentValues.put("height", Integer.valueOf(height));
                    CamLog.d(TAG, "set uri WIDTH = " + width + ", HEIGHT = " + height);
                    if (location != null) {
                        contentValues.put("latitude", Double.valueOf(location.getLatitude()));
                        contentValues.put("longitude", Double.valueOf(location.getLongitude()));
                    }
                    if (ProjectVariables.isAppliedBurstPlayer()) {
                        if (isBurst) {
                            String burstID = getBurstID(filename);
                            contentValues.put("burst_id", burstID);
                            CamLog.d(TAG, "set burst_id = " + burstID);
                        } else {
                            contentValues.put("burst_id", filename);
                            CamLog.d(TAG, "set burst_id = " + filename);
                        }
                    }
                    CamLog.d(TAG, "##DEV:pre insert jpeg");
                    Uri resultUri = cr.insert(STORAGE_URI, contentValues);
                    CamLog.d(TAG, "##DEV:after insert jpeg");
                    return resultUri;
                } catch (Exception e) {
                    CamLog.e(TAG, "error insert database", e);
                    return null;
                }
            } catch (FileNotFoundException e2) {
                ex = e2;
                outputStream = outputStream2;
                try {
                    CamLog.w(TAG, ex.toString(), ex);
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (Throwable t2) {
                            CamLog.e(TAG, "addJpegImage : " + t2);
                            return null;
                        }
                    }
                    return null;
                } catch (Throwable th2) {
                    th = th2;
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (Throwable t22) {
                            CamLog.e(TAG, "addJpegImage : " + t22);
                            throw th;
                        }
                    }
                    throw th;
                }
            } catch (IOException e3) {
                ex2 = e3;
                outputStream = outputStream2;
                CamLog.w(TAG, ex2.toString(), ex2);
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (Throwable t222) {
                        CamLog.e(TAG, "addJpegImage : " + t222);
                        return null;
                    }
                }
                return null;
            } catch (Throwable th3) {
                th = th3;
                outputStream = outputStream2;
                if (outputStream != null) {
                    outputStream.close();
                }
                throw th;
            }
        } catch (FileNotFoundException e4) {
            ex = e4;
            CamLog.w(TAG, ex.toString(), ex);
            if (outputStream != null) {
                outputStream.close();
            }
            return null;
        } catch (IOException e5) {
            ex2 = e5;
            CamLog.w(TAG, ex2.toString(), ex2);
            if (outputStream != null) {
                outputStream.close();
            }
            return null;
        }
    }

    public static boolean saveTempFileForTimeMachineShot(byte[] jpegData, String directory, String filename, String ext) {
        FileNotFoundException ex;
        Throwable th;
        IOException ex2;
        CamLog.i(TAG, "saveTempFileForTimeMachineShot-start:" + filename);
        OutputStream outputStream = null;
        try {
            File dir = new File(directory);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            OutputStream outputStream2 = new FileOutputStream(new File(directory, filename + ext));
            try {
                outputStream2.write(jpegData);
                if (outputStream2 != null) {
                    try {
                        outputStream2.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                CamLog.d(TAG, "saveTempFileForTimeMachineShot-end");
                return true;
            } catch (FileNotFoundException e2) {
                ex = e2;
                outputStream = outputStream2;
                try {
                    CamLog.w(TAG, ex.toString(), ex);
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e3) {
                            e3.printStackTrace();
                        }
                    }
                    return false;
                } catch (Throwable th2) {
                    th = th2;
                    if (outputStream != null) {
                        try {
                            outputStream.close();
                        } catch (IOException e32) {
                            e32.printStackTrace();
                        }
                    }
                    throw th;
                }
            } catch (IOException e4) {
                ex2 = e4;
                outputStream = outputStream2;
                CamLog.w(TAG, ex2.toString(), ex2);
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (IOException e322) {
                        e322.printStackTrace();
                    }
                }
                return false;
            } catch (Throwable th3) {
                th = th3;
                outputStream = outputStream2;
                if (outputStream != null) {
                    outputStream.close();
                }
                throw th;
            }
        } catch (FileNotFoundException e5) {
            ex = e5;
            CamLog.w(TAG, ex.toString(), ex);
            if (outputStream != null) {
                outputStream.close();
            }
            return false;
        } catch (IOException e6) {
            ex2 = e6;
            CamLog.w(TAG, ex2.toString(), ex2);
            if (outputStream != null) {
                outputStream.close();
            }
            return false;
        }
    }

    public static boolean isMediaScannerScanning(ContentResolver cr) {
        boolean result = false;
        Cursor cursor = cr.query(MediaStore.getMediaScannerUri(), new String[]{"volume"}, null, null, null);
        if (cursor != null) {
            try {
                if (cursor.getCount() == 1) {
                    cursor.moveToFirst();
                    result = "external".equals(cursor.getString(0));
                }
            } catch (Exception ex) {
                CamLog.e(TAG, "isMediaScannerScanning() Exception! " + ex.toString());
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Throwable th) {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        if (cursor != null) {
            cursor.close();
        }
        return result;
    }

    public static Bitmap loadBitmap(ContentResolver cr, String strURI, boolean isThumb, int sampleSize) {
        CamLog.i(TAG, "loadBitmp uri = " + strURI);
        Bitmap bitmap = null;
        if (strURI == null) {
            return null;
        }
        Uri uri = Uri.parse(strURI);
        if (uri != null) {
            Options opts;
            if (uri.getScheme().compareToIgnoreCase("file") != 0) {
                ParcelFileDescriptor pfd = null;
                try {
                    pfd = cr.openFileDescriptor(uri, "r");
                    if (pfd != null) {
                        FileDescriptor fd = pfd.getFileDescriptor();
                        opts = new Options();
                        opts.inDither = true;
                        if (isThumb) {
                            sampleSize = 8;
                        }
                        opts.inSampleSize = sampleSize;
                        bitmap = BitmapFactory.decodeFileDescriptor(fd, null, opts);
                    }
                    if (pfd != null) {
                        try {
                            pfd.close();
                        } catch (IOException ex) {
                            CamLog.e(TAG, "loadBitmap() IOException! " + ex);
                        }
                    }
                } catch (Exception ex2) {
                    CamLog.e(TAG, "loadBitmap() Exception! " + ex2);
                    if (pfd != null) {
                        try {
                            pfd.close();
                        } catch (IOException ex3) {
                            CamLog.e(TAG, "loadBitmap() IOException! " + ex3);
                        }
                    }
                } catch (Throwable th) {
                    if (pfd != null) {
                        try {
                            pfd.close();
                        } catch (IOException ex32) {
                            CamLog.e(TAG, "loadBitmap() IOException! " + ex32);
                        }
                    }
                }
            } else {
                String filePath = uri.getPath();
                opts = new Options();
                opts.inDither = true;
                if (isThumb) {
                    sampleSize = 8;
                }
                opts.inSampleSize = sampleSize;
                bitmap = BitmapFactory.decodeFile(filePath, opts);
            }
        }
        return bitmap;
    }

    public static Bitmap loadScaledBitmap(ContentResolver cr, String strURI, int dstWidth, int dstHeight) {
        CamLog.i(TAG, "loadBitmp uri = " + strURI);
        Bitmap resizeBmp = null;
        if (strURI == null) {
            return null;
        }
        Uri uri = Uri.parse(strURI);
        if (uri != null) {
            Options opts;
            Bitmap bitmap;
            String strScheme = uri.getScheme();
            if (strScheme != null) {
                if (strScheme.compareToIgnoreCase("file") != 0) {
                    ParcelFileDescriptor pfd = null;
                    try {
                        pfd = cr.openFileDescriptor(uri, "r");
                        if (pfd != null) {
                            FileDescriptor fd = pfd.getFileDescriptor();
                            opts = new Options();
                            opts.inDither = true;
                            opts.inSampleSize = Util.getSampleSize(null, fd, null, opts, dstWidth, dstHeight);
                            bitmap = BitmapFactory.decodeFileDescriptor(fd, null, opts);
                            if (bitmap != null) {
                                if (bitmap.getWidth() == dstWidth && bitmap.getHeight() == dstHeight) {
                                    if (pfd != null) {
                                        try {
                                            pfd.close();
                                        } catch (IOException ex) {
                                            CamLog.e(TAG, "loadScaledBitmap() IOException! " + ex);
                                        }
                                    }
                                    return bitmap;
                                }
                                resizeBmp = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, true);
                                bitmap.recycle();
                            }
                        }
                        if (pfd != null) {
                            try {
                                pfd.close();
                            } catch (IOException ex2) {
                                CamLog.e(TAG, "loadScaledBitmap() IOException! " + ex2);
                            }
                        }
                    } catch (Exception ex3) {
                        CamLog.e(TAG, "loadScaledBitmap() Exception! " + ex3);
                        if (pfd != null) {
                            try {
                                pfd.close();
                            } catch (IOException ex22) {
                                CamLog.e(TAG, "loadScaledBitmap() IOException! " + ex22);
                            }
                        }
                    } catch (Throwable th) {
                        if (pfd != null) {
                            try {
                                pfd.close();
                            } catch (IOException ex222) {
                                CamLog.e(TAG, "loadScaledBitmap() IOException! " + ex222);
                            }
                        }
                    }
                }
            }
            String filePath = uri.getPath();
            opts = new Options();
            opts.inDither = true;
            opts.inSampleSize = Util.getSampleSize(null, null, filePath, opts, dstWidth, dstHeight);
            bitmap = BitmapFactory.decodeFile(filePath, opts);
            if (bitmap != null) {
                if (bitmap.getWidth() == dstWidth && bitmap.getHeight() == dstHeight) {
                    return bitmap;
                }
                resizeBmp = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, true);
                bitmap.recycle();
            }
        }
        return resizeBmp;
    }

    private static String getBurstID(String filename) {
        String burstID = "";
        String[] arr = filename.split("_");
        return arr[0] + "_" + arr[1];
    }
}
