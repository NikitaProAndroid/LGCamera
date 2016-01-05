package com.lge.camera.command;

import android.graphics.Color;
import android.text.TextPaint;
import android.text.TextUtils.TruncateAt;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import com.lge.camera.ControllerFunction;
import com.lge.camera.R;
import com.lge.camera.components.RotateLayout;
import com.lge.camera.components.RotateView;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;
import java.util.ArrayList;

public class ShowPIPFrameSubMenu extends Command {
    private static int BOARDER_WIDTH;
    private boolean isLayoutInited;
    private OnClickListener mOnPIPFrameEffectClickListener;
    private ArrayList<Integer> mPIPFrameMaskMenuString;
    private ArrayList<Integer> mPIPFrameMenuImage;
    private int mPIPFrameMenuOffset;
    private int mPIPFrameSelectedMenu;
    private int mPIPFrameSelectedMenuPrev;

    static {
        BOARDER_WIDTH = 10;
    }

    public ShowPIPFrameSubMenu(ControllerFunction function) {
        super(function);
        this.mPIPFrameMenuOffset = 0;
        this.mPIPFrameSelectedMenu = 0;
        this.mPIPFrameSelectedMenuPrev = 0;
        this.isLayoutInited = false;
        this.mOnPIPFrameEffectClickListener = new OnClickListener() {
            public void onClick(View v) {
                if (ShowPIPFrameSubMenu.this.mGet.isPressedShutterButton()) {
                    CamLog.d(FaceDetector.TAG, "ShutterButton pressed -> block PIPFrameEffect click");
                    return;
                }
                ShowPIPFrameSubMenu.this.mPIPFrameSelectedMenu = ((Integer) v.getTag()).intValue();
                ShowPIPFrameSubMenu.this.mGet.doCommandUi(Command.HIDE_PIP_SUBWINDOW_RESIZE_HANDLER);
                if (ShowPIPFrameSubMenu.this.mGet.setPIPMask(ShowPIPFrameSubMenu.this.mPIPFrameSelectedMenu)) {
                    View pipDrawerMenu = ShowPIPFrameSubMenu.this.mGet.findViewById(R.id.pip_frame_sliding_drawer_menu);
                    if (!(pipDrawerMenu == null || pipDrawerMenu.findViewWithTag(Integer.valueOf(ShowPIPFrameSubMenu.this.mPIPFrameSelectedMenuPrev)) == null)) {
                        pipDrawerMenu.findViewWithTag(Integer.valueOf(ShowPIPFrameSubMenu.this.mPIPFrameSelectedMenuPrev)).setSelected(false);
                    }
                    v.setSelected(true);
                    if (ShowPIPFrameSubMenu.this.mGet.isSmartZoomRecordingActive()) {
                        if (ShowPIPFrameSubMenu.this.mPIPFrameSelectedMenu == 0) {
                            ShowPIPFrameSubMenu.this.mGet.hideSmartZoomFocusView();
                            if (ShowPIPFrameSubMenu.this.mGet.getSmartZoomFocusViewMode() == 2) {
                                ShowPIPFrameSubMenu.this.mGet.disableObjectTrackingForSmartZoom();
                                ShowPIPFrameSubMenu.this.mGet.unregisterObjectCallback();
                            }
                        } else {
                            if (ShowPIPFrameSubMenu.this.mPIPFrameSelectedMenuPrev == 0) {
                                ShowPIPFrameSubMenu.this.mGet.initSmartZoomFocusView();
                            }
                            ShowPIPFrameSubMenu.this.mGet.showSmartZoomFocusView();
                        }
                    }
                    ShowPIPFrameSubMenu.this.mPIPFrameSelectedMenuPrev = ShowPIPFrameSubMenu.this.mPIPFrameSelectedMenu;
                    return;
                }
                ShowPIPFrameSubMenu.this.mPIPFrameSelectedMenu = ShowPIPFrameSubMenu.this.mPIPFrameSelectedMenuPrev;
            }
        };
        addPIPFrameMenuImageToArray();
        addPIPFrameMaskStringToArray();
    }

