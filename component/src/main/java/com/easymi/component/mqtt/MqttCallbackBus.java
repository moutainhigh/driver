package com.easymi.component.mqtt;

import com.easymi.component.utils.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * 使用EventBus分发事件
 *
 * @author LichFaker on 16/3/25.
 * @Email lichfaker@gmail.com
 */
public class MqttCallbackBus implements MqttCallback {

    @Override
    public void connectionLost(Throwable cause) {
        Log.e("MqttCallbackBus","connectionLost-->"+cause.getMessage());
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) {
//        Logger.d(topic + "====" + message.toString());
//        EventBus.getDefault().post(message);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }

}
