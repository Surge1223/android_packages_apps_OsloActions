package android.frameworks.stats.V1_0;

import java.util.Objects;
import android.os.HidlSupport;
import android.os.HwBlob;
import java.util.ArrayList;
import android.os.HwParcel;

public final class SlowIo
{
    public int count;
    public int operation;
    
    public static final ArrayList<SlowIo> readVectorFromParcel(final HwParcel hwParcel) {
        final ArrayList<SlowIo> list = new ArrayList<SlowIo>();
        final HwBlob buffer = hwParcel.readBuffer(16L);
        final int int32 = buffer.getInt32(8L);
        final HwBlob embeddedBuffer = hwParcel.readEmbeddedBuffer((long)(int32 * 8), buffer.handle(), 0L, true);
        list.clear();
        for (int i = 0; i < int32; ++i) {
            final SlowIo slowIo = new SlowIo();
            slowIo.readEmbeddedFromParcel(hwParcel, embeddedBuffer, i * 8);
            list.add(slowIo);
        }
        return list;
    }
    
    public static final void writeVectorToParcel(final HwParcel hwParcel, final ArrayList<SlowIo> list) {
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
        if (o.getClass() != SlowIo.class) {
            return false;
        }
        final SlowIo slowIo = (SlowIo)o;
        return this.operation == slowIo.operation && this.count == slowIo.count;
    }
    
    @Override
    public final int hashCode() {
        return Objects.hash(HidlSupport.deepHashCode((Object)this.operation), HidlSupport.deepHashCode((Object)this.count));
    }
    
    public final void readEmbeddedFromParcel(final HwParcel hwParcel, final HwBlob hwBlob, final long n) {
        this.operation = hwBlob.getInt32(0L + n);
        this.count = hwBlob.getInt32(4L + n);
    }
    
    public final void readFromParcel(final HwParcel hwParcel) {
        this.readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(8L), 0L);
    }
    
    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(".operation = ");
        sb.append(IoOperation.toString(this.operation));
        sb.append(", .count = ");
        sb.append(this.count);
        sb.append("}");
        return sb.toString();
    }
    
    public final void writeEmbeddedToBlob(final HwBlob hwBlob, final long n) {
        hwBlob.putInt32(0L + n, this.operation);
        hwBlob.putInt32(4L + n, this.count);
    }
    
    public final void writeToParcel(final HwParcel hwParcel) {
        final HwBlob hwBlob = new HwBlob(8);
        this.writeEmbeddedToBlob(hwBlob, 0L);
        hwParcel.writeBuffer(hwBlob);
    }
    
    public static final class IoOperation
    {
        public static final int READ = 1;
        public static final int SYNC = 4;
        public static final int UNKNOWN = 0;
        public static final int UNMAP = 3;
        public static final int WRITE = 2;
        
        public static final String dumpBitfield(final int n) {
            final ArrayList<String> list = new ArrayList<String>();
            boolean b = false;
            list.add("UNKNOWN");
            if ((n & 0x1) == 0x1) {
                list.add("READ");
                b = (false | true);
            }
            int n2 = b ? 1 : 0;
            if ((n & 0x2) == 0x2) {
                list.add("WRITE");
                n2 = ((b ? 1 : 0) | 0x2);
            }
            int n3 = n2;
            if ((n & 0x3) == 0x3) {
                list.add("UNMAP");
                n3 = (n2 | 0x3);
            }
            int n4 = n3;
            if ((n & 0x4) == 0x4) {
                list.add("SYNC");
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
                return "READ";
            }
            if (n == 2) {
                return "WRITE";
            }
            if (n == 3) {
                return "UNMAP";
            }
            if (n == 4) {
                return "SYNC";
            }
            final StringBuilder sb = new StringBuilder();
            sb.append("0x");
            sb.append(Integer.toHexString(n));
            return sb.toString();
        }
    }
}
