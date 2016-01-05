package com.lge.camera.properties;

import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.SystemProperties;
import android.util.Log;
import com.lge.olaworks.define.LGT_Limit;
import com.lge.olaworks.define.Ola_Exif.ThumbNailSize;
import com.lge.olaworks.define.Ola_ImageFormat;
import com.lge.olaworks.define.Ola_ShotParam;
import com.lge.olaworks.library.FaceDetector;
import java.util.HashMap;

public final class ModelProperties {
    public static final int CARRIER_CODE_ACG = 17;
    public static final int CARRIER_CODE_ATNT = 5;
    public static final int CARRIER_CODE_BELL = 14;
    public static final int CARRIER_CODE_CMCC = 33;
    public static final int CARRIER_CODE_DCM = 4;
    public static final int CARRIER_CODE_IUSACELL = 16;
    public static final int CARRIER_CODE_KDDI = 7;
    public static final int CARRIER_CODE_KT = 3;
    public static final int CARRIER_CODE_LGUPLUS = 1;
    public static final int CARRIER_CODE_M1 = 30;
    public static final int CARRIER_CODE_MPCS = 8;
    public static final int CARRIER_CODE_O2 = 22;
    public static final int CARRIER_CODE_OPEN_BRA = 13;
    public static final int CARRIER_CODE_OPEN_EUR = 9;
    public static final int CARRIER_CODE_OPEN_JPN = 32;
    public static final int CARRIER_CODE_OPEN_KOR = 31;
    public static final int CARRIER_CODE_ORG = 25;
    public static final int CARRIER_CODE_ROGERS = 20;
    public static final int CARRIER_CODE_SKT = 2;
    public static final int CARRIER_CODE_SPCS = 10;
    public static final int CARRIER_CODE_STAR_HUB = 28;
    public static final int CARRIER_CODE_STL = 29;
    public static final int CARRIER_CODE_TELCEL = 12;
    public static final int CARRIER_CODE_TELSTRA = 21;
    public static final int CARRIER_CODE_TELUS = 15;
    public static final int CARRIER_CODE_TESCO = 23;
    public static final int CARRIER_CODE_TMUS = 19;
    public static final int CARRIER_CODE_UNKNOWN = 0;
    public static final int CARRIER_CODE_USC = 18;
    public static final int CARRIER_CODE_VDF = 24;
    public static final int CARRIER_CODE_VIDEOTRON = 26;
    public static final int CARRIER_CODE_VIVO = 11;
    public static final int CARRIER_CODE_VZW = 6;
    public static final int CARRIER_CODE_WIND = 27;
    public static final int CODE_A = 14;
    public static final int CODE_B1 = 23;
    public static final int CODE_F9 = 10;
    public static final int CODE_FX1 = 5;
    public static final int CODE_FX3 = 6;
    public static final int CODE_FX6 = 12;
    public static final int CODE_G1_BASE = 1;
    public static final int CODE_G2 = 9;
    public static final int CODE_G2M = 24;
    public static final int CODE_G2M_NV = 25;
    public static final int CODE_GK = 7;
    public static final int CODE_GXR = 32;
    public static final int CODE_IPROJECT = 0;
    public static final int CODE_J1 = 2;
    public static final int CODE_J1_DCM = 3;
    public static final int CODE_L10 = 19;
    public static final int CODE_L20 = 33;
    public static final int CODE_L30 = 27;
    public static final int CODE_L35 = 30;
    public static final int CODE_L50 = 28;
    public static final int CODE_L65 = 29;
    public static final int CODE_NEXUS4 = 8;
    public static final int CODE_OMEGA = 18;
    public static final int CODE_V5 = 34;
    public static final int CODE_V7 = 31;
    public static final int CODE_V9 = 11;
    public static final int CODE_VU2 = 4;
    public static final int CODE_VU3 = 15;
    public static final int CODE_W3 = 17;
    public static final int CODE_W5 = 20;
    public static final int CODE_W6 = 26;
    public static final int CODE_W7 = 16;
    public static final int CODE_X3 = 21;
    public static final int CODE_X5 = 22;
    public static final int CODE_Z = 13;
    public static final String NAME_A_070 = "LG-V507L";
    public static final String NAME_A_OPEN = "LG-V500";
    public static final String NAME_A_VZW = "VK810 4G";
    public static final String NAME_B1_CMCC = "LG-D838";
    public static final String NAME_B1_GLOBAL = "LG-D830";
    public static final String NAME_B1_KT = "LG-F350K";
    public static final String NAME_B1_LGT = "LG-F350L";
    public static final String NAME_B1_SKT = "LG-F350S";
    public static final String NAME_F9_DCM = "L-05E";
    public static final String NAME_FX1_ACG = "LG-AS780";
    public static final String NAME_FX1_ATT = "LG-P740";
    public static final String NAME_FX1_OPEN = "LG-P780";
    public static final String NAME_FX1_SKT = "LG-F260S";
    public static final String NAME_FX1_SPCS = "LG-LG870";
    public static final String NAME_FX1_TMUS = "LG-P789";
    public static final String NAME_FX1_USC = "LG-US780";
    public static final String NAME_FX1_VZW = "LG-VS935";
    public static final String NAME_FX3Q_TMUS = "LG-D520";
    public static final String NAME_FX3Q_VZW = "VS890 4G";
    public static final String NAME_FX3_ATT = "LG-P650";
    public static final String NAME_FX3_CDMA_TRF = "LGL25L";
    public static final String NAME_FX3_CRICKET = "LG-LW790";
    public static final String NAME_FX3_MPCS = "LG-MS790";
    public static final String NAME_FX3_MPCS_TMUS = "LGMS659";
    public static final String NAME_FX3_SPCS = "LG-LS720";
    public static final String NAME_FX3_TMUS = "LG-P659";
    public static final String NAME_FX3_WCDMA_TRF = "LGL20L";
    public static final String NAME_FX6_EU_OPEN = "LG-D505";
    public static final String NAME_FX6_MPCS_TMUS = "LGMS500";
    public static final String NAME_FX6_TMUS = "LG-D500";
    public static final String NAME_G1_D1L_ATT = "LG-P860";
    public static final String NAME_G1_D1L_K = "LG-F160K";
    public static final String NAME_G1_D1L_L = "LG-F160L";
    public static final String NAME_G1_D1L_S = "LG-F160S";
    public static final String NAME_G1_D1L_VZW = "VS930 4G";
    public static final String NAME_G2M_CIS = "LG-D618";
    public static final String NAME_G2M_DTAG = "LG-D620r";
    public static final String NAME_G2M_GLOBAL_OPEN_SSIM = "LG-D610";
    public static final String NAME_G2M_GLOBAL_OPEN_SSIM_TR = "LG-D610TR";
    public static final String NAME_G2M_JP_OPEN = "LG-D620J";
    public static final String NAME_G2M_LGUPLUS = "LG-F390L";
    public static final String NAME_G2M_NV_TCL = "LG-D625";
    public static final String NAME_G2M_NZ = "LG-D620k";
    public static final String NAME_G2M_SFR = "LG-D620fr";
    public static final String NAME_G2M_SS_AR = "LG-D610ARn";
    public static final String NAME_G2M_VDF = "LG-D620";
    public static final String NAME_G2_ARG = "LG-D806";
    public static final String NAME_G2_ATT = "LG-D800";
    public static final String NAME_G2_CANADA = "LG-D803";
    public static final String NAME_G2_DCM = "L-01F";
    public static final String NAME_G2_KDDI = "LGL22";
    public static final String NAME_G2_KT = "LG-F320K";
    public static final String NAME_G2_LGT = "LG-F320L";
    public static final String NAME_G2_OPEN = "LG-D802";
    public static final String NAME_G2_OPT = "LG-D802T";
    public static final String NAME_G2_SKT = "LG-F320S";
    public static final String NAME_G2_SPCS = "LG-LS980";
    public static final String NAME_G2_TELSTRA = "LG-D800T";
    public static final String NAME_G2_TIM = "LG-D805";
    public static final String NAME_G2_TMO = "LG-D801";
    public static final String NAME_G2_TR = "LG-D802TR";
    public static final String NAME_G2_TRF_ATT = "LGL996L";
    public static final String NAME_G2_VZW = "VS980 4G";
    public static final String NAME_GK_ATT = "LG-E980";
    public static final String NAME_GK_CMCC = "LG-E985T";
    public static final String NAME_GK_HK = "LG-E988";
    public static final String NAME_GK_KT = "LG-F240K";
    public static final String NAME_GK_LGT = "LG-F240L";
    public static final String NAME_GK_OPEN_BR = "LG-E989";
    public static final String NAME_GK_OPEN_CIS = "LG-E988";
    public static final String NAME_GK_OPEN_EU = "LG-E986";
    public static final String NAME_GK_SKT = "LG-F240S";
    public static final String NAME_GK_TELCEL = "LG-E980h";
    public static final String NAME_GV_DCM = "L-04E";
    public static final String NAME_GV_KDDI = "DS1201";
    public static final String NAME_GV_KT = "LG-F220K";
    public static final String NAME_GV_LGT = "LG-F220L";
    public static final String NAME_GV_SKT = "LG-F220S";
    public static final String NAME_GXR_LGU = "LG-F310LR";
    public static final String NAME_IPROJECT_ATNT = "LG-P930";
    public static final String NAME_IPROJECT_K = "LG-KV6200";
    public static final String NAME_IPROJECT_L = "LG-LU6200";
    public static final String NAME_IPROJECT_S = "LG-SU640";
    public static final String NAME_IPROJECT_TELUS = "LG-P935";
    public static final String NAME_IPROJECT_VZW = "VS920 4G";
    public static final String NAME_J1_AME_T = "LG-E975T";
    public static final String NAME_J1_ATT = "LG-E970";
    public static final String NAME_J1_DCM = "L-01E";
    public static final String NAME_J1_K = "LG-F180K";
    public static final String NAME_J1_KDDI = "LGL21";
    public static final String NAME_J1_L = "LG-F180L";
    public static final String NAME_J1_ROGES = "LG-E971";
    public static final String NAME_J1_S = "LG-F180S";
    public static final String NAME_J1_SPCS = "LG-LS970";
    public static final String NAME_J1_TELSTRA = "LG-E975K";
    public static final String NAME_J1_TELUS = "LG-E973";
    public static final String NAME_J1_VDF = "LG-E975";
    public static final String NAME_J1_VZW = "geefhd_vzw_us";
    public static final String NAME_L10_DS = "LG-D685";
    public static final String NAME_L10_DS_D686 = "LG-D686";
    public static final String NAME_L10_NFC = "LG-D683";
    public static final String NAME_L10_NFC_D684 = "LG-D684";
    public static final String NAME_L10_SS = "LG-D680";
    public static final String NAME_L10_SS_AR = "LG-D681";
    public static final String NAME_L10_SS_CL = "LG-D682";
    public static final String NAME_L10_SS_TR = "LG-D678";
    public static final String NAME_L20_AUS_TELSTRA = "LG-D100f";
    public static final String NAME_L20_BR_OPEN = "LG-D107f";
    public static final String NAME_L20_EU_VDF = "LG-D100";
    public static final String NAME_L20_GLOBAL = "LG-D105f";
    public static final String NAME_L20_MX_TCL = "LG-D100g";
    public static final String NAME_L30_AR = "LG-D120AR";
    public static final String NAME_L30_BRAZIL_OPEN = "LG-D127f";
    public static final String NAME_L30_CHILE = "LG-D120j";
    public static final String NAME_L30_INDONESIA = "LG-D125";
    public static final String NAME_L30_PANAMA = "LG-D125g";
    public static final String NAME_L30_TCL = "LG-D120g";
    public static final String NAME_L30_TIM = "LG-D125f";
    public static final String NAME_L30_VDF = "LG-D120";
    public static final String NAME_L30_VIVO = "LG-D120f";
    public static final String NAME_L35_COM = "LG-D150";
    public static final String NAME_L35_SCA = "LG-D157f";
    public static final String NAME_L50_3M = "LG-D221";
    public static final String NAME_L50_FBD = "LG-D213f";
    public static final String NAME_L50_OPEN = "LG-D213";
    public static final String NAME_L50_OPEN_SCA = "LG-D228";
    public static final String NAME_L50_TEL = "LG-D213k";
    public static final String NAME_L50_TIM = "LG-D227";
    public static final String NAME_L50_TR = "LG-D213TR";
    public static final String NAME_L50_VDF = "LG-D213n";
    public static final String NAME_L65_AR = "LG-D280AR";
    public static final String NAME_L65_BR = "LG-D280f";
    public static final String NAME_L65_BR_DS = "LG-D285f";
    public static final String NAME_L65_CIS = "LG-D285";
    public static final String NAME_L65_EU_OPEN = "LG-D280";
    public static final String NAME_L65_EU_OPEN_NFC = "LG-D280n";
    public static final String NAME_L65_LATIN = "LG-D280g";
    public static final String NAME_L65_PA = "LG-D285g";
    public static final String NAME_L65_TR = "LG-D280TR";
    public static final String NAME_L90_TR = "LG-D682TR";
    public static final String NAME_NEXUS4 = "Nexus 4";
    public static final String NAME_OMEGA_LGT = "LG-F310L";
    public static final String NAME_V5_OPEN = "LG-E460";
    public static final String NAME_V5_OPEN_BR = "LG-E455";
    public static final String NAME_V5_OPEN_BR_F = "LG-E455f";
    public static final String NAME_V5_OPEN_BR_G = "LG-E455g";
    public static final String NAME_V5_OPEN_F = "LG-E460f";
    public static final String NAME_V5_OPEN_G = "LG-E460g";
    public static final String NAME_V5_VIVO = "LG-E450";
    public static final String NAME_V5_VIVO_F = "LG-E450f";
    public static final String NAME_V5_VIVO_G = "LG-E450g";
    public static final String NAME_V7_NFC_SSIM_EU = "LG-P710";
    public static final String NAME_V7_NFC_SSIM_SA = "LG-P711";
    public static final String NAME_V7_NNFC_DSIM_EU = "LG-P715";
    public static final String NAME_V7_NNFC_DSIM_SA = "LG-P716";
    public static final String NAME_V7_NNFC_SSIM_AU = "LG-P713GO";
    public static final String NAME_V7_NNFC_SSIM_EU = "LG-P713";
    public static final String NAME_V7_NNFC_SSIM_SA = "LG-P714";
    public static final String NAME_V7_NNFC_SSIM_TR = "LG-P713TR";
    public static final String NAME_V7_OPEN = "LG-Vee7e";
    public static final String NAME_V9_NFC_SSIM_EU = "LG-D605";
    public static final String NAME_VU2_KT = "LG-F200K";
    public static final String NAME_VU2_LGT = "LG-F200L";
    public static final String NAME_VU2_SKT = "LG-F200S";
    public static final String NAME_VU3_KT = "LG-F300K";
    public static final String NAME_VU3_LGT = "LG-F300L";
    public static final String NAME_VU3_SKT = "LG-F300S";
    public static final String NAME_W3 = "LG-D175f";
    public static final String NAME_W3_GLOBAL = "LG-D170";
    public static final String NAME_W3_SCA = "LG-D180f";
    public static final String NAME_W3_SCA_ARG = "LG-D165AR";
    public static final String NAME_W3_SCA_BR = "LG-D165f";
    public static final String NAME_W3_SCA_CL = "LG-D160j";
    public static final String NAME_W3_SCA_VE = "LG-D165g";
    public static final String NAME_W3_TCL = "LG-D160g";
    public static final String NAME_W3_TH = "LG-D160f";
    public static final String NAME_W3_TR = "LG-D160TR";
    public static final String NAME_W3_TRF_VZW = "LGL34C";
    public static final String NAME_W3_VDF = "LG-D160";
    public static final String NAME_W3_VZW = "VS415PP";
    public static final String NAME_W5_ACG = "LG-AS750";
    public static final String NAME_W5_AIO = "LG-D321";
    public static final String NAME_W5_ARGENTINA = "LG-D320AR";
    public static final String NAME_W5_ASIA = "LG-D325";
    public static final String NAME_W5_BRAZIL1 = "LG-D325f8";
    public static final String NAME_W5_BRAZIL2 = "LG-D340f8";
    public static final String NAME_W5_DS = "LG-D325f";
    public static final String NAME_W5_ENTEL = "LG-D320J8";
    public static final String NAME_W5_EU_OPEN = "LG-D320n";
    public static final String NAME_W5_FRANCH = "LG-D320nr";
    public static final String NAME_W5_IUSACELL = "LG-D320g8";
    public static final String NAME_W5_KR = "LG-D329";
    public static final String NAME_W5_MPCS = "LGMS323";
    public static final String NAME_W5_OPEN = "LG-D320";
    public static final String NAME_W5_PANAMA = "LG-D325g8";
    public static final String NAME_W5_SPCS = "LGLS620";
    public static final String NAME_W5_SS = "LG-D320N";
    public static final String NAME_W5_TCF = "LGL42G";
    public static final String NAME_W5_TCF_VZW = "LGL41C";
    public static final String NAME_W5_TCL = "LG-D320f8";
    public static final String NAME_W5_TH = "LG-D320f";
    public static final String NAME_W5_TR = "LG-D320TR";
    public static final String NAME_W5_US = "LGAS323";
    public static final String NAME_W5_VDF = "LG-D330";
    public static final String NAME_W5_VZW = "LG-VS450PP";
    public static final String NAME_W6_AR = "LG-D375AR";
    public static final String NAME_W6_CA = "LG-D370";
    public static final String NAME_W6_CIS = "LG-D375";
    public static final String NAME_W6_EU = "LG-D373EU";
    public static final String NAME_W6_TCL = "LG-D373";
    public static final String NAME_W6_TIM = "LG-D385";
    public static final String NAME_W6_TR = "LG-D370TR";
    public static final String NAME_W6_VIVO = "LG-D380";
    public static final String NAME_W7_BR = "LG-D410hn";
    public static final String NAME_W7_BR_OPEN = "LG-D410h";
    public static final String NAME_W7_CIS = "LG-D410";
    public static final String NAME_W7_EU = "LG-D405n";
    public static final String NAME_W7_ISR = "LG-D405h";
    public static final String NAME_W7_MPCS = "LG-MS415";
    public static final String NAME_W7_OPEN = "LG-D405";
    public static final String NAME_W7_SCA = "LG-D400";
    public static final String NAME_W7_SCA_ARG = "LG-D400ARn";
    public static final String NAME_W7_SCA_COL = "LG-D400h";
    public static final String NAME_W7_SCA_ENTEL = "LG-D400n";
    public static final String NAME_W7_SCA_MEX_ = "LG-D400hn";
    public static final String NAME_W7_TMUS = "LG-D415";
    public static final String NAME_W7_TMUS_BK = "LG-D415BK";
    public static final String NAME_W7_TR = "LG-D405TR";
    public static final String NAME_X3_AU_OPT_SSIM = "LG-D315k";
    public static final String NAME_X3_EU_OPEN_SSIM = "LG-D315";
    public static final String NAME_X3_FR_SFR_SSIM = "LG-D315s";
    public static final String NAME_X3_GLOBAL_OPEN_DSIM = "LG-D317";
    public static final String NAME_X3_IL_SSIM = "LG-D315L";
    public static final String NAME_X3_IN_OPEN_SSIM = "LG-D315I";
    public static final String NAME_X3_KT = "LG-F370K";
    public static final String NAME_X3_LGT = "LG-F370L";
    public static final String NAME_X3_SCA = "LG-D315h";
    public static final String NAME_X3_SKT = "LG-F370S";
    public static final String NAME_X3_TRF_ATT = "LGL31L";
    public static final String NAME_X3_TRF_VZW = "LGL30L";
    public static final String NAME_X3_VIVO = "LG-D317H";
    public static final String NAME_X5_LRA = "AS876";
    public static final String NAME_X5_SPCS = "LGLS740";
    public static final String NAME_X5_VZW = "VS876";
    public static final String NAME_Z_ATT = "LG-D950";
    public static final String NAME_Z_BR = "LG-D956";
    public static final String NAME_Z_CN = "LG-D975";
    public static final String NAME_Z_HK = "LG-D958";
    public static final String NAME_Z_KDDI = "LGL23";
    public static final String NAME_Z_KT = "LG-F340K";
    public static final String NAME_Z_LGT = "LG-F340L";
    public static final String NAME_Z_OPEN_EU = "LG-D955";
    public static final String NAME_Z_SKT = "LG-F340S";
    public static final String NAME_Z_SPCS = "LG-LS995";
    public static final String NAME_Z_TMUS = "LG-D959";
    private static HashMap<String, Integer> carrierCodeMap;
    private static int mCarrierCode;
    private static int mProjectCode;
    private static HashMap<String, Integer> projectCodeMap;

