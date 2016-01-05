package com.lge.olaworks.define;

public class Ola_ReturnType {
    public static final int OLA_ERROR_ALREADY_EXISTS = -20;
    public static final int OLA_ERROR_BUFFER_OVERFLOW = -6;
    public static final int OLA_ERROR_DB = -300;
    public static final int OLA_ERROR_DUPLICATED = -21;
    public static final int OLA_ERROR_FILE_NOT_FOUND = -101;
    public static final int OLA_ERROR_GENERAL = -1;
    public static final int OLA_ERROR_INTERRUPTED = -5;
    public static final int OLA_ERROR_INVALID_ARGUMENT = -4;
    public static final int OLA_ERROR_INVALID_STATUS = -7;
    public static final int OLA_ERROR_INVALID_UID = -30;
    public static final int OLA_ERROR_IO = -100;
    public static final int OLA_ERROR_MMC_NOT_INSTALLED = -130;
    public static final int OLA_ERROR_NOMEM = -200;
    public static final int OLA_ERROR_NOT_REGISTERED = -31;
    public static final int OLA_ERROR_NOT_SUPPORTED = -2;
    public static final int OLA_ERROR_NO_FACES = -8;
    public static final int OLA_ERROR_NULL_POINTER = -3;
    public static final int OLA_ERROR_TOO_MANY_FACES = -13;
    public static final int OLA_ERROR_TOO_MANY_PERSONS = -15;
    public static final int OLA_ERROR_TOO_MANY_PHOTOS = -12;
    public static final int OLA_ERROR_TOO_MANY_PLACES = -14;
    public static final int OLA_ERROR_TOO_MANY_TAGS = -11;
    public static final int OLA_SUCCESS = 0;
    public static final int USER_ERROR_BROKEN_DATA = -500;
    public static final int USER_ERROR_WRONG_FUNCTION = -501;

    public static String GetErrorString(int errno) {
        switch (errno) {
            case USER_ERROR_WRONG_FUNCTION /*-501*/:
                return "error_wrong_function";
            case USER_ERROR_BROKEN_DATA /*-500*/:
                return "error_broken_data";
            case OLA_ERROR_DB /*-300*/:
                return "error_db";
            case OLA_ERROR_NOMEM /*-200*/:
                return "error_nomem";
            case OLA_ERROR_MMC_NOT_INSTALLED /*-130*/:
                return "error_mmc_not_installed";
            case OLA_ERROR_FILE_NOT_FOUND /*-101*/:
                return "error_file_not_found";
            case OLA_ERROR_IO /*-100*/:
                return "error_io";
            case OLA_ERROR_NOT_REGISTERED /*-31*/:
                return "error_not_registered";
            case OLA_ERROR_INVALID_UID /*-30*/:
                return "error_invalid_uid";
            case OLA_ERROR_DUPLICATED /*-21*/:
                return "error_duplicated";
            case OLA_ERROR_ALREADY_EXISTS /*-20*/:
                return "error_already_exists";
            case OLA_ERROR_TOO_MANY_PERSONS /*-15*/:
                return "error_too_many_persons";
            case OLA_ERROR_TOO_MANY_PLACES /*-14*/:
                return "error_too_many_places";
            case OLA_ERROR_TOO_MANY_FACES /*-13*/:
                return "error_too_many_faces";
            case OLA_ERROR_TOO_MANY_PHOTOS /*-12*/:
                return "error_too_many_photos";
            case OLA_ERROR_TOO_MANY_TAGS /*-11*/:
                return "error_too_many_tags";
            case OLA_ERROR_NO_FACES /*-8*/:
                return "error_no_faces";
            case OLA_ERROR_INVALID_STATUS /*-7*/:
                return "error_invalid_status";
            case OLA_ERROR_BUFFER_OVERFLOW /*-6*/:
                return "error_buffer_overflow";
            case OLA_ERROR_INTERRUPTED /*-5*/:
                return "error_interrupted";
            case OLA_ERROR_INVALID_ARGUMENT /*-4*/:
                return "error_invalid_argument";
            case OLA_ERROR_NULL_POINTER /*-3*/:
                return "error_null_pointer";
            case OLA_ERROR_NOT_SUPPORTED /*-2*/:
                return "error_not_supported";
            case OLA_ERROR_GENERAL /*-1*/:
                return "error_general";
            case OLA_SUCCESS /*0*/:
                return "success";
            default:
                return "Not Defined Error number";
        }
    }

    public static boolean success(int ret) {
        return ret >= 0;
    }

    public static boolean error(int ret) {
        return ret < 0;
    }
}
