package android.frameworks.stats.V1_0;

import java.util.Objects;
import android.os.HidlSupport;
import android.os.HwBlob;
import java.util.ArrayList;
import android.os.HwParcel;

public final class SpeechDspStat
{
    public int totalCrashCount;
    public int totalDowntimeMillis;
    public int totalRecoverCount;
    public int totalUptimeMillis;
    
    public static final ArrayList<SpeechDspStat> readVectorFromParcel(final HwParcel hwParcel) {
        final ArrayList<SpeechDspStat> list = new ArrayList<SpeechDspStat>();
        final HwBlob buffer = hwParcel.readBuffer(16L);
        final int int32 = buffer.getInt32(8L);
        final HwBlob embeddedBuffer = hwParcel.readEmbeddedBuffer((long)(int32 * 16), buffer.handle(), 0L, true);
        list.clear();
        for (int i = 0; i < int32; ++i) {
            final SpeechDspStat speechDspStat = new SpeechDspStat();
            speechDspStat.readEmbeddedFromParcel(hwParcel, embeddedBuffer, i * 16);
            list.add(speechDspStat);
        }
        return list;
    }
    
    public static final void writeVectorToParcel(final HwParcel hwParcel, final ArrayList<SpeechDspStat> list) {
        final HwBlob hwBlob = new HwBlob(16);
        final int size = list.size();
        hwBlob.putInt32(8L, size);
        hwBlob.putBool(12L, false);
        final HwBlob hwBlob2 = new HwBlob(size * 16);
        for (int i = 0; i < size; ++i) {
            list.get(i).writeEmbeddedToBlob(hwBlob2, i * 16);
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
        if (o.getClass() != SpeechDspStat.class) {
            return false;
        }
        final SpeechDspStat speechDspStat = (SpeechDspStat)o;
        return this.totalUptimeMillis == speechDspStat.totalUptimeMillis && this.totalDowntimeMillis == speechDspStat.totalDowntimeMillis && this.totalCrashCount == speechDspStat.totalCrashCount && this.totalRecoverCount == speechDspStat.totalRecoverCount;
    }
    
    @Override
    public final int hashCode() {
        return Objects.hash(HidlSupport.deepHashCode((Object)this.totalUptimeMillis), HidlSupport.deepHashCode((Object)this.totalDowntimeMillis), HidlSupport.deepHashCode((Object)this.totalCrashCount), HidlSupport.deepHashCode((Object)this.totalRecoverCount));
    }
    
    public final void readEmbeddedFromParcel(final HwParcel hwParcel, final HwBlob hwBlob, final long n) {
        this.totalUptimeMillis = hwBlob.getInt32(0L + n);
        this.totalDowntimeMillis = hwBlob.getInt32(4L + n);
        this.totalCrashCount = hwBlob.getInt32(8L + n);
        this.totalRecoverCount = hwBlob.getInt32(12L + n);
    }
    
    public final void readFromParcel(final HwParcel hwParcel) {
        this.readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(16L), 0L);
    }
    
    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(".totalUptimeMillis = ");
        sb.append(this.totalUptimeMillis);
        sb.append(", .totalDowntimeMillis = ");
        sb.append(this.totalDowntimeMillis);
        sb.append(", .totalCrashCount = ");
        sb.append(this.totalCrashCount);
        sb.append(", .totalRecoverCount = ");
        sb.append(this.totalRecoverCount);
        sb.append("}");
        return sb.toString();
    }
    
    public final void writeEmbeddedToBlob(final HwBlob hwBlob, final long n) {
        hwBlob.putInt32(0L + n, this.totalUptimeMillis);
        hwBlob.putInt32(4L + n, this.totalDowntimeMillis);
        hwBlob.putInt32(8L + n, this.totalCrashCount);
        hwBlob.putInt32(12L + n, this.totalRecoverCount);
    }
    
    public final void writeToParcel(final HwParcel hwParcel) {
        final HwBlob hwBlob = new HwBlob(16);
        this.writeEmbeddedToBlob(hwBlob, 0L);
        hwParcel.writeBuffer(hwBlob);
    }
}
