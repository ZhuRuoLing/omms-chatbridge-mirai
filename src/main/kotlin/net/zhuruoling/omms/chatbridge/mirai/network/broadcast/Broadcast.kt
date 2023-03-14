package net.zhuruoling.omms.chatbridge.mirai.network.broadcast


class Broadcast(var displayName: String, var content: String, var id: String, var broadcastType: BroadcastType) {
    var server = "OMMS Mirai QQ Bot"
}
enum class BroadcastType {
    DEATH, ADVANCEMENT, CHAT, QQ
}
