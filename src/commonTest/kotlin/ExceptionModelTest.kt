import kotlin.test.Test
import kotlin.test.assertEquals


class ExceptionModelTest {

    @Test
    fun buildsExceptionModelForRealException() {
        val model = exceptionModelFor(
            """
                org.gradle.api.InvalidUserCodeException: Cannot access project ':' from project ':app'
                    at org.gradle.configurationcache.problems.DefaultProblemFactory_problem_1_build_diagnostics_1.get(DefaultProblemFactory.kt:79)
                    at org.gradle.configurationcache.problems.DefaultProblemFactory_problem_1_build_diagnostics_1.get(DefaultProblemFactory.kt:79)
                    at org.gradle.internal.problems.DefaultProblemDiagnosticsFactory_DefaultProblemStream.getImplicitThrowable(DefaultProblemDiagnosticsFactory.java:147)
                    at org.gradle.internal.problems.DefaultProblemDiagnosticsFactory_DefaultProblemStream.forCurrentCaller(DefaultProblemDiagnosticsFactory.java:136)
                    at org.gradle.configurationcache.problems.DefaultProblemFactory_problem_1.build(DefaultProblemFactory.kt:79)
                    at org.gradle.configurationcache.ProblemReportingCrossProjectModelAccess_ProblemReportingProject.reportCrossProjectAccessProblem(ProblemReportingCrossProjectModelAccess.kt:1119)
                    at org.gradle.configurationcache.ProblemReportingCrossProjectModelAccess_ProblemReportingProject.reportCrossProjectAccessProblem_default(ProblemReportingCrossProjectModelAccess.kt:1110)
                    at org.gradle.configurationcache.ProblemReportingCrossProjectModelAccess_ProblemReportingProject.onAccess(ProblemReportingCrossProjectModelAccess.kt:1043)
                    at org.gradle.configurationcache.ProblemReportingCrossProjectModelAccess_ProblemReportingProject.getExtensions(ProblemReportingCrossProjectModelAccess.kt:980)
                    at org.gradle.configurationcache.ProblemReportingCrossProjectModelAccess_ProblemReportingProject.getExtensions(ProblemReportingCrossProjectModelAccess.kt:157)
                    at com.intellij.gradle.toolingExtension.impl.util.javaPluginUtil.ExtensionJavaPluginAccessor.getJavaPluginExtension(ExtensionJavaPluginAccessor.java:18)
                    at com.intellij.gradle.toolingExtension.impl.util.javaPluginUtil.ExtensionJavaPluginAccessor.getSourceSetContainer(ExtensionJavaPluginAccessor.java:25)
                    at com.intellij.gradle.toolingExtension.impl.util.javaPluginUtil.JavaPluginUtil.getSourceSetContainer(JavaPluginUtil.java:33)
                    at com.intellij.gradle.toolingExtension.impl.util.javaPluginUtil.JavaPluginUtil_getSourceSetContainer_1.call(Unknown Source)
                    at org.codehaus.groovy.runtime.callsite.CallSiteArray.defaultCall(CallSiteArray.java:47)
                    at org.codehaus.groovy.runtime.callsite.AbstractCallSite.call(AbstractCallSite.java:125)
                    at org.codehaus.groovy.runtime.callsite.AbstractCallSite.call(AbstractCallSite.java:139)
                    at com.intellij.gradle.toolingExtension.impl.model.sourceSetModel.GradleSourceSetModelBuilder.getSourceSets(GradleSourceSetModelBuilder.groovy:253)
                    at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
                    at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
                    at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
                    at java.base/java.lang.reflect.Method.invoke(Method.java:566)
                    at org.codehaus.groovy.reflection.CachedMethod.invoke(CachedMethod.java:107)
                    at org.codehaus.groovy.runtime.callsite.StaticMetaMethodSite_StaticMetaMethodSiteNoUnwrapNoCoerce.invoke(StaticMetaMethodSite.java:149)
                    at org.codehaus.groovy.runtime.callsite.StaticMetaMethodSite.callStatic(StaticMetaMethodSite.java:100)
                    at org.codehaus.groovy.runtime.callsite.AbstractCallSite.callStatic(AbstractCallSite.java:240)
                    at com.intellij.gradle.toolingExtension.impl.model.sourceSetModel.GradleSourceSetModelBuilder.buildAll(GradleSourceSetModelBuilder.groovy:64)
                    at com.intellij.gradle.toolingExtension.impl.modelBuilder.ExtraModelBuilder.buildServiceModel(ExtraModelBuilder.java:128)
                    at com.intellij.gradle.toolingExtension.impl.modelBuilder.ExtraModelBuilder.buildModel(ExtraModelBuilder.java:97)
                    at com.intellij.gradle.toolingExtension.impl.modelBuilder.ExtraModelBuilder.buildAll(ExtraModelBuilder.java:70)
            """.trimIndent())

        assertEquals(
            actual = model.message,
            expected = "org.gradle.api.InvalidUserCodeException: Cannot access project ':' from project ':app'"
        )

        assertEquals(
            actual = model.stackTraceParts,
            expected = listOf(
                StackTracePart(
                    isInternal = true,
                    """
                        at org.gradle.configurationcache.problems.DefaultProblemFactory_problem_1_build_diagnostics_1.get(DefaultProblemFactory.kt:79)
                        at org.gradle.configurationcache.problems.DefaultProblemFactory_problem_1_build_diagnostics_1.get(DefaultProblemFactory.kt:79)
                        at org.gradle.internal.problems.DefaultProblemDiagnosticsFactory_DefaultProblemStream.getImplicitThrowable(DefaultProblemDiagnosticsFactory.java:147)
                        at org.gradle.internal.problems.DefaultProblemDiagnosticsFactory_DefaultProblemStream.forCurrentCaller(DefaultProblemDiagnosticsFactory.java:136)
                        at org.gradle.configurationcache.problems.DefaultProblemFactory_problem_1.build(DefaultProblemFactory.kt:79)
                        at org.gradle.configurationcache.ProblemReportingCrossProjectModelAccess_ProblemReportingProject.reportCrossProjectAccessProblem(ProblemReportingCrossProjectModelAccess.kt:1119)
                        at org.gradle.configurationcache.ProblemReportingCrossProjectModelAccess_ProblemReportingProject.reportCrossProjectAccessProblem_default(ProblemReportingCrossProjectModelAccess.kt:1110)
                        at org.gradle.configurationcache.ProblemReportingCrossProjectModelAccess_ProblemReportingProject.onAccess(ProblemReportingCrossProjectModelAccess.kt:1043)
                        at org.gradle.configurationcache.ProblemReportingCrossProjectModelAccess_ProblemReportingProject.getExtensions(ProblemReportingCrossProjectModelAccess.kt:980)
                        at org.gradle.configurationcache.ProblemReportingCrossProjectModelAccess_ProblemReportingProject.getExtensions(ProblemReportingCrossProjectModelAccess.kt:157)
                    """.trimIndent().lines()
                ),
                StackTracePart(
                    isInternal = false,
                    """
                        at com.intellij.gradle.toolingExtension.impl.util.javaPluginUtil.ExtensionJavaPluginAccessor.getJavaPluginExtension(ExtensionJavaPluginAccessor.java:18)
                        at com.intellij.gradle.toolingExtension.impl.util.javaPluginUtil.ExtensionJavaPluginAccessor.getSourceSetContainer(ExtensionJavaPluginAccessor.java:25)
                        at com.intellij.gradle.toolingExtension.impl.util.javaPluginUtil.JavaPluginUtil.getSourceSetContainer(JavaPluginUtil.java:33)
                        at com.intellij.gradle.toolingExtension.impl.util.javaPluginUtil.JavaPluginUtil_getSourceSetContainer_1.call(Unknown Source)
                    """.trimIndent().lines()
                ),
                StackTracePart(
                    isInternal = true,
                    """
                        at org.codehaus.groovy.runtime.callsite.CallSiteArray.defaultCall(CallSiteArray.java:47)
                        at org.codehaus.groovy.runtime.callsite.AbstractCallSite.call(AbstractCallSite.java:125)
                        at org.codehaus.groovy.runtime.callsite.AbstractCallSite.call(AbstractCallSite.java:139)
                    """.trimIndent().lines()
                ),
                StackTracePart(
                    isInternal = false,
                    """
                        at com.intellij.gradle.toolingExtension.impl.model.sourceSetModel.GradleSourceSetModelBuilder.getSourceSets(GradleSourceSetModelBuilder.groovy:253)
                    """.trimIndent().lines()
                ),
                StackTracePart(
                    isInternal = true,
                    """
                        at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke0(Native Method)
                        at java.base/jdk.internal.reflect.NativeMethodAccessorImpl.invoke(NativeMethodAccessorImpl.java:62)
                        at java.base/jdk.internal.reflect.DelegatingMethodAccessorImpl.invoke(DelegatingMethodAccessorImpl.java:43)
                        at java.base/java.lang.reflect.Method.invoke(Method.java:566)
                        at org.codehaus.groovy.reflection.CachedMethod.invoke(CachedMethod.java:107)
                        at org.codehaus.groovy.runtime.callsite.StaticMetaMethodSite_StaticMetaMethodSiteNoUnwrapNoCoerce.invoke(StaticMetaMethodSite.java:149)
                        at org.codehaus.groovy.runtime.callsite.StaticMetaMethodSite.callStatic(StaticMetaMethodSite.java:100)
                        at org.codehaus.groovy.runtime.callsite.AbstractCallSite.callStatic(AbstractCallSite.java:240)
                    """.trimIndent().lines()
                ),
                StackTracePart(
                    isInternal = false,
                    """
                        at com.intellij.gradle.toolingExtension.impl.model.sourceSetModel.GradleSourceSetModelBuilder.buildAll(GradleSourceSetModelBuilder.groovy:64)
                        at com.intellij.gradle.toolingExtension.impl.modelBuilder.ExtraModelBuilder.buildServiceModel(ExtraModelBuilder.java:128)
                        at com.intellij.gradle.toolingExtension.impl.modelBuilder.ExtraModelBuilder.buildModel(ExtraModelBuilder.java:97)
                        at com.intellij.gradle.toolingExtension.impl.modelBuilder.ExtraModelBuilder.buildAll(ExtraModelBuilder.java:70)
                    """.trimIndent().lines()
                )
            )
        )
    }

    @Test
    fun buildsModelWithMultilineMessage() {
        val model = exceptionModelFor(
            """
                org.example.OopsException: line one,
                line two
                    at org.example.reporting.Report(Unknown Source)
            """.trimIndent()
        )

        assertEquals(model.message, "org.example.OopsException: line one,\nline two")
        assertEquals(model.stackTraceParts.size, 1)
        assertEquals(model.stackTraceParts[0], StackTracePart(false, listOf("at org.example.reporting.Report(Unknown Source)")))
    }

    @Test
    fun treatsNonExceptionAsMessage() {
        val model = exceptionModelFor(
            """
                some text
                that does not look like exception
            """.trimIndent()
        )

        assertEquals(model.message, "some text\nthat does not look like exception")
        assertEquals(model.stackTraceParts.size, 0)
    }
}
