package android.frameworks.stats.V1_0;

import java.util.Objects;
import android.os.HidlSupport;
import android.os.HwBlob;
import java.util.ArrayList;
import android.os.HwParcel;

public final class UsbPortOverheatEvent
{
    public int maxTemperatureDeciC;
    public int plugTemperatureDeciC;
    public int timeToHysteresis;
    public int timeToInactive;
    public int timeToOverheat;
    
    public static final ArrayList<UsbPortOverheatEvent> readVectorFromParcel(final HwParcel hwParcel) {
        final ArrayList<UsbPortOverheatEvent> list = new ArrayList<UsbPortOverheatEvent>();
        final HwBlob buffer = hwParcel.readBuffer(16L);
        final int int32 = buffer.getInt32(8L);
        final HwBlob embeddedBuffer = hwParcel.readEmbeddedBuffer((long)(int32 * 20), buffer.handle(), 0L, true);
        list.clear();
        for (int i = 0; i < int32; ++i) {
            final UsbPortOverheatEvent usbPortOverheatEvent = new UsbPortOverheatEvent();
            usbPortOverheatEvent.readEmbeddedFromParcel(hwParcel, embeddedBuffer, i * 20);
            list.add(usbPortOverheatEvent);
        }
        return list;
    }
    
    public static final void writeVectorToParcel(final HwParcel hwParcel, final ArrayList<UsbPortOverheatEvent> list) {
        final HwBlob hwBlob = new HwBlob(16);
        final int size = list.size();
        hwBlob.putInt32(8L, size);
        hwBlob.putBool(12L, false);
        final HwBlob hwBlob2 = new HwBlob(size * 20);
        for (int i = 0; i < size; ++i) {
            list.get(i).writeEmbeddedToBlob(hwBlob2, i * 20);
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
        if (o.getClass() != UsbPortOverheatEvent.class) {
            return false;
        }
        final UsbPortOverheatEvent usbPortOverheatEvent = (UsbPortOverheatEvent)o;
        return this.plugTemperatureDeciC == usbPortOverheatEvent.plugTemperatureDeciC && this.maxTemperatureDeciC == usbPortOverheatEvent.maxTemperatureDeciC && this.timeToOverheat == usbPortOverheatEvent.timeToOverheat && this.timeToHysteresis == usbPortOverheatEvent.timeToHysteresis && this.timeToInactive == usbPortOverheatEvent.timeToInactive;
    }
    
    @Override
    public final int hashCode() {
        return Objects.hash(HidlSupport.deepHashCode((Object)this.plugTemperatureDeciC), HidlSupport.deepHashCode((Object)this.maxTemperatureDeciC), HidlSupport.deepHashCode((Object)this.timeToOverheat), HidlSupport.deepHashCode((Object)this.timeToHysteresis), HidlSupport.deepHashCode((Object)this.timeToInactive));
    }
    
    public final void readEmbeddedFromParcel(final HwParcel hwParcel, final HwBlob hwBlob, final long n) {
        this.plugTemperatureDeciC = hwBlob.getInt32(0L + n);
        this.maxTemperatureDeciC = hwBlob.getInt32(4L + n);
        this.timeToOverheat = hwBlob.getInt32(8L + n);
        this.timeToHysteresis = hwBlob.getInt32(12L + n);
        this.timeToInactive = hwBlob.getInt32(16L + n);
    }
    
    public final void readFromParcel(final HwParcel hwParcel) {
        this.readEmbeddedFromParcel(hwParcel, hwParcel.readBuffer(20L), 0L);
    }
    
    @Override
    public final String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append(".plugTemperatureDeciC = ");
        sb.append(this.plugTemperatureDeciC);
        sb.append(", .maxTemperatureDeciC = ");
        sb.append(this.maxTemperatureDeciC);
        sb.append(", .timeToOverheat = ");
        sb.append(this.timeToOverheat);
        sb.append(", .timeToHysteresis = ");
        sb.append(this.timeToHysteresis);
        sb.append(", .timeToInactive = ");
        sb.append(this.timeToInactive);
        sb.append("}");
        return sb.toString();
    }
    
    public final void writeEmbeddedToBlob(final HwBlob hwBlob, final long n) {
        hwBlob.putInt32(0L + n, this.plugTemperatureDeciC);
        hwBlob.putInt32(4L + n, this.maxTemperatureDeciC);
        hwBlob.putInt32(8L + n, this.timeToOverheat);
        hwBlob.putInt32(12L + n, this.timeToHysteresis);
        hwBlob.putInt32(16L + n, this.timeToInactive);
    }
    
    public final void writeToParcel(final HwParcel hwParcel) {
        final HwBlob hwBlob = new HwBlob(20);
        this.writeEmbeddedToBlob(hwBlob, 0L);
        hwParcel.writeBuffer(hwBlob);
    }
}
