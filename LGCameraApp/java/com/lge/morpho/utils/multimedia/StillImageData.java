package com.lge.morpho.utils.multimedia;

import com.lge.morpho.utils.NativeMemoryAllocator;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class StillImageData {
    public int mId;
    public ByteBuffer mImage;
    public ByteBuffer mMotionData;
    public int mPreviewCnt;

    public StillImageData(int image_id, int preview_cnt, byte[] still_image, byte[] motion_data) {
        this.mId = image_id;
        this.mPreviewCnt = preview_cnt;
        this.mImage = createByteBuffer(still_image);
        this.mMotionData = createByteBuffer(motion_data);
    }

    private ByteBuffer createByteBuffer(byte[] src) {
        ByteBuffer bb = NativeMemoryAllocator.allocateBuffer(src.length);
        bb.order(ByteOrder.nativeOrder());
        bb.position(0);
        bb.put(src);
        bb.position(0);
        return bb;
    }
}
