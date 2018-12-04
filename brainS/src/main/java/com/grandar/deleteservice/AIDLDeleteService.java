/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: E:\\¹¤×÷Ïà¹Ø\\code\\DeleteService\\src\\com\\grandar\\deleteservice\\AIDLDeleteService.aidl
 */
package com.grandar.deleteservice;

public interface AIDLDeleteService extends android.os.IInterface {
    /**
     * Local-side IPC implementation stub class.
     */
    public static abstract class Stub extends android.os.Binder implements com.grandar.deleteservice.AIDLDeleteService {
        private static final java.lang.String DESCRIPTOR = "com.grandar.deleteservice.AIDLDeleteService";

        /**
         * Construct the stub at attach it to the interface.
         */
        public Stub() {
            this.attachInterface(this, DESCRIPTOR);
        }

        /**
         * Cast an IBinder object into an com.grandar.deleteservice.AIDLDeleteService interface,
         * generating a proxy if needed.
         */
        public static com.grandar.deleteservice.AIDLDeleteService asInterface(android.os.IBinder obj) {
            if ((obj == null)) {
                return null;
            }
            android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
            if (((iin != null) && (iin instanceof com.grandar.deleteservice.AIDLDeleteService))) {
                return ((com.grandar.deleteservice.AIDLDeleteService) iin);
            }
            return new com.grandar.deleteservice.AIDLDeleteService.Stub.Proxy(obj);
        }

        @Override
        public android.os.IBinder asBinder() {
            return this;
        }

        @Override
        public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException {
            switch (code) {
                case INTERFACE_TRANSACTION: {
                    reply.writeString(DESCRIPTOR);
                    return true;
                }
                case TRANSACTION_deleteApk: {
                    data.enforceInterface(DESCRIPTOR);
                    java.lang.String _arg0;
                    _arg0 = data.readString();
                    this.deleteApk(_arg0);
                    reply.writeNoException();
                    return true;
                }
            }
            return super.onTransact(code, data, reply, flags);
        }

        private static class Proxy implements com.grandar.deleteservice.AIDLDeleteService {
            private android.os.IBinder mRemote;

            Proxy(android.os.IBinder remote) {
                mRemote = remote;
            }

            @Override
            public android.os.IBinder asBinder() {
                return mRemote;
            }

            public java.lang.String getInterfaceDescriptor() {
                return DESCRIPTOR;
            }

            @Override
            public void deleteApk(java.lang.String packageName) throws android.os.RemoteException {
                android.os.Parcel _data = android.os.Parcel.obtain();
                android.os.Parcel _reply = android.os.Parcel.obtain();
                try {
                    _data.writeInterfaceToken(DESCRIPTOR);
                    _data.writeString(packageName);
                    mRemote.transact(Stub.TRANSACTION_deleteApk, _data, _reply, 0);
                    _reply.readException();
                } finally {
                    _reply.recycle();
                    _data.recycle();
                }
            }
        }

        static final int TRANSACTION_deleteApk = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
    }

    public void deleteApk(java.lang.String packageName) throws android.os.RemoteException;
}
