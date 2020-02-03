package android.frameworks.stats.V1_0;

import java.util.Objects;
import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;

public final class VendorAtom
{
    public int atomId;
    public String reverseDomainName;
    public ArrayList<Value> values;
    
    public VendorAtom() {
        this.reverseDomainName = new String();
        this.values = new ArrayList<Value>();
    }
    
    public static final ArrayList<VendorAtom> readVectorFromParcel(final HwParcel hwParcel) {
        final ArrayList<VendorAtom> list = new ArrayList<VendorAtom>();
        final HwBlob buffer = hwParcel.readBuffer(16L);
        final int int32 = buffer.getInt32(8L);
        final HwBlob embeddedBuffer = hwParcel.readEmbeddedBuffer((long)(int32 * 40), buffer.handle(), 0L, true);
        list.clear();
        for (int i = 0; i < int32; ++i) {
            final VendorAtom vendorAtom = new VendorAtom();
            vendorAtom.readEmbeddedFromParcel(hwParcel, embeddedBuffer, i * 40);
            list.add(vendorAtom);
        }
        return list;
    }
    
    public static final void writeVectorToParcel(final HwParcel hwParcel, final ArrayList<VendorAtom> list) {
        final HwBlob hwBlob = new HwBlob(16);
        final int size = list.size();
        hwBlob.putInt32(8L, size);
        hwBlob.putBool(12L, false);
        final HwBlob hwBlob2 = new HwBlob(size * 40);
        for (int i = 0; i < size; ++i) {
            list.get(i).writeEmbeddedToBlob(hwBlob2, i * 40);
        }
        hwBlob.putBlob(0L, hwBlob2);
        hwParcel.writeBuffer(hwBlob);
    }
    
    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (o.getClass() != VendorAtom.class) {
            return false;
        }
        final VendorAtom vendorAtom = (VendorAtom)o;
        return HidlSupport.deepEquals((Object)this.reverseDomainName, (Object)vendorAtom.reverseDomainName) && this.atomId == vendorAtom.atomId && HidlSupport.deepEquals((Object)this.values, (Object)vendorAtom.values);
    }
    
    @Override
    public final int hashCode() {
        return Objects.hash(HidlSupport.deepHashCode((Object)this.reverseDomainName), HidlSupport.deepHashCode((Object)this.atomId), HidlSupport.deepHashCode((Object)this.values));
    }
    
    public final void readEmbeddedFromParcel(final HwParcel hwParcel, final HwBlob hwBlob, final long n) {
        this.reverseDomainName = hwBlob.getString(n + 0L);
        hwParcel.readEmbeddedBuffer((long)(this.reverseDomainName.getBytes().length + 1), hwBlob.handle(), n + 0L + 0L, false);
        this.atomId = hwBlob.getInt32(n + 16L);
        final int int32 = hwBlob.getInt32(n + 24L + 8L);
        final HwBlob embeddedBuffer = hwParcel.readEmbeddedBuffer((long)(int32 * 24), hwBlob.handle(), n + 24L + 0L, true);
        this.values.clear();
        for (int i = 0; i < int32; ++i) {
            final Value value = new Value();
            value.readEmbeddedFromParcel(hwParcel, embeddedBuffer, i * 24);
            this.values.add(value);
        }
    }
    
    public final void readFromParcel(final HwParcel hwParcel) {
        this.readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(40L), 0L);
    }
    
    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(".reverseDomainName = ");
        sb.append(this.reverseDomainName);
        sb.append(", .atomId = ");
        sb.append(this.atomId);
        sb.append(", .values = ");
        sb.append(this.values);
        sb.append("}");
        return sb.toString();
    }
    
    public final void writeEmbeddedToBlob(final HwBlob hwBlob, final long n) {
        hwBlob.putString(n + 0L, this.reverseDomainName);
        hwBlob.putInt32(16L + n, this.atomId);
        final int size = this.values.size();
        hwBlob.putInt32(n + 24L + 8L, size);
        hwBlob.putBool(n + 24L + 12L, false);
        final HwBlob hwBlob2 = new HwBlob(size * 24);
        for (int i = 0; i < size; ++i) {
            this.values.get(i).writeEmbeddedToBlob(hwBlob2, i * 24);
        }
        hwBlob.putBlob(24L + n + 0L, hwBlob2);
    }
    
    public final void writeToParcel(final HwParcel hwParcel) {
        final HwBlob hwBlob = new HwBlob(40);
        this.writeEmbeddedToBlob(hwBlob, 0L);
        hwParcel.writeBuffer(hwBlob);
    }
    
    public static final class Value
    {
        private byte hidl_d;
        private Object hidl_o;
        
        public Value() {
            this.hidl_d = 0;
            this.hidl_o = null;
        }
        
        public static final ArrayList<Value> readVectorFromParcel(final HwParcel hwParcel) {
            final ArrayList<Value> list = new ArrayList<Value>();
            final HwBlob buffer = hwParcel.readBuffer(16L);
            final int int32 = buffer.getInt32(8L);
            final HwBlob embeddedBuffer = hwParcel.readEmbeddedBuffer((long)(int32 * 24), buffer.handle(), 0L, true);
            list.clear();
            for (int i = 0; i < int32; ++i) {
                final Value value = new Value();
                value.readEmbeddedFromParcel(hwParcel, embeddedBuffer, i * 24);
                list.add(value);
            }
            return list;
        }
        
        public static final void writeVectorToParcel(final HwParcel hwParcel, final ArrayList<Value> list) {
            final HwBlob hwBlob = new HwBlob(16);
            final int size = list.size();
            hwBlob.putInt32(8L, size);
            hwBlob.putBool(12L, false);
            final HwBlob hwBlob2 = new HwBlob(size * 24);
            for (int i = 0; i < size; ++i) {
                list.get(i).writeEmbeddedToBlob(hwBlob2, i * 24);
            }
            hwBlob.putBlob(0L, hwBlob2);
            hwParcel.writeBuffer(hwBlob);
        }
        
        @Override
        public final boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null) {
                return false;
            }
            if (o.getClass() != Value.class) {
                return false;
            }
            final Value value = (Value)o;
            return this.hidl_d == value.hidl_d && HidlSupport.deepEquals(this.hidl_o, value.hidl_o);
        }
        
        public float floatValue() {
            if (this.hidl_d != 2) {
                final Object hidl_o = this.hidl_o;
                String name;
                if (hidl_o != null) {
                    name = hidl_o.getClass().getName();
                }
                else {
                    name = "null";
                }
                final StringBuilder sb = new StringBuilder();
                sb.append("Read access to inactive union components is disallowed. Discriminator value is ");
                sb.append(this.hidl_d);
                sb.append(" (corresponding to ");
                sb.append(hidl_discriminator.getName(this.hidl_d));
                sb.append("), and hidl_o is of type ");
                sb.append(name);
                sb.append(".");
                throw new IllegalStateException(sb.toString());
            }
            final Object hidl_o2 = this.hidl_o;
            if (hidl_o2 != null && !Float.class.isInstance(hidl_o2)) {
                throw new Error("Union is in a corrupted state.");
            }
            return (float)this.hidl_o;
        }
        
        public void floatValue(final float n) {
            this.hidl_d = 2;
            this.hidl_o = n;
        }
        
        public byte getDiscriminator() {
            return this.hidl_d;
        }
        
        @Override
        public final int hashCode() {
            return Objects.hash(HidlSupport.deepHashCode(this.hidl_o), Objects.hashCode(this.hidl_d));
        }
        
        public int intValue() {
            if (this.hidl_d != 0) {
                final Object hidl_o = this.hidl_o;
                String name;
                if (hidl_o != null) {
                    name = hidl_o.getClass().getName();
                }
                else {
                    name = "null";
                }
                final StringBuilder sb = new StringBuilder();
                sb.append("Read access to inactive union components is disallowed. Discriminator value is ");
                sb.append(this.hidl_d);
                sb.append(" (corresponding to ");
                sb.append(hidl_discriminator.getName(this.hidl_d));
                sb.append("), and hidl_o is of type ");
                sb.append(name);
                sb.append(".");
                throw new IllegalStateException(sb.toString());
            }
            final Object hidl_o2 = this.hidl_o;
            if (hidl_o2 != null && !Integer.class.isInstance(hidl_o2)) {
                throw new Error("Union is in a corrupted state.");
            }
            return (int)this.hidl_o;
        }
        
        public void intValue(final int n) {
            this.hidl_d = 0;
            this.hidl_o = n;
        }
        
        public long longValue() {
            if (this.hidl_d != 1) {
                final Object hidl_o = this.hidl_o;
                String name;
                if (hidl_o != null) {
                    name = hidl_o.getClass().getName();
                }
                else {
                    name = "null";
                }
                final StringBuilder sb = new StringBuilder();
                sb.append("Read access to inactive union components is disallowed. Discriminator value is ");
                sb.append(this.hidl_d);
                sb.append(" (corresponding to ");
                sb.append(hidl_discriminator.getName(this.hidl_d));
                sb.append("), and hidl_o is of type ");
                sb.append(name);
                sb.append(".");
                throw new IllegalStateException(sb.toString());
            }
            final Object hidl_o2 = this.hidl_o;
            if (hidl_o2 != null && !Long.class.isInstance(hidl_o2)) {
                throw new Error("Union is in a corrupted state.");
            }
            return (long)this.hidl_o;
        }
        
        public void longValue(final long n) {
            this.hidl_d = 1;
            this.hidl_o = n;
        }
        
        public final void readEmbeddedFromParcel(final HwParcel hwParcel, final HwBlob hwBlob, final long n) {
            this.hidl_d = hwBlob.getInt8(n + 0L);
            final byte hidl_d = this.hidl_d;
            if (hidl_d != 0) {
                if (hidl_d != 1) {
                    if (hidl_d != 2) {
                        if (hidl_d != 3) {
                            final StringBuilder sb = new StringBuilder();
                            sb.append("Unknown union discriminator (value: ");
                            sb.append(this.hidl_d);
                            sb.append(").");
                            throw new IllegalStateException(sb.toString());
                        }
                        this.hidl_o = new String();
                        this.hidl_o = hwBlob.getString(n + 8L);
                        hwParcel.readEmbeddedBuffer((long)(((String)this.hidl_o).getBytes().length + 1), hwBlob.handle(), n + 8L + 0L, false);
                    }
                    else {
                        this.hidl_o = hwBlob.getFloat(n + 8L);
                    }
                }
                else {
                    this.hidl_o = hwBlob.getInt64(n + 8L);
                }
            }
            else {
                this.hidl_o = hwBlob.getInt32(n + 8L);
            }
        }
        
        public final void readFromParcel(final HwParcel hwParcel) {
            this.readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(24L), 0L);
        }
        
        public String stringValue() {
            if (this.hidl_d != 3) {
                final Object hidl_o = this.hidl_o;
                String name;
                if (hidl_o != null) {
                    name = hidl_o.getClass().getName();
                }
                else {
                    name = "null";
                }
                final StringBuilder sb = new StringBuilder();
                sb.append("Read access to inactive union components is disallowed. Discriminator value is ");
                sb.append(this.hidl_d);
                sb.append(" (corresponding to ");
                sb.append(hidl_discriminator.getName(this.hidl_d));
                sb.append("), and hidl_o is of type ");
                sb.append(name);
                sb.append(".");
                throw new IllegalStateException(sb.toString());
            }
            final Object hidl_o2 = this.hidl_o;
            if (hidl_o2 != null && !String.class.isInstance(hidl_o2)) {
                throw new Error("Union is in a corrupted state.");
            }
            return (String)this.hidl_o;
        }
        
        public void stringValue(final String hidl_o) {
            this.hidl_d = 3;
            this.hidl_o = hidl_o;
        }
        
        @Override
        public final String toString() {
            final StringBuilder sb = new StringBuilder();
            sb.append("{");
            final byte hidl_d = this.hidl_d;
            if (hidl_d != 0) {
                if (hidl_d != 1) {
                    if (hidl_d != 2) {
                        if (hidl_d != 3) {
                            final StringBuilder sb2 = new StringBuilder();
                            sb2.append("Unknown union discriminator (value: ");
                            sb2.append(this.hidl_d);
                            sb2.append(").");
                            throw new Error(sb2.toString());
                        }
                        sb.append(".stringValue = ");
                        sb.append(this.stringValue());
                    }
                    else {
                        sb.append(".floatValue = ");
                        sb.append(this.floatValue());
                    }
                }
                else {
                    sb.append(".longValue = ");
                    sb.append(this.longValue());
                }
            }
            else {
                sb.append(".intValue = ");
                sb.append(this.intValue());
            }
            sb.append("}");
            return sb.toString();
        }
        
        public final void writeEmbeddedToBlob(final HwBlob hwBlob, final long n) {
            hwBlob.putInt8(0L + n, this.hidl_d);
            final byte hidl_d = this.hidl_d;
            if (hidl_d != 0) {
                if (hidl_d != 1) {
                    if (hidl_d != 2) {
                        if (hidl_d != 3) {
                            final StringBuilder sb = new StringBuilder();
                            sb.append("Unknown union discriminator (value: ");
                            sb.append(this.hidl_d);
                            sb.append(").");
                            throw new Error(sb.toString());
                        }
                        hwBlob.putString(8L + n, this.stringValue());
                    }
                    else {
                        hwBlob.putFloat(8L + n, this.floatValue());
                    }
                }
                else {
                    hwBlob.putInt64(8L + n, this.longValue());
                }
            }
            else {
                hwBlob.putInt32(8L + n, this.intValue());
            }
        }
        
        public final void writeToParcel(final HwParcel hwParcel) {
            final HwBlob hwBlob = new HwBlob(24);
            this.writeEmbeddedToBlob(hwBlob, 0L);
            hwParcel.writeBuffer(hwBlob);
        }
        
        public static final class hidl_discriminator
        {
            public static final byte floatValue = 2;
            public static final byte intValue = 0;
            public static final byte longValue = 1;
            public static final byte stringValue = 3;
            
            private hidl_discriminator() {
            }
            
            public static final String getName(final byte b) {
                if (b == 0) {
                    return "intValue";
                }
                if (b == 1) {
                    return "longValue";
                }
                if (b == 2) {
                    return "floatValue";
                }
                if (b != 3) {
                    return "Unknown";
                }
                return "stringValue";
            }
        }
    }
}
