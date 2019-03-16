package com.jasonhhouse

import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import org.pmw.tinylog.Logger

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class Main {


    internal val seasons = intArrayOf(39, 74, 107, 139, 165, 194, 219, 253, 291)

    @Parameter(names = arrayOf("--folder", "-f"))
    internal var folder: String? = null

    private fun run() {
        var episode = 1
        var season = 1
        var count = 1

        val parent = File(folder!!)
        for (children in parent.listFiles()!!) {
            if (!children.isDirectory) {

                val newPath = Paths.get(folder!!, "Dragonball Z - S0" + season + "E" + (if (episode < 10) "0" else "") + episode + ".mkv")
                Logger.info("newPath:$newPath")
                Files.move(children.toPath(), newPath, StandardCopyOption.REPLACE_EXISTING)
                episode++
                if (count > seasons[season - 1]) {
                    season++
                    episode = 1
                }

                count++
            }
        }
    }

    companion object {

        fun main(args: Array<String>) {
            val main = Main()
            JCommander.newBuilder()
                    .addObject(main)
                    .build()
                    .parse(*args)
            try {
                main.run()
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
    }
}
