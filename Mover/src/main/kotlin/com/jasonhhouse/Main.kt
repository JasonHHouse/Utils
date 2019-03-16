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

    @Parameter(names = ["--parents", "-p"])
    internal var parents: List<String>? = null

    private val deleteExtensionTypes = HashSet<String>()

    private val videoExtensionTypes = HashSet<String>()

    fun prep() {
        deleteExtensionTypes.add("nfo")
        deleteExtensionTypes.add("txt")
        deleteExtensionTypes.add("srt")
        deleteExtensionTypes.add("jpg")
        deleteExtensionTypes.add("exe")

        videoExtensionTypes.add("mkv")
        videoExtensionTypes.add("avi")
        videoExtensionTypes.add("mp4")
        videoExtensionTypes.add("mov")
    }

    private fun findMovie(child: File ) :Boolean {
        child.listFiles()!!.forEach { file ->
            val extension = FilenameUtils.getExtension(file.absolutePath)
            if(videoExtensionTypes.contains(extension)) {
                return true
            }
        }
        return false
    }

    private fun moveMovieAndDeleteFolder(child : File, parent : String) {
        child.listFiles()!!.forEach { file ->

            if (!file.isDirectory) {
                if(deleteExtensionTypes.contains(FilenameUtils.getExtension(file.absolutePath))) {
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
    }

    fun run() {
        parents!!.forEach { parent ->
            val topFolder = File(parent)
            val children = topFolder.listFiles()
            if (children != null) {
                for (child in children) {
                    if (child.isDirectory) {

                        if(!findMovie(child)) {
                            println("No movie found in " + child.absolutePath + ". Skipping folder.")
                            //Skip folders without a movie inside
                            continue
                        }

                        moveMovieAndDeleteFolder(child, parent)

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
