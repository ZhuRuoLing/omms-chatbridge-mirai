package net.zhuruoling.omms.controller.mirai

import net.mamoe.mirai.console.MiraiConsole
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.enable
import net.mamoe.mirai.console.plugin.PluginManager.INSTANCE.load
import net.mamoe.mirai.console.terminal.MiraiConsoleTerminalLoader
import net.mamoe.mirai.console.util.ConsoleExperimentalApi

@OptIn(ConsoleExperimentalApi::class)
suspend fun main() {
    MiraiConsoleTerminalLoader.startAsDaemon()

    //如果是Kotlin
    //PluginMain.load()
    //PluginMain.enable()
    //如果是Java
//    JavaPluginMain.INSTANCE.load()
//    JavaPluginMain.INSTANCE.enable()
    MiraiConsole.job.join()
}