# ZUKORA BOOST — Android starter project

This is an Android Studio source project (Java, no third-party libraries).

## Build an APK using GitHub Actions (phone-friendly)
1. Create a new GitHub repository.
2. Upload the contents of this project folder to the repository root (not the ZIP file itself).
3. Open the repository's **Actions** tab.
4. Select **Build ZUKORA BOOST APK** and tap **Run workflow** (or wait for the first push build).
5. Open the completed workflow run. Under **Artifacts**, download `ZUKORA-BOOST-debug-apk`.
6. Extract the downloaded artifact ZIP; it contains `app-debug.apk`. Open it on your Android phone to install.

The included workflow installs Java 17, Android SDK, and Gradle 8.9 in GitHub's cloud runner. A local Gradle Wrapper is therefore not required for this cloud build.

## Build locally
Open the folder in Android Studio with Android Gradle Plugin 8.7.3 and Gradle 8.9.

## Important limitations
- Reads available RAM and battery level/temperature using public Android APIs.
- Does not force higher FPS or read another game's live FPS. Ordinary Android apps cannot reliably force higher FPS in other apps.
- The Prepare Gaming button refreshes device readings and gives safe guidance; it does not kill background apps or change protected system settings.
- The game finder filters launchable app names and may not detect every game.

Package: `com.zukora.boost`
