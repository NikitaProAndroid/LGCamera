package com.lge.camera;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.MediaStore.Video.Media;
import android.widget.Toast;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.MultimediaProperties;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;
import java.io.File;
import java.lang.ref.WeakReference;

public class VideoFile {
    public static final String VIDEO_EXTENSION_3GP = ".3gp";
    public static final String VIDEO_EXTENSION_MP4 = ".mp4";
    private WeakReference<Context> mContext;
    private File mFile;
    private String mFileDirectory;
    private String mFileExtension;
    private String mFileName;
    private String mFilePath;
    private boolean mInitialized;
    private boolean mOccured_execption;
    private long mRecordingTime_duration;
    private Uri mUri;
    private int misAudiozoomcontent;

    public boolean getAudiozoomExection_state() {
        return this.mOccured_execption;
    }

    public void setAudiozoomExection_state(boolean isOccured) {
        this.mOccured_execption = isOccured;
    }

    public void setAudiozoomcontent(int contenttype) {
        this.misAudiozoomcontent = contenttype;
    }

    public int getAudiozoomcontent() {
        return this.misAudiozoomcontent;
    }

    public void setRecordingTime_duration(long recordingTime_duration) {
        if (recordingTime_duration < 0) {
            recordingTime_duration = 0;
        }
        this.mRecordingTime_duration = recordingTime_duration;
    }

    public VideoFile(Context context, String fileName, int purpose) {
        this.mInitialized = false;
        this.mRecordingTime_duration = 0;
        this.misAudiozoomcontent = 0;
        this.mOccured_execption = false;
        if (purpose == 0) {
            this.mFileName = fileName + VIDEO_EXTENSION_MP4;
            this.mFileExtension = VIDEO_EXTENSION_MP4;
        } else {
            this.mFileName = fileName + VIDEO_EXTENSION_3GP;
            this.mFileExtension = VIDEO_EXTENSION_3GP;
        }
        CamLog.d(FaceDetector.TAG, "mFileName: " + this.mFileName);
        initialize(context);
    }

    public VideoFile(Context context, String directory, String fileName, int purpose) {
        this.mInitialized = false;
        this.mRecordingTime_duration = 0;
        this.misAudiozoomcontent = 0;
        this.mOccured_execption = false;
        if (purpose == 0) {
            this.mFileName = fileName + VIDEO_EXTENSION_MP4;
            this.mFileExtension = VIDEO_EXTENSION_MP4;
        } else {
            this.mFileName = fileName + VIDEO_EXTENSION_3GP;
            this.mFileExtension = VIDEO_EXTENSION_3GP;
        }
        this.mFileDirectory = directory;
        CamLog.d(FaceDetector.TAG, "mFileName: " + this.mFileName);
        initialize(context);
    }

    public VideoFile(Context context, String fileName, boolean testMode, int purpose) {
        this.mInitialized = false;
        this.mRecordingTime_duration = 0;
        this.misAudiozoomcontent = 0;
        this.mOccured_execption = false;
        if (purpose == 0) {
            this.mFileName = fileName + VIDEO_EXTENSION_MP4;
            this.mFileExtension = VIDEO_EXTENSION_MP4;
        } else {
            this.mFileName = fileName + VIDEO_EXTENSION_3GP;
            this.mFileExtension = VIDEO_EXTENSION_3GP;
        }
        CamLog.d(FaceDetector.TAG, "mFileName: " + this.mFileName);
        initialize(context);
    }

    public VideoFile(Context context, String fileName, int storage, int purpose) {
        this.mInitialized = false;
        this.mRecordingTime_duration = 0;
        this.misAudiozoomcontent = 0;
        this.mOccured_execption = false;
        if (purpose == 0) {
            this.mFileName = fileName + VIDEO_EXTENSION_MP4;
            this.mFileExtension = VIDEO_EXTENSION_MP4;
        } else {
            this.mFileName = fileName + VIDEO_EXTENSION_3GP;
            this.mFileExtension = VIDEO_EXTENSION_3GP;
        }
        CamLog.d(FaceDetector.TAG, "mFileName: " + this.mFileName);
        initialize(context, storage);
    }

