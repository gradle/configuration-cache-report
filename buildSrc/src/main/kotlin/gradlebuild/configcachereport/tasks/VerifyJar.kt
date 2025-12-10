package gradlebuild.configcachereport.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.file.ArchiveOperations
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import javax.inject.Inject


@CacheableTask
abstract class VerifyJar : DefaultTask() {

    @get:InputFile
    @get:PathSensitive(PathSensitivity.NONE)
    abstract val jarFile: RegularFileProperty

    @get:OutputFile
    abstract val receiptFile: RegularFileProperty

    @get:Inject
    protected abstract val archiveOps: ArchiveOperations

    @TaskAction
    fun action() {
        val jarFiles = archiveOps.zipTree(jarFile.get().asFile).files
        require(jarFiles.isNotEmpty()) {
            "The JAR is empty!"
        }
        require(jarFiles.size == 2) {
            "Expected two files in the JAR, but got ${jarFiles.size}!"
        }
        val expectedFilenames = listOf("MANIFEST.MF", "configuration-cache-report.html")
        val actualFilenames = jarFiles.map { it.name }
        require(actualFilenames == expectedFilenames) {
            "Expected file names $expectedFilenames but got $actualFilenames"
        }
        receiptFile.get().asFile.writeText("OK")
    }
}