    static {
        projectCodeMap = null;
        mProjectCode = setProjectCode();
        carrierCodeMap = null;
        mCarrierCode = setCarrierCode();
    }

    public static int getProjectCode() {
        return mProjectCode;
    }

    private static void initProjectCodeMap() {
        projectCodeMap = new HashMap();
        projectCodeMap.put(NAME_IPROJECT_L, Integer.valueOf(CODE_IPROJECT));
        projectCodeMap.put(NAME_IPROJECT_K, Integer.valueOf(CODE_IPROJECT));
        projectCodeMap.put(NAME_IPROJECT_S, Integer.valueOf(CODE_IPROJECT));
        projectCodeMap.put(NAME_IPROJECT_ATNT, Integer.valueOf(CODE_IPROJECT));
        projectCodeMap.put(NAME_IPROJECT_TELUS, Integer.valueOf(CODE_IPROJECT));
        projectCodeMap.put(NAME_IPROJECT_VZW, Integer.valueOf(CODE_IPROJECT));
        projectCodeMap.put(NAME_G1_D1L_L, Integer.valueOf(CODE_G1_BASE));
        projectCodeMap.put(NAME_G1_D1L_K, Integer.valueOf(CODE_G1_BASE));
        projectCodeMap.put(NAME_G1_D1L_S, Integer.valueOf(CODE_G1_BASE));
        projectCodeMap.put(NAME_G1_D1L_ATT, Integer.valueOf(CODE_G1_BASE));
        projectCodeMap.put(NAME_G1_D1L_VZW, Integer.valueOf(CODE_G1_BASE));
        projectCodeMap.put(NAME_J1_VZW, Integer.valueOf(CODE_J1));
        projectCodeMap.put(NAME_J1_S, Integer.valueOf(CODE_J1));
        projectCodeMap.put(NAME_J1_K, Integer.valueOf(CODE_J1));
        projectCodeMap.put(NAME_J1_L, Integer.valueOf(CODE_J1));
        projectCodeMap.put(NAME_J1_SPCS, Integer.valueOf(CODE_J1));
        projectCodeMap.put(NAME_J1_ATT, Integer.valueOf(CODE_J1));
        projectCodeMap.put(NAME_J1_VDF, Integer.valueOf(CODE_J1));
        projectCodeMap.put(NAME_J1_TELUS, Integer.valueOf(CODE_J1));
        projectCodeMap.put(NAME_J1_ROGES, Integer.valueOf(CODE_J1));
        projectCodeMap.put(NAME_J1_TELSTRA, Integer.valueOf(CODE_J1));
        projectCodeMap.put(NAME_J1_AME_T, Integer.valueOf(CODE_J1));
        projectCodeMap.put(NAME_J1_DCM, Integer.valueOf(CODE_J1_DCM));
        projectCodeMap.put(NAME_J1_KDDI, Integer.valueOf(CODE_J1_DCM));
        projectCodeMap.put(NAME_VU2_KT, Integer.valueOf(CODE_VU2));
        projectCodeMap.put(NAME_VU2_SKT, Integer.valueOf(CODE_VU2));
        projectCodeMap.put(NAME_VU2_LGT, Integer.valueOf(CODE_VU2));
        projectCodeMap.put(NAME_FX1_ATT, Integer.valueOf(CODE_FX1));
        projectCodeMap.put(NAME_FX1_SKT, Integer.valueOf(CODE_FX1));
        projectCodeMap.put(NAME_FX1_USC, Integer.valueOf(CODE_FX1));
        projectCodeMap.put(NAME_FX1_ACG, Integer.valueOf(CODE_FX1));
        projectCodeMap.put(NAME_FX1_OPEN, Integer.valueOf(CODE_FX1));
        projectCodeMap.put(NAME_FX1_TMUS, Integer.valueOf(CODE_FX1));
        projectCodeMap.put(NAME_FX1_VZW, Integer.valueOf(CODE_FX1));
        projectCodeMap.put(NAME_FX1_SPCS, Integer.valueOf(CODE_FX1));
        projectCodeMap.put(NAME_FX3_SPCS, Integer.valueOf(CODE_FX3));
        projectCodeMap.put(NAME_FX3_MPCS, Integer.valueOf(CODE_FX3));
        projectCodeMap.put(NAME_FX3_ATT, Integer.valueOf(CODE_FX3));
        projectCodeMap.put(NAME_FX3_TMUS, Integer.valueOf(CODE_FX3));
        projectCodeMap.put(NAME_FX3_MPCS_TMUS, Integer.valueOf(CODE_FX3));
        projectCodeMap.put(NAME_FX3Q_VZW, Integer.valueOf(CODE_FX3));
        projectCodeMap.put(NAME_FX3Q_TMUS, Integer.valueOf(CODE_FX3));
        projectCodeMap.put(NAME_FX3_CDMA_TRF, Integer.valueOf(CODE_FX3));
        projectCodeMap.put(NAME_FX3_WCDMA_TRF, Integer.valueOf(CODE_FX3));
        projectCodeMap.put(NAME_FX3_CRICKET, Integer.valueOf(CODE_FX3));
        projectCodeMap.put(NAME_FX6_TMUS, Integer.valueOf(CODE_FX6));
        projectCodeMap.put(NAME_FX6_MPCS_TMUS, Integer.valueOf(CODE_FX6));
        projectCodeMap.put(NAME_FX6_EU_OPEN, Integer.valueOf(CODE_FX6));
        projectCodeMap.put(NAME_GK_KT, Integer.valueOf(CODE_GK));
        projectCodeMap.put(NAME_GK_SKT, Integer.valueOf(CODE_GK));
        projectCodeMap.put(NAME_GK_ATT, Integer.valueOf(CODE_GK));
        projectCodeMap.put(NAME_GK_LGT, Integer.valueOf(CODE_GK));
        projectCodeMap.put(NAME_GK_OPEN_CIS, Integer.valueOf(CODE_GK));
        projectCodeMap.put(NAME_GV_DCM, Integer.valueOf(CODE_GK));
        projectCodeMap.put(NAME_GV_KDDI, Integer.valueOf(CODE_GK));
        projectCodeMap.put(NAME_GV_LGT, Integer.valueOf(CODE_GK));
        projectCodeMap.put(NAME_GV_KT, Integer.valueOf(CODE_GK));
        projectCodeMap.put(NAME_GV_SKT, Integer.valueOf(CODE_GK));
        projectCodeMap.put(NAME_GK_CMCC, Integer.valueOf(CODE_GK));
        projectCodeMap.put(NAME_GK_TELCEL, Integer.valueOf(CODE_GK));
        projectCodeMap.put(NAME_GK_OPEN_EU, Integer.valueOf(CODE_GK));
        projectCodeMap.put(NAME_GK_OPEN_BR, Integer.valueOf(CODE_GK));
        projectCodeMap.put(NAME_GK_OPEN_CIS, Integer.valueOf(CODE_GK));
        projectCodeMap.put(NAME_NEXUS4, Integer.valueOf(CODE_NEXUS4));
        projectCodeMap.put(NAME_G2_LGT, Integer.valueOf(CODE_G2));
        projectCodeMap.put(NAME_G2_KT, Integer.valueOf(CODE_G2));
        projectCodeMap.put(NAME_G2_SKT, Integer.valueOf(CODE_G2));
        projectCodeMap.put(NAME_G2_VZW, Integer.valueOf(CODE_G2));
        projectCodeMap.put(NAME_G2_ATT, Integer.valueOf(CODE_G2));
        projectCodeMap.put(NAME_G2_SPCS, Integer.valueOf(CODE_G2));
        projectCodeMap.put(NAME_G2_DCM, Integer.valueOf(CODE_G2));
        projectCodeMap.put(NAME_G2_TMO, Integer.valueOf(CODE_G2));
        projectCodeMap.put(NAME_G2_OPEN, Integer.valueOf(CODE_G2));
        projectCodeMap.put(NAME_G2_OPT, Integer.valueOf(CODE_G2));
        projectCodeMap.put(NAME_G2_CANADA, Integer.valueOf(CODE_G2));
        projectCodeMap.put(NAME_G2_TELSTRA, Integer.valueOf(CODE_G2));
        projectCodeMap.put(NAME_G2_KDDI, Integer.valueOf(CODE_G2));
        projectCodeMap.put(NAME_G2_TIM, Integer.valueOf(CODE_G2));
        projectCodeMap.put(NAME_G2_ARG, Integer.valueOf(CODE_G2));
        projectCodeMap.put(NAME_G2_TRF_ATT, Integer.valueOf(CODE_G2));
        projectCodeMap.put(NAME_G2_TR, Integer.valueOf(CODE_G2));
        projectCodeMap.put(NAME_F9_DCM, Integer.valueOf(CODE_F9));
        projectCodeMap.put(NAME_V9_NFC_SSIM_EU, Integer.valueOf(CODE_V9));
        projectCodeMap.put(NAME_Z_LGT, Integer.valueOf(CODE_Z));
        projectCodeMap.put(NAME_Z_SKT, Integer.valueOf(CODE_Z));
        projectCodeMap.put(NAME_Z_KT, Integer.valueOf(CODE_Z));
        projectCodeMap.put(NAME_Z_SPCS, Integer.valueOf(CODE_Z));
        projectCodeMap.put(NAME_Z_ATT, Integer.valueOf(CODE_Z));
        projectCodeMap.put(NAME_Z_TMUS, Integer.valueOf(CODE_Z));
        projectCodeMap.put(NAME_Z_KDDI, Integer.valueOf(CODE_Z));
        projectCodeMap.put(NAME_Z_OPEN_EU, Integer.valueOf(CODE_Z));
        projectCodeMap.put(NAME_Z_HK, Integer.valueOf(CODE_Z));
        projectCodeMap.put(NAME_Z_BR, Integer.valueOf(CODE_Z));
        projectCodeMap.put(NAME_Z_CN, Integer.valueOf(CODE_Z));
        projectCodeMap.put(NAME_A_OPEN, Integer.valueOf(CODE_A));
        projectCodeMap.put(NAME_A_070, Integer.valueOf(CODE_A));
        projectCodeMap.put(NAME_A_VZW, Integer.valueOf(CODE_A));
        projectCodeMap.put(NAME_VU3_KT, Integer.valueOf(CODE_VU3));
        projectCodeMap.put(NAME_VU3_SKT, Integer.valueOf(CODE_VU3));
        projectCodeMap.put(NAME_VU3_LGT, Integer.valueOf(CODE_VU3));
        projectCodeMap.put(NAME_W7_SCA, Integer.valueOf(CODE_W7));
        projectCodeMap.put(NAME_W7_SCA_MEX_, Integer.valueOf(CODE_W7));
        projectCodeMap.put(NAME_W7_SCA_COL, Integer.valueOf(CODE_W7));
        projectCodeMap.put(NAME_W7_SCA_ENTEL, Integer.valueOf(CODE_W7));
        projectCodeMap.put(NAME_W7_SCA_ARG, Integer.valueOf(CODE_W7));
        projectCodeMap.put(NAME_W7_OPEN, Integer.valueOf(CODE_W7));
        projectCodeMap.put(NAME_W7_EU, Integer.valueOf(CODE_W7));
        projectCodeMap.put(NAME_W7_TR, Integer.valueOf(CODE_W7));
        projectCodeMap.put(NAME_W7_ISR, Integer.valueOf(CODE_W7));
        projectCodeMap.put(NAME_W7_CIS, Integer.valueOf(CODE_W7));
        projectCodeMap.put(NAME_W7_BR, Integer.valueOf(CODE_W7));
        projectCodeMap.put(NAME_W7_BR_OPEN, Integer.valueOf(CODE_W7));
        projectCodeMap.put(NAME_W7_TMUS, Integer.valueOf(CODE_W7));
        projectCodeMap.put(NAME_W7_TMUS_BK, Integer.valueOf(CODE_W7));
        projectCodeMap.put(NAME_W7_MPCS, Integer.valueOf(CODE_W7));
        projectCodeMap.put(NAME_W3, Integer.valueOf(CODE_W3));
        projectCodeMap.put(NAME_W3_GLOBAL, Integer.valueOf(CODE_W3));
        projectCodeMap.put(NAME_W3_VZW, Integer.valueOf(CODE_W3));
        projectCodeMap.put(NAME_W3_TRF_VZW, Integer.valueOf(CODE_W3));
        projectCodeMap.put(NAME_W3_VDF, Integer.valueOf(CODE_W3));
        projectCodeMap.put(NAME_W3_TH, Integer.valueOf(CODE_W3));
        projectCodeMap.put(NAME_W3_TR, Integer.valueOf(CODE_W3));
        projectCodeMap.put(NAME_W3_SCA, Integer.valueOf(CODE_W3));
        projectCodeMap.put(NAME_W3_TCL, Integer.valueOf(CODE_W3));
        projectCodeMap.put(NAME_W3_SCA_CL, Integer.valueOf(CODE_W3));
        projectCodeMap.put(NAME_W3_SCA_BR, Integer.valueOf(CODE_W3));
        projectCodeMap.put(NAME_W3_SCA_VE, Integer.valueOf(CODE_W3));
        projectCodeMap.put(NAME_W3_SCA_ARG, Integer.valueOf(CODE_W3));
        projectCodeMap.put(NAME_OMEGA_LGT, Integer.valueOf(CODE_OMEGA));
        projectCodeMap.put(NAME_L10_DS, Integer.valueOf(CODE_L10));
        projectCodeMap.put(NAME_L10_SS, Integer.valueOf(CODE_L10));
        projectCodeMap.put(NAME_L10_NFC, Integer.valueOf(CODE_L10));
        projectCodeMap.put(NAME_L10_DS_D686, Integer.valueOf(CODE_L10));
        projectCodeMap.put(NAME_L10_NFC_D684, Integer.valueOf(CODE_L10));
        projectCodeMap.put(NAME_L10_SS_AR, Integer.valueOf(CODE_L10));
        projectCodeMap.put(NAME_L10_SS_CL, Integer.valueOf(CODE_L10));
        projectCodeMap.put(NAME_L10_SS_TR, Integer.valueOf(CODE_L10));
        projectCodeMap.put(NAME_L90_TR, Integer.valueOf(CODE_L10));
        projectCodeMap.put(NAME_W5_DS, Integer.valueOf(CODE_W5));
        projectCodeMap.put(NAME_W5_VZW, Integer.valueOf(CODE_W5));
        projectCodeMap.put(NAME_W5_MPCS, Integer.valueOf(CODE_W5));
        projectCodeMap.put(NAME_W5_VDF, Integer.valueOf(CODE_W5));
        projectCodeMap.put(NAME_W5_EU_OPEN, Integer.valueOf(CODE_W5));
        projectCodeMap.put(NAME_W5_TCL, Integer.valueOf(CODE_W5));
        projectCodeMap.put(NAME_W5_IUSACELL, Integer.valueOf(CODE_W5));
        projectCodeMap.put(NAME_W5_AIO, Integer.valueOf(CODE_W5));
        projectCodeMap.put(NAME_W5_TH, Integer.valueOf(CODE_W5));
        projectCodeMap.put(NAME_W5_TR, Integer.valueOf(CODE_W5));
        projectCodeMap.put(NAME_W5_SS, Integer.valueOf(CODE_W5));
        projectCodeMap.put(NAME_W5_ACG, Integer.valueOf(CODE_W5));
        projectCodeMap.put(NAME_W5_SPCS, Integer.valueOf(CODE_W5));
        projectCodeMap.put(NAME_W5_TCF_VZW, Integer.valueOf(CODE_W5));
        projectCodeMap.put(NAME_W5_US, Integer.valueOf(CODE_W5));
        projectCodeMap.put(NAME_W5_KR, Integer.valueOf(CODE_W5));
        projectCodeMap.put(NAME_W5_FRANCH, Integer.valueOf(CODE_W5));
        projectCodeMap.put(NAME_W5_OPEN, Integer.valueOf(CODE_W5));
        projectCodeMap.put(NAME_W5_ARGENTINA, Integer.valueOf(CODE_W5));
        projectCodeMap.put(NAME_W5_ENTEL, Integer.valueOf(CODE_W5));
        projectCodeMap.put(NAME_W5_TCF, Integer.valueOf(CODE_W5));
        projectCodeMap.put(NAME_W5_ASIA, Integer.valueOf(CODE_W5));
        projectCodeMap.put(NAME_W5_PANAMA, Integer.valueOf(CODE_W5));
        projectCodeMap.put(NAME_W5_BRAZIL1, Integer.valueOf(CODE_W5));
        projectCodeMap.put(NAME_W5_BRAZIL2, Integer.valueOf(CODE_W5));
        projectCodeMap.put(NAME_X3_TRF_VZW, Integer.valueOf(CODE_X3));
        projectCodeMap.put(NAME_X3_TRF_ATT, Integer.valueOf(CODE_X3));
        projectCodeMap.put(NAME_X3_EU_OPEN_SSIM, Integer.valueOf(CODE_X3));
        projectCodeMap.put(NAME_X3_AU_OPT_SSIM, Integer.valueOf(CODE_X3));
        projectCodeMap.put(NAME_X3_IN_OPEN_SSIM, Integer.valueOf(CODE_X3));
        projectCodeMap.put(NAME_X3_FR_SFR_SSIM, Integer.valueOf(CODE_X3));
        projectCodeMap.put(NAME_X3_IL_SSIM, Integer.valueOf(CODE_X3));
        projectCodeMap.put(NAME_X3_GLOBAL_OPEN_DSIM, Integer.valueOf(CODE_X3));
        projectCodeMap.put(NAME_X3_SKT, Integer.valueOf(CODE_X3));
        projectCodeMap.put(NAME_X3_KT, Integer.valueOf(CODE_X3));
        projectCodeMap.put(NAME_X3_LGT, Integer.valueOf(CODE_X3));
        projectCodeMap.put(NAME_X3_VIVO, Integer.valueOf(CODE_X3));
        projectCodeMap.put(NAME_X3_SCA, Integer.valueOf(CODE_X3));
        projectCodeMap.put(NAME_X5_VZW, Integer.valueOf(CODE_X5));
        projectCodeMap.put(NAME_X5_SPCS, Integer.valueOf(CODE_X5));
        projectCodeMap.put(NAME_X5_LRA, Integer.valueOf(CODE_X5));
        projectCodeMap.put(NAME_B1_SKT, Integer.valueOf(CODE_B1));
        projectCodeMap.put(NAME_B1_KT, Integer.valueOf(CODE_B1));
        projectCodeMap.put(NAME_B1_LGT, Integer.valueOf(CODE_B1));
        projectCodeMap.put(NAME_B1_GLOBAL, Integer.valueOf(CODE_B1));
        projectCodeMap.put(NAME_B1_CMCC, Integer.valueOf(CODE_B1));
        projectCodeMap.put(NAME_G2M_CIS, Integer.valueOf(CODE_G2M));
        projectCodeMap.put(NAME_G2M_VDF, Integer.valueOf(CODE_G2M));
        projectCodeMap.put(NAME_G2M_LGUPLUS, Integer.valueOf(CODE_G2M));
        projectCodeMap.put(NAME_G2M_GLOBAL_OPEN_SSIM, Integer.valueOf(CODE_G2M));
        projectCodeMap.put(NAME_G2M_NZ, Integer.valueOf(CODE_G2M));
        projectCodeMap.put(NAME_G2M_SS_AR, Integer.valueOf(CODE_G2M));
        projectCodeMap.put(NAME_G2M_DTAG, Integer.valueOf(CODE_G2M));
        projectCodeMap.put(NAME_G2M_SFR, Integer.valueOf(CODE_G2M));
        projectCodeMap.put(NAME_G2M_GLOBAL_OPEN_SSIM_TR, Integer.valueOf(CODE_G2M));
        projectCodeMap.put(NAME_G2M_JP_OPEN, Integer.valueOf(CODE_G2M));
        projectCodeMap.put(NAME_G2M_NV_TCL, Integer.valueOf(CODE_G2M_NV));
        projectCodeMap.put(NAME_W6_TIM, Integer.valueOf(CODE_W6));
        projectCodeMap.put(NAME_W6_TCL, Integer.valueOf(CODE_W6));
        projectCodeMap.put(NAME_W6_CIS, Integer.valueOf(CODE_W6));
        projectCodeMap.put(NAME_W6_EU, Integer.valueOf(CODE_W6));
        projectCodeMap.put(NAME_W6_AR, Integer.valueOf(CODE_W6));
        projectCodeMap.put(NAME_W6_VIVO, Integer.valueOf(CODE_W6));
        projectCodeMap.put(NAME_W6_CA, Integer.valueOf(CODE_W6));
        projectCodeMap.put(NAME_W6_TR, Integer.valueOf(CODE_W6));
        projectCodeMap.put(NAME_L30_TIM, Integer.valueOf(CODE_L30));
        projectCodeMap.put(NAME_L30_VDF, Integer.valueOf(CODE_L30));
        projectCodeMap.put(NAME_L30_VIVO, Integer.valueOf(CODE_L30));
        projectCodeMap.put(NAME_L30_TCL, Integer.valueOf(CODE_L30));
        projectCodeMap.put(NAME_L30_AR, Integer.valueOf(CODE_L30));
        projectCodeMap.put(NAME_L30_CHILE, Integer.valueOf(CODE_L30));
        projectCodeMap.put(NAME_L30_INDONESIA, Integer.valueOf(CODE_L30));
        projectCodeMap.put(NAME_L30_PANAMA, Integer.valueOf(CODE_L30));
        projectCodeMap.put(NAME_L30_BRAZIL_OPEN, Integer.valueOf(CODE_L30));
        projectCodeMap.put(NAME_L50_TIM, Integer.valueOf(CODE_L50));
        projectCodeMap.put(NAME_L50_OPEN_SCA, Integer.valueOf(CODE_L50));
        projectCodeMap.put(NAME_L50_VDF, Integer.valueOf(CODE_L50));
        projectCodeMap.put(NAME_L50_TEL, Integer.valueOf(CODE_L50));
        projectCodeMap.put(NAME_L50_FBD, Integer.valueOf(CODE_L50));
        projectCodeMap.put(NAME_L50_TR, Integer.valueOf(CODE_L50));
        projectCodeMap.put(NAME_L50_3M, Integer.valueOf(CODE_L50));
        projectCodeMap.put(NAME_L50_OPEN, Integer.valueOf(CODE_L50));
        projectCodeMap.put(NAME_L65_EU_OPEN, Integer.valueOf(CODE_L65));
        projectCodeMap.put(NAME_L65_EU_OPEN_NFC, Integer.valueOf(CODE_L65));
        projectCodeMap.put(NAME_L65_TR, Integer.valueOf(CODE_L65));
        projectCodeMap.put(NAME_L65_BR, Integer.valueOf(CODE_L65));
        projectCodeMap.put(NAME_L65_LATIN, Integer.valueOf(CODE_L65));
        projectCodeMap.put(NAME_L65_AR, Integer.valueOf(CODE_L65));
        projectCodeMap.put(NAME_L65_CIS, Integer.valueOf(CODE_L65));
        projectCodeMap.put(NAME_L65_BR_DS, Integer.valueOf(CODE_L65));
        projectCodeMap.put(NAME_L65_PA, Integer.valueOf(CODE_L65));
        projectCodeMap.put(NAME_L35_COM, Integer.valueOf(CODE_L35));
        projectCodeMap.put(NAME_L35_SCA, Integer.valueOf(CODE_L35));
        projectCodeMap.put(NAME_V7_OPEN, Integer.valueOf(CODE_V7));
        projectCodeMap.put(NAME_V7_NFC_SSIM_EU, Integer.valueOf(CODE_V7));
        projectCodeMap.put(NAME_V7_NFC_SSIM_SA, Integer.valueOf(CODE_V7));
        projectCodeMap.put(NAME_V7_NNFC_SSIM_EU, Integer.valueOf(CODE_V7));
        projectCodeMap.put(NAME_V7_NNFC_SSIM_AU, Integer.valueOf(CODE_V7));
        projectCodeMap.put(NAME_V7_NNFC_SSIM_SA, Integer.valueOf(CODE_V7));
        projectCodeMap.put(NAME_V7_NNFC_DSIM_EU, Integer.valueOf(CODE_V7));
        projectCodeMap.put(NAME_V7_NNFC_DSIM_SA, Integer.valueOf(CODE_V7));
        projectCodeMap.put(NAME_V7_NNFC_SSIM_TR, Integer.valueOf(CODE_V7));
        projectCodeMap.put(NAME_GXR_LGU, Integer.valueOf(CODE_GXR));
        projectCodeMap.put(NAME_L20_GLOBAL, Integer.valueOf(CODE_L20));
        projectCodeMap.put(NAME_L20_MX_TCL, Integer.valueOf(CODE_L20));
        projectCodeMap.put(NAME_L20_EU_VDF, Integer.valueOf(CODE_L20));
        projectCodeMap.put(NAME_L20_AUS_TELSTRA, Integer.valueOf(CODE_L20));
        projectCodeMap.put(NAME_L20_BR_OPEN, Integer.valueOf(CODE_L20));
        projectCodeMap.put(NAME_V5_OPEN_BR, Integer.valueOf(CODE_V5));
        projectCodeMap.put(NAME_V5_OPEN_BR_F, Integer.valueOf(CODE_V5));
        projectCodeMap.put(NAME_V5_OPEN_BR_G, Integer.valueOf(CODE_V5));
        projectCodeMap.put(NAME_V5_VIVO, Integer.valueOf(CODE_V5));
        projectCodeMap.put(NAME_V5_VIVO_F, Integer.valueOf(CODE_V5));
        projectCodeMap.put(NAME_V5_VIVO_G, Integer.valueOf(CODE_V5));
        projectCodeMap.put(NAME_V5_OPEN, Integer.valueOf(CODE_V5));
        projectCodeMap.put(NAME_V5_OPEN_F, Integer.valueOf(CODE_V5));
        projectCodeMap.put(NAME_V5_OPEN_G, Integer.valueOf(CODE_V5));
    }

