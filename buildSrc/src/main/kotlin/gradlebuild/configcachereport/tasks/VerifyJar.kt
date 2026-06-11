package gradlebuild.configcachereport.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import java.util.zip.ZipFile


@CacheableTask
abstract class VerifyJar : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val jarFile: RegularFileProperty

    @get:OutputFile
    abstract val receiptFile: RegularFileProperty

    @TaskAction
    fun action() {
        val entries = ZipFile(jarFile.get().asFile).use { zip ->
            zip.entries().asSequence().filter { !it.isDirectory }.map { it.name }.toList()
        }
        val expectedEntries = listOf(
            "META-INF/MANIFEST.MF",
            "org/gradle/internal/configuration/problems/configuration-cache-report.html",
        )
        require(entries.sorted() == expectedEntries.sorted()) {
            "Expected JAR entries $expectedEntries but got $entries"
        }
        receiptFile.get().asFile.writeText("OK")
    }
}
