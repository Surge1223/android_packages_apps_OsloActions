package android.frameworks.stats.V1_0;

import java.util.Objects;
import android.os.HidlSupport;
import android.os.HwBlob;
import java.util.ArrayList;
import android.os.HwParcel;

public final class BatteryHealthSnapshotArgs
{
    public int currentMicroA;
    public int levelPercent;
    public int openCircuitVoltageMicroV;
    public int resistanceMicroOhm;
    public int temperatureDeciC;
    public int type;
    public int voltageMicroV;
    
    public static final ArrayList<BatteryHealthSnapshotArgs> readVectorFromParcel(final HwParcel hwParcel) {
        final ArrayList<BatteryHealthSnapshotArgs> list = new ArrayList<BatteryHealthSnapshotArgs>();
        final HwBlob buffer = hwParcel.readBuffer(16L);
        final int int32 = buffer.getInt32(8L);
        final HwBlob embeddedBuffer = hwParcel.readEmbeddedBuffer((long)(int32 * 28), buffer.handle(), 0L, true);
        list.clear();
        for (int i = 0; i < int32; ++i) {
            final BatteryHealthSnapshotArgs batteryHealthSnapshotArgs = new BatteryHealthSnapshotArgs();
            batteryHealthSnapshotArgs.readEmbeddedFromParcel(hwParcel, embeddedBuffer, i * 28);
            list.add(batteryHealthSnapshotArgs);
        }
        return list;
    }
    
