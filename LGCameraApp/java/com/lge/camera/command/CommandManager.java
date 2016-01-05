package com.lge.camera.command;

import com.lge.camera.ControllerFunction;
import com.lge.camera.util.CamLog;
import com.lge.olaworks.library.FaceDetector;
import java.util.HashMap;

public class CommandManager {
    private boolean beRemovedAll;
    private ControllerFunction mGet;
    private HashMap<String, Command> mHashMap;

    public CommandManager(ControllerFunction function) {
        this.mHashMap = null;
        this.beRemovedAll = false;
        this.mGet = null;
        this.mHashMap = new HashMap();
        this.mGet = function;
    }

    public void unbind() {
        if (this.mHashMap != null) {
            this.mHashMap.clear();
            this.mHashMap = null;
        }
    }

    public Command getCommand(String msg) {
        Command command;
        if (this.mHashMap.containsKey(msg)) {
            command = (Command) this.mHashMap.get(msg);
            if (command == null) {
                CamLog.d(FaceDetector.TAG, "getCommand error: command is null");
                return null;
            }
        } else if (this.beRemovedAll) {
            CamLog.d(FaceDetector.TAG, "all commands are removing now...so return!");
            return null;
        } else {
            try {
                command = (Command) Class.forName(msg).getConstructor(new Class[]{ControllerFunction.class}).newInstance(new Object[]{this.mGet});
                this.mHashMap.put(msg, command);
            } catch (Exception e) {
                CamLog.e(FaceDetector.TAG, "getCommand error: " + e);
                e.printStackTrace();
                return null;
            }
        }
        command.resetStartTime();
        return command;
    }

    public HashMap<String, Command> getCommandHashMap() {
        return this.mHashMap;
    }

    public void doCommandNoneParameter(String msg) {
        Command c = getCommand(msg);
        if (c == null) {
            CamLog.d(FaceDetector.TAG, "command is null");
        } else {
            c.executeNoneParameter();
        }
    }

    public void doCommandNoneParameter(String msg, Object arg1) {
        Command c = getCommand(msg);
        if (c == null) {
            CamLog.d(FaceDetector.TAG, "command is null");
        } else {
            c.executeNoneParameter(arg1);
        }
    }

    public void doCommand(String msg) {
        Command c = getCommand(msg);
        if (c == null) {
            CamLog.d(FaceDetector.TAG, "command is null");
        } else {
            c.execute();
        }
    }

    public void doCommand(String msg, Object arg1) {
        Command c = getCommand(msg);
        if (c == null) {
            CamLog.d(FaceDetector.TAG, "command is null");
        } else {
            c.execute(arg1);
        }
    }

    public void doCommand(String msg, Object arg1, Object arg2) {
        Command c = getCommand(msg);
        if (c == null) {
            CamLog.d(FaceDetector.TAG, "command is null");
        } else {
            c.execute(arg1, arg2);
        }
    }

    public void doCommandUi(String msg) {
        Command c = getCommand(msg);
        if (c == null || this.mGet == null) {
            CamLog.d(FaceDetector.TAG, String.format("command:%s", new Object[]{c}));
            return;
        }
        c.setArgument(null);
        this.mGet.runOnUiThread(c);
    }

    public void doCommandUi(String msg, Object arg1) {
        Command c = getCommand(msg);
        if (c == null || this.mGet == null) {
            CamLog.d(FaceDetector.TAG, String.format("command:%s", new Object[]{c}));
            return;
        }
        c.setArgument(arg1);
        this.mGet.runOnUiThread(c);
    }

    public void doCommandUi(String msg, Object arg1, Object arg2) {
        Command c = getCommand(msg);
        if (c == null || this.mGet == null) {
            CamLog.d(FaceDetector.TAG, String.format("command:%s", new Object[]{c}));
            return;
        }
        c.setArgument(arg1, arg2);
        this.mGet.runOnUiThread(c);
    }

    public void doCommandDelayed(String msg, long delay) {
        doCommandWithFixedRate(msg, delay, 0);
    }

    public void doCommandWithFixedRate(String msg, long delay, long period) {
        Command c = getCommand(msg);
        if (c == null || this.mGet == null || this.mGet.getHandler() == null) {
            CamLog.d(FaceDetector.TAG, String.format("command:%s", new Object[]{c}));
            return;
        }
        c.setArgument(null);
        c.setRepeat(period);
        c.setPosted(true);
        this.mGet.getHandler().removeCallbacks(c);
        this.mGet.getHandler().postDelayed(c, delay);
    }

    public void doCommandDelayed(String msg, Object arg1, long delay) {
        doCommandWithFixedRate(msg, arg1, delay, 0);
    }

    public void doCommandWithFixedRate(String msg, Object arg1, long delay, long period) {
        Command c = getCommand(msg);
        if (c == null || this.mGet == null || this.mGet.getHandler() == null) {
            CamLog.d(FaceDetector.TAG, String.format("command:%s", new Object[]{c}));
            return;
        }
        c.setArgument(arg1);
        c.setRepeat(period);
        c.setPosted(true);
        this.mGet.getHandler().removeCallbacks(c);
        this.mGet.getHandler().postDelayed(c, delay);
    }

    public void removeScheduledCommand(String msg) {
        Command c = getCommand(msg);
        if (c == null || this.mGet == null || this.mGet.getHandler() == null) {
            CamLog.d(FaceDetector.TAG, String.format("command:%s", new Object[]{c}));
            return;
        }
        c.setPosted(false);
        this.mGet.getHandler().removeCallbacks(c);
    }

    public void removeScheduledAllCommand() {
        this.beRemovedAll = true;
        for (Object key : ((HashMap) this.mHashMap.clone()).keySet()) {
            Command value = this.mHashMap.get(key);
            if (value != null) {
                Command c = value;
                if (c == null || this.mGet == null || this.mGet.getHandler() == null) {
                    CamLog.d(FaceDetector.TAG, String.format("command:%s", new Object[]{c}));
                    this.beRemovedAll = false;
                    return;
                }
                c.setPosted(false);
                this.mGet.getHandler().removeCallbacks(c);
            } else {
                CamLog.d(FaceDetector.TAG, " value is null");
            }
        }
        this.beRemovedAll = false;
    }

    public boolean findScheduledCommand(String msg) {
        if (!this.mHashMap.containsKey(msg)) {
            return false;
        }
        Command c = (Command) this.mHashMap.get(msg);
        if (c == null) {
            CamLog.d(FaceDetector.TAG, "command is null");
            return false;
        } else if (c.getPosted()) {
            return true;
        } else {
            return false;
        }
    }
}