    public static void setProjectCodeUsingDeviceName() {
        String targetDevice = Build.DEVICE;
        Log.d(FaceDetector.TAG, "SetProjectCode-DEVICE : " + targetDevice);
        if ("g2".equals(targetDevice)) {
            mProjectCode = CODE_G2;
        } else if ("zee".equals(targetDevice)) {
            mProjectCode = CODE_Z;
        } else if ("vu3".equals(targetDevice)) {
            mProjectCode = CODE_VU3;
        } else if ("awifi".equals(targetDevice) || "awifi070u".equals(targetDevice) || "altev".equals(targetDevice)) {
            mProjectCode = CODE_A;
        } else if ("b1".equals(targetDevice) || "b1w".equals(targetDevice)) {
            mProjectCode = CODE_B1;
        } else if ("w3".equals(targetDevice) || "w3c".equals(targetDevice) || "w3ds".equals(targetDevice) || "w3ts".equals(targetDevice)) {
            mProjectCode = CODE_W3;
        } else if ("w5".equals(targetDevice) || "w5c".equals(targetDevice) || "w5n".equals(targetDevice)) {
            mProjectCode = CODE_W5;
        } else if ("w7".equals(targetDevice) || "w7n".equals(targetDevice) || "w7ds".equals(targetDevice) || "w7dsn".equals(targetDevice)) {
            mProjectCode = CODE_W7;
        } else if ("x3".equals(targetDevice) || "x3c".equals(targetDevice) || "x3n".equals(targetDevice) || "f70n".equals(targetDevice)) {
            mProjectCode = CODE_X3;
        } else if ("x5".equals(targetDevice) || "x5c".equals(targetDevice)) {
            mProjectCode = CODE_X5;
        } else if ("w6".equals(targetDevice) || "w6ds".equals(targetDevice)) {
            mProjectCode = CODE_W6;
        } else if ("luv30ds".equals(targetDevice) || "luv30ss".equals(targetDevice) || "luv30ts".equals(targetDevice)) {
            mProjectCode = CODE_L30;
        } else if ("luv20ds".equals(targetDevice) || "luv20ss".equals(targetDevice) || "luv20dg".equals(targetDevice) || "luv20ts".equals(targetDevice)) {
            mProjectCode = CODE_L20;
        } else if ("luv50ds".equals(targetDevice) || "luv50ts".equals(targetDevice) || "luv50ss".equals(targetDevice)) {
            mProjectCode = CODE_L50;
        } else if ("g2mss".equals(targetDevice)) {
            mProjectCode = CODE_G2M;
            if (NAME_G2M_NV_TCL.equals(readModelName())) {
                mProjectCode = CODE_G2M_NV;
            }
        } else if ("w55ds".equals(targetDevice) || "w55".equals(targetDevice) || "w55n".equals(targetDevice)) {
            mProjectCode = CODE_L65;
        } else if ("gee".equals(targetDevice) || "geeb".equals(targetDevice) || "geehrc".equals(targetDevice) || "geehrc4g".equals(targetDevice)) {
            mProjectCode = CODE_J1;
        } else if ("w35ds".equals(targetDevice) || "w35".equals(targetDevice)) {
            mProjectCode = CODE_L35;
        } else if ("omegar".equals(targetDevice)) {
            mProjectCode = CODE_GXR;
        }
    }

