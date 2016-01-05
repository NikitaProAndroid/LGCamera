package com.lge.camera.command;

import android.util.Log;
import com.lge.camera.ControllerFunction;
import com.lge.camera.properties.ModelProperties;
import com.lge.camera.setting.ListPreference;
import com.lge.camera.setting.SettingMenuItem;
import com.lge.olaworks.library.FaceDetector;
import java.util.ArrayList;

public class HideQuickFunctionDragDrop extends Command {
    public HideQuickFunctionDragDrop(ControllerFunction function) {
        super(function);
    }

    public void execute() {
        Log.d(FaceDetector.TAG, "HideQuickFunctionDragDrop executed");
        ArrayList<String> qfIndexList = this.mGet.getQfIndexList();
        for (int i = 0; i < qfIndexList.size(); i++) {
            ListPreference pref = this.mGet.getQuickFunctionDragControllerMenuTag(i);
            if (pref != null) {
                qfIndexList.set(i, pref.getKey());
                SettingMenuItem menuItem = this.mGet.getSettingMenuItem(pref.getKey());
                if (menuItem != null) {
                    this.mGet.setQFLMenuEnabled(i, menuItem.enable);
                }
            } else {
                qfIndexList.set(i, null);
            }
        }
        this.mGet.saveQFLIndex();
        this.mGet.setMainButtonVisible(true);
        this.mGet.showIndicatorController();
        this.mGet.setSubButton(0, 0);
        this.mGet.setSubButton(1, 0);
        this.mGet.setSubButton(2, 0);
        if (this.mGet.isAttachIntent()) {
            this.mGet.setSwitcherVisible(false);
        } else {
            this.mGet.setSwitcherVisible(true);
        }
        if (ModelProperties.is3dSupportedModel()) {
            this.mGet.set3DSwitchVisible(true);
        }
        this.mGet.updateThumbnailButtonVisibility();
        this.mGet.setSubMenuMode(0);
        showAllControl();
        this.mGet.checkStorage(false);
        this.mGet.hideQuickFunctionDragController(true);
        this.mGet.quickFunctionControllerRefresh(true);
        this.mGet.setQuickButtonMenuEnable(true, false);
        this.mGet.showQuickFunctionController();
        this.mGet.setQuickButtonVisible(100, 0, true);
    }

    private void showAllControl() {
        if (checkMediator()) {
            this.mGet.showControllerForHideSettingMenu(false, false);
        }
    }
}
