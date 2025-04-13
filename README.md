# GitPairPlugin

The GitPairPlugin is a Gradle plugin designed to simplify the process of setting and managing pair programming information in your Git configuration. It allows you to quickly switch between different pairs of developers when pair programming.

## Features

*   **Easy Pair Configuration:** Quickly set up the name and email for a pair of developers, or for a single developer.
*   **`.git-pairs.json` Configuration:** Uses a simple `.git-pairs.json` file to store developer information.
*   **Git User Management:** Automatically updates the local `user.name` and `user.email` Git configuration.
*   **Multiple pairs**: The plugin supports using `n` developers at once.


### 1. Installation

To use the GitPairPlugin, you need to include it in your project.

#### a. `settings.gradle.kts` 
Add the `buildLogic` directory to your `settings.gradle.kts` file:

#### b. `app/build.gradle.kts`
Apply the plugin in your module's `build.gradle.kts` file (e.g., `app/build.gradle.kts`):


### 2. Configure `.git-pairs.json`
Create a `.git-pairs.json` file in the root directory of your project. This file will contain the details of your developers.


#### Example `.git-pairs.json`
*   **`initial`:** A unique abbreviation or identifier for each developer. This is what you'll use to select the pair.
*   **`first_name`:** The developer's first name.
*   **`last_name`:** The developer's last name.
*   **`domain`:** The domain for the developer's email address (e.g., "example.com").

### 3. Usage

#### Using the `gitPair` Task

The GitPairPlugin adds a `gitPair` Gradle task to your project. This task takes an `initials` parameter to define the pair, or a single developer.

**Example:**

To set up the git user for t1 and t2:

```
➜  GitPairPluginDemo git:(main) ✗  ./gradlew gitPair -P initials=t1,t2

> Task :app:gitPairTask
Git user updated to: Name: Test 1 and Test 2 Email: <pair+Test.1+Test.2@test.com>
```