    public static String readModelName() {
        if ("".equals(SystemProperties.get("ro.model.name"))) {
            return Build.MODEL;
        }
        return SystemProperties.get("ro.model.name");
    }

    public static int setProjectCode() {
        String currentModel = readModelName();
        if (projectCodeMap == null) {
            initProjectCodeMap();
        }
        Integer projectCode = (Integer) projectCodeMap.get(currentModel);
        if (projectCode != null) {
            mProjectCode = projectCode.intValue();
        } else {
            if (isHVGAmodel()) {
                mProjectCode = CODE_W3;
            } else if (isWVGAmodel()) {
                mProjectCode = CODE_W5;
            } else if (isXGAmodel()) {
                mProjectCode = CODE_VU2;
            } else if (isWXGAmodel()) {
                mProjectCode = CODE_J1;
            } else if (isUVGAmodel()) {
                mProjectCode = CODE_VU3;
            } else if (isFHDmodel()) {
                mProjectCode = CODE_GK;
            } else if (isHDmodel()) {
                mProjectCode = CODE_Z;
            } else if ((CameraConstants.LCD_SIZE_WIDTH == 1794 && CameraConstants.LCD_SIZE_HEIGHT == 1080) || ((CameraConstants.LCD_SIZE_WIDTH == 1080 && CameraConstants.LCD_SIZE_HEIGHT == 1794) || ((CameraConstants.LCD_SIZE_WIDTH == 1776 && CameraConstants.LCD_SIZE_HEIGHT == 1080) || (CameraConstants.LCD_SIZE_WIDTH == 1080 && CameraConstants.LCD_SIZE_HEIGHT == 1776)))) {
                mProjectCode = CODE_G2;
            } else {
                mProjectCode = CODE_G2;
            }
            setProjectCodeUsingDeviceName();
        }
        if (NAME_FX3Q_VZW.equals(currentModel) || NAME_FX3Q_TMUS.equals(currentModel)) {
            FunctionProperties.isSupportVRPanoramaForSameProjectcode = true;
        }
        Log.d(FaceDetector.TAG, "SetProjectCode-MODEL : " + currentModel + ", mProjectCode = " + mProjectCode);
        return mProjectCode;
    }

