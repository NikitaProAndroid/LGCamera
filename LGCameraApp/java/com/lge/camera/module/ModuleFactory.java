package com.lge.camera.module;

import com.lge.camera.ControllerFunction;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;
import java.util.HashMap;

public class ModuleFactory {
    private Module mCurrentModule;
    private String mCurrentModuleName;
    private ControllerFunction mGet;
    private HashMap<String, Module> mHashMap;
    private String mWorkerPackageName;

    public ModuleFactory(String packageName, ControllerFunction function) {
        this.mHashMap = new HashMap();
        this.mWorkerPackageName = "";
        this.mCurrentModule = null;
        this.mCurrentModuleName = "";
        this.mGet = null;
        this.mWorkerPackageName = packageName;
        this.mGet = function;
    }

    public void setCurrentModule(String module) {
        if (this.mHashMap.containsKey(module)) {
            this.mCurrentModuleName = module;
            this.mCurrentModule = (Module) this.mHashMap.get(module);
        }
    }

    public Module getCurrentModule() {
        if (!this.mHashMap.containsKey(this.mCurrentModuleName)) {
            return null;
        }
        this.mCurrentModule = (Module) this.mHashMap.get(this.mCurrentModuleName);
        return this.mCurrentModule;
    }

    public String getCurrentModuleName() {
        return this.mCurrentModuleName;
    }

    public Module getModule(String name) {
        if (this.mHashMap.containsKey(name)) {
            return (Module) this.mHashMap.get(name);
        }
        try {
            StringBuffer classFullName = new StringBuffer(this.mWorkerPackageName);
            classFullName.append('.');
            classFullName.append(name);
            Module module = (Module) Class.forName(classFullName.toString()).getConstructor(new Class[]{ControllerFunction.class}).newInstance(new Object[]{this.mGet});
            this.mHashMap.put(name, module);
            setCurrentModule(name);
            return module;
        } catch (Exception e) {
            CamLog.e(FaceDetector.TAG, "getCommand error: " + e);
            return null;
        }
    }

    public void unbind() {
        if (this.mHashMap != null) {
            this.mHashMap.clear();
            this.mHashMap = null;
        }
        this.mCurrentModule = null;
    }
}
