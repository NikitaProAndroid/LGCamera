package com.lge.camera.util;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.provider.MediaStore.Images;
import android.provider.MediaStore.Video.Thumbnails;
import java.util.WeakHashMap;

public class BitmapManager {
    private static final String TAG = "CameraApp";
    private static BitmapManager sManager;
    private final WeakHashMap<Thread, ThreadStatus> mThreadStatus;

    private enum State {
        CANCEL,
        ALLOW
    }

    private static class ThreadStatus {
        public Options mOptions;
        public State mState;

        private ThreadStatus() {
            this.mState = State.ALLOW;
        }

        public String toString() {
            String s;
            if (this.mState == State.CANCEL) {
                s = "Cancel";
            } else if (this.mState == State.ALLOW) {
                s = "Allow";
            } else {
                s = "?";
            }
            return "thread state = " + s + ", options = " + this.mOptions;
        }
    }

    static {
        sManager = null;
    }

    private BitmapManager() {
        this.mThreadStatus = new WeakHashMap();
    }

    private synchronized ThreadStatus getOrCreateThreadStatus(Thread t) {
        ThreadStatus status;
        status = (ThreadStatus) this.mThreadStatus.get(t);
        if (status == null) {
            status = new ThreadStatus();
            this.mThreadStatus.put(t, status);
        }
        return status;
    }

    public synchronized boolean canThreadDecoding(Thread t) {
        boolean result = true;
        synchronized (this) {
            ThreadStatus status = (ThreadStatus) this.mThreadStatus.get(t);
            if (status != null) {
                if (status.mState == State.CANCEL) {
                    result = false;
                }
            }
        }
        return result;
    }

    public static String getRealPathFromURI(Activity activity, Uri contentUri) {
        String path = null;
        if (!(activity == null || contentUri == null)) {
            Cursor cursor = null;
            try {
                cursor = activity.getContentResolver().query(contentUri, new String[]{"_data"}, null, null, null);
                if (cursor != null) {
                    int column_index = cursor.getColumnIndexOrThrow("_data");
                    if (cursor.moveToFirst()) {
                        path = cursor.getString(column_index);
                    }
                } else if ("file".equals(contentUri.getScheme())) {
                    path = contentUri.getPath();
                }
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Exception e) {
                CamLog.e(TAG, String.format("failed to get path from uri", new Object[0]));
                e.printStackTrace();
                if (cursor != null) {
                    cursor.close();
                }
            } catch (Throwable th) {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        CamLog.w(TAG, String.format("return null: activity:%s uri:%s", new Object[]{activity, contentUri}));
        return path;
    }

    public Bitmap getThumbnail(ContentResolver cr, long origId, int kind, Options options, boolean isVideo) {
        Thread t = Thread.currentThread();
        ThreadStatus status = getOrCreateThreadStatus(t);
        if (!canThreadDecoding(t)) {
            CamLog.d(TAG, "Thread " + t + " is not allowed to decode.");
            return null;
        } else if (isVideo) {
            try {
                r1 = Thumbnails.getThumbnail(cr, origId, t.getId(), kind, null);
                synchronized (status) {
                    status.notifyAll();
                }
                return r1;
            } catch (Exception e) {
                CamLog.e(TAG, "failed to getThumbnail()");
                e.printStackTrace();
                synchronized (status) {
                }
                status.notifyAll();
                return null;
            } catch (Throwable th) {
                synchronized (status) {
                }
                status.notifyAll();
            }
        } else {
            r1 = Images.Thumbnails.getThumbnail(cr, origId, t.getId(), kind, null);
            synchronized (status) {
                status.notifyAll();
            }
            return r1;
        }
    }

    public static synchronized BitmapManager instance() {
        BitmapManager bitmapManager;
        synchronized (BitmapManager.class) {
            if (sManager == null) {
                sManager = new BitmapManager();
            }
            bitmapManager = sManager;
        }
        return bitmapManager;
    }
}
