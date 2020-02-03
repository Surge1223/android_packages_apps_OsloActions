package com.google.oslo.service.serviceinterface;

import android.frameworks.stats.V1_0.VendorAtom;
//import android.hardware.google.pixel.vendor.PixelAtoms;
import java.util.ArrayList;

public class OsloAtoms {
    public static final int CAPACITY_OFFSET = 1;
    protected static final boolean DEBUG = true;
    public static final int FIELD_OFFSET = 2;
  //  public static final PixelAtoms.ReverseDomainNames RDN = ((PixelAtoms.ReverseDomainNames) PixelAtoms.ReverseDomainNames.newBuilder().build());
    private static final String TAG = "Oslo/OsloAtoms";
    public static final int OSLO_ENABLED = 100001;


    public static VendorAtom packIntoVendorAtom(boolean enabled) {
      //  state = enabled;
        VendorAtom osloEnabledVendorAtom = createVendorAtom(OSLO_ENABLED);
        ArrayList<VendorAtom.Value> list = constructArrayList(1);
     //   list.get(0).intValue(((PixelAtoms.OsloEnabled) PixelAtoms.OsloEnabled.newBuilder().setEnabled(state).build()).getEnabled().getNumber());
        osloEnabledVendorAtom.values = list;
        return osloEnabledVendorAtom;
    }

    public static ArrayList<VendorAtom.Value> constructArrayList(int maxCapacity) {
        ArrayList<VendorAtom.Value> list = new ArrayList<>(maxCapacity);
        for (int i = 0; i < maxCapacity; i++) {
            list.add(new VendorAtom.Value());
        }
        return list;
    }

    private static VendorAtom createVendorAtom(int atomId) {
        VendorAtom baseAtom = new VendorAtom();
      //  baseAtom.reverseDomainName = RDN.getPixel();
        baseAtom.atomId = atomId;
        return baseAtom;
    }
}
