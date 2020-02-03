package com.google.oslo.service.proto.nano;

import com.google.protobuf.nano.CodedInputByteBufferNano;
import com.google.protobuf.nano.CodedOutputByteBufferNano;
import com.google.protobuf.nano.InternalNano;
import com.google.protobuf.nano.InvalidProtocolBufferNanoException;
import com.google.protobuf.nano.MessageNano;
import com.google.protobuf.nano.WireFormatNano;
import java.io.IOException;

public interface OsloMessages {
    public static final int AUDIO_PLAYBACK = 1;
    public static final int BINARY = 1;
    public static final int DEFAULT = 4;
    public static final int DEVICE_IN_MOTION = 3;
    public static final int EAST = 1;
    public static final int FLICK_DISABLE = 2;
    public static final int FLICK_ENABLE = 1;
    public static final int FLICK_OUTPUT = 3;
    public static final int GAME = 3;
    public static final int GATING_CONFIG = 16;
    public static final int HIGH = 3;
    public static final int HIGH_PRIORITY = 2;
    public static final int LOW = 1;
    public static final int LOW_POWER = 2;
    public static final int LOW_PRIORITY = 1;
    public static final int MAX_TX_POWER = 3;
    public static final int MEDIUM = 2;
    public static final int NANOAPP_LOADED = 18;
    public static final int NORTH = 3;
    public static final int NORTH_EAST = 2;
    public static final int NORTH_WEST = 4;
    public static final int OSLO_ACTIVE = 1;
    public static final int PASS_THROUGH = 1;
    public static final int PRESENCE_DISABLE = 5;
    public static final int PRESENCE_ENABLE = 4;
    public static final int PRESENCE_OUTPUT = 6;
    public static final int PROX_ONLY = 3;
    public static final int REACH_DISABLE = 8;
    public static final int REACH_ENABLE = 7;
    public static final int REACH_OUTPUT = 9;
    public static final int SENSOR_OCCLUDED = 2;
    public static final int SET_PARAM = 17;
    public static final int SOUTH = 7;
    public static final int SOUTH_EAST = 8;
    public static final int SOUTH_WEST = 6;
    public static final int STATUS_DISABLE = 11;
    public static final int STATUS_ENABLE = 10;
    public static final int STATUS_OUTPUT = 12;
    public static final int SWIPE_DISABLE = 14;
    public static final int SWIPE_ENABLE = 13;
    public static final int SWIPE_OUTPUT = 15;
    public static final int UI = 2;
    public static final int UNKNOWN_DIRECTION = 0;
    public static final int UNKNOWN_GATING_MODE = 0;
    public static final int UNKNOWN_GATING_REASON = 0;
    public static final int UNKNOWN_GRANULARITY = 0;
    public static final int UNKNOWN_MESSAGE_TYPE = 0;
    public static final int UNKNOWN_PARAM = 0;
    public static final int UNKNOWN_PRIORITY = 0;
    public static final int UNKNOWN_SENSITIVITY = 0;
    public static final int WEST = 5;
    public static final int WIRELESS_CHARGING = 2;

    public static final class FlickEnable extends MessageNano {
        private static volatile FlickEnable[] _emptyArray;
        public int granularity;
        public float radius;
        public int sensitivity;

