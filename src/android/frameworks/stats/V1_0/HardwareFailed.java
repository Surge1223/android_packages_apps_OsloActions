package android.frameworks.stats.V1_0;

import java.util.Objects;
import android.os.HidlSupport;
import android.os.HwBlob;
import java.util.ArrayList;
import android.os.HwParcel;

public final class HardwareFailed
{
    public int errorCode;
    public int hardwareLocation;
    public int hardwareType;
    
    public static final ArrayList<HardwareFailed> readVectorFromParcel(final HwParcel hwParcel) {
        final ArrayList<HardwareFailed> list = new ArrayList<HardwareFailed>();
        final HwBlob buffer = hwParcel.readBuffer(16L);
        final int int32 = buffer.getInt32(8L);
        final HwBlob embeddedBuffer = hwParcel.readEmbeddedBuffer((long)(int32 * 12), buffer.handle(), 0L, true);
        list.clear();
        for (int i = 0; i < int32; ++i) {
            final HardwareFailed hardwareFailed = new HardwareFailed();
            hardwareFailed.readEmbeddedFromParcel(hwParcel, embeddedBuffer, i * 12);
            list.add(hardwareFailed);
        }
        return list;
    }
    
    public static final void writeVectorToParcel(final HwParcel hwParcel, final ArrayList<HardwareFailed> list) {
        final HwBlob hwBlob = new HwBlob(16);
        final int size = list.size();
        hwBlob.putInt32(8L, size);
        hwBlob.putBool(12L, false);
        final HwBlob hwBlob2 = new HwBlob(size * 12);
        for (int i = 0; i < size; ++i) {
            list.get(i).writeEmbeddedToBlob(hwBlob2, i * 12);
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
        if (o.getClass() != HardwareFailed.class) {
            return false;
        }
        final HardwareFailed hardwareFailed = (HardwareFailed)o;
        return this.hardwareType == hardwareFailed.hardwareType && this.hardwareLocation == hardwareFailed.hardwareLocation && this.errorCode == hardwareFailed.errorCode;
    }
    
    @Override
    public final int hashCode() {
        return Objects.hash(HidlSupport.deepHashCode((Object)this.hardwareType), HidlSupport.deepHashCode((Object)this.hardwareLocation), HidlSupport.deepHashCode((Object)this.errorCode));
    }
    
    public final void readEmbeddedFromParcel(final HwParcel hwParcel, final HwBlob hwBlob, final long n) {
        this.hardwareType = hwBlob.getInt32(0L + n);
        this.hardwareLocation = hwBlob.getInt32(4L + n);
        this.errorCode = hwBlob.getInt32(8L + n);
    }
    
    public final void readFromParcel(final HwParcel hwParcel) {
        this.readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(12L), 0L);
    }
    
    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(".hardwareType = ");
        sb.append(HardwareType.toString(this.hardwareType));
        sb.append(", .hardwareLocation = ");
        sb.append(this.hardwareLocation);
        sb.append(", .errorCode = ");
        sb.append(HardwareErrorCode.toString(this.errorCode));
        sb.append("}");
        return sb.toString();
    }
    
    public final void writeEmbeddedToBlob(final HwBlob hwBlob, final long n) {
        hwBlob.putInt32(0L + n, this.hardwareType);
        hwBlob.putInt32(4L + n, this.hardwareLocation);
        hwBlob.putInt32(8L + n, this.errorCode);
    }
    
    public final void writeToParcel(final HwParcel hwParcel) {
        final HwBlob hwBlob = new HwBlob(12);
        this.writeEmbeddedToBlob(hwBlob, 0L);
        hwParcel.writeBuffer(hwBlob);
    }
    
    public static final class HardwareErrorCode
    {
        public static final int COMPLETE = 1;
        public static final int DEGRADE = 6;
        public static final int FINGERPRINT_SENSOR_BROKEN = 4;
        public static final int FINGERPRINT_TOO_MANY_DEAD_PIXELS = 5;
        public static final int SPEAKER_HIGH_Z = 2;
        public static final int SPEAKER_SHORT = 3;
        public static final int UNKNOWN = 0;
        
        public static final String dumpBitfield(final int n) {
            final ArrayList<String> list = new ArrayList<String>();
            boolean b = false;
            list.add("UNKNOWN");
            if ((n & 0x1) == 0x1) {
                list.add("COMPLETE");
                b = (false | true);
            }
            int n2 = b ? 1 : 0;
            if ((n & 0x2) == 0x2) {
                list.add("SPEAKER_HIGH_Z");
                n2 = ((b ? 1 : 0) | 0x2);
            }
            int n3 = n2;
            if ((n & 0x3) == 0x3) {
                list.add("SPEAKER_SHORT");
                n3 = (n2 | 0x3);
            }
            int n4 = n3;
            if ((n & 0x4) == 0x4) {
                list.add("FINGERPRINT_SENSOR_BROKEN");
                n4 = (n3 | 0x4);
            }
            int n5 = n4;
            if ((n & 0x5) == 0x5) {
                list.add("FINGERPRINT_TOO_MANY_DEAD_PIXELS");
                n5 = (n4 | 0x5);
            }
            int n6 = n5;
            if ((n & 0x6) == 0x6) {
                list.add("DEGRADE");
                n6 = (n5 | 0x6);
            }
            if (n != n6) {
                final StringBuilder sb = new StringBuilder();
                sb.append("0x");
                sb.append(Integer.toHexString(n6 & n));
                list.add(sb.toString());
            }
            return String.join(" | ", list);
        }
        
        public static final String toString(final int n) {
            if (n == 0) {
                return "UNKNOWN";
            }
            if (n == 1) {
                return "COMPLETE";
            }
            if (n == 2) {
                return "SPEAKER_HIGH_Z";
            }
            if (n == 3) {
                return "SPEAKER_SHORT";
            }
            if (n == 4) {
                return "FINGERPRINT_SENSOR_BROKEN";
            }
            if (n == 5) {
                return "FINGERPRINT_TOO_MANY_DEAD_PIXELS";
            }
            if (n == 6) {
                return "DEGRADE";
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("0x");
            sb.append(Integer.toHexString(n));
            return sb.toString();
        }
    }
    
    public static final class HardwareType
    {
        public static final int CODEC = 2;
        public static final int FINGERPRINT = 4;
        public static final int MICROPHONE = 1;
        public static final int SPEAKER = 3;
        public static final int UNKNOWN = 0;
        
        public static final String dumpBitfield(final int n) {
            final ArrayList<String> list = new ArrayList<String>();
            boolean b = false;
            list.add("UNKNOWN");
            if ((n & 0x1) == 0x1) {
                list.add("MICROPHONE");
                b = (false | true);
            }
            int n2 = b ? 1 : 0;
            if ((n & 0x2) == 0x2) {
                list.add("CODEC");
                n2 = ((b ? 1 : 0) | 0x2);
            }
            int n3 = n2;
            if ((n & 0x3) == 0x3) {
                list.add("SPEAKER");
                n3 = (n2 | 0x3);
            }
            int n4 = n3;
            if ((n & 0x4) == 0x4) {
                list.add("FINGERPRINT");
                n4 = (n3 | 0x4);
            }
            if (n != n4) {
                final StringBuilder sb = new StringBuilder();
                sb.append("0x");
                sb.append(Integer.toHexString(n4 & n));
                list.add(sb.toString());
            }
            return String.join(" | ", list);
        }
        
        public static final String toString(final int n) {
            if (n == 0) {
                return "UNKNOWN";
            }
            if (n == 1) {
                return "MICROPHONE";
            }
            if (n == 2) {
                return "CODEC";
            }
            if (n == 3) {
                return "SPEAKER";
            }
            if (n == 4) {
                return "FINGERPRINT";
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("0x");
            sb.append(Integer.toHexString(n));
            return sb.toString();
        }
    }
}