    public static int getCarrierCode() {
        return mCarrierCode;
    }

    private static void initCarrierCodeMap() {
        carrierCodeMap = new HashMap();
        carrierCodeMap.put("LGU", Integer.valueOf(CODE_G1_BASE));
        carrierCodeMap.put("SKT", Integer.valueOf(CODE_J1));
        carrierCodeMap.put("KT", Integer.valueOf(CODE_J1_DCM));
        carrierCodeMap.put("DCM", Integer.valueOf(CODE_VU2));
        carrierCodeMap.put("ATT", Integer.valueOf(CODE_FX1));
        carrierCodeMap.put("VZW", Integer.valueOf(CODE_FX3));
        carrierCodeMap.put("KDDI", Integer.valueOf(CODE_GK));
        carrierCodeMap.put("MPCS", Integer.valueOf(CODE_NEXUS4));
        carrierCodeMap.put("OPEN", Integer.valueOf(CODE_G2));
        carrierCodeMap.put("SPR", Integer.valueOf(CODE_F9));
        carrierCodeMap.put("VIV", Integer.valueOf(CODE_V9));
        carrierCodeMap.put("TCL", Integer.valueOf(CODE_FX6));
        carrierCodeMap.put("VTR", Integer.valueOf(CODE_W6));
        carrierCodeMap.put("WIN", Integer.valueOf(CODE_L30));
        carrierCodeMap.put("VDF", Integer.valueOf(CODE_G2M));
        carrierCodeMap.put("ORG", Integer.valueOf(CODE_G2M_NV));
        carrierCodeMap.put("RGS", Integer.valueOf(CODE_W5));
        carrierCodeMap.put("TEL", Integer.valueOf(CODE_X3));
        carrierCodeMap.put("ACG", Integer.valueOf(CODE_W3));
        carrierCodeMap.put("USC", Integer.valueOf(CODE_OMEGA));
        carrierCodeMap.put("TMO", Integer.valueOf(CODE_L10));
        carrierCodeMap.put("BELL", Integer.valueOf(CODE_A));
        carrierCodeMap.put("TLS", Integer.valueOf(CODE_VU3));
        carrierCodeMap.put("SHB", Integer.valueOf(CODE_L50));
        carrierCodeMap.put("STL", Integer.valueOf(CODE_L65));
        carrierCodeMap.put("MON", Integer.valueOf(CODE_L35));
        carrierCodeMap.put("CMCC", Integer.valueOf(CODE_L20));
    }

