package com.jasonhhouse

import com.beust.jcommander.JCommander
import com.beust.jcommander.Parameter
import org.apache.commons.io.FileUtils
import org.apache.commons.io.FilenameUtils

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

fun main(args: Array<String>) {
    val main = Main()
    JCommander.newBuilder()
            .addObject(main)
            .build()
            .parse(*args)
    try {
        main.prep()
        main.run()
    } catch (e: IOException) {
        e.printStackTrace()
    }

}

class Main {

    @Parameter(names = arrayOf("--parents", "-p"))
    internal var parents: List<String>? = null

    val extensionTypes = HashSet<String>()


    fun prep() {
        extensionTypes.add("nfo")
        extensionTypes.add("txt")
        extensionTypes.add("srt")
        extensionTypes.add("jpg")
        extensionTypes.add("exe")
    }

    fun run() {
        parents!!.forEach { parent ->
            val topFolder = File(parent)
            val children = topFolder.listFiles()
            if (children != null) {
                for (child in children) {
                    if (child.isDirectory) {
                        child.listFiles()!!.forEach { file ->

                            if (!file.isDirectory) {
                                if(extensionTypes.contains(FilenameUtils.getExtension(file.absolutePath))) {
                                    Files.delete(file.toPath())
                                } else {
                                    val newPath = Paths.get(parent + File.separator + child.name + "." + FilenameUtils.getExtension(file.absolutePath))
                                    Files.move(file.toPath(), newPath, StandardCopyOption.REPLACE_EXISTING)
                                }
                            } else {
                                //Delete sub folders
                                FileUtils.deleteDirectory(file)
                            }
                        }

                        //Delete the folder
                        if(child.listFiles().isEmpty()) {
                            Files.delete(child.toPath())
                        }
                    }
                }
            }
        }

    }

}
