package com.lge.camera;

import android.content.Context;
import android.os.Handler;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.properties.ProjectVariables;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.Common;
import com.lge.olaworks.library.FaceDetector;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class CheckTemperature {
    private static final int AP_TEMP_FOR_FIRST_ENTRY = 57;
    private static final int AP_TEMP_FOR_FIRST_ENTRY_INITIALADD = 0;
    private static final int AP_TEMP_FOR_RELIABILITY = 56;
    private static final int BATTERY_TEMP_FOR_RELIABILITY = 490;
    private static final int BATTERY_TEMP_FOR_RELIABILITY_NOT_INITIAL = 470;
    private static final int CHECK_TIME_INITIAL = 3000;
    private static final int CHECK_TIME_INTERVAL = 30000;
    private static final int ENSURE_TIME_RUNNING = 180000;
    private static final int EXTAP_TEMP_FOR_INITIAL = 99;
    private static final double EXTAP_TEMP_FOR_RELIABILITY = 64.0d;
    private static final int IFPLUGGED_ADD_DEGREE_AP = 5;
    private static final double IFPLUGGED_ADD_DEGREE_EXTAP = 6.5d;
    private static final int WAIT_TIME_FORCED_FINISH = 5000;
    private int iAPTemper;
    private int iAPTemperLimit;
    public int iBatteryTemper;
    private double iEXTAPTemper;
    private double iEXTAPTemperLimit;
    private CheckTemperatureFunction mGet;
    private Handler mHandlerFinishCamera;
    private Handler mHandlerTempCheck;
    private boolean mInitialCheck;
    public Runnable mRunFinishCamera;
    private boolean mRunState;
    Runnable mRunTempCheck;

    public interface CheckTemperatureFunction {
        CameraActivity getActivity();

        Context getApplicationContext();

        boolean getIsCharging();

        String getString(int i);
    }

    public CheckTemperature(CheckTemperatureFunction function) {
        this.mRunState = false;
        this.mInitialCheck = false;
        this.iAPTemperLimit = AP_TEMP_FOR_FIRST_ENTRY_INITIALADD;
        this.iEXTAPTemperLimit = 0.0d;
        this.iAPTemper = AP_TEMP_FOR_FIRST_ENTRY_INITIALADD;
        this.iEXTAPTemper = 0.0d;
        this.iBatteryTemper = AP_TEMP_FOR_FIRST_ENTRY_INITIALADD;
        this.mHandlerTempCheck = null;
        this.mHandlerFinishCamera = null;
        this.mGet = null;
        this.mRunTempCheck = new Runnable() {
            public void run() {
                try {
                    CheckTemperature.this.checkAvailablityToRunCameraApp();
                } catch (Exception e) {
                    CamLog.d(FaceDetector.TAG, "Exception:", e);
                }
                if (CheckTemperature.this.mRunState && CheckTemperature.this.mHandlerTempCheck != null) {
                    if (CheckTemperature.this.mInitialCheck) {
                        CheckTemperature.this.mHandlerTempCheck.postDelayed(CheckTemperature.this.mRunTempCheck, 180000);
                        CheckTemperature.this.mInitialCheck = false;
                        return;
                    }
                    CheckTemperature.this.mHandlerTempCheck.postDelayed(CheckTemperature.this.mRunTempCheck, 30000);
                }
            }
        };
        this.mRunFinishCamera = new Runnable() {
            public void run() {
                try {
                    CheckTemperature.this.mGet.getActivity().finish();
                } catch (Exception e) {
                    CamLog.d(FaceDetector.TAG, "Exception:", e);
                }
            }
        };
        this.mGet = function;
    }

    public void checkTemperatureForKddi() {
        if (ModelProperties.getCarrierCode() == 7 && ModelProperties.getProjectCode() != 9 && ModelProperties.getProjectCode() != 13) {
            this.mHandlerTempCheck = new Handler();
            this.mHandlerFinishCamera = new Handler();
            this.mInitialCheck = true;
            if (this.mHandlerTempCheck != null) {
                this.mHandlerTempCheck.postDelayed(this.mRunTempCheck, ProjectVariables.keepDuration);
            }
        }
    }

    public void checkAvailablityToRunCameraApp() {
        checkAPTemperature();
        CamLog.d(FaceDetector.TAG, "[Camera temperature scenario] checkAPTemperature end, current AP temper : " + this.iAPTemper);
        CamLog.d(FaceDetector.TAG, "[Camera temperature scenario] checkAPTemperature end, current battery temper : " + this.iBatteryTemper);
        CamLog.d(FaceDetector.TAG, "[Camera temperature scenario] checkAPTemperature end, current EXTAP temper : " + this.iEXTAPTemper);
        if (this.mInitialCheck) {
            CamLog.d(FaceDetector.TAG, "[Camera temperature scenario] Initial check");
            this.iAPTemperLimit = AP_TEMP_FOR_FIRST_ENTRY;
            this.iEXTAPTemperLimit = 99.0d;
        } else {
            this.iAPTemperLimit = AP_TEMP_FOR_RELIABILITY;
            this.iEXTAPTemperLimit = EXTAP_TEMP_FOR_RELIABILITY;
        }
        if (this.mGet.getIsCharging()) {
            if (this.mInitialCheck) {
                this.iAPTemperLimit += AP_TEMP_FOR_FIRST_ENTRY_INITIALADD;
            } else {
                this.iAPTemperLimit += IFPLUGGED_ADD_DEGREE_AP;
                this.iEXTAPTemperLimit += IFPLUGGED_ADD_DEGREE_EXTAP;
            }
        }
        CamLog.d(FaceDetector.TAG, "[Camera temperature scenario] limit of AP temper : " + this.iAPTemperLimit);
        CamLog.d(FaceDetector.TAG, "[Camera temperature scenario] limit of BATTERY temper initial : 490");
        CamLog.d(FaceDetector.TAG, "[Camera temperature scenario] limit of EXTAP temper : " + this.iEXTAPTemperLimit);
        if (!this.mInitialCheck) {
            CamLog.d(FaceDetector.TAG, "[Camera temperature scenario] limit of BATTERY temp not initial : 470");
            if ((this.iAPTemper <= this.iAPTemperLimit || this.iEXTAPTemper <= this.iEXTAPTemperLimit) && this.iBatteryTemper <= BATTERY_TEMP_FOR_RELIABILITY_NOT_INITIAL) {
                this.mRunState = true;
                return;
            }
            Common.toastLong(this.mGet.getApplicationContext(), this.mGet.getString(R.string.sp_force_close_on_high_temperature_NORMAL));
            if (this.mHandlerFinishCamera != null) {
                this.mHandlerFinishCamera.postDelayed(this.mRunFinishCamera, CameraConstants.TOAST_LENGTH_LONG);
            }
        } else if (this.iAPTemper > this.iAPTemperLimit || this.iBatteryTemper > BATTERY_TEMP_FOR_RELIABILITY) {
            Common.toastLong(this.mGet.getApplicationContext(), this.mGet.getString(R.string.sp_force_close_on_high_temperature_NORMAL));
            if (this.mHandlerFinishCamera != null) {
                this.mHandlerFinishCamera.postDelayed(this.mRunFinishCamera, CameraConstants.TOAST_LENGTH_LONG);
            }
        } else {
            this.mRunState = true;
        }
    }

    public void checkAPTemperature() {
        BufferedReader temper;
        Exception e;
        Reader temperFileReader;
        FileReader temperExtFileReader;
        BufferedReader exttemper;
        IOException e2;
        FileNotFoundException e3;
        Throwable th;
        CamLog.d(FaceDetector.TAG, "[Camera temperature scenario] checkAPTemperature start");
        String mReadData = "00";
        String mExtReadData = "00";
        String TEMPER = "/sys/bus/i2c/devices/4-004c/temperature";
        String EXT_TEMPER = "/sys/bus/i2c/devices/4-004c/ext_temperature";
        BufferedReader temper2 = null;
        BufferedReader exttemper2 = null;
        FileReader temperFileReader2 = null;
        FileReader temperExtFileReader2 = null;
        try {
            Reader temperFileReader3 = new FileReader("/sys/bus/i2c/devices/4-004c/temperature");
            try {
                temper = new BufferedReader(temperFileReader3);
            } catch (Exception e4) {
                e = e4;
                temperFileReader = temperFileReader3;
                try {
                    e.printStackTrace();
                    if (temperFileReader2 != null) {
                        try {
                            temperFileReader2.close();
                        } catch (Exception e5) {
                            e5.printStackTrace();
                        }
                    }
                    if (temper2 != null) {
                        temper2.close();
                    }
                    temperExtFileReader = new FileReader("/sys/bus/i2c/devices/4-004c/ext_temperature");
                    exttemper = new BufferedReader(temperExtFileReader);
                    try {
                        mExtReadData = exttemper.readLine();
                        if (mExtReadData != null) {
                            this.iEXTAPTemper = Double.parseDouble(mExtReadData);
                        }
                        if (temperExtFileReader != null) {
                            try {
                                temperExtFileReader.close();
                            } catch (IOException e22) {
                                e22.printStackTrace();
                                temperExtFileReader2 = temperExtFileReader;
                                exttemper2 = exttemper;
                                return;
                            }
                        }
                        if (exttemper != null) {
                            exttemper.close();
                        }
                        temperExtFileReader2 = temperExtFileReader;
                        exttemper2 = exttemper;
                    } catch (FileNotFoundException e6) {
                        e3 = e6;
                        temperExtFileReader2 = temperExtFileReader;
                        exttemper2 = exttemper;
                        try {
                            e3.printStackTrace();
                            if (temperExtFileReader2 != null) {
                                try {
                                    temperExtFileReader2.close();
                                } catch (IOException e222) {
                                    e222.printStackTrace();
                                    return;
                                }
                            }
                            if (exttemper2 == null) {
                                exttemper2.close();
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            if (temperExtFileReader2 != null) {
                                try {
                                    temperExtFileReader2.close();
                                } catch (IOException e2222) {
                                    e2222.printStackTrace();
                                    throw th;
                                }
                            }
                            if (exttemper2 != null) {
                                exttemper2.close();
                            }
                            throw th;
                        }
                    } catch (IOException e7) {
                        e2222 = e7;
                        temperExtFileReader2 = temperExtFileReader;
                        exttemper2 = exttemper;
                        e2222.printStackTrace();
                        if (temperExtFileReader2 != null) {
                            try {
                                temperExtFileReader2.close();
                            } catch (IOException e22222) {
                                e22222.printStackTrace();
                                return;
                            }
                        }
                        if (exttemper2 == null) {
                            exttemper2.close();
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        temperExtFileReader2 = temperExtFileReader;
                        exttemper2 = exttemper;
                        if (temperExtFileReader2 != null) {
                            temperExtFileReader2.close();
                        }
                        if (exttemper2 != null) {
                            exttemper2.close();
                        }
                        throw th;
                    }
                } catch (Throwable th4) {
                    th = th4;
                    if (temperFileReader2 != null) {
                        try {
                            temperFileReader2.close();
                        } catch (Exception e52) {
                            e52.printStackTrace();
                            throw th;
                        }
                    }
                    if (temper2 != null) {
                        temper2.close();
                    }
                    throw th;
                }
            } catch (Throwable th5) {
                th = th5;
                temperFileReader = temperFileReader3;
                if (temperFileReader2 != null) {
                    temperFileReader2.close();
                }
                if (temper2 != null) {
                    temper2.close();
                }
                throw th;
            }
            try {
                mReadData = temper.readLine();
                if (mReadData != null) {
                    this.iAPTemper = Integer.parseInt(mReadData);
                }
                if (temperFileReader3 != null) {
                    try {
                        temperFileReader3.close();
                    } catch (Exception e522) {
                        e522.printStackTrace();
                        temperFileReader = temperFileReader3;
                        temper2 = temper;
                    }
                }
                if (temper != null) {
                    temper.close();
                }
                temperFileReader = temperFileReader3;
                temper2 = temper;
            } catch (Exception e8) {
                e522 = e8;
                temperFileReader = temperFileReader3;
                temper2 = temper;
                e522.printStackTrace();
                if (temperFileReader2 != null) {
                    temperFileReader2.close();
                }
                if (temper2 != null) {
                    temper2.close();
                }
                temperExtFileReader = new FileReader("/sys/bus/i2c/devices/4-004c/ext_temperature");
                exttemper = new BufferedReader(temperExtFileReader);
                mExtReadData = exttemper.readLine();
                if (mExtReadData != null) {
                    this.iEXTAPTemper = Double.parseDouble(mExtReadData);
                }
                if (temperExtFileReader != null) {
                    temperExtFileReader.close();
                }
                if (exttemper != null) {
                    exttemper.close();
                }
                temperExtFileReader2 = temperExtFileReader;
                exttemper2 = exttemper;
            } catch (Throwable th6) {
                th = th6;
                temperFileReader = temperFileReader3;
                temper2 = temper;
                if (temperFileReader2 != null) {
                    temperFileReader2.close();
                }
                if (temper2 != null) {
                    temper2.close();
                }
                throw th;
            }
        } catch (Exception e9) {
            e522 = e9;
            e522.printStackTrace();
            if (temperFileReader2 != null) {
                temperFileReader2.close();
            }
            if (temper2 != null) {
                temper2.close();
            }
            temperExtFileReader = new FileReader("/sys/bus/i2c/devices/4-004c/ext_temperature");
            exttemper = new BufferedReader(temperExtFileReader);
            mExtReadData = exttemper.readLine();
            if (mExtReadData != null) {
                this.iEXTAPTemper = Double.parseDouble(mExtReadData);
            }
            if (temperExtFileReader != null) {
                temperExtFileReader.close();
            }
            if (exttemper != null) {
                exttemper.close();
            }
            temperExtFileReader2 = temperExtFileReader;
            exttemper2 = exttemper;
        }
        try {
            temperExtFileReader = new FileReader("/sys/bus/i2c/devices/4-004c/ext_temperature");
            try {
                exttemper = new BufferedReader(temperExtFileReader);
                mExtReadData = exttemper.readLine();
                if (mExtReadData != null) {
                    this.iEXTAPTemper = Double.parseDouble(mExtReadData);
                }
                if (temperExtFileReader != null) {
                    temperExtFileReader.close();
                }
                if (exttemper != null) {
                    exttemper.close();
                }
                temperExtFileReader2 = temperExtFileReader;
                exttemper2 = exttemper;
            } catch (FileNotFoundException e10) {
                e3 = e10;
                temperExtFileReader2 = temperExtFileReader;
                e3.printStackTrace();
                if (temperExtFileReader2 != null) {
                    temperExtFileReader2.close();
                }
                if (exttemper2 == null) {
                    exttemper2.close();
                }
            } catch (IOException e11) {
                e22222 = e11;
                temperExtFileReader2 = temperExtFileReader;
                e22222.printStackTrace();
                if (temperExtFileReader2 != null) {
                    temperExtFileReader2.close();
                }
                if (exttemper2 == null) {
                    exttemper2.close();
                }
            } catch (Throwable th7) {
                th = th7;
                temperExtFileReader2 = temperExtFileReader;
                if (temperExtFileReader2 != null) {
                    temperExtFileReader2.close();
                }
                if (exttemper2 != null) {
                    exttemper2.close();
                }
                throw th;
            }
        } catch (FileNotFoundException e12) {
            e3 = e12;
            e3.printStackTrace();
            if (temperExtFileReader2 != null) {
                temperExtFileReader2.close();
            }
            if (exttemper2 == null) {
                exttemper2.close();
            }
        } catch (IOException e13) {
            e22222 = e13;
            e22222.printStackTrace();
            if (temperExtFileReader2 != null) {
                temperExtFileReader2.close();
            }
            if (exttemper2 == null) {
                exttemper2.close();
            }
        }
    }

    public void setBatteryTemper(int temper) {
        this.iBatteryTemper = temper;
    }

    public void releaseCheckTemperature() {
        this.mRunState = false;
        this.mInitialCheck = true;
        if (ModelProperties.getCarrierCode() == 7) {
            if (this.mHandlerTempCheck != null) {
                this.mHandlerTempCheck.removeCallbacks(this.mRunTempCheck);
            }
            if (this.mHandlerFinishCamera != null) {
                this.mHandlerFinishCamera.removeCallbacks(this.mRunFinishCamera);
            }
        }
        this.mHandlerTempCheck = null;
        this.mHandlerFinishCamera = null;
    }
}