    public static int setCarrierCode() {
        String strOperatorIso = SystemProperties.get("ro.build.target_operator");
        if (carrierCodeMap == null) {
            initCarrierCodeMap();
        }
        Integer carrierCode = (Integer) carrierCodeMap.get(strOperatorIso);
        String currentModel = readModelName();
        if (carrierCode == null) {
            mCarrierCode = CODE_IPROJECT;
            if ("TRF".equals(strOperatorIso)) {
                if (NAME_FX3_CDMA_TRF.equals(currentModel)) {
                    mCarrierCode = CODE_F9;
                } else if (NAME_FX3_WCDMA_TRF.equals(currentModel)) {
                    mCarrierCode = CODE_L10;
                } else if (NAME_G2_TRF_ATT.equals(currentModel)) {
                    mCarrierCode = CODE_FX1;
                } else if (NAME_X3_TRF_ATT.equals(currentModel)) {
                    mCarrierCode = CODE_FX1;
                } else if (NAME_X3_TRF_VZW.equals(currentModel)) {
                    mCarrierCode = CODE_FX3;
                } else if (NAME_W3_TRF_VZW.equals(currentModel)) {
                    mCarrierCode = CODE_FX3;
                } else if (NAME_W5_TCF_VZW.equals(currentModel)) {
                    mCarrierCode = CODE_FX3;
                } else if (NAME_W5_TCF.equals(currentModel)) {
                    mCarrierCode = CODE_FX1;
                }
            } else if ("AIO".equals(strOperatorIso) && NAME_W5_AIO.equals(currentModel)) {
                mCarrierCode = CODE_FX1;
            }
        } else {
            mCarrierCode = carrierCode.intValue();
            if (mCarrierCode == CODE_FX1) {
                if ("i_bell".equals(SystemProperties.get("ro.build.product"))) {
                    mCarrierCode = CODE_A;
                }
            }
            if (mCarrierCode == CODE_G2 && "KR".equals(SystemProperties.get("ro.build.target_country"))) {
                mCarrierCode = CODE_V7;
            }
            if (mCarrierCode == CODE_G2 && "JP".equals(SystemProperties.get("ro.build.target_country"))) {
                mCarrierCode = CODE_GXR;
            }
        }
        Log.d(FaceDetector.TAG, "strOperatorIso : " + strOperatorIso + ", mCarrierCode = " + mCarrierCode);
        return mCarrierCode;
    }

