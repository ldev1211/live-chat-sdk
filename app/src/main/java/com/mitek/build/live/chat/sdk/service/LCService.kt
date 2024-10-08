package com.mitek.build.live.chat.sdk.service

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.mitek.build.live.chat.sdk.core.LiveChatSDK
import com.mitek.build.live.chat.sdk.core.LiveChatSDK.observingMessage
import com.mitek.build.live.chat.sdk.model.chat.LCMessage
import com.mitek.build.live.chat.sdk.model.chat.LCSender
import com.mitek.build.live.chat.sdk.util.LCLog
import com.mitek.build.live.chat.sdk.util.LCParseUtils
import org.json.JSONObject

open class LCService : FirebaseMessagingService() {
    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)
        val data = message.data
        LCLog.logI("SDK receive fcm: $data")
        if(data["software"] == "live-chat-sdk"){
            if(!LiveChatSDK.isReceiveFromFCM()) return
            val from = JSONObject(data["sender"] as String)
            if(from.getString("id") == LiveChatSDK.getLCSession().visitorJid){
                return
            }
            val rawContent = JSONObject(data["content"] as String)
            val lcContent = LCParseUtils.parseLCContentFrom(rawContent,true)
            val lcMessage = LCMessage(
                data["id"]!!.toInt(),
                null,
                lcContent,
                LCSender(from.getString("id"),from.getString("name")),
                data["created_at"]!!,
            )
            observingMessage(lcMessage)
        }
    }
}
