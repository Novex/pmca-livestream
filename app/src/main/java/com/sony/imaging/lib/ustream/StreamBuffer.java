package com.sony.imaging.lib.ustream;

import com.sony.scalar.hardware.DSP;
import com.sony.scalar.hardware.DeviceBuffer;

public class StreamBuffer {
    public int AudioESBufferOffset;
    public int AudioESBufferSize;
    public int AudioInfoOffset;
    public int AudioInfoSize;
    public int StatusAreaMCodecOffset;
    public int StatusAreaMCodecSize;
    public int StatusAreaScalarOffset;
    public int StatusAreaScalarSize;
    public int VideoESBufferOffset;
    public int VideoESBufferSize;
    public int VideoInfoOffset;
    public int VideoInfoSize;
    DSP mDSP = DSP.createProcessor("sony-di-dsp");
    DeviceBuffer mWork = this.mDSP.directCreateBuffer(605 /* random const? */);

    private native void _calculate(int i, int i2, int i3, int i4, int i5, int i6);

    static {
        LibraryLoader.init();
    }

    public StreamBuffer(int size) {
    }

    public void calculate() {
        _calculate(this.StatusAreaScalarSize, this.StatusAreaMCodecSize, this.VideoInfoSize, this.AudioInfoSize, this.VideoESBufferSize, this.AudioESBufferSize);
    }

    public DeviceBuffer getBuffer() {
        return this.mWork;
    }

    /* Access modifiers changed, original: 0000 */
    public int getAddr() {
        return this.mDSP.getPropertyAsInt(this.mWork, "memory-address");
    }

    /* Access modifiers changed, original: 0000 */
    public int getSize() {
        return this.mDSP.getPropertyAsInt(this.mWork, "memory-size");
    }

    public void release() {
        this.mWork.release();
        this.mDSP.release();
    }
}
