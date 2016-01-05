package com.lge.olaworks.library;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.location.Location;
import com.lge.camera.components.RotateView;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.datastruct.Ola_ExifGpsUrational;
import com.lge.olaworks.datastruct.Ola_ExifInfo.Img;
import com.lge.olaworks.datastruct.Ola_ExifInfo.Private;
import com.lge.olaworks.datastruct.Ola_ExifUrational;
import com.lge.olaworks.define.Ola_Exif;
import com.lge.olaworks.define.Ola_Exif.Tag;
import com.lge.olaworks.define.Ola_Exif.ThumbNailSize;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.jni.OlaExifInterfaceJNI;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.util.Calendar;

public class Exif {
    private static final String TAG = "CameraApp";
    private static float mFocalLength;

    static {
        mFocalLength = RotateView.DEFAULT_TEXT_SCALE_X;
    }

    public static void setFocalLength(float length) {
        mFocalLength = length;
    }

    private static byte[] makeExifThumbnail(Bitmap srcBitmap) {
        int tw = ThumbNailSize.width;
        int th = Ola_ShotParam.Sampler_Complete;
        double width = (double) srcBitmap.getWidth();
        double srcRatio = (1.0d * r0) / ((double) srcBitmap.getHeight());
        width = (double) 240;
        double thumbRatio = (1.0d * ((double) 320)) / r0;
        if (srcRatio > thumbRatio) {
            width = (double) srcBitmap.getHeight();
            width = (double) 320;
            th = (int) ((((1.0d * r0) * r0) / ((double) srcBitmap.getWidth())) + 0.5d);
        } else if (srcRatio < thumbRatio) {
            width = (double) srcBitmap.getWidth();
            width = (double) 240;
            tw = (int) ((((1.0d * r0) * r0) / ((double) srcBitmap.getHeight())) + 0.5d);
        }
        Bitmap thumbNailBitmap = Bitmap.createBitmap(tw, th, Config.ARGB_8888);
        new Canvas(thumbNailBitmap).drawBitmap(srcBitmap, new Rect(0, 0, srcBitmap.getWidth(), srcBitmap.getHeight()), new Rect(0, 0, thumbNailBitmap.getWidth(), thumbNailBitmap.getHeight()), null);
        ByteArrayOutputStream ostream = new ByteArrayOutputStream();
        byte[] thumbJpg = null;
        if (ostream != null) {
            thumbNailBitmap.compress(CompressFormat.JPEG, 75, ostream);
            thumbJpg = ostream.toByteArray();
            thumbNailBitmap.recycle();
            try {
                ostream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            CamLog.d(TAG, " error! ostream can't create. ostream is null");
            thumbNailBitmap.recycle();
        }
        return OlaExifInterfaceJNI.stripJpegHeader(thumbJpg);
    }

    public static byte[] processLoadExif(byte[] before, byte[] after, Bitmap beforeBitmap) {
        Img original = new Img(before, before.length);
        if (OlaExifInterfaceJNI.create() < 0) {
            CamLog.d(TAG, "OlaExifInterfaceJNI.create() is failed");
            return null;
        } else if (OlaExifInterfaceJNI.load(original) < 0) {
            CamLog.d(TAG, "OlaExifInterfaceJNI.load() is failed");
            OlaExifInterfaceJNI.destroy();
            return null;
        } else if (OlaExifInterfaceJNI.setPrimaryImg(after) < 0) {
            CamLog.d(TAG, "OlaExifInterfaceJNI.setPrimaryImg() is failed");
            OlaExifInterfaceJNI.destroy();
            return null;
        } else {
            byte[] thumbJpg = makeExifThumbnail(beforeBitmap);
            if (thumbJpg == null) {
                CamLog.d(TAG, "OlaExifInterfaceJNI.stripJpegHeader() is failed");
                OlaExifInterfaceJNI.destroy();
                return null;
            } else if (OlaExifInterfaceJNI.setThumbNail(6, thumbJpg) < 0) {
                CamLog.d(TAG, "OlaExifInterfaceJNI.setThumbNail() is failed");
                OlaExifInterfaceJNI.destroy();
                return null;
            } else {
                byte[] generatedJpg = OlaExifInterfaceJNI.generate();
                OlaExifInterfaceJNI.destroy();
                return generatedJpg;
            }
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    public static int getOrientation(byte[] r15) {
        /*
        r14 = 8;
        r3 = 1;
        r13 = 4;
        r12 = 2;
        r9 = 0;
        if (r15 != 0) goto L_0x0009;
    L_0x0008:
        return r9;
    L_0x0009:
        r5 = 0;
        r2 = 0;
    L_0x000b:
        r10 = r5 + 3;
        r11 = r15.length;
        if (r10 >= r11) goto L_0x0034;
    L_0x0010:
        r6 = r5 + 1;
        r10 = r15[r5];
        r10 = r10 & 255;
        r11 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        if (r10 != r11) goto L_0x00cf;
    L_0x001a:
        r10 = r15[r6];
        r4 = r10 & 255;
        r10 = 255; // 0xff float:3.57E-43 double:1.26E-321;
        if (r4 != r10) goto L_0x0024;
    L_0x0022:
        r5 = r6;
        goto L_0x000b;
    L_0x0024:
        r5 = r6 + 1;
        r10 = 216; // 0xd8 float:3.03E-43 double:1.067E-321;
        if (r4 == r10) goto L_0x000b;
    L_0x002a:
        if (r4 == r3) goto L_0x000b;
    L_0x002c:
        r10 = 217; // 0xd9 float:3.04E-43 double:1.07E-321;
        if (r4 == r10) goto L_0x0034;
    L_0x0030:
        r10 = 218; // 0xda float:3.05E-43 double:1.077E-321;
        if (r4 != r10) goto L_0x004c;
    L_0x0034:
        if (r2 <= r14) goto L_0x00c6;
    L_0x0036:
        r8 = pack(r15, r5, r13, r9);
        r10 = 1229531648; // 0x49492a00 float:823968.0 double:6.074693478E-315;
        if (r8 == r10) goto L_0x0080;
    L_0x003f:
        r10 = 1296891946; // 0x4d4d002a float:2.14958752E8 double:6.40749757E-315;
        if (r8 == r10) goto L_0x0080;
    L_0x0044:
        r10 = "CameraApp";
        r11 = "Invalid byte order";
        com.lge.camera.util.CamLog.e(r10, r11);
        goto L_0x0008;
    L_0x004c:
        r2 = pack(r15, r5, r12, r9);
        if (r2 < r12) goto L_0x0057;
    L_0x0052:
        r10 = r5 + r2;
        r11 = r15.length;
        if (r10 <= r11) goto L_0x005f;
    L_0x0057:
        r10 = "CameraApp";
        r11 = "Invalid length";
        com.lge.camera.util.CamLog.e(r10, r11);
        goto L_0x0008;
    L_0x005f:
        r10 = 225; // 0xe1 float:3.15E-43 double:1.11E-321;
        if (r4 != r10) goto L_0x007d;
    L_0x0063:
        if (r2 < r14) goto L_0x007d;
    L_0x0065:
        r10 = r5 + 2;
        r10 = pack(r15, r10, r13, r9);
        r11 = 1165519206; // 0x45786966 float:3974.5874 double:5.758429993E-315;
        if (r10 != r11) goto L_0x007d;
    L_0x0070:
        r10 = r5 + 6;
        r10 = pack(r15, r10, r12, r9);
        if (r10 != 0) goto L_0x007d;
    L_0x0078:
        r5 = r5 + 8;
        r2 = r2 + -8;
        goto L_0x0034;
    L_0x007d:
        r5 = r5 + r2;
        r2 = 0;
        goto L_0x000b;
    L_0x0080:
        r10 = 1229531648; // 0x49492a00 float:823968.0 double:6.074693478E-315;
        if (r8 != r10) goto L_0x009c;
    L_0x0085:
        r10 = r5 + 4;
        r10 = pack(r15, r10, r13, r3);
        r0 = r10 + 2;
        r10 = 10;
        if (r0 < r10) goto L_0x0093;
    L_0x0091:
        if (r0 <= r2) goto L_0x009e;
    L_0x0093:
        r10 = "CameraApp";
        r11 = "Invalid offset";
        com.lge.camera.util.CamLog.e(r10, r11);
        goto L_0x0008;
    L_0x009c:
        r3 = r9;
        goto L_0x0085;
    L_0x009e:
        r5 = r5 + r0;
        r2 = r2 - r0;
        r10 = r5 + -2;
        r0 = pack(r15, r10, r12, r3);
        r1 = r0;
    L_0x00a7:
        r0 = r1 + -1;
        if (r1 <= 0) goto L_0x00c6;
    L_0x00ab:
        r10 = 12;
        if (r2 < r10) goto L_0x00c6;
    L_0x00af:
        r8 = pack(r15, r5, r12, r3);
        r10 = 274; // 0x112 float:3.84E-43 double:1.354E-321;
        if (r8 != r10) goto L_0x00c0;
    L_0x00b7:
        r9 = r5 + 8;
        r7 = pack(r15, r9, r12, r3);
        r9 = r7;
        goto L_0x0008;
    L_0x00c0:
        r5 = r5 + 12;
        r2 = r2 + -12;
        r1 = r0;
        goto L_0x00a7;
    L_0x00c6:
        r10 = "CameraApp";
        r11 = "Orientation not found";
        com.lge.camera.util.CamLog.i(r10, r11);
        goto L_0x0008;
    L_0x00cf:
        r5 = r6;
        goto L_0x0034;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.lge.olaworks.library.Exif.getOrientation(byte[]):int");
    }

    private static int pack(byte[] bytes, int offset, int length, boolean littleEndian) {
        int step = 1;
        if (littleEndian) {
            offset += length - 1;
            step = -1;
        }
        int value = 0;
        int length2 = length;
        while (true) {
            length = length2 - 1;
            if (length2 <= 0) {
                return value;
            }
            value = (value << 8) | (bytes[offset] & Ola_ShotParam.AnimalMask_Random);
            offset += step;
            length2 = length;
        }
    }

    public static byte[] processNewExif(byte[] orgJpg, Bitmap orgBitmap, Location loc, int orientation) {
        return processNewExif(orgJpg, makeExifThumbnail(orgBitmap), orgBitmap.getWidth(), orgBitmap.getHeight(), loc, orientation);
    }

    public static byte[] processNewExif(byte[] orgJpg, Bitmap thumbBitmap, int width, int height, Location loc, int orientation) {
        return processNewExif(orgJpg, makeExifThumbnail(thumbBitmap), width, height, loc, orientation);
    }

    public static byte[] processNewExif(byte[] orgJpg, byte[] thumbJpg, int width, int height, Location loc, int orientation) {
        if (thumbJpg == null) {
            CamLog.d(TAG, "OlaExifInterfaceJNI.stripJpegHeader() is failed");
            OlaExifInterfaceJNI.destroy();
            return null;
        }
        Img primeImg = new Img(1, 0, orgJpg, orgJpg.length, 72, 1, 72, 1, 2);
        Img thumbImg = new Img(0, 6, thumbJpg, thumbJpg.length, 72, 1, 72, 1, 2);
        Private exifPriv = new Private(808596016, 16909056, Ola_Exif.INTEROP_VER, 1, width, height);
        if (OlaExifInterfaceJNI.create() < 0) {
            CamLog.d(TAG, "OlaExifInterfaceJNI.create() is failed");
            return null;
        } else if (OlaExifInterfaceJNI.initialize(primeImg, thumbImg, exifPriv) < 0) {
            CamLog.d(TAG, "OlaExifInterfaceJNI.initialize() is failed");
            OlaExifInterfaceJNI.destroy();
            return null;
        } else {
            int mOrient = 1;
            if (orientation == 0) {
                mOrient = 1;
            } else if (orientation == 90) {
                mOrient = 6;
            } else if (orientation == 180) {
                mOrient = 3;
            } else if (orientation == 270) {
                mOrient = 8;
            }
            if (OlaExifInterfaceJNI.setInt(0, Tag.ORIENTATION, 3, 1, mOrient) < 0) {
                CamLog.d(TAG, "OlaExifInterfaceJNI.setByteArray(PRIME, ORIENTATION) is failed");
                OlaExifInterfaceJNI.destroy();
                return null;
            }
            Calendar cal = Calendar.getInstance();
            String calStr = String.format("%04d:%02d:%02d %02d:%02d:%02d", new Object[]{Integer.valueOf(cal.get(1)), Integer.valueOf(cal.get(2) + 1), Integer.valueOf(cal.get(5)), Integer.valueOf(cal.get(11)), Integer.valueOf(cal.get(12)), Integer.valueOf(cal.get(13))});
            if (OlaExifInterfaceJNI.setByteArray(1, Tag.DATETIME_ORIGINAL, 2, calStr.length(), calStr.getBytes()) < 0) {
                CamLog.d(TAG, "OlaExifInterfaceJNI.setByteArray(PRIVATE, DATETIME_ORIGINAL) is failed");
                OlaExifInterfaceJNI.destroy();
                return null;
            }
            if (OlaExifInterfaceJNI.setByteArray(1, Tag.DATETIME_DIGITIZED, 2, calStr.length(), calStr.getBytes()) < 0) {
                CamLog.d(TAG, "OlaExifInterfaceJNI.setByteArray(PRIVATE, DATETIME_DIGITIZED) is failed");
                OlaExifInterfaceJNI.destroy();
                return null;
            }
            Ola_ExifUrational rationalData = new Ola_ExifUrational();
            rationalData.numerator = (int) (mFocalLength * CameraConstants.PIP_VIEW_ALLOWABLE_MOVEMENT_EXTENT_FOR_TOGGLE);
            rationalData.denominator = 100;
            if (OlaExifInterfaceJNI.setUrational(1, Tag.FOCALLENGTH, 5, 1, rationalData) < 0) {
                CamLog.d(TAG, "OlaExifInterfaceJNI.setUrational(PRIVATE, FOCALLENGTH) is failed");
                OlaExifInterfaceJNI.destroy();
                return null;
            }
            if (OlaExifInterfaceJNI.setByteArray(2, 1, 2, Ola_Exif.INTEROP_INDEX_STR.length(), Ola_Exif.INTEROP_INDEX_STR.getBytes()) < 0) {
                CamLog.d(TAG, "OlaExifInterfaceJNI.setByteArray(INTEROP, INTEROP_INDEX) is failed");
                OlaExifInterfaceJNI.destroy();
                return null;
            } else if (OlaExifInterfaceJNI.setInt(2, 2, 7, 4, Ola_Exif.INTEROP_VER) < 0) {
                CamLog.d(TAG, "OlaExifInterfaceJNI.setInt(INTEROP, INTEROP_VERSION) is failed");
                OlaExifInterfaceJNI.destroy();
                return null;
            } else if (OlaExifInterfaceJNI.setInt(3, 0, 1, 4, Ola_Exif.GPS_VER) < 0) {
                CamLog.d(TAG, "OlaExifInterfaceJNI.setInt(GPS, GPS_VERSION) is failed");
                OlaExifInterfaceJNI.destroy();
                return null;
            } else {
                if (loc != null) {
                    double value = loc.getLatitude();
                    if (OlaExifInterfaceJNI.setByteArray(3, 1, 2, 1, (value > 0.0d ? "N" : "S").getBytes()) < 0) {
                        CamLog.d(TAG, "OlaExifInterfaceJNI.setByteArray(GPS, GPS_LAT_REF) is failed");
                        OlaExifInterfaceJNI.destroy();
                        return null;
                    }
                    String strCoord = Location.convert(value, 2);
                    CamLog.d(TAG, "Latitude strCoordinate=" + strCoord);
                    String[] arrCoordinate = strCoord.split(":");
                    int degree = Math.abs(Integer.valueOf(arrCoordinate[0]).intValue());
                    int min = Integer.valueOf(arrCoordinate[1]).intValue();
                    int sec = Double.valueOf(arrCoordinate[2]).intValue();
                    CamLog.d(TAG, "Latitude d,m,s=" + degree + "," + min + "," + sec);
                    if (OlaExifInterfaceJNI.setGpsUrational(3, 2, 5, 3, new Ola_ExifGpsUrational(degree, 1, min, 1, sec, 1)) < 0) {
                        CamLog.d(TAG, "OlaExifInterfaceJNI.setGpsUrational(GPS, GPS_LAT) is failed");
                        OlaExifInterfaceJNI.destroy();
                        return null;
                    }
                    value = loc.getLongitude();
                    if (OlaExifInterfaceJNI.setByteArray(3, 3, 2, 1, (value > 0.0d ? "E" : "W").getBytes()) < 0) {
                        CamLog.d(TAG, "OlaExifInterfaceJNI.setByteArray(GPS, GPS_LONG_REF) is failed");
                        OlaExifInterfaceJNI.destroy();
                        return null;
                    }
                    strCoord = Location.convert(value, 2);
                    CamLog.d(TAG, "Longitude strCoordinate=" + strCoord);
                    arrCoordinate = strCoord.split(":");
                    degree = Math.abs(Integer.valueOf(arrCoordinate[0]).intValue());
                    min = Integer.valueOf(arrCoordinate[1]).intValue();
                    sec = Double.valueOf(arrCoordinate[2]).intValue();
                    CamLog.d(TAG, "Longitude d,m,s=" + degree + "," + min + "," + sec);
                    if (OlaExifInterfaceJNI.setGpsUrational(3, 4, 5, 3, new Ola_ExifGpsUrational(degree, 1, min, 1, sec, 1)) < 0) {
                        CamLog.d(TAG, "OlaExifInterfaceJNI.setGpsUrational(GPS, GPS_LONG) is failed");
                        OlaExifInterfaceJNI.destroy();
                        return null;
                    } else if (OlaExifInterfaceJNI.setInt(3, 5, 1, 1, 0) < 0) {
                        CamLog.d(TAG, "OlaExifInterfaceJNI.setByteArray(GPS, GPS_ALT_REF) is failed");
                        OlaExifInterfaceJNI.destroy();
                        return null;
                    } else {
                        value = loc.getAltitude();
                        degree = (int) value;
                        min = (int) ((value - ((double) degree)) * 60.0d);
                        if (OlaExifInterfaceJNI.setGpsUrational(3, 6, 5, 3, new Ola_ExifGpsUrational(degree, 1, min, 1, (int) ((((value - ((double) degree)) * 60.0d) - ((double) min)) * 60.0d), 1)) < 0) {
                            CamLog.d(TAG, "OlaExifInterfaceJNI.setGpsUrational(GPS, GPS_ALT) is failed");
                            OlaExifInterfaceJNI.destroy();
                            return null;
                        }
                        cal.setTime(new Date(loc.getTime()));
                        if (OlaExifInterfaceJNI.setGpsUrational(3, 7, 5, 3, new Ola_ExifGpsUrational(cal.get(11), 1, cal.get(12), 1, cal.get(13), 1)) < 0) {
                            CamLog.d(TAG, "OlaExifInterfaceJNI.setGpsUrational(GPS, GPS_TIMESTAMP) is failed");
                            OlaExifInterfaceJNI.destroy();
                            return null;
                        }
                        if (OlaExifInterfaceJNI.setByteArray(3, 18, 2, 6, Ola_Exif.MAP_DATUM.getBytes()) < 0) {
                            CamLog.d(TAG, "OlaExifInterfaceJNI.setByteArray(GPS, GPS_MAP_DATUM) is failed");
                            OlaExifInterfaceJNI.destroy();
                            return null;
                        }
                    }
                }
                cal.setTime(new Date(System.currentTimeMillis()));
                if (OlaExifInterfaceJNI.setGpsUrational(3, 7, 5, 3, new Ola_ExifGpsUrational(cal.get(11), 1, cal.get(12), 1, cal.get(13), 1)) < 0) {
                    CamLog.d(TAG, "OlaExifInterfaceJNI.setGpsUrational(GPS, GPS_TIMESTAMP) is failed");
                    OlaExifInterfaceJNI.destroy();
                    return null;
                }
                byte[] generatedJpg = OlaExifInterfaceJNI.generate();
                OlaExifInterfaceJNI.destroy();
                return generatedJpg;
            }
        }
    }
}