    public void execute() {
        CamLog.d(FaceDetector.TAG, "ShowPIPFrameSubMenu is EXECUTED !!!");
        View pipLayout = this.mGet.findViewById(R.id.pip_frame_sliding_drawer_menu_view);
        if (pipLayout != null && pipLayout.getVisibility() == 0 && this.mGet.getCurrentPIPMask() == this.mPIPFrameSelectedMenu) {
            CamLog.d(FaceDetector.TAG, "ShowPIPFrameSubMenu return");
            return;
        }
        if (!this.isLayoutInited) {
            makePIPFrameMenu();
        }
        show();
    }

    private void addPIPFrameMenuImageToArray() {
        this.mPIPFrameMenuImage = new ArrayList();
        this.mPIPFrameMenuImage.add(Integer.valueOf(R.drawable.dual_camera_btn_none));
        this.mPIPFrameMenuImage.add(Integer.valueOf(R.drawable.dual_camera_btn_window));
        this.mPIPFrameMenuImage.add(Integer.valueOf(R.drawable.dual_camera_btn_stamp));
        this.mPIPFrameMenuImage.add(Integer.valueOf(R.drawable.dual_camera_btn_ovalblur));
        this.mPIPFrameMenuImage.add(Integer.valueOf(R.drawable.dual_camera_btn_instantpic));
        this.mPIPFrameMenuImage.add(Integer.valueOf(R.drawable.dual_camera_btn_heart));
        this.mPIPFrameMenuImage.add(Integer.valueOf(R.drawable.dual_camera_btn_star));
        this.mPIPFrameMenuImage.add(Integer.valueOf(R.drawable.dual_camera_btn_fisheye));
        this.mPIPFrameMenuImage.add(Integer.valueOf(R.drawable.dual_camera_btn_splitview1));
        this.mPIPFrameMenuImage.add(Integer.valueOf(R.drawable.dual_camera_btn_splitview2));
    }

    private void addPIPFrameMaskStringToArray() {
        this.mPIPFrameMaskMenuString = new ArrayList();
        this.mPIPFrameMaskMenuString.add(Integer.valueOf(R.string.dual_camera_effect_menu_none));
        this.mPIPFrameMaskMenuString.add(Integer.valueOf(R.string.dual_camera_effect_menu_window));
        this.mPIPFrameMaskMenuString.add(Integer.valueOf(R.string.dual_camera_effect_menu_stamp));
        this.mPIPFrameMaskMenuString.add(Integer.valueOf(R.string.dual_camera_effect_menu_ovalblur));
        this.mPIPFrameMaskMenuString.add(Integer.valueOf(R.string.dual_camera_effect_menu_instantpic));
        this.mPIPFrameMaskMenuString.add(Integer.valueOf(R.string.dual_camera_effect_menu_heart));
        this.mPIPFrameMaskMenuString.add(Integer.valueOf(R.string.dual_camera_effect_menu_star));
        this.mPIPFrameMaskMenuString.add(Integer.valueOf(R.string.dual_camera_effect_menu_fisheye));
        this.mPIPFrameMaskMenuString.add(Integer.valueOf(R.string.dual_camera_effect_menu_splitview1));
        this.mPIPFrameMaskMenuString.add(Integer.valueOf(R.string.dual_camera_effect_menu_splitview2));
    }

