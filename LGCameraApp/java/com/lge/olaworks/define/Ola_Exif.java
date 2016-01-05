package com.lge.olaworks.define;

import com.lge.camera.properties.ModelProperties;

public class Ola_Exif {
    public static final int GPS_VER = 538968064;
    public static final String INTEROP_INDEX_STR = "R98";
    public static final int INTEROP_VER = 808529968;
    public static final String MAKE_STR = "LG Electronics";
    public static final String MAP_DATUM = "WGS-84";
    public static final String MODEL_STR;
    public static final int NONE = 0;
    public static final int X_RESOL_DPI = 72;
    public static final int Y_RESOL_DPI = 72;

    public class Compression {
        public static final int JPEG = 6;
        public static final int UNCOMP = 1;
    }

    public class Format {
        public static final int FMT_BYTE = 1;
        public static final int FMT_DOUBLE = 12;
        public static final int FMT_SBYTE = 6;
        public static final int FMT_SINGLE = 11;
        public static final int FMT_SLONG = 9;
        public static final int FMT_SRATIONAL = 10;
        public static final int FMT_SSHORT = 8;
        public static final int FMT_STRING = 2;
        public static final int FMT_ULONG = 4;
        public static final int FMT_UNDEFINED = 7;
        public static final int FMT_URATIONAL = 5;
        public static final int FMT_USHORT = 3;
        public static final int NUM_FORMATS = 12;
    }

    public class Positioning {
        public static final int CENTERED = 1;
        public static final int COSITED = 2;
    }

    public class ResolutionUnit {
        public static final int CENTI = 3;
        public static final int INCHES = 2;
    }

    public class Section {
        public static final int GPS = 3;
        public static final int INTEROP = 2;
        public static final int PRIME = 0;
        public static final int PRIVATE = 1;
        public static final int THUMBNAIL = 4;
    }

