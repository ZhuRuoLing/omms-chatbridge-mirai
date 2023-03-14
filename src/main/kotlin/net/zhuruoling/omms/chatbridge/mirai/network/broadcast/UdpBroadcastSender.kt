package net.zhuruoling.omms.chatbridge.mirai.network.broadcast

import org.slf4j.LoggerFactory
import java.io.IOException
import java.net.*
import java.nio.charset.StandardCharsets
import java.util.concurrent.ConcurrentHashMap

data class Target(val address:String, val port:Int)

class UdpBroadcastSender : Thread() {
    private val logger = LoggerFactory.getLogger("UdpBroadcastSender")
    var isStopped = false
    private val queue = ConcurrentHashMap<Target, ByteArray>()
    private val multicastSocketCache = HashMap<Target, MulticastSocket?>()

    init {
        name = "UdpBroadcastSender#" + this.id
    }

    override fun run() {
        logger.info("Starting UdpBroadcastSender.")
        while (!isStopped) {
            try{
                if (!queue.isEmpty()) {
                    queue.forEach { (target: Target, content: ByteArray) -> send(target, content) }
                }
                sleep(10)
            }catch (e:Exception){
                e.printStackTrace()
            }
        }
        logger.info("Stopped!")
    }

    fun addToQueue(target: Target, content: String) {
        queue[target] = content.toByteArray(StandardCharsets.UTF_8)
    }

    private fun send(target: Target, content: ByteArray) {
        queue.remove(target, content)
        val socket: MulticastSocket?
        try {
            if (multicastSocketCache.containsKey(target)) {
                socket = multicastSocketCache[target]
            } else {
                socket = createMulticastSocket(target.address, target.port)
                multicastSocketCache[target] = socket
            }
            val packet = DatagramPacket(
                content,
                content.size,
                InetSocketAddress(target.address, target.port).address,
                target.port
            )
            socket!!.send(packet)
        } catch (e: Exception) {
            logger.error("Cannot send UDP Broadcast.\n\tTarget=$target\n\tContent=$content")
        }
    }

    companion object {
        @Throws(IOException::class)
        private fun createMulticastSocket(addr: String?, port: Int): MulticastSocket {
            val inetAddress: InetAddress = InetAddress.getByName(addr)
            val socket = MulticastSocket(port)
            socket.joinGroup(InetSocketAddress(inetAddress, port), NetworkInterface.getByInetAddress(inetAddress))
            return socket
        }

        fun createTarget(s: String, i: Int): Target {
            return Target(s,i)
        }
    }
}