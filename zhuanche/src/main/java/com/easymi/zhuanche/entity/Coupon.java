package com.easymi.zhuanche.entity;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Created by liuzihao on 2018/4/4.
 */

public class Coupon implements Serializable{
    @SerializedName("couponType")
    public int couponType;// 1  折扣卷     2  抵扣卷

    public double discount;//折扣卷

    public double deductible;//抵扣卷
    //满减额
    @SerializedName("couponTypeVoucherSubtractionMoney")
    public double SubtractionMoney;
    //封顶额
    @SerializedName("couponTypeVoucherTopMoney")
    public double TopMoney;

//    /**
//     * 计算优惠费用
//     *
//     * @param fee           优惠前金额
//     * @return 优惠金额
//     */
//    public  CouponFeeResult couponFee(double fee) {
//        BigDecimal couponFee = BigDecimal.ZERO;
//
//        if (fee >= SubtractionMoney) {
//            // 金额大于满减金额时，才优惠
//            switch (couponType) {
//                case 1:
//                    // 比例折扣
//                    Double discount = salesCouponVo.getDiscount();
//                    couponFee = fee.multiply(BigDecimal.valueOf(100).subtract(BigDecimal.valueOf(discount)).divide(BigDecimal.valueOf(100), 2, BigDecimal.ROUND_DOWN)).setScale(2, BigDecimal.ROUND_DOWN);
//                    // 折扣上限金额
//                    BigDecimal couponTypeVoucherTopMoney = salesCouponVo.getCouponTypeVoucherTopMoney();
//                    if (couponFee.compareTo(couponTypeVoucherTopMoney) == 1) {
//                        // 优惠费用超过上限金额， 那么优惠金额 = 上限
//                        couponFee = couponTypeVoucherTopMoney;
//                    }
//                    break;
//                case 2:
//                    // 金额抵扣
//                    BigDecimal deductible = salesCouponVo.getDeductible();
//                    if (fee.compareTo(deductible) == 1) {
//                        // 预算金额大于抵扣金额
//                        couponFee = fee.subtract(deductible).setScale(2, BigDecimal.ROUND_DOWN);
//                    } else {
//                        couponFee = fee;
//                    }
//                    break;
//                default:
//                    return CouponFeeResult.error(ErrorCode.COUPON_TYPE_ERROR);
//            }
//        }
//        CouponFeeResult couponFeeResult = new CouponFeeResult();
//        couponFeeResult.setCouponFee(couponFee);
//        couponFeeResult.setCode(Integer.parseInt(ErrorCode.SUCCESS));
//        return couponFeeResult;
//    }
}