        public static FlickEnable[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new FlickEnable[0];
                    }
                }
            }
            return _emptyArray;
        }

        public FlickEnable() {
            clear();
        }

        public FlickEnable clear() {
            this.radius = 0.0f;
            this.sensitivity = 0;
            this.granularity = 0;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (Float.floatToIntBits(this.radius) != Float.floatToIntBits(0.0f)) {
                output.writeFloat(1, this.radius);
            }
            int i = this.sensitivity;
            if (i != 0) {
                output.writeInt32(2, i);
            }
            int i2 = this.granularity;
            if (i2 != 0) {
                output.writeInt32(3, i2);
            }
            super.writeTo(output);
        }

        public int computeSerializedSize() {
            int size = super.computeSerializedSize();
            if (Float.floatToIntBits(this.radius) != Float.floatToIntBits(0.0f)) {
                size += CodedOutputByteBufferNano.computeFloatSize(1, this.radius);
            }
            int i = this.sensitivity;
            if (i != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(2, i);
            }
            int i2 = this.granularity;
            if (i2 != 0) {
                return size + CodedOutputByteBufferNano.computeInt32Size(3, i2);
            }
            return size;
        }

        public FlickEnable mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 13) {
                    this.radius = input.readFloat();
                } else if (tag == 16) {
                    int value = input.readInt32();
                    if (value == 0 || value == 1 || value == 2 || value == 3) {
                        this.sensitivity = value;
                    }
                } else if (tag == 24) {
                    int value2 = input.readInt32();
                    if (value2 == 0 || value2 == 1 || value2 == 2 || value2 == 3) {
                        this.granularity = value2;
                    }
                } else if (!WireFormatNano.parseUnknownField(input, tag)) {
                    return this;
                }
            }
        }

        public static FlickEnable parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (FlickEnable) MessageNano.mergeFrom(new FlickEnable(), data);
        }

        public static FlickEnable parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new FlickEnable().mergeFrom(input);
        }
    }

    public static final class FlickOutput extends MessageNano {
        private static volatile FlickOutput[] _emptyArray;
        public boolean detected;
        public int direction;
        public float distance;
        public float likelihood;

        public static FlickOutput[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new FlickOutput[0];
                    }
                }
            }
            return _emptyArray;
        }

        public FlickOutput() {
            clear();
        }

        public FlickOutput clear() {
            this.detected = false;
            this.likelihood = 0.0f;
            this.distance = 0.0f;
            this.direction = 0;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            boolean z = this.detected;
            if (z) {
                output.writeBool(1, z);
            }
            if (Float.floatToIntBits(this.likelihood) != Float.floatToIntBits(0.0f)) {
                output.writeFloat(2, this.likelihood);
            }
            if (Float.floatToIntBits(this.distance) != Float.floatToIntBits(0.0f)) {
                output.writeFloat(3, this.distance);
            }
            int i = this.direction;
            if (i != 0) {
                output.writeInt32(4, i);
            }
            super.writeTo(output);
        }

        
        public int computeSerializedSize() {
            int size = super.computeSerializedSize();
            boolean z = this.detected;
            if (z) {
                size += CodedOutputByteBufferNano.computeBoolSize(1, z);
            }
            if (Float.floatToIntBits(this.likelihood) != Float.floatToIntBits(0.0f)) {
                size += CodedOutputByteBufferNano.computeFloatSize(2, this.likelihood);
            }
            if (Float.floatToIntBits(this.distance) != Float.floatToIntBits(0.0f)) {
                size += CodedOutputByteBufferNano.computeFloatSize(3, this.distance);
            }
            int i = this.direction;
            if (i != 0) {
                return size + CodedOutputByteBufferNano.computeInt32Size(4, i);
            }
            return size;
        }

        public FlickOutput mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    this.detected = input.readBool();
                } else if (tag == 21) {
                    this.likelihood = input.readFloat();
                } else if (tag != 29) {
                    if (tag == 32) {
                        int value = input.readInt32();
                        switch (value) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                                this.direction = value;
                                break;
                        }
                    } else if (!WireFormatNano.parseUnknownField(input, tag)) {
                        return this;
                    }
                } else {
                    this.distance = input.readFloat();
                }
            }
        }

        public static FlickOutput parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (FlickOutput) MessageNano.mergeFrom(new FlickOutput(), data);
        }

        public static FlickOutput parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new FlickOutput().mergeFrom(input);
        }
    }

    public static final class GatingConfig extends MessageNano {
        private static volatile GatingConfig[] _emptyArray;
        public int mode;

        public static GatingConfig[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new GatingConfig[0];
                    }
                }
            }
            return _emptyArray;
        }

        public GatingConfig() {
            clear();
        }

        public GatingConfig clear() {
            this.mode = 0;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            int i = this.mode;
            if (i != 0) {
                output.writeInt32(1, i);
            }
            super.writeTo(output);
        }

        
        public int computeSerializedSize() {
            int size = super.computeSerializedSize();
            int i = this.mode;
            if (i != 0) {
                return size + CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            return size;
        }

        public GatingConfig mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    int value = input.readInt32();
                    if (value == 0 || value == 1 || value == 2 || value == 3 || value == 4) {
                        this.mode = value;
                    }
                } else if (!WireFormatNano.parseUnknownField(input, tag)) {
                    return this;
                }
            }
        }

        public static GatingConfig parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (GatingConfig) MessageNano.mergeFrom(new GatingConfig(), data);
        }

        public static GatingConfig parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new GatingConfig().mergeFrom(input);
        }
    }

    public static final class NanoappLoadedOutput extends MessageNano {
        private static volatile NanoappLoadedOutput[] _emptyArray;

        public static NanoappLoadedOutput[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new NanoappLoadedOutput[0];
                    }
                }
            }
            return _emptyArray;
        }

        public NanoappLoadedOutput() {
            clear();
        }

        public NanoappLoadedOutput clear() {
            this.cachedSize = -1;
            return this;
        }

        public NanoappLoadedOutput mergeFrom(CodedInputByteBufferNano input) throws IOException {
            int tag;
            do {
                tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
            } while (WireFormatNano.parseUnknownField(input, tag));
            return this;
        }

        public static NanoappLoadedOutput parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (NanoappLoadedOutput) MessageNano.mergeFrom(new NanoappLoadedOutput(), data);
        }

        public static NanoappLoadedOutput parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new NanoappLoadedOutput().mergeFrom(input);
        }
    }

    public static final class PresenceEnable extends MessageNano {
        private static volatile PresenceEnable[] _emptyArray;
        public float debounce;
        public int granularity;
        public float radius;
        public int sensitivity;

        public static PresenceEnable[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new PresenceEnable[0];
                    }
                }
            }
            return _emptyArray;
        }

        public PresenceEnable() {
            clear();
        }

        public PresenceEnable clear() {
            this.radius = 0.0f;
            this.debounce = 0.0f;
            this.sensitivity = 0;
            this.granularity = 0;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (Float.floatToIntBits(this.radius) != Float.floatToIntBits(0.0f)) {
                output.writeFloat(1, this.radius);
            }
            if (Float.floatToIntBits(this.debounce) != Float.floatToIntBits(0.0f)) {
                output.writeFloat(2, this.debounce);
            }
            int i = this.sensitivity;
            if (i != 0) {
                output.writeInt32(3, i);
            }
            int i2 = this.granularity;
            if (i2 != 0) {
                output.writeInt32(4, i2);
            }
            super.writeTo(output);
        }

        
        public int computeSerializedSize() {
            int size = super.computeSerializedSize();
            if (Float.floatToIntBits(this.radius) != Float.floatToIntBits(0.0f)) {
                size += CodedOutputByteBufferNano.computeFloatSize(1, this.radius);
            }
            if (Float.floatToIntBits(this.debounce) != Float.floatToIntBits(0.0f)) {
                size += CodedOutputByteBufferNano.computeFloatSize(2, this.debounce);
            }
            int i = this.sensitivity;
            if (i != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(3, i);
            }
            int i2 = this.granularity;
            if (i2 != 0) {
                return size + CodedOutputByteBufferNano.computeInt32Size(4, i2);
            }
            return size;
        }

        public PresenceEnable mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 13) {
                    this.radius = input.readFloat();
                } else if (tag == 21) {
                    this.debounce = input.readFloat();
                } else if (tag == 24) {
                    int value = input.readInt32();
                    if (value == 0 || value == 1 || value == 2 || value == 3) {
                        this.sensitivity = value;
                    }
                } else if (tag == 32) {
                    int value2 = input.readInt32();
                    if (value2 == 0 || value2 == 1 || value2 == 2 || value2 == 3) {
                        this.granularity = value2;
                    }
                } else if (!WireFormatNano.parseUnknownField(input, tag)) {
                    return this;
                }
            }
        }

        public static PresenceEnable parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (PresenceEnable) MessageNano.mergeFrom(new PresenceEnable(), data);
        }

        public static PresenceEnable parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new PresenceEnable().mergeFrom(input);
        }
    }

    public static final class PresenceOutput extends MessageNano {
        private static volatile PresenceOutput[] _emptyArray;
        public float angle;
        public float axialVelocity;
        public boolean detected;
        public float distance;
        public float likelihood;

        public static PresenceOutput[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new PresenceOutput[0];
                    }
                }
            }
            return _emptyArray;
        }

        public PresenceOutput() {
            clear();
        }

        public PresenceOutput clear() {
            this.detected = false;
            this.likelihood = 0.0f;
            this.distance = 0.0f;
            this.axialVelocity = 0.0f;
            this.angle = 0.0f;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            boolean z = this.detected;
            if (z) {
                output.writeBool(1, z);
            }
            if (Float.floatToIntBits(this.likelihood) != Float.floatToIntBits(0.0f)) {
                output.writeFloat(2, this.likelihood);
            }
            if (Float.floatToIntBits(this.distance) != Float.floatToIntBits(0.0f)) {
                output.writeFloat(3, this.distance);
            }
            if (Float.floatToIntBits(this.axialVelocity) != Float.floatToIntBits(0.0f)) {
                output.writeFloat(4, this.axialVelocity);
            }
            if (Float.floatToIntBits(this.angle) != Float.floatToIntBits(0.0f)) {
                output.writeFloat(5, this.angle);
            }
            super.writeTo(output);
        }

        
        public int computeSerializedSize() {
            int size = super.computeSerializedSize();
            boolean z = this.detected;
            if (z) {
                size += CodedOutputByteBufferNano.computeBoolSize(1, z);
            }
            if (Float.floatToIntBits(this.likelihood) != Float.floatToIntBits(0.0f)) {
                size += CodedOutputByteBufferNano.computeFloatSize(2, this.likelihood);
            }
            if (Float.floatToIntBits(this.distance) != Float.floatToIntBits(0.0f)) {
                size += CodedOutputByteBufferNano.computeFloatSize(3, this.distance);
            }
            if (Float.floatToIntBits(this.axialVelocity) != Float.floatToIntBits(0.0f)) {
                size += CodedOutputByteBufferNano.computeFloatSize(4, this.axialVelocity);
            }
            if (Float.floatToIntBits(this.angle) != Float.floatToIntBits(0.0f)) {
                return size + CodedOutputByteBufferNano.computeFloatSize(5, this.angle);
            }
            return size;
        }

        public PresenceOutput mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    this.detected = input.readBool();
                } else if (tag == 21) {
                    this.likelihood = input.readFloat();
                } else if (tag == 29) {
                    this.distance = input.readFloat();
                } else if (tag == 37) {
                    this.axialVelocity = input.readFloat();
                } else if (tag == 45) {
                    this.angle = input.readFloat();
                } else if (!WireFormatNano.parseUnknownField(input, tag)) {
                    return this;
                }
            }
        }

        public static PresenceOutput parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (PresenceOutput) MessageNano.mergeFrom(new PresenceOutput(), data);
        }

        public static PresenceOutput parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new PresenceOutput().mergeFrom(input);
        }
    }

    public static final class ReachEnable extends MessageNano {
        private static volatile ReachEnable[] _emptyArray;
        public int granularity;
        public float radius;
        public int sensitivity;

        public static ReachEnable[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new ReachEnable[0];
                    }
                }
            }
            return _emptyArray;
        }

        public ReachEnable() {
            clear();
        }

        public ReachEnable clear() {
            this.radius = 0.0f;
            this.sensitivity = 0;
            this.granularity = 0;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (Float.floatToIntBits(this.radius) != Float.floatToIntBits(0.0f)) {
                output.writeFloat(1, this.radius);
            }
            int i = this.sensitivity;
            if (i != 0) {
                output.writeInt32(2, i);
            }
            int i2 = this.granularity;
            if (i2 != 0) {
                output.writeInt32(3, i2);
            }
            super.writeTo(output);
        }

        
        public int computeSerializedSize() {
            int size = super.computeSerializedSize();
            if (Float.floatToIntBits(this.radius) != Float.floatToIntBits(0.0f)) {
                size += CodedOutputByteBufferNano.computeFloatSize(1, this.radius);
            }
            int i = this.sensitivity;
            if (i != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(2, i);
            }
            int i2 = this.granularity;
            if (i2 != 0) {
                return size + CodedOutputByteBufferNano.computeInt32Size(3, i2);
            }
            return size;
        }

        public ReachEnable mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 13) {
                    this.radius = input.readFloat();
                } else if (tag == 16) {
                    int value = input.readInt32();
                    if (value == 0 || value == 1 || value == 2 || value == 3) {
                        this.sensitivity = value;
                    }
                } else if (tag == 24) {
                    int value2 = input.readInt32();
                    if (value2 == 0 || value2 == 1 || value2 == 2 || value2 == 3) {
                        this.granularity = value2;
                    }
                } else if (!WireFormatNano.parseUnknownField(input, tag)) {
                    return this;
                }
            }
        }

        public static ReachEnable parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (ReachEnable) MessageNano.mergeFrom(new ReachEnable(), data);
        }

        public static ReachEnable parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new ReachEnable().mergeFrom(input);
        }
    }

    public static final class ReachOutput extends MessageNano {
        private static volatile ReachOutput[] _emptyArray;
        public float[] angle;
        public float axialVelocity;
        public boolean detected;
        public float distance;
        public float likelihood;

        public static ReachOutput[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new ReachOutput[0];
                    }
                }
            }
            return _emptyArray;
        }

        public ReachOutput() {
            clear();
        }

        public ReachOutput clear() {
            this.detected = false;
            this.likelihood = 0.0f;
            this.distance = 0.0f;
            this.axialVelocity = 0.0f;
            this.angle = WireFormatNano.EMPTY_FLOAT_ARRAY;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            boolean z = this.detected;
            if (z) {
                output.writeBool(1, z);
            }
            if (Float.floatToIntBits(this.likelihood) != Float.floatToIntBits(0.0f)) {
                output.writeFloat(2, this.likelihood);
            }
            if (Float.floatToIntBits(this.distance) != Float.floatToIntBits(0.0f)) {
                output.writeFloat(3, this.distance);
            }
            if (Float.floatToIntBits(this.axialVelocity) != Float.floatToIntBits(0.0f)) {
                output.writeFloat(4, this.axialVelocity);
            }
            float[] fArr = this.angle;
            if (fArr != null && fArr.length > 0) {
                int i = 0;
                while (true) {
                    float[] fArr2 = this.angle;
                    if (i >= fArr2.length) {
                        break;
                    }
                    output.writeFloat(5, fArr2[i]);
                    i++;
                }
            }
            super.writeTo(output);
        }

        
        public int computeSerializedSize() {
            int size = super.computeSerializedSize();
            boolean z = this.detected;
            if (z) {
                size += CodedOutputByteBufferNano.computeBoolSize(1, z);
            }
            if (Float.floatToIntBits(this.likelihood) != Float.floatToIntBits(0.0f)) {
                size += CodedOutputByteBufferNano.computeFloatSize(2, this.likelihood);
            }
            if (Float.floatToIntBits(this.distance) != Float.floatToIntBits(0.0f)) {
                size += CodedOutputByteBufferNano.computeFloatSize(3, this.distance);
            }
            if (Float.floatToIntBits(this.axialVelocity) != Float.floatToIntBits(0.0f)) {
                size += CodedOutputByteBufferNano.computeFloatSize(4, this.axialVelocity);
            }
            float[] fArr = this.angle;
            if (fArr == null || fArr.length <= 0) {
                return size;
            }
            return size + (fArr.length * 4) + (fArr.length * 1);
        }

        public ReachOutput mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    this.detected = input.readBool();
                } else if (tag == 21) {
                    this.likelihood = input.readFloat();
                } else if (tag == 29) {
                    this.distance = input.readFloat();
                } else if (tag == 37) {
                    this.axialVelocity = input.readFloat();
                } else if (tag == 42) {
                    int length = input.readRawVarint32();
                    int limit = input.pushLimit(length);
                    int arrayLength = length / 4;
                    float[] fArr = this.angle;
                    int i = fArr == null ? 0 : fArr.length;
                    float[] newArray = new float[(i + arrayLength)];
                    if (i != 0) {
                        System.arraycopy(this.angle, 0, newArray, 0, i);
                    }
                    while (i < newArray.length) {
                        newArray[i] = input.readFloat();
                        i++;
                    }
                    this.angle = newArray;
                    input.popLimit(limit);
                } else if (tag == 45) {
                    int arrayLength2 = WireFormatNano.getRepeatedFieldArrayLength(input, 45);
                    float[] fArr2 = this.angle;
                    int i2 = fArr2 == null ? 0 : fArr2.length;
                    float[] newArray2 = new float[(i2 + arrayLength2)];
                    if (i2 != 0) {
                        System.arraycopy(this.angle, 0, newArray2, 0, i2);
                    }
                    while (i2 < newArray2.length - 1) {
                        newArray2[i2] = input.readFloat();
                        input.readTag();
                        i2++;
                    }
                    newArray2[i2] = input.readFloat();
                    this.angle = newArray2;
                } else if (!WireFormatNano.parseUnknownField(input, tag)) {
                    return this;
                }
            }
        }

        public static ReachOutput parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (ReachOutput) MessageNano.mergeFrom(new ReachOutput(), data);
        }

        public static ReachOutput parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new ReachOutput().mergeFrom(input);
        }
    }

    public static final class SetParam extends MessageNano {
        private static volatile SetParam[] _emptyArray;
        public int param;
        public int value;

        public static SetParam[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new SetParam[0];
                    }
                }
            }
            return _emptyArray;
        }

        public SetParam() {
            clear();
        }

        public SetParam clear() {
            this.param = 0;
            this.value = 0;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            int i = this.param;
            if (i != 0) {
                output.writeInt32(1, i);
            }
            int i2 = this.value;
            if (i2 != 0) {
                output.writeInt32(2, i2);
            }
            super.writeTo(output);
        }

        
        public int computeSerializedSize() {
            int size = super.computeSerializedSize();
            int i = this.param;
            if (i != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            int i2 = this.value;
            if (i2 != 0) {
                return size + CodedOutputByteBufferNano.computeInt32Size(2, i2);
            }
            return size;
        }

        public SetParam mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    int value2 = input.readInt32();
                    if (value2 == 0 || value2 == 1 || value2 == 2 || value2 == 3) {
                        this.param = value2;
                    }
                } else if (tag == 16) {
                    this.value = input.readInt32();
                } else if (!WireFormatNano.parseUnknownField(input, tag)) {
                    return this;
                }
            }
        }

        public static SetParam parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (SetParam) MessageNano.mergeFrom(new SetParam(), data);
        }

        public static SetParam parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new SetParam().mergeFrom(input);
        }
    }

    public static final class StatusOutput extends MessageNano {
        private static volatile StatusOutput[] _emptyArray;
        public int gatingReason;

        public static StatusOutput[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new StatusOutput[0];
                    }
                }
            }
            return _emptyArray;
        }

        public StatusOutput() {
            clear();
        }

        public StatusOutput clear() {
            this.gatingReason = 0;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            int i = this.gatingReason;
            if (i != 0) {
                output.writeInt32(1, i);
            }
            super.writeTo(output);
        }

        
        public int computeSerializedSize() {
            int size = super.computeSerializedSize();
            int i = this.gatingReason;
            if (i != 0) {
                return size + CodedOutputByteBufferNano.computeInt32Size(1, i);
            }
            return size;
        }

        public StatusOutput mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    int value = input.readInt32();
                    if (value == 0 || value == 1 || value == 2 || value == 3) {
                        this.gatingReason = value;
                    }
                } else if (!WireFormatNano.parseUnknownField(input, tag)) {
                    return this;
                }
            }
        }

        public static StatusOutput parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (StatusOutput) MessageNano.mergeFrom(new StatusOutput(), data);
        }

        public static StatusOutput parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new StatusOutput().mergeFrom(input);
        }
    }

    public static final class SwipeEnable extends MessageNano {
        private static volatile SwipeEnable[] _emptyArray;
        public int granularity;
        public float radius;
        public int sensitivity;

        public static SwipeEnable[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new SwipeEnable[0];
                    }
                }
            }
            return _emptyArray;
        }

        public SwipeEnable() {
            clear();
        }

        public SwipeEnable clear() {
            this.radius = 0.0f;
            this.sensitivity = 0;
            this.granularity = 0;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            if (Float.floatToIntBits(this.radius) != Float.floatToIntBits(0.0f)) {
                output.writeFloat(1, this.radius);
            }
            int i = this.sensitivity;
            if (i != 0) {
                output.writeInt32(2, i);
            }
            int i2 = this.granularity;
            if (i2 != 0) {
                output.writeInt32(3, i2);
            }
            super.writeTo(output);
        }

        
        public int computeSerializedSize() {
            int size = super.computeSerializedSize();
            if (Float.floatToIntBits(this.radius) != Float.floatToIntBits(0.0f)) {
                size += CodedOutputByteBufferNano.computeFloatSize(1, this.radius);
            }
            int i = this.sensitivity;
            if (i != 0) {
                size += CodedOutputByteBufferNano.computeInt32Size(2, i);
            }
            int i2 = this.granularity;
            if (i2 != 0) {
                return size + CodedOutputByteBufferNano.computeInt32Size(3, i2);
            }
            return size;
        }

        public SwipeEnable mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 13) {
                    this.radius = input.readFloat();
                } else if (tag == 16) {
                    int value = input.readInt32();
                    if (value == 0 || value == 1 || value == 2 || value == 3) {
                        this.sensitivity = value;
                    }
                } else if (tag == 24) {
                    int value2 = input.readInt32();
                    if (value2 == 0 || value2 == 1 || value2 == 2 || value2 == 3) {
                        this.granularity = value2;
                    }
                } else if (!WireFormatNano.parseUnknownField(input, tag)) {
                    return this;
                }
            }
        }

        public static SwipeEnable parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (SwipeEnable) MessageNano.mergeFrom(new SwipeEnable(), data);
        }

        public static SwipeEnable parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new SwipeEnable().mergeFrom(input);
        }
    }

    public static final class SwipeOutput extends MessageNano {
        private static volatile SwipeOutput[] _emptyArray;
        public float axialVelocity;
        public boolean detected;
        public int direction;
        public float distance;
        public float likelihood;

        public static SwipeOutput[] emptyArray() {
            if (_emptyArray == null) {
                synchronized (InternalNano.LAZY_INIT_LOCK) {
                    if (_emptyArray == null) {
                        _emptyArray = new SwipeOutput[0];
                    }
                }
            }
            return _emptyArray;
        }

        public SwipeOutput() {
            clear();
        }

        public SwipeOutput clear() {
            this.detected = false;
            this.likelihood = 0.0f;
            this.distance = 0.0f;
            this.axialVelocity = 0.0f;
            this.direction = 0;
            this.cachedSize = -1;
            return this;
        }

        public void writeTo(CodedOutputByteBufferNano output) throws IOException {
            boolean z = this.detected;
            if (z) {
                output.writeBool(1, z);
            }
            if (Float.floatToIntBits(this.likelihood) != Float.floatToIntBits(0.0f)) {
                output.writeFloat(2, this.likelihood);
            }
            if (Float.floatToIntBits(this.distance) != Float.floatToIntBits(0.0f)) {
                output.writeFloat(3, this.distance);
            }
            if (Float.floatToIntBits(this.axialVelocity) != Float.floatToIntBits(0.0f)) {
                output.writeFloat(4, this.axialVelocity);
            }
            int i = this.direction;
            if (i != 0) {
                output.writeInt32(5, i);
            }
            super.writeTo(output);
        }

        
        public int computeSerializedSize() {
            int size = super.computeSerializedSize();
            boolean z = this.detected;
            if (z) {
                size += CodedOutputByteBufferNano.computeBoolSize(1, z);
            }
            if (Float.floatToIntBits(this.likelihood) != Float.floatToIntBits(0.0f)) {
                size += CodedOutputByteBufferNano.computeFloatSize(2, this.likelihood);
            }
            if (Float.floatToIntBits(this.distance) != Float.floatToIntBits(0.0f)) {
                size += CodedOutputByteBufferNano.computeFloatSize(3, this.distance);
            }
            if (Float.floatToIntBits(this.axialVelocity) != Float.floatToIntBits(0.0f)) {
                size += CodedOutputByteBufferNano.computeFloatSize(4, this.axialVelocity);
            }
            int i = this.direction;
            if (i != 0) {
                return size + CodedOutputByteBufferNano.computeInt32Size(5, i);
            }
            return size;
        }

        public SwipeOutput mergeFrom(CodedInputByteBufferNano input) throws IOException {
            while (true) {
                int tag = input.readTag();
                if (tag == 0) {
                    return this;
                }
                if (tag == 8) {
                    this.detected = input.readBool();
                } else if (tag == 21) {
                    this.likelihood = input.readFloat();
                } else if (tag == 29) {
                    this.distance = input.readFloat();
                } else if (tag != 37) {
                    if (tag == 40) {
                        int value = input.readInt32();
                        switch (value) {
                            case 0:
                            case 1:
                            case 2:
                            case 3:
                            case 4:
                            case 5:
                            case 6:
                            case 7:
                            case 8:
                                this.direction = value;
                                break;
                        }
                    } else if (!WireFormatNano.parseUnknownField(input, tag)) {
                        return this;
                    }
                } else {
                    this.axialVelocity = input.readFloat();
                }
            }
        }

        public static SwipeOutput parseFrom(byte[] data) throws InvalidProtocolBufferNanoException {
            return (SwipeOutput) MessageNano.mergeFrom(new SwipeOutput(), data);
        }

        public static SwipeOutput parseFrom(CodedInputByteBufferNano input) throws IOException {
            return new SwipeOutput().mergeFrom(input);
        }
    }
}
