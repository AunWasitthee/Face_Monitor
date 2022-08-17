package com.fg.mdp.facemonitor.model

import java.io.Serializable

data class MqttMsgModel(
    val empid: String,
    val pic_path: String,
    val timepstamp: String
):Serializable