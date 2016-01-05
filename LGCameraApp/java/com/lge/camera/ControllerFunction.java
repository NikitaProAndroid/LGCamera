package com.lge.camera;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.hardware.Camera.Parameters;
import android.location.Location;
import android.net.Uri;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.animation.Animation.AnimationListener;
import android.widget.RelativeLayout;
import com.lge.camera.command.CommandManager;
import com.lge.camera.controller.BrightnessControllerExpand;
import com.lge.camera.controller.PreviewPanelController;
import com.lge.camera.controller.SettingController;
import com.lge.camera.controller.ToastController;
import com.lge.camera.module.Module;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.PreferenceGroup;
import com.lge.camera.setting.SettingMenu;
import com.lge.camera.setting.SettingMenuItem;
import com.lge.camera.util.ImageHandler;
import com.lge.camera.util.MainHandler;
import com.lge.hardware.LGCamera;
import com.lge.hardware.LGCamera.LGParameters;
import com.lge.olaworks.library.BaseEngine;
import com.lge.olaworks.library.EngineProcessor;
import com.lge.olaworks.library.FaceBeauty;
import java.util.ArrayList;
import java.util.Locale;

public interface ControllerFunction {
    void activityFinish();

    void addMMSTexture(PreferenceGroup preferenceGroup);

    void addQuickButton(Context context, int i, int i2);

    void addQuickButton(Context context, int i, int i2, int i3);

    void afterOnDismissForSelectVideoLength();

    void allSettingMenuSelectedChild(String str, boolean z);

    void applyCameraChange();

    void audioCallbackRestartEngine();

    boolean beDirectlyGoingToCropGallery();

    void cancelAutoFocus();

    void changeButtonResource(int i, int i2);

    void changeButtonResource(int i, int i2, int i3);

    void changeLiveSnapshotMaxFileSize(long j);

    void changePreviewModeOnUiThread(int i, int i2);

    void changeQuickPreviewMode(int i, int i2);

    void changeShutterSound(int i);

    boolean checkActivity();

    boolean checkAndCloseChildView();

    boolean checkAutoReviewForQuickView();

    boolean checkAutoReviewOff(boolean z);

    boolean checkCameraShutterSoundLoaded();

    boolean checkCurrentShotModeForModule();

    boolean checkFaceDetectionNoUI();

    boolean checkFocusController();

    boolean checkFsWritable();

    boolean checkPreviewController();

    boolean checkPreviewPanelController();

    void checkSceneMode(LGParameters lGParameters, boolean z, String str);

    boolean checkSettingValue(String str, String str2);

    boolean checkShotModeForMultiWindowAF();

    boolean checkSlowMotionMode();

    void checkStorage(boolean z);

    boolean checkSupportVideoSize(Uri uri);

    boolean checkSurfaceHolder();

    boolean checkUpdateThumbnail();

    void clearFocusState();

    void clearQuickFunctionSubMenu();

    void clearScreen();

    void clearSubMenu();

    void clearSubMenu(boolean z);

    void clearViewOn(boolean z);

    void closeGalleryQuickView(boolean z);

    boolean deleteClearShotImages();

    void deleteProgressDialog();

    boolean deleteRefocusShotImages();

    void deleteSavingProgressDialog();

    boolean deleteTimeMachineImages();

    void dialogControllerOnDismiss();

    void disableObjectTrackingForSmartZoom();

    void displayQuickFunctionSettingView(String str);

    void displaySettingView();

    void doAttach();

    void doCamcorderContinuousFocusCallback(boolean z);

    void doCommand(String str);

    void doCommand(String str, Object obj);

    void doCommand(String str, Object obj, Object obj2);

    void doCommandDelayed(String str, long j);

    void doCommandDelayed(String str, Object obj, long j);

    void doCommandNoneParameter(String str);

    void doCommandNoneParameter(String str, Object obj);

    void doCommandUi(String str);

    void doCommandUi(String str, Object obj);

    void doCommandUi(String str, Object obj, Object obj2);

    void doFocus(boolean z);

    void doFocusOnCaf();

    void doSmartCameraModeCallback(int[] iArr);

    boolean doTakePictureCommand();

