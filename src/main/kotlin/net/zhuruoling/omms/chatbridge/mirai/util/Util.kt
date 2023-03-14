package net.zhuruoling.omms.chatbridge.mirai.util

import com.google.gson.GsonBuilder
import net.zhuruoling.omms.chatbridge.mirai.network.broadcast.Broadcast
import net.zhuruoling.omms.chatbridge.mirai.network.broadcast.BroadcastType
import net.zhuruoling.omms.chatbridge.mirai.network.broadcast.UdpBroadcastSender
import java.io.File
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.streams.toList

val TARGET_CHAT = UdpBroadcastSender.createTarget("224.114.51.4", 10010)
var oldId = ""


enum class RPType {
    NICE, FUCK, NORMAL, NULL, ONE_HUNDRED
}

fun formatBroadcastIntoString(broadcast: Broadcast): String {
    return when(broadcast.broadcastType){
        BroadcastType.ADVANCEMENT -> {
            "${broadcast.displayName} has got advancement ${broadcast.content}"
        }
        BroadcastType.CHAT -> {
            "[MC] [${broadcast.displayName}]: ${broadcast.content}"
        }
        BroadcastType.DEATH -> {
            broadcast.content
        }
        else -> ""
    }
}

fun rpWithComment(rp: Int): String {
    val message = "你今日的人品是：$rp"
    return when (rpType(rp)) {
        RPType.FUCK -> message.plus(" 呜哇...")
        RPType.NORMAL -> message.plus(" 还行啦...")
        RPType.NICE -> message.plus(" 芜湖！")
        RPType.ONE_HUNDRED -> "你今日的人品是：100！！！！！！！100！！！！！！！100！！！！！！"
        else -> message
    }
}

fun rpType(rp: Int): RPType {
    if (rp in 0..30) {
        return RPType.FUCK
    }
    if (rp in 31..60) {
        return RPType.NORMAL
    }
    if (rp in 61 until 100) {
        return RPType.NICE
    }
    if (rp == 100) {
        return RPType.ONE_HUNDRED
    }
    return RPType.NULL
}

fun base64Encode(content: String): String? {
    return Base64.getEncoder().encodeToString(content.toByteArray(StandardCharsets.UTF_8))
}

fun getTimeCode(): String? {
    return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddhhmm"))
}

fun calculateToken(name: String): String {
    val tk = name + "O" + getTimeCode()
    return calculateTokenByDate(name.hashCode()).toString() + tk + base64Encode(tk)
}

fun calculateTokenByDate(password: Int): Int {
    val date = Date()
    val i = SimpleDateFormat("yyyyMMdd").format(date).toInt()
    val j = SimpleDateFormat("hhmm").format(date).toInt()
    val k = SimpleDateFormat("yyyyMMddhhmm").format(date).hashCode()
    return calculateToken(password, i, j, k)
}

fun calculateToken(password: Int, i: Int, j: Int, k: Int): Int {
    var token = 114514
    token += i
    token += j - k
    token = password xor token
    return token
}

fun getWorkingDir(): String? {
    val directory = File("")
    return directory.absolutePath
}

fun randomStringGen(len: Int): String {
    val ch = "abcdefghijklmnopqrstuvwxyzABCDEFGHIGKLMNOPQRSTUVWXYZ0123456789"
    val stringBuffer = StringBuilder()
    for (i in 0 until len) {
        val random = Random(System.nanoTime())
        val num = random.nextInt(62)
        stringBuffer.append(ch[num])
    }
    return stringBuffer.toString()
}

fun joinFilePaths(vararg pathComponent: String?): String {
    val paths: Array<out String?> = pathComponent
    val path = java.lang.StringBuilder()
    path.append(getWorkingDir())
    Arrays.stream(paths).toList().forEach { x ->
        path.append(File.separator)
        path.append(x)
    }
    return path.toString()
}

fun getType(raw: Class<*>, vararg args: Type) = object : ParameterizedType {
    override fun getRawType(): Type = raw
    override fun getActualTypeArguments(): Array<out Type> = args
    override fun getOwnerType(): Type? = null
}

fun toJson(obj: Any): String {
    return GsonBuilder().serializeNulls().create().toJson(obj)
}