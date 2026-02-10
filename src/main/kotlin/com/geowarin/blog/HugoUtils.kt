package com.geowarin.blog

import com.intellij.openapi.project.guessProjectDir

fun isHugoProject(project: com.intellij.openapi.project.Project?): Boolean {
    val projectDir = project?.guessProjectDir() ?: return false
    return projectDir.findChild("hugo.toml") != null ||
            projectDir.findChild("config.toml") != null ||
            projectDir.findChild("config.yaml") != null ||
            projectDir.findChild("config.yml") != null ||
            projectDir.findChild("config.json") != null
}