    public static boolean isDomesticModel() {
        return getCarrierCode() == CODE_G1_BASE || getCarrierCode() == CODE_J1 || getCarrierCode() == CODE_J1_DCM || getCarrierCode() == CODE_V7;
    }

    public static boolean isJapanModel() {
        return getCarrierCode() == CODE_VU2 || getCarrierCode() == CODE_GK || getCarrierCode() == CODE_GXR;
    }

    public static boolean isLDPImodel() {
        return (CameraConstants.LCD_SIZE_WIDTH == ThumbNailSize.width && CameraConstants.LCD_SIZE_HEIGHT == Ola_ShotParam.Sampler_Complete) || (CameraConstants.LCD_SIZE_WIDTH == Ola_ShotParam.Sampler_Complete && CameraConstants.LCD_SIZE_HEIGHT == ThumbNailSize.width);
    }

    public static boolean isQHDmodel() {
        return (CameraConstants.LCD_SIZE_WIDTH == LGT_Limit.IMAGE_SIZE_WALLPAPER_WIDTH && CameraConstants.LCD_SIZE_HEIGHT == 540) || (CameraConstants.LCD_SIZE_WIDTH == 540 && CameraConstants.LCD_SIZE_HEIGHT == LGT_Limit.IMAGE_SIZE_WALLPAPER_WIDTH);
    }