    void effectCameraStopPreview();

    void effectCameraStopPreviewByCallFrom(int i);

    void effectRecorderStopPreview();

    void effectRecorderStopPreviewByCallFrom(int i);

    void enableCommand(boolean z);

    void enableInput(boolean z);

    void facePreviewInitController();

    ListPreference findPreference(String str);

    int findPreferenceIndex(String str);

    View findViewById(int i);

    Activity getActivity();

    int getActualBatteryLevel();

    Context getApplicationContext();

    int getApplicationMode();

    String getApplicationModeString();

    boolean getAudiozoomStart();

    boolean getAudiozoomStartInRecording();

    String getAudiozoomvalue();

    long getAvailablePictureCount();

    PreferenceGroup getBack3dPreferenceGroup();

    boolean getBackKeyRecStop();

    PreferenceGroup getBackPreferenceGroup();

    int getBackupCurrentMenuIndex();

    int getBatteryLevel();

    boolean getBeautyshotProgress();

    boolean getBlockTouchByCallPopUp();

    BrightnessControllerExpand getBrightnessControllerExpand();

    boolean getCafOnGoing();

    Camera getCameraDevice();

    int getCameraDimension();

    int getCameraId();

    int getCameraMode();

    int getChildIndex(String str);

    CommandManager getCommandManager();

    ContentResolver getContentResolver();

    Dialog getCurrentDialog();

    int getCurrentIAMode();

    Location getCurrentLocation();

    String getCurrentMenuKey();

    Module getCurrentModule();

    String getCurrentModuleName();

    int getCurrentPIPMask();

    long getCurrentRecordingTime();

    String getCurrentSelectedTitle();

    SettingMenu getCurrentSettingMenu();

    int getCurrentSettingMenuIndex();

    int getCurrentSettingMenuIndex(String str);

    String getCurrentSettingMenuName();

    int getCurrentStorage();

    String getCurrentStorageDirectory();

    String getDefaultFocusModeParameterForMultiWindowAF(LGParameters lGParameters);

    int getDeviceDegree();

    int getDialogID();

    Drawable getDrawable(int i);

    int[] getDualCameraPictureSize();

    boolean getEffectRecorderPausing();

    boolean getEnableInput();

    EngineProcessor getEngineProcessor();

    FaceBeauty getFaceBeauty();

    boolean getFlashEnableForShotMode();

    int getFocusAreaHeight();

    int getFocusAreaLeftMargin();

    int getFocusAreaWidth();

    int getFocusState();

    int getFreePanoramaEngineStatus();

    int[] getFreePanoramaResultSize();

    int getFreePanoramaStatus();

    long getFreeSpace();

    PreferenceGroup getFrontPreferenceGroup();

    boolean getGoingAutoReviewForQuickView();

    MainHandler getHandler();

    int getHeadsetstate();

    boolean getIAFlashStatus();

    Bitmap getImage(Bitmap bitmap, int i, boolean z);

    ImageHandler getImageHandler();

    ImageHandler getImageHandler(boolean z);

    ArrayList<Integer> getImageListRotation();

    ArrayList<Uri> getImageListUri();

    int getImageRotationDegree();

    boolean getInCaptureProgress();

    String getIndexMenuKey(int i);

    boolean getIsCharging();

    boolean getIsFileSizeLimitReached();

    LGCamera getLG();

    LGParameters getLGParam();

    String getLanguageType();

    Bitmap getLastThumbnail(Uri uri);

    ListPreference getListPreference(int i);

    String getLiveEffect();

    ArrayList<String> getLiveEffectList();

    Locale getLocale();

    boolean getLocationOn();

    int getMainBarAlphaValue();

    int getMainCameraDimension();

    int getManualFocusValue();

    int getMaxVideoDurationInMs();

    boolean getMediaUSBConnectAtStartRecord();

    String getMenuCommand();

    Uri getMostRecentThumbnailUri(boolean z, int i);

    boolean getNeedProgressDuringCapture();

    int getObjectTrackingState();

    boolean getOpenLBSSetting();

    int getOrientation();

    int getOrientationDegree();

    int getParameteredRotation();

    Parameters getParameters();

    long getPicturesRemaining();