    public boolean initialize(Context context) {
        this.mContext = new WeakReference(context);
        this.mFilePath = getFilePath();
        this.mFile = getFile();
        this.mInitialized = true;
        return this.mInitialized;
    }

    public boolean initialize(Context context, int storage) {
        this.mContext = new WeakReference(context);
        if (storage == 0) {
            this.mFilePath = getFilePath();
        } else {
            this.mFilePath = getFileExternalPath();
        }
        this.mFile = getFile();
        this.mInitialized = true;
        return this.mInitialized;
    }

    public boolean isInitialized() {
        return this.mInitialized;
    }

    public String getFilePath() {
        if (this.mFilePath == null) {
            this.mFilePath = this.mFileDirectory + this.mFileName;
        }
        return this.mFilePath;
    }

    public String getFileExternalPath() {
        if (this.mFilePath == null) {
            this.mFilePath = this.mFileDirectory + this.mFileName;
        }
        return this.mFilePath;
    }

    public String getFileName() {
        return this.mFileName;
    }

    public String getFileExtension() {
        return this.mFileExtension;
    }

    public void clearEmptyFile() {
        CamLog.d(FaceDetector.TAG, "clearEmptyFile() " + this.mFilePath);
        if (this.mFilePath != null) {
            File file = new File(this.mFilePath);
            if (!file.exists()) {
                return;
            }
            if (file.length() != 0) {
                CamLog.d(FaceDetector.TAG, "File is not empty: " + this.mFilePath);
            } else if (file.delete()) {
                CamLog.d(FaceDetector.TAG, "Empty file deleted: " + this.mFilePath);
                this.mFilePath = null;
                this.mFileName = null;
            } else {
                CamLog.d(FaceDetector.TAG, "Empty file delete failed: " + this.mFilePath);
            }
        }
    }

    public File getFile() {
        if (this.mFile == null && this.mFilePath != null) {
            this.mFile = new File(this.mFilePath);
        }
        if (this.mFile == null) {
            CamLog.d(FaceDetector.TAG, "Error!! mFile can't create!!!");
        }
        return this.mFile;
    }

    public void deleteFile() {
        if (this.mFile != null) {
            CamLog.w(FaceDetector.TAG, "delete invalid video file");
            this.mFile.delete();
            this.mFile = null;
            return;
        }
        CamLog.w(FaceDetector.TAG, this.mFile + " not found");
    }

