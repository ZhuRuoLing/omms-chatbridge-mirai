package net.zhuruoling.omms.chatbridge.mirai

import java.io.File
import java.io.FileNotFoundException
import java.io.FileReader
import java.nio.file.Files
import java.util.*
import kotlin.io.path.Path

object Config {
    var botId: Long = 0L
    var groups = mutableListOf<Long>()
    private val defaultProperties: Properties

    init {
        val properties = Properties()
        properties.setProperty("botId", "123456")
        properties.setProperty("groups", "12345678")
        defaultProperties = properties
    }

    @JvmStatic
    fun createConfig(configFolder: File) {
        val config = configFolder.absolutePath + "\\config.properties"
        Files.createFile(Path(config))
        Files.write(
            Path(config), """
                botId=123456
                groups=12345678
            """.trimIndent().encodeToByteArray()
        )
    }

    @JvmStatic//opString
    fun readConfig(configPath: String) {
        if (!Files.exists(Path(configPath))) {
            throw FileNotFoundException(configPath)
        }
        val properties: Properties = defaultProperties
        properties.load(FileReader(configPath))
        botId = properties.getProperty("botId").toLong()
        val groupString = properties.getProperty("groups")
        if ("," in groupString) {
            val groups = groupString.split(",").toMutableList()
            groups.forEach {
                Config.groups.add(it.toLong())
            }
        } else {
            groups.add(groupString.toLong())
        }
    }
}