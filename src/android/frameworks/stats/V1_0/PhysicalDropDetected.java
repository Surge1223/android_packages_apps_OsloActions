package android.frameworks.stats.V1_0;

import java.util.Objects;
import android.os.HidlSupport;
import android.os.HwBlob;
import java.util.ArrayList;
import android.os.HwParcel;

public final class PhysicalDropDetected
{
    public int accelPeak;
    public byte confidencePctg;
    public int freefallDuration;
    
    public static final ArrayList<PhysicalDropDetected> readVectorFromParcel(final HwParcel hwParcel) {
        final ArrayList<PhysicalDropDetected> list = new ArrayList<PhysicalDropDetected>();
        final HwBlob buffer = hwParcel.readBuffer(16L);
        final int int32 = buffer.getInt32(8L);
        final HwBlob embeddedBuffer = hwParcel.readEmbeddedBuffer((long)(int32 * 12), buffer.handle(), 0L, true);
        list.clear();
        for (int i = 0; i < int32; ++i) {
            final PhysicalDropDetected physicalDropDetected = new PhysicalDropDetected();
            physicalDropDetected.readEmbeddedFromParcel(hwParcel, embeddedBuffer, i * 12);
            list.add(physicalDropDetected);
        }
        return list;
    }
    
    public static final void writeVectorToParcel(final HwParcel hwParcel, final ArrayList<PhysicalDropDetected> list) {
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
        if (o.getClass() != PhysicalDropDetected.class) {
            return false;
        }
        final PhysicalDropDetected physicalDropDetected = (PhysicalDropDetected)o;
        return this.confidencePctg == physicalDropDetected.confidencePctg && this.accelPeak == physicalDropDetected.accelPeak && this.freefallDuration == physicalDropDetected.freefallDuration;
    }
    
    @Override
    public final int hashCode() {
        return Objects.hash(HidlSupport.deepHashCode((Object)this.confidencePctg), HidlSupport.deepHashCode((Object)this.accelPeak), HidlSupport.deepHashCode((Object)this.freefallDuration));
    }
    
    public final void readEmbeddedFromParcel(final HwParcel hwParcel, final HwBlob hwBlob, final long n) {
        this.confidencePctg = hwBlob.getInt8(0L + n);
        this.accelPeak = hwBlob.getInt32(4L + n);
        this.freefallDuration = hwBlob.getInt32(8L + n);
    }
    
    public final void readFromParcel(final HwParcel hwParcel) {
        this.readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(12L), 0L);
    }
    
    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(".confidencePctg = ");
        sb.append(this.confidencePctg);
        sb.append(", .accelPeak = ");
        sb.append(this.accelPeak);
        sb.append(", .freefallDuration = ");
        sb.append(this.freefallDuration);
        sb.append("}");
        return sb.toString();
    }
    
    public final void writeEmbeddedToBlob(final HwBlob hwBlob, final long n) {
        hwBlob.putInt8(0L + n, this.confidencePctg);
        hwBlob.putInt32(4L + n, this.accelPeak);
        hwBlob.putInt32(8L + n, this.freefallDuration);
    }
    
    public final void writeToParcel(final HwParcel hwParcel) {
        final HwBlob hwBlob = new HwBlob(12);
        this.writeEmbeddedToBlob(hwBlob, 0L);
        hwParcel.writeBuffer(hwBlob);
    }
}