    int[] getPlanePanoramaResultSize();

    int getPlanePanoramaStatus();

    PreferenceGroup getPreferenceGroup();

    PreviewPanelController getPreviewPanelController();

    String getPreviewSizeOnDevice();

    String getPreviewSizeOnScreen();

    int getPreviousEffectType();

    String getPreviousPictureSize();

    String getPreviousRecordModeString();

    String getPreviousResolution();

    String getPreviousShotModeString();

    String getQFIndexListItem(int i);

    boolean[] getQFLMenuEnable();

    int getQfIndex(String str);

    ArrayList<String> getQfIndexList();

    int getQueueCount();

    ListPreference getQuickFunctionControllerMenuTag(int i);

    View getQuickFunctionControllerMenuView(int i);

    ListPreference getQuickFunctionDragControllerMenuTag(int i);

    int getRecentMessageType();

    boolean getRecordLocation();

    long getRecordingDurationLimit();

    long getRecordingSizeLimit();

    boolean getRefocusPictures();

    long getRequestedVideoSizeLimit();

    Resources getResources();

    Uri getSaveURI();

    String getSavedFileName();

    Uri getSavedImageUri();

    SettingMenuItem getSelectedChild();

    int getSelectedChildCount(int i);

    int getSelectedChildIndex();

    String getSelectedQuickFunctionMenuKey();

    SettingController getSettingController();

    int getSettingIndex(String str);

    ListPreference getSettingListPreference(String str);

    String getSettingMenuCommand();

    int getSettingMenuCount();

    boolean getSettingMenuEnable(int i);

    SettingMenuItem getSettingMenuItem(int i);

    SettingMenuItem getSettingMenuItem(String str);

    String getSettingParameterValue();

    String getSettingValue(String str);

    boolean getShowCameraErrorPopup();

    String getSmartModeForPictureSize();

    int getSmartZoomFocusViewMode();

    Runnable getSnapshotRunnable();

    long getStartTime();

    int getStatus();

    String getStorageBucketId();

    String getStorageBucketId(int i);

    int getStorageMessageId();

    String getStoragePopupMessage();

    int getStorageState();

    String getString(int i);

    boolean getSubCameraModeRunning();

    int getSubMenuMode();

    SurfaceHolder getSurfaceHolder();

    void getThumbnailAndUpdateButton();

    Uri getThumbnailControllerUri();

    boolean getTimeMachineComplete();

    boolean getTimeMachinePictures();

    String getTimeMachineStorageDirectory();

    int getTimerCaptureDelay();

    ToastController getToastController();

    VideoFile getVideoFile();

    long getVideoFileSize();

    int getVideoState();

    int getZoomBarValue();

    int getZoomCursorMaxStep();

    float getZoomMaxValue();

    float getZoomRatio();

    void gotoHelpActivity(String str);

    boolean hasSaveURI();

    void hideChildCustomView(boolean z);

    boolean hideFocus();

    boolean hideForPhotoStory();

    void hideFreePanoramaTakingGuide();

    void hideGestureGuide();

    void hideIndicatorController();

    void hideOptionMenu();

    void hideOsd();

    void hideOsdByForce();

    void hidePopupAnimation(int i);

    void hidePreviewPanelController();

    void hidePreviewPanelLiveSnapshotButton();

    void hideQuickFunctionController();

    void hideQuickFunctionDragController(boolean z);

    void hideRecoridngStopButton();

    void hideShotModeMenu(boolean z);

    void hideSmartZoomFocusView();

    void hideSubWindowResizeHandler();

    View inflateStub(int i);

    View inflateView(int i);

    void initControllers();

    void initFaceDetectInfo();

    void initFocusAreas();

    void initMultiWindowAFView();

    void initQuickFunctionEnabled();

    RelativeLayout initSettingBrightnessBar();

    void initSettingMenu();

    void initSmartZoomFocusView();

    void initializeRecordingDual(String str, long j, int i, long j2);

    void initializeRecordingEffect(String str, long j, int i, long j2);

    boolean isAttachIntent();

    boolean isAttachMode();

    boolean isAudiozoom_ExceptionCase(boolean z);

    boolean isAvailableResumeVideo();

