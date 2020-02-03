package com.google.oslo.service.serviceinterface.aidl;

import android.os.Parcel;
import android.os.Binder;
import android.os.RemoteException;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;

public interface IOsloService extends IInterface
{
    void registerListener(final IBinder p0, final IBinder p1, final int p2, final Bundle p3) throws RemoteException;
    
    void unregisterListener(final IBinder p0, final IBinder p1) throws RemoteException;
    
    public static class Default implements IOsloService
    {
        public IBinder asBinder() {
            return null;
        }
        
        @Override
        public void registerListener(final IBinder binder, final IBinder binder2, final int n, final Bundle bundle) throws RemoteException {
        }
        
        @Override
        public void unregisterListener(final IBinder binder, final IBinder binder2) throws RemoteException {
        }
    }
    
    public abstract static class Stub extends Binder implements IOsloService
    {
        private static final String DESCRIPTOR = "com.google.oslo.service.serviceinterface.aidl.IOsloService";
        static final int TRANSACTION_registerListener = 1;
        static final int TRANSACTION_unregisterListener = 2;
        
        public Stub() {
            this.attachInterface((IInterface)this, "com.google.oslo.service.serviceinterface.aidl.IOsloService");
        }
        
        public static IOsloService asInterface(final IBinder binder) {
            if (binder == null) {
                return null;
            }
            final IInterface queryLocalInterface = binder.queryLocalInterface("com.google.oslo.service.serviceinterface.aidl.IOsloService");
            if (queryLocalInterface != null && queryLocalInterface instanceof IOsloService) {
                return (IOsloService)queryLocalInterface;
            }
            return new Proxy(binder);
        }
        
        public static IOsloService getDefaultImpl() {
            return Proxy.sDefaultImpl;
        }
        
        public static boolean setDefaultImpl(final IOsloService sDefaultImpl) {
            if (Proxy.sDefaultImpl == null && sDefaultImpl != null) {
                Proxy.sDefaultImpl = sDefaultImpl;
                return true;
            }
            return false;
        }
        
        public IBinder asBinder() {
            return (IBinder)this;
        }
        
        public boolean onTransact(int int1, final Parcel parcel, final Parcel parcel2, final int n) throws RemoteException {
            if (int1 == 1) {
                parcel.enforceInterface("com.google.oslo.service.serviceinterface.aidl.IOsloService");
                final IBinder strongBinder = parcel.readStrongBinder();
                final IBinder strongBinder2 = parcel.readStrongBinder();
                int1 = parcel.readInt();
                Bundle bundle;
                if (parcel.readInt() != 0) {
                    bundle = (Bundle)Bundle.CREATOR.createFromParcel(parcel);
                }
                else {
                    bundle = null;
                }
                this.registerListener(strongBinder, strongBinder2, int1, bundle);
                parcel2.writeNoException();
                return true;
            }
            if (int1 == 2) {
                parcel.enforceInterface("com.google.oslo.service.serviceinterface.aidl.IOsloService");
                this.unregisterListener(parcel.readStrongBinder(), parcel.readStrongBinder());
                parcel2.writeNoException();
                return true;
            }
            if (int1 != 1598968902) {
                return super.onTransact(int1, parcel, parcel2, n);
            }
            parcel2.writeString("com.google.oslo.service.serviceinterface.aidl.IOsloService");
            return true;
        }
        
        private static class Proxy implements IOsloService
        {
            public static IOsloService sDefaultImpl;
            private IBinder mRemote;
            
            Proxy(final IBinder mRemote) {
                this.mRemote = mRemote;
            }
            
            public IBinder asBinder() {
                return this.mRemote;
            }
            
            public String getInterfaceDescriptor() {
                return "com.google.oslo.service.serviceinterface.aidl.IOsloService";
            }
            
            @Override
            public void registerListener(final IBinder binder, final IBinder binder2, final int n, final Bundle bundle) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.oslo.service.serviceinterface.aidl.IOsloService");
                    obtain.writeStrongBinder(binder);
                    obtain.writeStrongBinder(binder2);
                    obtain.writeInt(n);
                    if (bundle != null) {
                        obtain.writeInt(1);
                        bundle.writeToParcel(obtain, 0);
                    }
                    else {
                        obtain.writeInt(0);
                    }
                    if (!this.mRemote.transact(1, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().registerListener(binder, binder2, n, bundle);
                        return;
                    }
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
            
            @Override
            public void unregisterListener(final IBinder binder, final IBinder binder2) throws RemoteException {
                final Parcel obtain = Parcel.obtain();
                final Parcel obtain2 = Parcel.obtain();
                try {
                    obtain.writeInterfaceToken("com.google.oslo.service.serviceinterface.aidl.IOsloService");
                    obtain.writeStrongBinder(binder);
                    obtain.writeStrongBinder(binder2);
                    if (!this.mRemote.transact(2, obtain, obtain2, 0) && Stub.getDefaultImpl() != null) {
                        Stub.getDefaultImpl().unregisterListener(binder, binder2);
                        return;
                    }
                    obtain2.readException();
                }
                finally {
                    obtain2.recycle();
                    obtain.recycle();
                }
            }
        }
    }
}

