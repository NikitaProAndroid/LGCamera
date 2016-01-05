package com.lge.camera.dialog;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.properties.CameraConstants;
import com.lge.camera.setting.Setting;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.DialogCreater;
import com.lge.camera.util.Util;
import com.lge.morpho.core.Error;
import com.lge.morpho.utils.multimedia.MediaProviderUtils;
import com.lge.olaworks.library.FaceDetector;
import java.util.MissingFormatArgumentException;

public class HelpVoicePhotoPopupRotatableDialog extends HelpRotateDialog {
    public HelpVoicePhotoPopupRotatableDialog(ControllerFunction function) {
        super(function);
    }

    public void create(boolean useCheckBox, int dialogId) {
        super.create(useCheckBox, dialogId);
        if (this.mGet.getSettingValue(Setting.KEY_VOICESHUTTER).equals(CameraConstants.SMART_MODE_ON)) {
            this.mGet.setAudioRecogEngineStop();
        }
        this.mGet.getActivity().setVolumeControlStream(3);
    }

    protected View inflateHelpDialogView() {
        return this.mGet.inflateView(R.layout.rotate_voice_shutter_help_dialog);
    }

    protected void setExtraContents(View rotateHelpView) {
        setMultiCommandHelpGuide(rotateHelpView);
    }

    private void setMultiCommandHelpGuide(View helpDialogView) {
        try {
            boolean z;
            TextView voiceSoundText_cheese = (TextView) helpDialogView.findViewById(R.id.voice_command_sound_cheese);
            TextView voiceSoundText_smile = (TextView) helpDialogView.findViewById(R.id.voice_command_sound_smile);
            TextView voiceSoundText_whisky = (TextView) helpDialogView.findViewById(R.id.voice_command_sound_whisky);
            TextView voiceSoundText_kimchi = (TextView) helpDialogView.findViewById(R.id.voice_command_sound_kimchi);
            TextView voiceSoundText_LG = (TextView) helpDialogView.findViewById(R.id.voice_command_sound_lg);
            TextView voiceSoundText_torimasu = (TextView) helpDialogView.findViewById(R.id.voice_command_sound_torimasu);
            if (isDontNeedToSoundWord()) {
                voiceSoundText_cheese.setVisibility(8);
                voiceSoundText_smile.setVisibility(8);
                voiceSoundText_whisky.setVisibility(8);
                voiceSoundText_kimchi.setVisibility(8);
                voiceSoundText_LG.setVisibility(8);
                voiceSoundText_torimasu.setVisibility(8);
            } else {
                voiceSoundText_cheese.setText(String.format("[%s]", new Object[]{this.mGet.getString(R.string.sp_voiceshutter_sound_cheese_NORMAL)}));
                voiceSoundText_smile.setText(String.format("[%s]", new Object[]{this.mGet.getString(R.string.sp_voiceshutter_sound_smile_NORMAL)}));
                voiceSoundText_whisky.setText(String.format("[%s]", new Object[]{this.mGet.getString(R.string.sp_voiceshutter_sound_whisky_NORMAL)}));
                voiceSoundText_kimchi.setText(String.format("[%s]", new Object[]{this.mGet.getString(R.string.sp_voiceshutter_sound_kimchi_NORMAL)}));
                voiceSoundText_LG.setText(String.format("[%s]", new Object[]{this.mGet.getString(R.string.sp_voiceshutter_sound_LG_NORMAL)}));
                voiceSoundText_torimasu.setText(String.format("[%s]", new Object[]{this.mGet.getString(R.string.sp_voiceshutter_sound_torimasu_NORMAL)}));
            }
            setSpeakerClickListener(helpDialogView, R.id.speaker_image_cheese, 0, (String) voiceSoundText_cheese.getText());
            setSpeakerClickListener(helpDialogView, R.id.speaker_image_smile, 1, (String) voiceSoundText_smile.getText());
            setSpeakerClickListener(helpDialogView, R.id.speaker_image_whisky, 2, (String) voiceSoundText_whisky.getText());
            setSpeakerClickListener(helpDialogView, R.id.speaker_image_kimchi, 3, (String) voiceSoundText_kimchi.getText());
            setSpeakerClickListener(helpDialogView, R.id.speaker_image_lg, 4, (String) voiceSoundText_LG.getText());
            setSpeakerClickListener(helpDialogView, R.id.speaker_image_torimasu, 5, (String) voiceSoundText_torimasu.getText());
            Context applicationContext = this.mGet.getApplicationContext();
            if (Util.isEqualDegree(this.mGet.getResources(), this.mGet.getOrientationDegree(), 0) || Util.isEqualDegree(this.mGet.getResources(), this.mGet.getOrientationDegree(), MediaProviderUtils.ROTATION_180)) {
                z = true;
            } else {
                z = false;
            }
            DialogCreater.setCommandLayout(applicationContext, helpDialogView, z);
        } catch (NullPointerException e) {
            CamLog.w(FaceDetector.TAG, "NullPointerException:", e);
        } catch (MissingFormatArgumentException e2) {
            CamLog.w(FaceDetector.TAG, "MissingFormatArgumentException:", e2);
        }
    }