    boolean isBackKeyPressed();

    boolean isBlockingFaceTrFocusing();

    boolean isBurstShotStop();

    boolean isCafSupported();

    boolean isCamcorderRotation(boolean z);

    boolean isCameraKeyLongPressed();

    boolean isChangeMode();

    boolean isChangingToOtherActivity();

    boolean isClearView();

    boolean isCompleteProcessFrame();

    boolean isConfigurationChanging();

    boolean isConfigureLandscape();

    boolean isContinuousFocusActivating();

    boolean isControllerInitialized();

    boolean isCurrnetModuleRunning();

    boolean isDualCameraActive();

    boolean isDualRecordingActive();

    boolean isEffectsCamcorderActive();

    boolean isEffectsCameraActive();

    boolean isEnteringViewShowing();

    boolean isErrorOccuredAndFinish();

    boolean isExitIgnoreDuringSaving();

    boolean isExternalStorageRemoved();

    boolean isFinishingActivity();

    boolean isFlashOffByHighTemperature();

    boolean isFocusViewVisible();

    boolean isGalleryLaunching();

    boolean isGestureShotActivated();

    boolean isIndicatorControllerInitialized();

    boolean isLiveEffectActive();

    boolean isLiveEffectDrawerOpened();

    boolean isLiveEffectDrawerShown();

    boolean isMMSIntent();

    boolean isMMSRecording();

    boolean isManualFocusBarVisible();

    boolean isMediaScanning();

    boolean isNullQuickFunctionSettingView();

    boolean isNullSettingView();

    boolean isObjectTrackingEnabledForSmartZoom();

    boolean isOptionMenuShowing();

    boolean isPIPFrameDrawerOpened();

    boolean isPIPFrameDrawerShown();

    boolean isPanoramaStarted();

    boolean isPanoramaUIShown();

    boolean isPanoramaUpdatebutton();

    boolean isPausing();

    boolean isPlayRingMode();

    boolean isPressedShutterButton();

    boolean isPreviewOnGoing();

    boolean isPreviewRendered();

    boolean isPreviewing();

    boolean isQuickFunctionDragControllerVisible();

    boolean isQuickFunctionList(int i);

    boolean isQuickFunctionSettingControllerShowing();

    boolean isQuickFunctionSettingRemoving();

    boolean isReadyEngineProcessor();

    boolean isRecordedLengthTooShort();

    boolean isRecordingControllerInit();

    boolean isRefocusShotHasPictures();

    boolean isRotateDialogVisible();

    boolean isSendBroadcastIntent();

    boolean isSensorSupportBackdropper();

    boolean isSettingControllerVisible();

    boolean isSettingViewRemoving();

    boolean isShotModeMenuVisible();

    boolean isShutterButtonEnable();

    boolean isShutterButtonLongKey();

    boolean isShutterFocusLongKey();

    boolean isSmartZoomRecordingActive();

    boolean isStopRecordingByMountedAction();

    boolean isStorageControllerInitialized();

    boolean isStorageFull();

    boolean isStorageToastShowing();

    boolean isSwapCameraProcessing();

    boolean isSwitcherLeverEnable();

    boolean isSwitcherLeverPressed();

    boolean isSynthesisInProgress();

    boolean isTimeMachineModeOn();

    boolean isTimemachineHasPictures();

    boolean isTimerShotCountdown();

    boolean isToastControllerShowing();

    boolean isfacePreviewInitialized();

    void keepScreenOnAwhile();

    boolean needProgressBar();

    void onDismissRotateDialog();

    void onFaceDetectionFromHal(Face[] faceArr);

    void pauseAndResumeRecording(boolean z);

    void pauseRecording();

    void perfLockAcquire();

    void playAFSound(boolean z);

    void playBurstShotShutterSound(boolean z);

    void playClearShotShutterSound(boolean z);

    void playContinuousShutterSound();

    void playFreePanoramaShutterSound();

    void playRecordingSound(boolean z);

    void playShutterSound();

    void playTimerSound(int i);

    void playVoiceCommandSound(int i);

    void postOnUiThread(Runnable runnable);

    void postOnUiThread(Runnable runnable, long j);