    private void makePIPFrameMenu() {
        setCurrentPIPMask();
        int i = this.mPIPFrameMenuOffset;
        while (true) {
            if (i < this.mPIPFrameMenuImage.size()) {
                RotateLayout rl = new RotateLayout(this.mGet.getActivity());
                rl.setLayoutParams(new LayoutParams(R.dimen.pip_frame_menu_rotatelayout_width, R.dimen.pip_frame_menu_rotatelayout_height));
                rl.setBackgroundResource(R.drawable.ripple_pip_frame_menu);
                rl.setTag(Integer.valueOf(i));
                rl.setOnClickListener(this.mOnPIPFrameEffectClickListener);
                int i2 = this.mPIPFrameSelectedMenu;
                if (r0 == i) {
                    rl.setSelected(true);
                }
                LinearLayout ll = new LinearLayout(this.mGet.getActivity());
                ll.setLayoutParams(new LinearLayout.LayoutParams(-1, -1));
                ll.setOrientation(1);
                ImageView iv = new ImageView(this.mGet.getActivity());
                iv.setImageResource(((Integer) this.mPIPFrameMenuImage.get(i)).intValue());
                String menuString = this.mGet.getString(((Integer) this.mPIPFrameMaskMenuString.get(i)).intValue());
                View textView = new TextView(this.mGet.getActivity());
                float width = (float) getPixelFromDimens(R.dimen.pip_frame_menu_rotatelayout_width);
                float strokeSize = (float) getPixelFromDimens(R.dimen.pip_frame_menu_textview_strokeSize);
                TextPaint tp = textView.getPaint();
                textView.setSingleLine();
                textView.setPaddingRelative(getPixelFromDimens(R.dimen.pip_frame_menu_textview_textleftPadding), 0, getPixelFromDimens(R.dimen.pip_frame_menu_textview_textrightPadding), 0);
                textView.setEllipsize(TruncateAt.MARQUEE);
                textView.setTextColor(-1);
                textView.setShadowLayer(strokeSize, 0.0f, 0.0f, Color.argb(153, 0, 0, 0));
                textView.setGravity(17);
                textView.setTextSize(0, (float) getPixelFromDimens(R.dimen.pip_frame_menu_textview_textSize));
                float textWidth = tp.measureText(menuString);
                if (textWidth > width - ((float) BOARDER_WIDTH)) {
                    textView = textView;
                    textView.setTextScaleX((width - ((float) BOARDER_WIDTH)) / textWidth);
                } else {
                    textView.setTextScaleX(RotateView.DEFAULT_TEXT_SCALE_X);
                }
                textView.setText(menuString);
                ll.addView(iv);
                ll.addView(textView);
                rl.addView(ll);
                MarginLayoutParams params = (MarginLayoutParams) iv.getLayoutParams();
                params.topMargin = getPixelFromDimens(R.dimen.pip_frame_menu_imageview_imageTopMargin);
                params.bottomMargin = getPixelFromDimens(R.dimen.pip_frame_menu_imageview_imageBottomMargin);
                iv.setLayoutParams(params);
                LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(getPixelFromDimens(R.dimen.pip_frame_menu_rotatelayout_width), getPixelFromDimens(R.dimen.pip_frame_menu_rotatelayout_height));
                ((LinearLayout) this.mGet.findViewById(R.id.pip_frame_sliding_drawer_menu)).addView(rl, param1);
                i++;
            } else {
                this.isLayoutInited = true;
                return;
            }
        }
    }

    private void show() {
        CamLog.v(FaceDetector.TAG, "show");
        this.mGet.startPIPFrameSubMenuRotation(this.mGet.getOrientationDegree());
        View pipMenu = this.mGet.findViewById(R.id.pip_frame_sliding_drawer_menu);
        if (pipMenu != null) {
            if (pipMenu.findViewWithTag(Integer.valueOf(8)) == null || pipMenu.findViewWithTag(Integer.valueOf(9)) == null) {
                makePIPFrameMenu();
            }
            if (this.mGet.getCurrentPIPMask() != this.mPIPFrameSelectedMenu) {
                pipMenu.findViewWithTag(Integer.valueOf(this.mPIPFrameSelectedMenu)).setSelected(false);
                setCurrentPIPMask();
                pipMenu.findViewWithTag(Integer.valueOf(this.mPIPFrameSelectedMenu)).setSelected(true);
            }
            this.mGet.findViewById(R.id.pip_frame_sliding_drawer_menu_view).setVisibility(0);
            if (this.mGet.getCurrentPIPMask() == 1) {
                ((ScrollView) this.mGet.findViewById(R.id.pip_frame_sliding_drawer_menu_scroll_view)).scrollTo(0, 0);
            }
            if (this.mGet.isSmartZoomRecordingActive()) {
                pipMenu.findViewWithTag(Integer.valueOf(8)).setVisibility(8);
                pipMenu.findViewWithTag(Integer.valueOf(9)).setVisibility(8);
                return;
            }
            pipMenu.findViewWithTag(Integer.valueOf(8)).setVisibility(0);
            pipMenu.findViewWithTag(Integer.valueOf(9)).setVisibility(0);
        }
    }

    private void setCurrentPIPMask() {
        this.mPIPFrameSelectedMenu = this.mGet.getCurrentPIPMask();
        this.mPIPFrameSelectedMenuPrev = this.mPIPFrameSelectedMenu;
    }
}
