package com.lge.util;

import android.os.RemoteException;
import android.util.Log;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ProxyUtil {
    private static final boolean PRIVATE_PERMITTED = true;
    private static final String TAG = "[LGSF]:ProxyUtil";

    private static Object setToAccessible(Object obj) {
        AccessibleObject ao = (AccessibleObject) obj;
        if (!(ao == null || ao.isAccessible())) {
            ao.setAccessible(PRIVATE_PERMITTED);
        }
        return ao;
    }

    public static Object loadMethod(Class<?> cls, String methodName, Class<?>... parameterTypes) {
        try {
            return setToAccessible(cls.getDeclaredMethod(methodName, parameterTypes));
        } catch (NoSuchMethodException e) {
            Log.e(TAG, e.toString());
            return new NoSuchMethodException(cls.toString() + "::" + methodName);
        }
    }

    private static boolean checkMethod(Object obj) {
        if (obj instanceof NoSuchMethodException) {
            return false;
        }
        return PRIVATE_PERMITTED;
    }

    public static Object loadField(Class<?> cls, String fieldName) {
        try {
            return setToAccessible(cls.getDeclaredField(fieldName));
        } catch (NoSuchFieldException e) {
            Log.e(TAG, e.toString());
            return new NoSuchFieldException(cls.toString() + "::" + fieldName);
        }
    }

    private static boolean checkField(Object obj) {
        if (obj instanceof NoSuchFieldException) {
            return false;
        }
        return PRIVATE_PERMITTED;
    }

    public static Object invokeMethod(Object method, Object receiver, Object... args) throws UnsupportedOperationException, RemoteException {
        if (checkMethod(method)) {
            Object obj = null;
            Method m = (Method) method;
            try {
                obj = m.invoke(receiver, args);
                Log.v(TAG, m.getName() + " is called");
                return obj;
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (!(cause instanceof RemoteException)) {
                    return obj;
                }
                throw ((RemoteException) cause);
            } catch (Exception e2) {
                e2.printStackTrace();
                throw new RuntimeException(e2);
            }
        }
        throw new UnsupportedOperationException((NoSuchMethodException) method);
    }

    public static Object getField(Object field, Object receiver) throws UnsupportedOperationException {
        if (checkField(field)) {
            Field f = (Field) field;
            try {
                Object re = f.get(receiver);
                Log.v(TAG, f.getName() + " is get");
                return re;
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        throw new UnsupportedOperationException((NoSuchFieldException) field);
    }

    public static void setField(Object field, Object receiver, Object value) throws UnsupportedOperationException {
        if (checkField(field)) {
            Field f = (Field) field;
            try {
                f.set(receiver, value);
                Log.v(TAG, f.getName() + " is set");
                return;
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
        throw new UnsupportedOperationException((NoSuchFieldException) field);
    }

    public static int getConstValue(Class className, String fieldName, int defaultVal) {
        try {
            defaultVal = ((Integer) getField(loadField(className, fieldName), null)).intValue();
        } catch (UnsupportedOperationException e) {
            Log.e(TAG, className.getSimpleName() + " has not " + fieldName);
        }
        return defaultVal;
    }
}