    boolean postviewRequestInit();

    void putPreviewFrameForGesture(byte[] bArr, Camera camera);

    void qflMenuAnimation(boolean z, int i, AnimationListener animationListener);

    void qflSettingAnimation(View view, boolean z);

    void quickFunctionAllMenuSelected(boolean z);

    void quickFunctionControllerInitMenu();

    void quickFunctionControllerRefresh(boolean z);

    void recordingControllerHide();

    void recordingControllerShow();

    void refresh3dDepthController();

    void refresh3dDepthSettingBars();

    void refreshBeautyshotController();

    void refreshBeautyshotSettingBars();

    void refreshBrightnessController();

    void refreshBrightnessExpandSettingBars();

    void refreshBrightnessSettingBars();

    void refreshManualFocusController();

    void refreshQuickButton();

    void refreshZoomController();

    void refreshZoomSettingBars();

    void registerFaceTrackingCallback();

    void registerObjectCallback();

    void releaseAllEngine();

    void releaseEngine(String str);

    void releaseEngine(boolean z);

    void releaseGestureEngine();

    void removeAllImageList();

    void removeCallbacks(Runnable runnable);

    void removeFreePanoramaBlackBg();

    void removeFreePanoramaView();

    void removePanoramaView();

    void removePlanePanoramaView();

    void removePostRunnable(Object obj);

    void removePreviewCallback();

    void removeQuickButton(int i);

    void removeQuickButtonAll();

    void removeQuickFunctionSettingView();

    void removeScheduledAllCommand();

    void removeScheduledCommand(String str);

    void removeSettingView();

    void removeSettingViewAll();

    void reset3dDepthController();

    void resetAudioZoomMenu();

    void resetBarController(int i);

    void resetBeautyshotController();

    void resetBrightnessController();

    void resetController();

    void resetDisplayTimeout3dDepth();

    void resetDisplayTimeoutBeautyshot();

    void resetDisplayTimeoutBrightness();

    void resetDisplayTimeoutManualFocus();

    void resetDisplayTimeoutZoom();

    void resetManualFocusController();

    void resetQFIndex();

    void resetScreenTimeout();

    void resetSettingMenu();

    void resetSwitcherLever();

    void resetZoomController();

    void restartFreePanorama();

    void restartPreview(LGParameters lGParameters, boolean z);

    void restoreLiveEffectSubMenu();

    void restoreSubWindow();

    void resumeRecording();

    void resumeUpdateReordingTime();

    void rotateAllController(int i, boolean z);

    void rotateSettingBar(int i, int i2);

    void runGestureEngine(boolean z);

    void runOnUiThread(Runnable runnable);

    boolean saveClearShotPicture(byte[] bArr, int i);

    boolean saveImageSavers(byte[] bArr, Bitmap bitmap, int i, boolean z, boolean z2);

    boolean savePicture(byte[] bArr, Bitmap bitmap);

    void saveQFLIndex();

    void saveRefocusShotMap(byte[] bArr);

    boolean saveRefocusShotPicture(byte[] bArr, int i);

    boolean saveTimeMachinePicture(byte[] bArr, int i);

    void set3DSwitchImage();

    void set3DSwitchVisible(boolean z);

    void setAllChildMenuEnabled(String str, boolean z);

    void setAllPreferenceApply(int i, String str, String str2);

    void setApplicationMode(int i);

    void setAudioRecogEngineStart();

    void setAudioRecogEngineStop();

    void setAudioZoomGuideViewLayout(int i, int i2, int i3);

    void setAudiozoomStart(boolean z);

    void setAudiozoomStartInRecording(boolean z);

    void setAudiozoom_ExceptionCase(boolean z);

    void setAudiozoombuttonstate();

    void setAudiozoomvalue(String str);

    void setBackKeyPressed(boolean z);

    void setBackKeyRecStop(boolean z);

    void setBackgroundColorBlack();

    void setBackgroundColorWhite();

    void setBackupCurrentMenuIndex(int i);

    void setBeautyshotProgress(boolean z);

    void setBlockingFaceTrFocusing(boolean z);

    void setBurstShotStop(boolean z);

    void setButtonRemainEnabled(int i, boolean z);

