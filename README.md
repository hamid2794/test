# MyMaar

A tiny classic Snake game for Android, styled after old Nokia phones.

## Features

- Classic green monochrome game board
- On-screen directional controls
- Score increases when food is collected
- Snake speeds up as the score rises
- Pause, restart, and game-over states
- No account, network access, or backend required

## Build locally

1. Install Android Studio and Android SDK Platform 35.
2. Open this repository in Android Studio.
3. Let Gradle sync complete.
4. Select **Build > Build Bundle(s) / APK(s) > Build APK(s)**.
5. Install the generated debug APK from app/build/outputs/apk/debug/app-debug.apk.

## Download a build from GitHub

Every push to master starts the Build MyMaar APK GitHub Actions workflow. When it succeeds, open the workflow run and download the MyMaar-debug-apk artifact.

The workflow builds an unsigned debug APK for testing. A release APK for public distribution should be signed with a private upload key stored in GitHub Actions secrets.