    public Uri registerUri(int mode, String resolution, Location location, boolean toast, int purpose) {
        CamLog.d(FaceDetector.TAG, "registerUri()");
        long dateTaken = System.currentTimeMillis();
        String name = this.mFileName;
        CamLog.d(FaceDetector.TAG, "name: " + name);
        if (name == null) {
            return null;
        }
        String title;
        int indexOfDot = name.lastIndexOf(46);
        if (indexOfDot != -1) {
            title = name.substring(0, indexOfDot);
        } else {
            title = name;
        }
        Context context;
        try {
            String VIDEO_MIME_TYPE = MultimediaProperties.VIDEO_MIME_TYPE;
            if (purpose == 1 || ModelProperties.getCarrierCode() == 6) {
                VIDEO_MIME_TYPE = MultimediaProperties.getVideoMimeType(null);
            }
            CamLog.v(FaceDetector.TAG, "video mime type : " + VIDEO_MIME_TYPE);
            ContentValues values = new ContentValues();
            values.put("title", title);
            values.put("_display_name", name);
            values.put("mime_type", VIDEO_MIME_TYPE);
            values.put("datetaken", Long.valueOf(dateTaken));
            values.put("date_added", Long.valueOf(dateTaken));
            values.put("date_modified", Long.valueOf(dateTaken / 1000));
            values.put("_data", getFilePath());
            values.put("_size", Long.valueOf(this.mFile.length()));
            values.put("duration", Long.valueOf(this.mRecordingTime_duration));
            if (resolution != null) {
                values.put("resolution", resolution.split("@")[0]);
            }
            if (location != null) {
                values.put("latitude", Double.valueOf(location.getLatitude()));
                values.put("longitude", Double.valueOf(location.getLongitude()));
            }
            context = (Context) this.mContext.get();
            if (context != null) {
                ContentResolver contentResolver = context.getContentResolver();
                CamLog.d(FaceDetector.TAG, String.format("insert to DB:%s", new Object[]{getFilePath()}));
                this.mUri = contentResolver.insert(Media.EXTERNAL_CONTENT_URI, values);
                CamLog.d(FaceDetector.TAG, String.format("insert to DB done.", new Object[0]));
                context.sendBroadcast(new Intent("android.hardware.action.NEW_VIDEO", this.mUri));
            } else {
                CamLog.d(FaceDetector.TAG, "Cannot insert URI because context is null");
            }
            CamLog.v(FaceDetector.TAG, "Current video URI: " + this.mUri);
        } catch (Exception e) {
            CamLog.e(FaceDetector.TAG, String.format("Failed to register uri: %s", new Object[]{e.getMessage()}));
            context = (Context) this.mContext.get();
            if (context != null && toast) {
                Toast.makeText(context, context.getString(R.string.error_occurred), 0).show();
            }
            this.mUri = null;
            CamLog.v(FaceDetector.TAG, "Current video URI: " + this.mUri);
        } catch (Throwable th) {
            CamLog.v(FaceDetector.TAG, "Current video URI: " + this.mUri);
        }
        CamLog.d(FaceDetector.TAG, "Manually registered uri: " + this.mUri);
        return this.mUri;
    }

    public Uri getUri() {
        if (this.mInitialized) {
            return this.mUri;
        }
        return null;
    }

    public boolean rename(String title, String extension) {
        if (!(extension.equals(VIDEO_EXTENSION_3GP) && extension.equals(VIDEO_EXTENSION_MP4))) {
            CamLog.d(FaceDetector.TAG, "videofile.rename extension error:" + extension);
        }
        this.mFileName = title + extension;
        this.mFilePath = this.mFileDirectory + this.mFileName;
        this.mFile = new File(this.mFilePath);
        ContentValues values = new ContentValues(5);
        values.put("title", title);
        values.put("_display_name", this.mFileName);
        values.put("_data", this.mFilePath);
        values.put("_size", Long.valueOf(this.mFile.length()));
        Context context = (Context) this.mContext.get();
        if (context != null) {
            context.getContentResolver().update(this.mUri, values, null, null);
        } else {
            CamLog.d(FaceDetector.TAG, "Cannot update name because context is null");
        }
        return true;
    }

    public boolean rename_ExternalSD(String title, String extension) {
        if (!(extension.equals(VIDEO_EXTENSION_3GP) && extension.equals(VIDEO_EXTENSION_MP4))) {
            CamLog.d(FaceDetector.TAG, "videofile.rename extension error:" + extension);
        }
        this.mFileName = title + extension;
        this.mFilePath = this.mFileDirectory + this.mFileName;
        this.mFile = new File(this.mFilePath);
        ContentValues values = new ContentValues(5);
        values.put("title", title);
        values.put("_display_name", this.mFileName);
        values.put("_data", this.mFilePath);
        values.put("_size", Long.valueOf(this.mFile.length()));
        Context context = (Context) this.mContext.get();
        if (context != null) {
            context.getContentResolver().update(this.mUri, values, null, null);
        } else {
            CamLog.d(FaceDetector.TAG, "Cannot update name because context is null");
        }
        return true;
    }

    public Bitmap getVideoThumb() {
        return ThumbnailUtils.createVideoThumbnail(this.mFilePath, 1);
    }
}