    void setButtonRemainEnabled(int i, boolean z, boolean z2);

    void setButtonRemainRefresh();

    void setCafOnGoing(boolean z);

    void setCameraId(int i);

    void setCameraIdBeforeStartInit();

    void setCameraMode(int i);

    void setCaptureData(byte[] bArr);

    void setChangeMode();

    void setChangingToOtherActivity(boolean z);

    void setCheckClickTime(long j);

    boolean setCheckToggleTime(int i);

    void setClearFocusAnimation();

    void setContinuousShotAlived(boolean z);

    void setCurrentIAMode(int i);

    void setCurrentPIPMask(int i);

    void setCurrentRecordingTime(long j);

    boolean setCurrentSettingMenu(int i);

    void setCurrentSettingMenuEnable(String str, String str2, boolean z);

    void setCurrentSettingMenuEnable(String str, boolean z);

    boolean setCurrentSettingMenuOnly(int i);

    void setCurrentStorage(int i);

    void setDefaultPIPMask();

    void setDegree(int i, int i2, int i3, boolean z);

    void setDegree(int i, int i2, boolean z);

    void setEffectRecorderPausing(boolean z);

    boolean setEnable3ALocks(LGParameters lGParameters, boolean z);

    void setEndTime(long j);

    void setEngineProcessor();

    void setErrorOccuredAndFinish(boolean z);

    void setExitIgnoreDuringSaving(boolean z);

    void setFaceBeutyShotParameter(int i);

    void setFlashOffByHighTemperature(boolean z);

    void setFocalLength(float f);

    void setFocusAreaWindow(int i, int i2, int i3);

    void setFocusPosition(int i);

    void setFocusRectangleInitialize();

    void setFocusState(int i);

    void setForced_audiozoom(boolean z);

    void setFullFrameContinuousShot(Parameters parameters, int i);

    void setGalleryLaunching(boolean z);

    void setGestureShotActivated(boolean z);

    void setIAFlashStatus(boolean z);

    void setImageRotationDegree(int i);

    void setInCaptureProgress(boolean z);

    void setIndicatorLayout(int i);

    void setIsFileSizeLimitReached(boolean z);

    void setIsSendBroadcastIntent(boolean z);

    void setIsSwapCameraProcessing(boolean z);

    void setKeepScreenOn();

    void setLastPictureThumb(byte[] bArr, Uri uri, boolean z);

    void setLastThumb(Uri uri, boolean z);

    void setLimitationToLiveeffect(boolean z);

    void setLiveEffect(String str);

    String setLiveeffectLimit();

    void setLocationOn(boolean z);

    void setLockChangeConfiguration(boolean z);

    void setLockScreenPreventPreview(boolean z);

    void setMainBarAlpha(int i);

    void setMainButtonDisable();

    void setMainButtonEnable();

    void setMainButtonEnable(String str);

    void setMainButtonVisible(boolean z);

    void setMainCameraDimension(int i);

    void setManualFocusValue(int i);

    void setMediaUSBConnectAtStartRecord(boolean z);

    void setMenuEnableForSceneMode(int i);

    void setModeMenuVisibility(int i);

    void setModule(String str);

    void setMultiWindowAFView(int[] iArr);

    void setNeedProgressDuringCapture(boolean z);

    void setOpenLBSSetting(boolean z);

    void setOrientationForced(int i);

    boolean setPIPMask(int i);

    void setPIPRotate(int i);

    void setPanoramaEngine();

    void setParameteredRotation(int i);

    void setParameters(Parameters parameters);

    void setPicturesRemaining(long j);

    void setPreferenceMenuEnable(String str, boolean z, boolean z2);

    void setPreferenceMenuOnlyEnable(String str, boolean z);

    void setPrevResolutionWithStoredValue();

    void setPreviewEffectForBeautyShotMode(LGParameters lGParameters, boolean z);

    void setPreviewRendered(boolean z);

    void setPreviewVisibility(int i);

    void setPreviewing(boolean z);

    void setPreviousPictureSize(String str);

    void setPreviousRecordModeString(String str);

    void setPreviousResolution(String str);

    void setPreviousShotModeString(String str);