    public class Tag {
        public static final int APERTURE = 37378;
        public static final int ARTIST = 315;
        public static final int BATTERY_LEVEL = 33423;
        public static final int BITS_PER_SAMPLE = 258;
        public static final int BRIGHTNESS_VALUE = 37379;
        public static final int CFA_PATTERN = 41730;
        public static final int CFA_PATTERN1 = 33422;
        public static final int CFA_REPEAT_PATTERN_DIM = 33421;
        public static final int COLOR_SPACE = 40961;
        public static final int COMPONENTS_CONFIG = 37121;
        public static final int COMPRESSION = 259;
        public static final int CONTRAST = 41992;
        public static final int COPYRIGHT = 33432;
        public static final int CPRS_BITS_PER_PIXEL = 37122;
        public static final int CUSTOM_RENDERED = 41985;
        public static final int DATETIME = 306;
        public static final int DATETIME_DIGITIZED = 36868;
        public static final int DATETIME_ORIGINAL = 36867;
        public static final int DIGITALZOOMRATIO = 41988;
        public static final int DISTANCE_RANGE = 41996;
        public static final int DOCUMENT_NAME = 269;
        public static final int EXIF_OFFSET = 34665;
        public static final int EXIF_VERSION = 36864;
        public static final int EXPOSURETIME = 33434;
        public static final int EXPOSURE_BIAS = 37380;
        public static final int EXPOSURE_INDEX = 41493;
        public static final int EXPOSURE_MODE = 41986;
        public static final int EXPOSURE_PROGRAM = 34850;
        public static final int FILE_SOURCE = 41728;
        public static final int FILL_ORDER = 266;
        public static final int FLASH = 37385;
        public static final int FLASH_ENERGY = 41483;
        public static final int FLASH_PIX_VERSION = 40960;
        public static final int FNUMBER = 33437;
        public static final int FOCALLENGTH = 37386;
        public static final int FOCALLENGTH_35MM = 41989;
        public static final int FOCAL_PLANE_UNITS = 41488;
        public static final int FOCAL_PLANE_XRES = 41486;
        public static final int FOCAL_PLANE_YRES = 41487;
        public static final int GAIN_CONTROL = 41991;
        public static final int GPSINFO = 34853;
        public static final int GPS_ALT = 6;
        public static final int GPS_ALT_REF = 5;
        public static final int GPS_AREA_INFO = 28;
        public static final int GPS_DATESTAMP = 29;
        public static final int GPS_DEST_BEAR = 24;
        public static final int GPS_DEST_BEAR_REF = 23;
        public static final int GPS_DEST_DIST = 26;
        public static final int GPS_DEST_DIST_REF = 25;
        public static final int GPS_DEST_LAT = 20;
        public static final int GPS_DEST_LAT_REF = 19;
        public static final int GPS_DEST_LON = 22;
        public static final int GPS_DEST_LON_REF = 21;
        public static final int GPS_DIFF = 30;
        public static final int GPS_DOP = 11;
        public static final int GPS_IMG_DIR = 17;
        public static final int GPS_IMG_DIR_REF = 16;
        public static final int GPS_LAT = 2;
        public static final int GPS_LAT_REF = 1;
        public static final int GPS_LONG = 4;
        public static final int GPS_LONG_REF = 3;
        public static final int GPS_MAP_DATUM = 18;
        public static final int GPS_MEASUREMODE = 10;
        public static final int GPS_PROCESS_METHOD = 27;
        public static final int GPS_SPEED = 13;
        public static final int GPS_SPEEDREF = 12;
        public static final int GPS_STATUS = 9;
        public static final int GPS_TIMESATELLITE = 8;
        public static final int GPS_TIMESTAMP = 7;
        public static final int GPS_TRACK = 15;
        public static final int GPS_TRACKREF = 14;
        public static final int GPS_VERSION = 0;
        public static final int IMAGE_DESCRIPTION = 270;
        public static final int IMAGE_LENGTH = 257;
        public static final int IMAGE_UNIQUE_ID = 42016;
        public static final int IMAGE_WIDTH = 256;
        public static final int INTEROP_INDEX = 1;
        public static final int INTEROP_OFFSET = 40965;
        public static final int INTEROP_VERSION = 2;
        public static final int INTER_COLOR_PROFILE = 34675;
        public static final int IPTC_NAA = 33723;
        public static final int ISO_EQUIVALENT = 34855;
        public static final int JPEG_PROC = 512;
        public static final int LIGHT_SOURCE = 37384;
        public static final int MAKE = 271;
        public static final int MAKER_NOTE = 37500;
        public static final int MAXAPERTURE = 37381;
        public static final int METERING_MODE = 37383;
        public static final int MODEL = 272;
        public static final int OECF = 34856;
        public static final int ORIENTATION = 274;
        public static final int PHOTOMETRIC_INTERP = 262;
        public static final int PIXEL_X_DIMENSION = 40962;
        public static final int PIXEL_Y_DIMENSION = 40963;
        public static final int PLANAR_CONFIGURATION = 284;
        public static final int PRIMARY_CHROMATICITIES = 319;
        public static final int REFERENCE_BLACK_WHITE = 532;
        public static final int RELATED_AUDIO_FILE = 40964;
        public static final int RELATED_IMAGE_LENGTH = 4098;
        public static final int RELATED_IMAGE_WIDTH = 4097;
        public static final int RESOLUTION_UNIT = 296;
        public static final int ROWS_PER_STRIP = 278;
        public static final int SAMPLES_PER_PIXEL = 277;
        public static final int SATURATION = 41993;
        public static final int SCENE_CAPTURE_TYPE = 41990;
        public static final int SCENE_TYPE = 41729;
        public static final int SENSING_METHOD = 41495;
        public static final int SHARPNESS = 41994;
        public static final int SHUTTERSPEED = 37377;
        public static final int SOFTWARE = 305;
        public static final int SPATIAL_FREQ_RESP = 41484;
        public static final int SPECTRAL_SENSITIVITY = 34852;
        public static final int SRIP_OFFSET = 273;
        public static final int STRIP_BYTE_COUNTS = 279;
        public static final int SUBJECTAREA = 37396;
        public static final int SUBJECT_DISTANCE = 37382;
        public static final int SUBJECT_LOCATION = 41492;
        public static final int SUBSEC_TIME = 37520;
        public static final int SUBSEC_TIME_DIG = 37522;
        public static final int SUBSEC_TIME_ORIG = 37521;
        public static final int THUMBNAIL_LENGTH = 514;
        public static final int THUMBNAIL_OFFSET = 513;
        public static final int TRANSFER_FUNCTION = 301;
        public static final int TRANSFER_RANGE = 342;
        public static final int USERCOMMENT = 37510;
        public static final int WHITEBALANCE = 41987;
        public static final int WHITE_POINT = 318;
        public static final int WINXP_AUTHOR = 40093;
        public static final int WINXP_COMMENT = 40092;
        public static final int WINXP_KEYWORDS = 40094;
        public static final int WINXP_SUBJECT = 40095;
        public static final int WINXP_TITLE = 40091;
        public static final int X_RESOLUTION = 282;
        public static final int Y_CB_CR_COEFFICIENTS = 529;
        public static final int Y_CB_CR_POSITIONING = 531;
        public static final int Y_CB_CR_SUB_SAMPLING = 530;
        public static final int Y_RESOLUTION = 283;
    }

    public class ThumbNailSize {
        public static final int height = 240;
        public static final int width = 320;
    }

    static {
        MODEL_STR = ModelProperties.readModelName();
    }
}
