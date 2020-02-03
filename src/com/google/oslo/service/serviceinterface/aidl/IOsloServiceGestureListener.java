package com.google.oslo.service.serviceinterface.aidl;

import android.os.Parcel;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.Bundle;
import android.os.IInterface;

public interface IOsloServiceGestureListener extends IInterface
{
    void onGestureDetected(final Bundle p0) throws RemoteException;
    
    public static class Default implements IOsloServiceGestureListener
    {
        public IBinder asBinder() {
            return null;
        }
        
        @Override
        public void onGestureDetected(final Bundle bundle) throws RemoteException {
        }
    }
    
    public abstract static class Stub extends Binder implements IOsloServiceGestureListener
    {
        private static final String DESCRIPTOR = "com.google.oslo.service.serviceinterface.aidl.IOsloServiceGestureListener";
        static final int TRANSACTION_onGestureDetected = 1;
        
        public Stub() {
            this.attachInterface((IInterface)this, "com.google.oslo.service.serviceinterface.aidl.IOsloServiceGestureListener");
        }
        
        public static IOsloServiceGestureListener asInterface(final IBinder binder) {
            if (binder == null) {
                return null;
            }
            final IInterface queryLocalInterface = binder.queryLocalInterface("com.google.oslo.service.serviceinterface.aidl.IOsloServiceGestureListener");
            if (queryLocalInterface != null && queryLocalInterface instanceof IOsloServiceGestureListener) {
                return (IOsloServiceGestureListener)queryLocalInterface;
            }
            return new Proxy(binder);
        }
        
        public static IOsloServiceGestureListener getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
        
        public static boolean setDefaultImpl(final IOsloServiceGestureListener sDefaultImpl) {
            if (Proxy.sDefaultImpl == null && sDefaultImpl != null) {
                Proxy.sDefaultImpl = sDefaultImpl;
                return true;
            }
            return false;
        }
        
        public IBinder asBinder() {
            return (IBinder)this;
        }
        
        public boolean onTransact(final int n, final Parcel parcel, final Parcel parcel2, final int n2) throws RemoteException {
            if (n == 1) {
                parcel.enforceInterface("com.google.oslo.service.serviceinterface.aidl.IOsloServiceGestureListener");
                Bundle bundle;
                if (parcel.readInt() != 0) {
                    bundle = (Bundle)Bundle.CREATOR.createFromParcel(parcel);
                }
                else {
                    bundle = null;
                }
                this.onGestureDetected(bundle);
                return true;
            }
            if (n != 1598968902) {
                return super.onTransact(n, parcel, parcel2, n2);
            }
            parcel2.writeString("com.google.oslo.service.serviceinterface.aidl.IOsloServiceGestureListener");
            return true;
        }
        
        private static class Proxy implements IOsloServiceGestureListener
        {
            public static IOsloServiceGestureListener sDefaultImpl;
            private IBinder mRemote;
            
            Proxy(final IBinder mRemote) {
                this.mRemote = mRemote;
            }
            
            public IBinder asBinder() {
                return this.mRemote;
            }
            
            public String getInterfaceDescriptor() {
                return "com.google.oslo.service.serviceinterface.aidl.IOsloServiceGestureListener";
            }
            
            @Override
            public void onGestureDetected(final Bundle bundle) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.oslo.service.serviceinterface.aidl.IOsloServiceGestureListener");
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    }
                    else {
                        obtain.writeInt(0);
                    }
                    if (!this.mRemote.transact(1, obtain, (Parcel)null, 1) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().onGestureDetected(bundle);
                    }
                }
                finally {
                    obtain.recycle();
                }
            }
        }
    }
}

