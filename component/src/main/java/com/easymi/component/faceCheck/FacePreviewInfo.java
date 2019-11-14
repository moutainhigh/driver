package com.easymi.component.faceCheck;

import com.arcsoft.face.FaceInfo;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: FacePreviewInfo
 * @Author: hufeng
 * @Date: 2019/11/13 下午2:59
 * @Description:
 * @History:
 */
public class FacePreviewInfo {
    private FaceInfo faceInfo;
    private int trackId;

    public FacePreviewInfo(FaceInfo faceInfo, int trackId) {
        this.faceInfo = faceInfo;
        this.trackId = trackId;
    }

    public FaceInfo getFaceInfo() {
        return faceInfo;
    }

    public void setFaceInfo(FaceInfo faceInfo) {
        this.faceInfo = faceInfo;
    }


    public int getTrackId() {
        return trackId;
    }

    public void setTrackId(int trackId) {
        this.trackId = trackId;
    }
}
