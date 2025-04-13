@file:OptIn(ExperimentalSerializationApi::class)

package com.eyeshield.gitpair


import kotlinx.serialization.ExperimentalSerializationApi
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.Input
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import org.gradle.api.GradleException
import org.gradle.api.provider.Property
import org.gradle.api.tasks.TaskAction
import java.io.File

@Serializable
data class Author(
    val initial: String,
    @JsonNames( "first_name")
    val firstName: String,
    @JsonNames( "last_name")
    val lastName: String,
    val domain: String
) {

    private val getFormattedName: (String) -> String = {
        it.lowercase().replaceFirstChar { char ->
            char.uppercase()
        }
    }

    val fullName: String
        get() = "${getFormattedName(firstName)} ${getFormattedName(lastName)}"

    val emailPlaceholder: String = "${getFormattedName(firstName)}.${getFormattedName(lastName)}"

    val email: String = "${emailPlaceholder}@$domain"
}

@Serializable
data class AuthorList(
    val authors: List<Author>
)

internal abstract class GitPairTask: DefaultTask() {

    @get:Input
    abstract val initials: Property<String>

    @TaskAction
    fun loadAuthors() {
        val file = File(project.rootDir, ".git-pairs.json")

        if (!file.exists()) {
            throw IllegalStateException(".git-pairs.json not found in project root")
        }

        val formattedInitials = initials.get().split(",").filter {
            it.isNotBlank()
        }.toSet()

        if(formattedInitials.isEmpty()) {
            throw GradleException("Please specify the initials")
        }

        val config = Json.decodeFromString<AuthorList>(file.readText())

        val result = configureAuthorNameAndEmailId(
            config = config,
            initials = formattedInitials
        )

        setGitUser(
            name = result.first,
            email = result.second
        )
    }

    private fun configureAuthorNameAndEmailId(config: AuthorList, initials: Set<String>): Pair<String, String> {
        val pairFirstAndLastName = StringBuilder("")
        val pairEmail = StringBuilder("<pair+")

        initials.forEachIndexed { index, initial ->

            val authorDetails = config.authors.firstOrNull {
                it.initial == initial
            } ?: throw GradleException("Invalid Initial. Please check git-pairs.json. May be you forgot to add one?")

            // vj, vs, sh
            // Vijay A, Vishal A and Shyam Kumar H
            if (initials.size > 2) {
                if (index == initials.size - 1) {
                    pairFirstAndLastName.append(" and ")
                    pairFirstAndLastName.append("${authorDetails.firstName} ${authorDetails.lastName}")

                    pairEmail.append("+")
                    pairEmail.append(authorDetails.fullName)
                    pairEmail.append("@${authorDetails.domain}")
                    pairEmail.append(">")
                    return@forEachIndexed

                } else if (index > 0) {
                    pairFirstAndLastName.append(", ")
                    pairEmail.append("+")
                }
            }

            pairFirstAndLastName.append("${authorDetails.firstName} ${authorDetails.lastName}")

            if (initials.size == 1) {
                pairEmail.clear()
                pairEmail.append(authorDetails.email)
                return@forEachIndexed
            }

            // vj,vs
            // Vijay A and Vishal A
            if (initials.size == 2) {
                if (index == 0) {
                    pairFirstAndLastName.append(" and ")
                } else {
                    pairEmail.append("+")
                    pairEmail.append(authorDetails.email)
                    pairEmail.append(">")

                    return@forEachIndexed
                }
            }
            pairEmail.append(authorDetails.emailPlaceholder)
        }
        return pairFirstAndLastName.toString() to pairEmail.toString()
    }

    private fun setGitUser(name: String, email: String, scope: String = "--local", repoDir: File = File(".")) {
        listOf(
            listOf("git", "config", scope, "user.name", name),
            listOf("git", "config", scope, "user.email", email)
        ).forEach { command ->
            val process = ProcessBuilder(command)
                .directory(repoDir)
                .inheritIO()
                .start()
            val result = process.waitFor()
            if (result != 0) {
                System.err.println("Failed to run: ${command.joinToString(" ")}")
            }
        }
        println("Git user updated to: Name: $name Email: $email")
    }
}