package net.zhuruoling.omms.chatbridge.mirai

import com.google.gson.GsonBuilder
import kotlinx.coroutines.runBlocking
import net.mamoe.mirai.Bot
import net.mamoe.mirai.console.plugin.jvm.JvmPluginDescription
import net.mamoe.mirai.console.plugin.jvm.KotlinPlugin
import net.mamoe.mirai.contact.Group
import net.mamoe.mirai.event.events.BotOnlineEvent
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.event.globalEventChannel
import net.mamoe.mirai.utils.info
import net.zhuruoling.omms.chatbridge.mirai.network.broadcast.Broadcast
import net.zhuruoling.omms.chatbridge.mirai.network.broadcast.BroadcastType
import net.zhuruoling.omms.chatbridge.mirai.network.broadcast.UdpBroadcastSender
import net.zhuruoling.omms.chatbridge.mirai.network.broadcast.UdpReceiver
import net.zhuruoling.omms.chatbridge.mirai.util.TARGET_CHAT
import net.zhuruoling.omms.chatbridge.mirai.util.formatBroadcastIntoString
import net.zhuruoling.omms.chatbridge.mirai.util.randomStringGen
import java.io.File


object PluginMain : KotlinPlugin(
    JvmPluginDescription(
        id = "net.zhuruoling.omms.chatbridge.mirai",
        name = "omms-chatbridge",
        version = "0.0.1"
    ) { author("ZhuRuoLing") }) {

    private var udpBroadcastSender = UdpBroadcastSender()
    private var udpReceiver: UdpReceiver? = null
    private val gson = GsonBuilder().serializeNulls().create()

    private fun checkUdpService(bot: Bot) {
        if (udpReceiver == null) {
            udpReceiver = UdpReceiver(bot, TARGET_CHAT) { b, s ->
                if (bot.isOnline) {
                    val broadcast = gson.fromJson(s, Broadcast::class.java)
                    if (broadcast.broadcastType != BroadcastType.QQ) {
                        val groups = mutableListOf<Group>()
                        for (group in Config.groups) {
                            groups.add(b.getGroup(group) ?: continue)
                        }
                        for (group in groups) {
                            runBlocking {
                                val message = formatBroadcastIntoString(broadcast)
                                if (message.isNotEmpty())
                                    group.sendMessage(message)
                            }
                        }
                    }
                }
            }
            udpReceiver!!.start()
        } else {
            if (!udpReceiver?.isAlive!!) {
                udpReceiver!!.start()
            }
        }

        if (!udpBroadcastSender.isAlive) {
            udpBroadcastSender = UdpBroadcastSender()
            udpBroadcastSender.start()
        }
    }

    override fun onEnable() {
        val config = configFolder.absolutePath + "\\config.properties"
        if (!File(config).exists()) {
            Config.createConfig(this.configFolder)
            Config.readConfig(config)
        } else {
            Config.readConfig(config)
        }
        logger.info { "Registering events." }
        logger.info("Config: ${Config.botId}@${Config.groups}")
        val eventChannel = globalEventChannel(this.coroutineContext)
        eventChannel.subscribeAlways<BotOnlineEvent> {
            if (this.bot.id == Config.botId) {
                checkUdpService(this.bot)
            }
        }
        eventChannel.subscribeAlways<GroupMessageEvent> {
            if (this.bot.id == Config.botId) {
                if (it.group.id in Config.groups) {
                    if (it.message.isEmpty()) return@subscribeAlways
                    checkUdpService(this.bot)
                    this@PluginMain.udpBroadcastSender.addToQueue(
                        TARGET_CHAT,
                        gson.toJson(
                            Broadcast(
                                this.senderName,
                                it.message.contentToString(),
                                randomStringGen(16),
                                BroadcastType.QQ
                            )
                        )
                    )
                }
            }
        }
        logger.info("OMMS Chatbridge Loaded!")
    }
}