    void setQFLMenuEnabled(int i, boolean z);

    void setQFLMenuSelected(int i, boolean z);

    void setQuickButtonForcedDisable(boolean z);

    void setQuickButtonMenuEnable(int i, boolean z, boolean z2);

    void setQuickButtonMenuEnable(boolean z, boolean z2);

    void setQuickButtonMode(boolean z);

    void setQuickButtonVisible(int i, int i2, boolean z);

    void setQuickFunctionAllMenuEnabled(boolean z, boolean z2);

    void setQuickFunctionControllerAllMenuIcons();

    void setQuickFunctionControllerMenu(int i);

    void setQuickFunctionControllerMenuIcon(int i, int i2);

    void setQuickFunctionControllerMmsLimit();

    void setQuickFunctionControllerMmsLimit(boolean z);

    void setQuickFunctionControllerVisible(boolean z);

    void setQuickFunctionDragControllerSelectIndex(int i);

    void setQuickFunctionMenuForcedDisable(boolean z);

    void setRecIndicatorLayout(int i, int i2, int i3);

    void setRecordLocation(boolean z);

    void setRecordingTime_realduration(long j);

    void setRefocusShotHasPictures(boolean z);

    void setRefocusShotPreviewGuideVisibility(boolean z);

    void setRemoveFreePanoramaBlackBg(boolean z);

    void setSavedFileName(String str);

    void setSavedImageUri(Uri uri);

    void setScaleWidthHeight(float f);

    void setSceneModeForAdvanced(Parameters parameters, String str);

    void setSelectedChild(String str, String str2, boolean z);

    boolean setSelectedChild(int i);

    boolean setSelectedChild(int i, int i2);

    boolean setSetting(int i, int i2);

    boolean setSetting(String str, String str2);

    boolean setSetting(String str, String str2, boolean z);

    void setSettingControllerMmsLimit(boolean z);

    void setSettingForDualCamera(boolean z);

    void setShowCameraErrorPopup(boolean z);

    void setShutterButtonClicked(boolean z);

    void setShutterButtonImage(boolean z, int i);

    void setShutterFocusLongKey(boolean z);

    void setSmartCameraMode(LGParameters lGParameters, boolean z);

    void setSmartModeForPictureSize(String str);

    void setSmartZoomFocusViewPosition(int i, int i2);

    void setStartrecordingdegree(int i);

    void setSubButton(int i, int i2);

    void setSubCameraModeRunning(boolean z);

    void setSubMenuMode(int i);

    void setSwitcherVisible(boolean z);

    void setSwithcerEnable(boolean z);

    void setThumbnailButtonVisibility(int i);

    void setTimeMachineComplete(boolean z);

    void setTimeMachineLimit(boolean z);

    void setTimeMachineShot(Parameters parameters, int i);

    void setTimemachineHasPictures(boolean z);

    void setTimerAndSceneSmartShutterEnable(Parameters parameters, boolean z, boolean z2, boolean z3);

    void setTimerSetting(int i);

    void setTimerShotCountdown(boolean z);

    void setVideoFlash(boolean z);

    void setVideoState(int i);

    void setVideoStateOnly(int i);

    void setZoomMaxValue(float f);

    void setZoomRatio(float f);

    void show3dDepthController(boolean z);

    void showBeautyShotBarForNewUx(boolean z);

    void showBeautyshotController(boolean z);

    void showBrightnessController(boolean z);

    void showBubblePopupVisibility(int i, long j, boolean z);

    void showCameraErrorAndFinish();

    void showCameraStoppedAndFinish();

    void showControllerForHideSettingMenu(boolean z, boolean z2);

    void showDefaultQuickButton(boolean z);

    void showDialogPopup(int i);

    boolean showFocus();

    boolean showFocus(boolean z);

    void showFreePanoramaBlackBg();

    void showFreePanoramaView();

    boolean showGalleryQuickViewWindow(boolean z, long j);

    void showGestureGuide();

    void showHeatingwarning();

    boolean showHelpGuidePopup(String str, int i, boolean z);

    void showIndicatorController();

    void showManualFocusController(boolean z);

    void showOsd();

    void showPanoramaView();

