package android.frameworks.stats.V1_0;

import java.util.Objects;
import android.os.HidlSupport;
import android.os.HwBlob;
import java.util.ArrayList;
import android.os.HwParcel;

public final class SpeakerImpedance
{
    public int milliOhms;
    public int speakerLocation;
    
    public static final ArrayList<SpeakerImpedance> readVectorFromParcel(final HwParcel hwParcel) {
        final ArrayList<SpeakerImpedance> list = new ArrayList<SpeakerImpedance>();
        final HwBlob buffer = hwParcel.readBuffer(16L);
        final int int32 = buffer.getInt32(8L);
        final HwBlob embeddedBuffer = hwParcel.readEmbeddedBuffer((long)(int32 * 8), buffer.handle(), 0L, true);
        list.clear();
        for (int i = 0; i < int32; ++i) {
            final SpeakerImpedance speakerImpedance = new SpeakerImpedance();
            speakerImpedance.readEmbeddedFromParcel(hwParcel, embeddedBuffer, i * 8);
            list.add(speakerImpedance);
        }
        return list;
    }
    
    public static final void writeVectorToParcel(final HwParcel hwParcel, final ArrayList<SpeakerImpedance> list) {
        final HwBlob hwBlob = new HwBlob(16);
        final int size = list.size();
        hwBlob.putInt32(8L, size);
        hwBlob.putBool(12L, false);
        final HwBlob hwBlob2 = new HwBlob(size * 8);
        for (int i = 0; i < size; ++i) {
            list.get(i).writeEmbeddedToBlob(hwBlob2, i * 8);
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
        if (o.getClass() != SpeakerImpedance.class) {
            return false;
        }
        final SpeakerImpedance speakerImpedance = (SpeakerImpedance)o;
        return this.speakerLocation == speakerImpedance.speakerLocation && this.milliOhms == speakerImpedance.milliOhms;
    }
    
    @Override
    public final int hashCode() {
        return Objects.hash(HidlSupport.deepHashCode((Object)this.speakerLocation), HidlSupport.deepHashCode((Object)this.milliOhms));
    }
    
    public final void readEmbeddedFromParcel(final HwParcel hwParcel, final HwBlob hwBlob, final long n) {
        this.speakerLocation = hwBlob.getInt32(0L + n);
        this.milliOhms = hwBlob.getInt32(4L + n);
    }
    
    public final void readFromParcel(final HwParcel hwParcel) {
        this.readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(8L), 0L);
    }
    
    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(".speakerLocation = ");
        sb.append(this.speakerLocation);
        sb.append(", .milliOhms = ");
        sb.append(this.milliOhms);
        sb.append("}");
        return sb.toString();
    }
    
    public final void writeEmbeddedToBlob(final HwBlob hwBlob, final long n) {
        hwBlob.putInt32(0L + n, this.speakerLocation);
        hwBlob.putInt32(4L + n, this.milliOhms);
    }
    
    public final void writeToParcel(final HwParcel hwParcel) {
        final HwBlob hwBlob = new HwBlob(8);
        this.writeEmbeddedToBlob(hwBlob, 0L);
        hwParcel.writeBuffer(hwBlob);
    }
}