    public static boolean isHVGAmodel() {
        return (CameraConstants.LCD_SIZE_WIDTH == LGT_Limit.PREVIEW_SIZE_HEIGHT && CameraConstants.LCD_SIZE_HEIGHT == ThumbNailSize.width) || (CameraConstants.LCD_SIZE_WIDTH == ThumbNailSize.width && CameraConstants.LCD_SIZE_HEIGHT == LGT_Limit.PREVIEW_SIZE_HEIGHT);
    }

    public static boolean isWVGAmodel() {
        return (CameraConstants.LCD_SIZE_WIDTH == LGT_Limit.IMAGE_SIZE_WALLPAPER_HEIGHT && CameraConstants.LCD_SIZE_HEIGHT == LGT_Limit.PREVIEW_SIZE_HEIGHT) || (CameraConstants.LCD_SIZE_WIDTH == LGT_Limit.PREVIEW_SIZE_HEIGHT && CameraConstants.LCD_SIZE_HEIGHT == LGT_Limit.IMAGE_SIZE_WALLPAPER_HEIGHT);
    }

    public static boolean isHDmodel() {
        return (CameraConstants.LCD_SIZE_WIDTH == 1280 && CameraConstants.LCD_SIZE_HEIGHT == 720) || ((CameraConstants.LCD_SIZE_WIDTH == 720 && CameraConstants.LCD_SIZE_HEIGHT == 1280) || ((CameraConstants.LCD_SIZE_WIDTH == 1280 && CameraConstants.LCD_SIZE_HEIGHT == 768) || ((CameraConstants.LCD_SIZE_WIDTH == 768 && CameraConstants.LCD_SIZE_HEIGHT == 1280) || ((CameraConstants.LCD_SIZE_WIDTH == 1196 && CameraConstants.LCD_SIZE_HEIGHT == 720) || (CameraConstants.LCD_SIZE_WIDTH == 720 && CameraConstants.LCD_SIZE_HEIGHT == 1196)))));
    }

    public static boolean isXGAmodel() {
        return (CameraConstants.LCD_SIZE_WIDTH == Ola_ImageFormat.YUVPLANAR_LABEL && CameraConstants.LCD_SIZE_HEIGHT == 768) || (CameraConstants.LCD_SIZE_WIDTH == 768 && CameraConstants.LCD_SIZE_HEIGHT == Ola_ImageFormat.YUVPLANAR_LABEL);
    }

    public static boolean isUVGAmodel() {
        return (CameraConstants.LCD_SIZE_WIDTH == 1280 && CameraConstants.LCD_SIZE_HEIGHT == LGT_Limit.IMAGE_SIZE_WALLPAPER_WIDTH) || (CameraConstants.LCD_SIZE_WIDTH == LGT_Limit.IMAGE_SIZE_WALLPAPER_WIDTH && CameraConstants.LCD_SIZE_HEIGHT == 1280);
    }

    public static boolean isFHDmodel() {
        return (CameraConstants.LCD_SIZE_WIDTH == 1920 && CameraConstants.LCD_SIZE_HEIGHT == 1080) || (CameraConstants.LCD_SIZE_WIDTH == 1080 && CameraConstants.LCD_SIZE_HEIGHT == 1920);
    }

    public static boolean isWXGAmodel() {
        return (CameraConstants.LCD_SIZE_WIDTH == 1280 && CameraConstants.LCD_SIZE_HEIGHT == 768) || (CameraConstants.LCD_SIZE_WIDTH == 768 && CameraConstants.LCD_SIZE_HEIGHT == 1280);
    }

    public static boolean isUWXGAmodel() {
        return (CameraConstants.LCD_SIZE_WIDTH == 1920 && CameraConstants.LCD_SIZE_HEIGHT == 1200) || (CameraConstants.LCD_SIZE_WIDTH == 1200 && CameraConstants.LCD_SIZE_HEIGHT == 1920);
    }

    public static boolean isFMCmodel() {
        return false;
    }

    public static boolean isMTKChipset() {
        switch (getProjectCode()) {
            case CODE_L10 /*19*/:
            case CODE_L30 /*27*/:
            case CODE_L50 /*28*/:
            case CODE_L20 /*33*/:
            case CODE_V5 /*34*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean isOMAP4Chipset() {
        getProjectCode();
        return false;
    }

    public static boolean isNVIDIAChipset() {
        String curModel = readModelName();
        switch (getProjectCode()) {
            case CODE_G2M_NV /*25*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean isQCTChipset() {
        return (isOMAP4Chipset() || isNVIDIAChipset() || isMTKChipset()) ? false : true;
    }

    public static boolean isFrontVGAOnlyModel() {
        getProjectCode();
        return false;
    }

    public static boolean isSupportFrontCameraModel() {
        String curModel = readModelName();
        switch (getProjectCode()) {
            case CODE_W3 /*17*/:
            case CODE_L30 /*27*/:
            case CODE_L35 /*30*/:
            case CODE_L20 /*33*/:
            case CODE_V5 /*34*/:
                return false;
            case CODE_W5 /*20*/:
                if (NAME_W5_VZW.equals(curModel)) {
                    return false;
                }
                break;
        }
        return true;
    }

    public static boolean is3dSupportedModel() {
        getProjectCode();
        return false;
    }

    public static boolean isFixedFocusModel() {
        String currentModel = readModelName();
        switch (getProjectCode()) {
            case CODE_W3 /*17*/:
            case CODE_L30 /*27*/:
            case CODE_L50 /*28*/:
            case CODE_L35 /*30*/:
            case CODE_L20 /*33*/:
                if (NAME_L50_TIM.equals(currentModel) || NAME_L50_OPEN_SCA.equals(currentModel)) {
                    return false;
                }
                return true;
            default:
                return false;
        }
    }

    public static boolean isSamsungModel() {
        String currentModel = readModelName();
        if (currentModel.equals("Galaxy Nexus") || currentModel.equals("SHW-M440S") || currentModel.equals("SHV-E300S")) {
            return true;
        }
        return false;
    }

    public static boolean isReferenceModel() {
        if (getProjectCode() == CODE_NEXUS4) {
            return true;
        }
        return false;
    }

    public static boolean isSupportShotModeModel() {
        getProjectCode();
        return true;
    }

    public static boolean isJBModel() {
        if (VERSION.SDK_INT >= CODE_W7) {
            return true;
        }
        return false;
    }

    public static boolean isJBPlusModel() {
        if (VERSION.SDK_INT >= CODE_W3) {
            return true;
        }
        return false;
    }

    public static boolean isLiveEffectLimitModel() {
        switch (getProjectCode()) {
            case CODE_FX3 /*6*/:
            case CODE_FX6 /*12*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean isSoftKeyNavigationBarModel() {
        switch (getProjectCode()) {
            case CODE_G2 /*9*/:
            case CODE_Z /*13*/:
            case CODE_A /*14*/:
            case CODE_B1 /*23*/:
            case CODE_G2M /*24*/:
            case CODE_G2M_NV /*25*/:
                return true;
            default:
                return false;
        }
    }

    public static int getFreePanoramaSensorMode() {
        switch (getProjectCode()) {
            case CODE_GK /*7*/:
            case CODE_OMEGA /*18*/:
            case CODE_GXR /*32*/:
                return CODE_VU2;
            default:
                return CODE_J1;
        }
    }

    public static boolean isTabletModel() {
        switch (getProjectCode()) {
            case CODE_A /*14*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean isWifiOnlyModel(Context mContext) {
        switch (getProjectCode()) {
            case CODE_A /*14*/:
                if (getCarrierCode() == CODE_FX3) {
                    return false;
                }
                return true;
            default:
                if (mContext != null) {
                    return !mContext.getPackageManager().hasSystemFeature("android.hardware.telephony");
                } else {
                    return false;
                }
        }
    }

    public static boolean isUS() {
        switch (getCarrierCode()) {
            case CODE_FX1 /*5*/:
            case CODE_FX3 /*6*/:
            case CODE_F9 /*10*/:
            case CODE_L10 /*19*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean isRenesasISP() {
        switch (getProjectCode()) {
            case CODE_GK /*7*/:
            case CODE_OMEGA /*18*/:
            case CODE_GXR /*32*/:
                return true;
            default:
                return false;
        }
    }

    public static boolean is8974Chipset() {
        switch (getProjectCode()) {
            case CODE_G2 /*9*/:
            case CODE_Z /*13*/:
            case CODE_VU3 /*15*/:
            case CODE_B1 /*23*/:
                return true;
            default:
                return false;
        }
    }
}
