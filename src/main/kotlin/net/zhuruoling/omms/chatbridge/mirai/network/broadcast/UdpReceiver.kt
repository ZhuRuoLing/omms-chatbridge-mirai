package net.zhuruoling.omms.chatbridge.mirai.network.broadcast

import net.mamoe.mirai.Bot
import org.slf4j.LoggerFactory
import java.net.*
import java.nio.charset.StandardCharsets
import java.util.function.BiConsumer

class UdpReceiver(server: Bot, target: Target?, function: BiConsumer<Bot, String>?) : Thread() {
    private var function: BiConsumer<Bot, String>? = null
    private var target: Target? = null
    private val server: Bot

    init {
        name = "UdpBroadcastReceiver#$id"
        this.server = server
        this.function = function
        this.target = target
    }

    override fun run() {
        try {
            val port = target!!.port
            val address = target!!.address // 224.114.51.4:10086
            val inetAddress: InetAddress = InetAddress.getByName(address)
            val socket = MulticastSocket(port)
            logger.info("Started Broadcast Receiver at $address:$port")
            socket.joinGroup(InetSocketAddress(inetAddress, port), NetworkInterface.getByInetAddress(inetAddress))
            while (true) {
                try {
                    val packet = DatagramPacket(ByteArray(1024), 1024)
                    socket.receive(packet)
                    val msg = String(
                        packet.data, packet.offset,
                        packet.length, StandardCharsets.UTF_8
                    )
                    function!!.accept(server, msg)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger("UdpBroadcastReceiver")
    }
}