    public static final void writeVectorToParcel(final HwParcel hwParcel, final ArrayList<BatteryHealthSnapshotArgs> list) {
        final HwBlob hwBlob = new HwBlob(16);
        final int size = list.size();
        hwBlob.putInt32(8L, size);
        hwBlob.putBool(12L, false);
        final HwBlob hwBlob2 = new HwBlob(size * 28);
        for (int i = 0; i < size; ++i) {
            list.get(i).writeEmbeddedToBlob(hwBlob2, i * 28);
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
        if (o.getClass() != BatteryHealthSnapshotArgs.class) {
            return false;
        }
        final BatteryHealthSnapshotArgs batteryHealthSnapshotArgs = (BatteryHealthSnapshotArgs)o;
        return this.type == batteryHealthSnapshotArgs.type && this.temperatureDeciC == batteryHealthSnapshotArgs.temperatureDeciC && this.voltageMicroV == batteryHealthSnapshotArgs.voltageMicroV && this.currentMicroA == batteryHealthSnapshotArgs.currentMicroA && this.openCircuitVoltageMicroV == batteryHealthSnapshotArgs.openCircuitVoltageMicroV && this.resistanceMicroOhm == batteryHealthSnapshotArgs.resistanceMicroOhm && this.levelPercent == batteryHealthSnapshotArgs.levelPercent;
    }
    
    @Override
    public final int hashCode() {
        return Objects.hash(HidlSupport.deepHashCode((Object)this.type), HidlSupport.deepHashCode((Object)this.temperatureDeciC), HidlSupport.deepHashCode((Object)this.voltageMicroV), HidlSupport.deepHashCode((Object)this.currentMicroA), HidlSupport.deepHashCode((Object)this.openCircuitVoltageMicroV), HidlSupport.deepHashCode((Object)this.resistanceMicroOhm), HidlSupport.deepHashCode((Object)this.levelPercent));
    }
    
    public final void readEmbeddedFromParcel(final HwParcel hwParcel, final HwBlob hwBlob, final long n) {
        this.type = hwBlob.getInt32(0L + n);
        this.temperatureDeciC = hwBlob.getInt32(4L + n);
        this.voltageMicroV = hwBlob.getInt32(8L + n);
        this.currentMicroA = hwBlob.getInt32(12L + n);
        this.openCircuitVoltageMicroV = hwBlob.getInt32(16L + n);
        this.resistanceMicroOhm = hwBlob.getInt32(20L + n);
        this.levelPercent = hwBlob.getInt32(24L + n);
    }
    
    public final void readFromParcel(final HwParcel hwParcel) {
        this.readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(28L), 0L);
    }
    
    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(".type = ");
        sb.append(BatterySnapshotType.toString(this.type));
        sb.append(", .temperatureDeciC = ");
        sb.append(this.temperatureDeciC);
        sb.append(", .voltageMicroV = ");
        sb.append(this.voltageMicroV);
        sb.append(", .currentMicroA = ");
        sb.append(this.currentMicroA);
        sb.append(", .openCircuitVoltageMicroV = ");
        sb.append(this.openCircuitVoltageMicroV);
        sb.append(", .resistanceMicroOhm = ");
        sb.append(this.resistanceMicroOhm);
        sb.append(", .levelPercent = ");
        sb.append(this.levelPercent);
        sb.append("}");
        return sb.toString();
    }
    
    public final void writeEmbeddedToBlob(final HwBlob hwBlob, final long n) {
        hwBlob.putInt32(0L + n, this.type);
        hwBlob.putInt32(4L + n, this.temperatureDeciC);
        hwBlob.putInt32(8L + n, this.voltageMicroV);
        hwBlob.putInt32(12L + n, this.currentMicroA);
        hwBlob.putInt32(16L + n, this.openCircuitVoltageMicroV);
        hwBlob.putInt32(20L + n, this.resistanceMicroOhm);
        hwBlob.putInt32(24L + n, this.levelPercent);
    }
    
    public final void writeToParcel(final HwParcel hwParcel) {
        final HwBlob hwBlob = new HwBlob(28);
        this.writeEmbeddedToBlob(hwBlob, 0L);
        hwParcel.writeBuffer(hwBlob);
    }
    
    public static final class BatterySnapshotType
    {
        public static final int AVG_RESISTANCE = 11;
        public static final int MAX_BATT_LEVEL = 10;
        public static final int MAX_CURRENT = 8;
        public static final int MAX_RESISTANCE = 4;
        public static final int MAX_TEMP = 2;
        public static final int MAX_VOLTAGE = 6;
        public static final int MIN_BATT_LEVEL = 9;
        public static final int MIN_CURRENT = 7;
        public static final int MIN_RESISTANCE = 3;
        public static final int MIN_TEMP = 1;
        public static final int MIN_VOLTAGE = 5;
        
        public static final String dumpBitfield(final int n) {
            final ArrayList<String> list = new ArrayList<String>();
            boolean b = false;
            if ((n & 0x1) == 0x1) {
                list.add("MIN_TEMP");
                b = (false | true);
            }
            int n2 = b ? 1 : 0;
            if ((n & 0x2) == 0x2) {
                list.add("MAX_TEMP");
                n2 = ((b ? 1 : 0) | 0x2);
            }
            int n3 = n2;
            if ((n & 0x3) == 0x3) {
                list.add("MIN_RESISTANCE");
                n3 = (n2 | 0x3);
            }
            int n4 = n3;
            if ((n & 0x4) == 0x4) {
                list.add("MAX_RESISTANCE");
                n4 = (n3 | 0x4);
            }
            int n5 = n4;
            if ((n & 0x5) == 0x5) {
                list.add("MIN_VOLTAGE");
                n5 = (n4 | 0x5);
            }
            int n6 = n5;
            if ((n & 0x6) == 0x6) {
                list.add("MAX_VOLTAGE");
                n6 = (n5 | 0x6);
            }
            int n7 = n6;
            if ((n & 0x7) == 0x7) {
                list.add("MIN_CURRENT");
                n7 = (n6 | 0x7);
            }
            int n8 = n7;
            if ((n & 0x8) == 0x8) {
                list.add("MAX_CURRENT");
                n8 = (n7 | 0x8);
            }
            int n9 = n8;
            if ((n & 0x9) == 0x9) {
                list.add("MIN_BATT_LEVEL");
                n9 = (n8 | 0x9);
            }
            int n10 = n9;
            if ((n & 0xA) == 0xA) {
                list.add("MAX_BATT_LEVEL");
                n10 = (n9 | 0xA);
            }
            int n11 = n10;
            if ((n & 0xB) == 0xB) {
                list.add("AVG_RESISTANCE");
                n11 = (n10 | 0xB);
            }
            if (n != n11) {
                final StringBuilder sb = new StringBuilder();
                sb.append("0x");
                sb.append(Integer.toHexString(n11 & n));
                list.add(sb.toString());
            }
            return String.join(" | ", list);
        }
        
        public static final String toString(final int n) {
            if (n == 1) {
                return "MIN_TEMP";
            }
            if (n == 2) {
                return "MAX_TEMP";
            }
            if (n == 3) {
                return "MIN_RESISTANCE";
            }
            if (n == 4) {
                return "MAX_RESISTANCE";
            }
            if (n == 5) {
                return "MIN_VOLTAGE";
            }
            if (n == 6) {
                return "MAX_VOLTAGE";
            }
            if (n == 7) {
                return "MIN_CURRENT";
            }
            if (n == 8) {
                return "MAX_CURRENT";
            }
            if (n == 9) {
                return "MIN_BATT_LEVEL";
            }
            if (n == 10) {
                return "MAX_BATT_LEVEL";
            }
            if (n == 11) {
                return "AVG_RESISTANCE";
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("0x");
            sb.append(Integer.toHexString(n));
            return sb.toString();
        }
    }
}
