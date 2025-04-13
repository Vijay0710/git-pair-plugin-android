package com.eyeshield.gitpair

import org.gradle.api.Plugin
import org.gradle.api.Project

class GitPairPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        project.tasks.register("gitPairTask", GitPairTask::class.java) {
            initials.set(
                project.findProperty("initials")?.toString() ?: ""
            )
        }
    }
}