    void showPopupAnimation(int i);

    void showPreview();

    void showPreviewPanelController();

    void showPreviewPanelLiveSnapShotButton();

    void showProgressDialog();

    void showQuickFunctionController();

    void showQuickFunctionDragController();

    void showQuickMenuEnteringGuide(boolean z);

    void showRecoridngStopButton();

    void showRequestedSizeLimit();

    void showSavingProgressDialog();

    void showSetting3dDepthControl(boolean z);

    void showSettingBeautyShotControl(boolean z);

    void showSettingBrightnessControl(boolean z);

    void showSettingZoomControl(boolean z);

    void showShotModeMenu();

    void showSmartZoomFocusView();

    void showStorageHint(int i);

    void showSubButtonInit(boolean z);

    void showSubWindowResizeHandler(float f, float f2);

    void showZoomController(boolean z);

    void smartShutterEnable(boolean z);

    boolean snapshotOnContinuousFocus();

    boolean snapshotOnIdle();

    void startActivityForResult(Intent intent, int i);

    void startAudiozoom();

    void startFaceDetection(boolean z);

    void startFaceDetectionFromHal(boolean z);

    void startFocusByTouchPress(int i, int i2, boolean z);

    void startFreePanorama();

    void startFreePanoramaEngine(Parameters parameters);

    void startGestureEngine();

    void startHeatingwarning();

    void startLiveEffectDrawerSubMenuRotation(int i);

    void startObjectTrackingFocus(int i, int i2, int i3, int i4, int i5);

    void startObjectTrackingFocusForSmartZoom(int i, int i2, int i3, int i4, int i5);

    void startPIPFrameSubMenuRotation(int i);

    void startPanorama();

    void startPlanePanorama();

    void startPlanePanoramaEngine();

    void startPreview(LGParameters lGParameters, boolean z);

    void startPreviewEffect();

    void startReceivingLocationUpdates();

    void startRecording();

    void startRecordingEffect();

    void startSubMenuRotation(int i);

    void startTimerShot();

    void stopAudiozoom();

    void stopBurstShotSound();

    boolean stopByUserAction();

    void stopClearShotSound();

    void stopFaceDetection();

    void stopFaceDetectionFromHal();

    void stopFreePanorama();

    void stopFreePanoramaEngine(Parameters parameters);

    void stopGestureEngine();

    void stopHeatingwarning();

    void stopPanorama();

    void stopPlanePanorama();

    void stopPlanePanoramaEngine();

    void stopPreview();

    void stopReceivingLocationUpdates();

    void stopRecording(boolean z);

    void stopRecordingByPausing();

    void stopRecordingEffect();

    void stopSoundContinuous();

    void stopVoiceCommandSound();

    void storageToastShow(String str, boolean z, boolean z2);

    void storageToasthide(boolean z);

    void storePreviousResolution(String str);

    void swapPreviewEffect(BaseEngine baseEngine);

    void switchCameraId(int i);

    void toast(int i);

    void toast(String str);

    void toast(String str, boolean z);

    void toastControllerHide(boolean z);

    void toastLong(String str);

    void toastMiddleLong(String str);

    void unregisterCAFCallback();

    void unregisterObjectCallback();

    void updateAllBars(int i, int i2);

    void updateAudioIndicator();

    void updateAudiozoom(boolean z, int i);

    boolean updateDualRecordingSelection();

    boolean updateEffectSelection();

    void updateFlashIndicator(boolean z, String str);

    void updateFocusIndicator();

    void updateGpsIndicator();

    void updateModeMenuIndicator();

    void updateModeMenuIndicator(String str);

    void updateNavigationBarShape();

    void updateRemainIndicator();

    void updateSceneIndicator(boolean z, String str);

    void updateSizeIndicator();

    boolean updateSmartZoomRecordingSelection();

    void updateStabilizationIndicator();

    void updateStorageIndicator();

    void updateThumbnailButton();

    void updateThumbnailButtonVisibility();

    void updateTimerIndicator();

    void updateVoiceShutterIndicator(boolean z);

    void waitAvailableQueueCount(int i);

    void waitSaveImageThreadDone();

    void waitStopRecordingEffectThreadDone();
}