    public void startRotation(int degree) {
        boolean z = false;
        super.startRotation(degree);
        Context applicationContext = this.mGet.getApplicationContext();
        View view = this.mView;
        if (Util.isEqualDegree(this.mGet.getResources(), this.mGet.getOrientationDegree(), 0) || Util.isEqualDegree(this.mGet.getResources(), this.mGet.getOrientationDegree(), MediaProviderUtils.ROTATION_180)) {
            z = true;
        }
        DialogCreater.setCommandLayout(applicationContext, view, z);
    }

    private void setSpeakerClickListener(View helpDialogView, int resId, final int voiceSound, String talkBackMessage) {
        if (helpDialogView != null) {
            ImageView speakerImage = (ImageView) helpDialogView.findViewById(resId);
            speakerImage.setContentDescription(talkBackMessage);
            speakerImage.setOnClickListener(new OnClickListener() {
                public void onClick(View view) {
                    if (HelpVoicePhotoPopupRotatableDialog.this.mGet != null) {
                        HelpVoicePhotoPopupRotatableDialog.this.mGet.playVoiceCommandSound(voiceSound);
                    }
                }
            });
        }
    }

    private boolean isDontNeedToSoundWord() {
        try {
            String commandWordCheese = this.mGet.getString(R.string.sp_voiceshutter_word_cheese_NORMAL).toLowerCase();
            String soundWordCheese = this.mGet.getString(R.string.sp_voiceshutter_sound_cheese_NORMAL).toLowerCase();
            String commandWordSmile = this.mGet.getString(R.string.sp_voiceshutter_word_smile_NORMAL).toLowerCase();
            String soundWordSmile = this.mGet.getString(R.string.sp_voiceshutter_sound_smile_NORMAL).toLowerCase();
            String commandWordWhisky = this.mGet.getString(R.string.sp_voiceshutter_word_whisky_NORMAL).toLowerCase();
            String soundWordWhisky = this.mGet.getString(R.string.sp_voiceshutter_sound_whisky_NORMAL).toLowerCase();
            String commandWordKimchi = this.mGet.getString(R.string.sp_voiceshutter_word_kimchi_NORMAL).toLowerCase();
            String soundWordKimchi = this.mGet.getString(R.string.sp_voiceshutter_sound_kimchi_NORMAL).toLowerCase();
            String commandWordLG = this.mGet.getString(R.string.sp_voiceshutter_word_LG_NORMAL).toLowerCase();
            String soundWordLG = this.mGet.getString(R.string.sp_voiceshutter_sound_LG_NORMAL).toLowerCase();
            String commandWordTorimasu = this.mGet.getString(R.string.sp_voiceshutter_word_torimasu_NORMAL).toLowerCase();
            String soundWordTorimasu = this.mGet.getString(R.string.sp_voiceshutter_sound_torimasu_NORMAL).toLowerCase();
            if (commandWordCheese.equals(soundWordCheese) && commandWordSmile.equals(soundWordSmile) && ((commandWordWhisky.equals(soundWordWhisky) || (commandWordWhisky.equals("whiskey") && soundWordWhisky.equals("whisky"))) && commandWordKimchi.equals(soundWordKimchi) && commandWordLG.equals(soundWordLG) && commandWordTorimasu.equals(soundWordTorimasu))) {
                return true;
            }
        } catch (NullPointerException e) {
            CamLog.w(FaceDetector.TAG, "NullPointerException:", e);
            return false;
        } catch (MissingFormatArgumentException e2) {
            CamLog.w(FaceDetector.TAG, "MissingFormatArgumentException:", e2);
        }
        return false;
    }

    protected int getHorizontalHelpDialogWidth() {
        return DialogCreater.getHorizontalHelpDialogWidth(this.mGet.getApplicationContext(), true);
    }

    protected int getVerticalMessageScrollWidth() {
        return -1;
    }

    public void onDismiss() {
        this.mGet.getActivity().setVolumeControlStream(Error.ERROR_GENERAL_ERROR);
        this.mGet.setAudioRecogEngineStart();
        super.onDismiss();
    }
}
