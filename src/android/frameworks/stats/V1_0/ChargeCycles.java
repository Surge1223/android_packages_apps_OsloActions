package android.frameworks.stats.V1_0;

import java.util.Objects;
import android.os.HidlSupport;
import android.os.HwBlob;
import android.os.HwParcel;
import java.util.ArrayList;

public final class ChargeCycles
{
    public ArrayList<Integer> cycleBucket;
    
    public ChargeCycles() {
        this.cycleBucket = new ArrayList<Integer>();
    }
    
    public static final ArrayList<ChargeCycles> readVectorFromParcel(final HwParcel hwParcel) {
        final ArrayList<ChargeCycles> list = new ArrayList<ChargeCycles>();
        final HwBlob buffer = hwParcel.readBuffer(16L);
        final int int32 = buffer.getInt32(8L);
        final HwBlob embeddedBuffer = hwParcel.readEmbeddedBuffer((long)(int32 * 16), buffer.handle(), 0L, true);
        list.clear();
        for (int i = 0; i < int32; ++i) {
            final ChargeCycles chargeCycles = new ChargeCycles();
            chargeCycles.readEmbeddedFromParcel(hwParcel, embeddedBuffer, i * 16);
            list.add(chargeCycles);
        }
        return list;
    }
    
    public static final void writeVectorToParcel(final HwParcel hwParcel, final ArrayList<ChargeCycles> list) {
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
        return this == o || (o != null && o.getClass() == ChargeCycles.class && HidlSupport.deepEquals((Object)this.cycleBucket, (Object)((ChargeCycles)o).cycleBucket));
    }
    
    @Override
    public final int hashCode() {
        return Objects.hash(HidlSupport.deepHashCode((Object)this.cycleBucket));
    }
    
    public final void readEmbeddedFromParcel(final HwParcel hwParcel, final HwBlob hwBlob, final long n) {
        final int int32 = hwBlob.getInt32(n + 0L + 8L);
        final HwBlob embeddedBuffer = hwParcel.readEmbeddedBuffer((long)(int32 * 4), hwBlob.handle(), n + 0L + 0L, true);
        this.cycleBucket.clear();
        for (int i = 0; i < int32; ++i) {
            this.cycleBucket.add(embeddedBuffer.getInt32((long)(i * 4)));
        }
    }
    
    public final void readFromParcel(final HwParcel hwParcel) {
        this.readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(16L), 0L);
    }
    
    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(".cycleBucket = ");
        sb.append(this.cycleBucket);
        sb.append("}");
        return sb.toString();
    }
    
    public final void writeEmbeddedToBlob(final HwBlob hwBlob, final long n) {
        final int size = this.cycleBucket.size();
        hwBlob.putInt32(n + 0L + 8L, size);
        hwBlob.putBool(n + 0L + 12L, false);
        final HwBlob hwBlob2 = new HwBlob(size * 4);
        for (int i = 0; i < size; ++i) {
            hwBlob2.putInt32((long)(i * 4), (int)this.cycleBucket.get(i));
        }
        hwBlob.putBlob(n + 0L + 0L, hwBlob2);
    }
    
    public final void writeToParcel(final HwParcel hwParcel) {
        final HwBlob hwBlob = new HwBlob(16);
        this.writeEmbeddedToBlob(hwBlob, 0L);
        hwParcel.writeBuffer(hwBlob);
    }
}
