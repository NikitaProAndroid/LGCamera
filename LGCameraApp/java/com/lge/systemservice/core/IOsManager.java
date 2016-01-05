package com.lge.systemservice.core;

import android.os.Binder;
import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public interface IOsManager extends IInterface {

    public static abstract class Stub extends Binder implements IOsManager {
        private static final String DESCRIPTOR = "com.lge.systemservice.core.IOsManager";
        static final int TRANSACTION_goToSleepWithForce = 3;
        static final int TRANSACTION_setSystemProperty = 1;
        static final int TRANSACTION_stopRingtoneSound = 2;
        static final int TRANSACTION_wakeUpWithForce = 4;

        private static class Proxy implements IOsManager {
            private IBinder mRemote;

            Proxy(IBinder remote) {
                this.mRemote = remote;
            }

            public IBinder asBinder() {
                return this.mRemote;
            }

            public String getInterfaceDescriptor() {
                return Stub.DESCRIPTOR;
            }

            public void setSystemProperty(String key, String val) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeString(key);
                    _data.writeString(val);
                    this.mRemote.transact(Stub.TRANSACTION_setSystemProperty, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void stopRingtoneSound() throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    this.mRemote.transact(Stub.TRANSACTION_stopRingtoneSound, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void goToSleepWithForce(long time, int reason) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(time);
                    _data.writeInt(reason);
                    this.mRemote.transact(Stub.TRANSACTION_goToSleepWithForce, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }

            public void wakeUpWithForce(long time) throws RemoteException {
                Parcel _data = Parcel.obtain();
                Parcel _reply = Parcel.obtain();
                try {
                    _data.writeInterfaceToken(Stub.DESCRIPTOR);
                    _data.writeLong(time);
                    this.mRemote.transact(Stub.TRANSACTION_wakeUpWithForce, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        public Stub() {
            attachInterface(this, DESCRIPTOR);
        }

        public static IOsManager asInterface(IBinder obj) {
            if (obj == null) {
                return null;
            }
            IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (iin == null || !(iin instanceof IOsManager)) {
                return new Proxy(obj);
            }
            return (IOsManager) iin;
        }

        public IBinder asBinder() {
            return this;
        }

        public boolean onTransact(int code, Parcel data, Parcel reply, int flags) throws RemoteException {
            switch (code) {
                case TRANSACTION_setSystemProperty /*1*/:
                    data.enforceInterface(DESCRIPTOR);
                    setSystemProperty(data.readString(), data.readString());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_stopRingtoneSound /*2*/:
                    data.enforceInterface(DESCRIPTOR);
                    stopRingtoneSound();
                    reply.writeNoException();
                    return true;
                case TRANSACTION_goToSleepWithForce /*3*/:
                    data.enforceInterface(DESCRIPTOR);
                    goToSleepWithForce(data.readLong(), data.readInt());
                    reply.writeNoException();
                    return true;
                case TRANSACTION_wakeUpWithForce /*4*/:
                    data.enforceInterface(DESCRIPTOR);
                    wakeUpWithForce(data.readLong());
                    reply.writeNoException();
                    return true;
                case 1598968902:
                    reply.writeString(DESCRIPTOR);
                    return true;
                default:
                    return super.onTransact(code, data, reply, flags);
            }
        }
    }

    void goToSleepWithForce(long j, int i) throws RemoteException;

    void setSystemProperty(String str, String str2) throws RemoteException;

    void stopRingtoneSound() throws RemoteException;

    void wakeUpWithForce(long j) throws RemoteException;
}
