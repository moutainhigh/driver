package com.easymi.common.faceCheck;

/**
 * @Copyright (C), 2012-2019, Sichuan Xiaoka Technology Co., Ltd.
 * @FileName: FaceListener
 * @Author: hufeng
 * @Date: 2019/11/13 下午1:57
 * @Description:
 * @History:
 */

import android.support.annotation.Nullable;

import com.arcsoft.face.FaceFeature;
import com.arcsoft.face.LivenessInfo;

/**
 * 人脸处理回调
 */
public interface FaceListener {
    /**
     * 当出现异常时执行
     *
     * @param e 异常信息
     */
    void onFail(Exception e);


    /**
     * 请求人脸特征后的回调
     *
     * @param faceFeature 人脸特征数据
     * @param requestId   请求码
     * @param errorCode   错误码
     */
    void onFaceFeatureInfoGet(@Nullable FaceFeature faceFeature, Integer requestId, Integer errorCode);

    /**
     * 请求活体检测后的回调
     *
     * @param livenessInfo 活体检测结果
     * @param requestId    请求码
     * @param errorCode    错误码
     */
    void onFaceLivenessInfoGet(@Nullable LivenessInfo livenessInfo, Integer requestId, Integer errorCode);
}