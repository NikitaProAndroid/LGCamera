package com.lge.camera.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.lge.camera.R;
import com.lge.camera.components.RotateView;
import com.lge.camera.util.CamLog;
import com.lge.camera.util.DialogCreater;
import com.lge.olaworks.library.FaceDetector;
import com.lge.voiceshutter.library.LGKeyRec;
import java.util.MissingFormatArgumentException;

public class CustomAlertDialog extends Dialog {
    private final OnLoadCompleteListener completeListener;
    private Activity mActivity;
    private ImageView mImage;
    private TextView mMessage;
    private Button mOkButton;
    private int mOrientation;
    private SoundPool mSound_pool;
    private int mSound_voiceShutter_LG;
    private int mSound_voiceShutter_cheese;
    private int mSound_voiceShutter_kimchi;
    private int mSound_voiceShutter_smile;
    private int mSound_voiceShutter_torimasu;
    private int mSound_voiceShutter_whisky;
    private TextView mTitle;
    private TextView mTitleInContent;
    private int mVoiceCommandStream;

    protected CustomAlertDialog(Context context, int orientation) {
        super(context, R.style.helpActivityDialog);
        this.mSound_voiceShutter_cheese = 0;
        this.mSound_voiceShutter_smile = 0;
        this.mSound_voiceShutter_whisky = 0;
        this.mSound_voiceShutter_kimchi = 0;
        this.mSound_voiceShutter_LG = 0;
        this.mSound_voiceShutter_torimasu = 0;
        this.mVoiceCommandStream = 0;
        this.mOrientation = 0;
        this.completeListener = new OnLoadCompleteListener() {
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                CamLog.v(FaceDetector.TAG, "Sound load complete" + sampleId + ",status:" + status);
            }
        };
        setContentView(R.layout.helpdialog);
        getWindow().setLayout(-1, -1);
        this.mSound_pool = new SoundPool(6, 3, 0);
        this.mSound_pool.setOnLoadCompleteListener(this.completeListener);
        this.mActivity = (Activity) context;
        this.mOrientation = orientation;
        this.mTitle = (TextView) findViewById(R.id.title_text);
        this.mTitleInContent = (TextView) findViewById(R.id.title_text_in_content);
        this.mImage = (ImageView) findViewById(R.id.message_image);
        this.mMessage = (TextView) findViewById(R.id.message_text);
        this.mOkButton = (Button) findViewById(R.id.ok_button);
        this.mOkButton.setOnClickListener(new OnClickListener() {
            public void onClick(View arg0) {
                CustomAlertDialog.this.dismiss();
            }
        });
        loadVoiceGuideSound();
    }

    protected void onCreate(Bundle savedInsatnceState) {
        super.onCreate(savedInsatnceState);
    }

    public void setContentView(int layouutID) {
        super.setContentView(layouutID);
    }

    public void setTitle(CharSequence text) {
        if (this.mTitle != null) {
            this.mTitle.setText(text);
            this.mTitleInContent.setText(text);
        }
    }

    public void show() {
        setMultiCommandHelpGuide();
        super.show();
    }

    public void setMessage(CharSequence text) {
        if (this.mMessage != null) {
            this.mMessage.setText(text);
        }
    }

    public void setMessageImage(int resource, int level) {
        if (this.mImage != null) {
            this.mImage.setImageResource(resource);
            this.mImage.setImageLevel(level);
        }
    }

    public void setVoiceShutterVisibility(boolean visible) {
        if (visible) {
            findViewById(R.id.voice_command_layout).setVisibility(0);
        } else {
            findViewById(R.id.voice_command_layout).setVisibility(8);
        }
    }

    private void setMultiCommandHelpGuide() {
        try {
            TextView voiceSoundText_cheese = (TextView) findViewById(R.id.voice_command_sound_cheese);
            TextView voiceSoundText_smile = (TextView) findViewById(R.id.voice_command_sound_smile);
            TextView voiceSoundText_whisky = (TextView) findViewById(R.id.voice_command_sound_whisky);
            TextView voiceSoundText_kimchi = (TextView) findViewById(R.id.voice_command_sound_kimchi);
            TextView voiceSoundText_LG = (TextView) findViewById(R.id.voice_command_sound_lg);
            TextView voiceSoundText_torimasu = (TextView) findViewById(R.id.voice_command_sound_torimasu);
            if (isDontNeedToSoundWord()) {
                voiceSoundText_cheese.setVisibility(8);
                voiceSoundText_smile.setVisibility(8);
                voiceSoundText_whisky.setVisibility(8);
                voiceSoundText_kimchi.setVisibility(8);
                voiceSoundText_LG.setVisibility(8);
                voiceSoundText_torimasu.setVisibility(8);
            } else {
                voiceSoundText_cheese.setText(String.format("[%s]", new Object[]{this.mActivity.getString(R.string.sp_voiceshutter_sound_cheese_NORMAL)}));
                voiceSoundText_smile.setText(String.format("[%s]", new Object[]{this.mActivity.getString(R.string.sp_voiceshutter_sound_smile_NORMAL)}));
                voiceSoundText_whisky.setText(String.format("[%s]", new Object[]{this.mActivity.getString(R.string.sp_voiceshutter_sound_whisky_NORMAL)}));
                voiceSoundText_kimchi.setText(String.format("[%s]", new Object[]{this.mActivity.getString(R.string.sp_voiceshutter_sound_kimchi_NORMAL)}));
                voiceSoundText_LG.setText(String.format("[%s]", new Object[]{this.mActivity.getString(R.string.sp_voiceshutter_sound_LG_NORMAL)}));
                voiceSoundText_torimasu.setText(String.format("[%s]", new Object[]{this.mActivity.getString(R.string.sp_voiceshutter_sound_torimasu_NORMAL)}));
            }
            setSpeakerClickListener(R.id.speaker_image_cheese, 0, (String) voiceSoundText_cheese.getText());
            setSpeakerClickListener(R.id.speaker_image_smile, 1, (String) voiceSoundText_smile.getText());
            setSpeakerClickListener(R.id.speaker_image_whisky, 2, (String) voiceSoundText_whisky.getText());
            setSpeakerClickListener(R.id.speaker_image_kimchi, 3, (String) voiceSoundText_kimchi.getText());
            setSpeakerClickListener(R.id.speaker_image_lg, 4, (String) voiceSoundText_LG.getText());
            setSpeakerClickListener(R.id.speaker_image_torimasu, 5, (String) voiceSoundText_torimasu.getText());
            View parentView = findViewById(R.id.custom_dialog);
            Context context = this.mActivity;
            boolean z = this.mOrientation == 0 || this.mOrientation == 2;
            DialogCreater.setCommandLayout(context, parentView, z);
        } catch (NullPointerException e) {
            CamLog.w(FaceDetector.TAG, "NullPointerException:", e);
        } catch (MissingFormatArgumentException e2) {
            CamLog.w(FaceDetector.TAG, "MissingFormatArgumentException:", e2);
        }
    }

    private boolean isDontNeedToSoundWord() {
        try {
            String commandWordCheese = this.mActivity.getString(R.string.sp_voiceshutter_word_cheese_NORMAL).toLowerCase();
            String soundWordCheese = this.mActivity.getString(R.string.sp_voiceshutter_sound_cheese_NORMAL).toLowerCase();
            String commandWordSmile = this.mActivity.getString(R.string.sp_voiceshutter_word_smile_NORMAL).toLowerCase();
            String soundWordSmile = this.mActivity.getString(R.string.sp_voiceshutter_sound_smile_NORMAL).toLowerCase();
            String commandWordWhisky = this.mActivity.getString(R.string.sp_voiceshutter_word_whisky_NORMAL).toLowerCase();
            String soundWordWhisky = this.mActivity.getString(R.string.sp_voiceshutter_sound_whisky_NORMAL).toLowerCase();
            String commandWordKimchi = this.mActivity.getString(R.string.sp_voiceshutter_word_kimchi_NORMAL).toLowerCase();
            String soundWordKimchi = this.mActivity.getString(R.string.sp_voiceshutter_sound_kimchi_NORMAL).toLowerCase();
            String commandWordLG = this.mActivity.getString(R.string.sp_voiceshutter_word_LG_NORMAL).toLowerCase();
            String soundWordLG = this.mActivity.getString(R.string.sp_voiceshutter_sound_LG_NORMAL).toLowerCase();
            String commandWordTorimasu = this.mActivity.getString(R.string.sp_voiceshutter_word_torimasu_NORMAL).toLowerCase();
            String soundWordTorimasu = this.mActivity.getString(R.string.sp_voiceshutter_sound_torimasu_NORMAL).toLowerCase();
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

    private void setSpeakerClickListener(int resId, final int voiceSound, String talkBackMessage) {
        ImageView speakerImage = (ImageView) findViewById(resId);
        speakerImage.setContentDescription(talkBackMessage);
        speakerImage.setOnClickListener(new OnClickListener() {
            public void onClick(View view) {
                CustomAlertDialog.this.stopVoiceCommandSound();
                CustomAlertDialog.this.playVoiceCommandSound(voiceSound);
            }
        });
    }

    public void stopVoiceCommandSound() {
        CamLog.d(FaceDetector.TAG, "stopVoiceCommandSound ");
        if (this.mSound_pool != null && this.mVoiceCommandStream != 0) {
            this.mSound_pool.stop(this.mVoiceCommandStream);
        }
    }

    public void playVoiceCommandSound(int soundIndex) {
        CamLog.d(FaceDetector.TAG, "playVoiceCommandSound in help activity guide popup : soundIndex = " + soundIndex);
        if (this.mSound_pool != null) {
            int soundSource = 0;
            switch (soundIndex) {
                case LGKeyRec.EVENT_INVALID /*0*/:
                    soundSource = this.mSound_voiceShutter_cheese;
                    break;
                case LGKeyRec.EVENT_NO_MATCH /*1*/:
                    soundSource = this.mSound_voiceShutter_smile;
                    break;
                case LGKeyRec.EVENT_INCOMPLETE /*2*/:
                    soundSource = this.mSound_voiceShutter_whisky;
                    break;
                case LGKeyRec.EVENT_STARTED /*3*/:
                    soundSource = this.mSound_voiceShutter_kimchi;
                    break;
                case LGKeyRec.EVENT_STOPPED /*4*/:
                    soundSource = this.mSound_voiceShutter_LG;
                    break;
                case LGKeyRec.EVENT_START_OF_VOICING /*5*/:
                    soundSource = this.mSound_voiceShutter_torimasu;
                    break;
            }
            this.mVoiceCommandStream = this.mSound_pool.play(soundSource, RotateView.DEFAULT_TEXT_SCALE_X, RotateView.DEFAULT_TEXT_SCALE_X, 0, 0, RotateView.DEFAULT_TEXT_SCALE_X);
        }
    }

    private void loadVoiceGuideSound() {
        if (this.mSound_pool != null) {
            this.mSound_voiceShutter_cheese = this.mSound_pool.load(this.mActivity.getApplicationContext(), R.raw.voicesound_cheese, 1);
            this.mSound_voiceShutter_smile = this.mSound_pool.load(this.mActivity.getApplicationContext(), R.raw.voicesound_smile, 1);
            this.mSound_voiceShutter_whisky = this.mSound_pool.load(this.mActivity.getApplicationContext(), R.raw.voicesound_whisky, 1);
            this.mSound_voiceShutter_kimchi = this.mSound_pool.load(this.mActivity.getApplicationContext(), R.raw.voicesound_kimchi, 1);
            this.mSound_voiceShutter_LG = this.mSound_pool.load(this.mActivity.getApplicationContext(), R.raw.voicesound_lg, 1);
            this.mSound_voiceShutter_torimasu = this.mSound_pool.load(this.mActivity.getApplicationContext(), R.raw.voicesound_torimasu, 1);
        }
    }

    private void unloadVoiceGuideSound() {
        if (this.mSound_pool != null) {
            unloadSoundPool(this.mSound_voiceShutter_cheese);
            unloadSoundPool(this.mSound_voiceShutter_smile);
            unloadSoundPool(this.mSound_voiceShutter_whisky);
            unloadSoundPool(this.mSound_voiceShutter_kimchi);
            unloadSoundPool(this.mSound_voiceShutter_LG);
            unloadSoundPool(this.mSound_voiceShutter_torimasu);
        }
    }

    private void unloadSoundPool(int index) {
        if (this.mSound_pool != null && index > 0) {
            this.mSound_pool.unload(index);
        }
    }

    public void unbind() {
        this.mActivity = null;
        this.mTitle = null;
        this.mTitleInContent = null;
        this.mImage = null;
        this.mMessage = null;
        this.mOkButton = null;
        unloadVoiceGuideSound();
        this.mSound_pool = null;
    }
}
