# Resolve AAR Metadata Issues (SDK 36 and Desugaring)

The project is currently hitting three build issues:
1. `Vico` library requires `compileSdk 36`.
2. `Vico` library (compose-m3) requires `compileSdk 36`.
3. `compose-material-dialogs-datetime` requires core library desugaring.

This plan updates the Android Gradle Plugin (AGP), the `compileSdk` version, and enables core library desugaring to resolve these issues.

## User Review Required

> [!IMPORTANT]
> Updating `compileSdk` to 36 (Android 16) and AGP to 9.3.1 is a significant update. While this resolves the current build errors, you should later verify if there are any runtime behavior changes when you decide to bump `targetSdk` to 36. For now, we are only bumping `compileSdk` to allow compilation.

## Proposed Changes

### Build Configuration

#### [MODIFY] [libs.versions.toml](file:///C:/Users/rmats/AndroidStudioProjects/Newhockeyapp/gradle/libs.versions.toml)
- Update `agp` version to `9.3.1`.
- Add `desugar-jdk-libs` dependency.

#### [MODIFY] [app/build.gradle.kts](file:///C:/Users/rmats/AndroidStudioProjects/Newhockeyapp/app/build.gradle.kts)
- Update `compileSdk` to `36`.
- Enable `isCoreLibraryDesugaringEnabled = true` in `compileOptions`.
- Add `coreLibraryDesugaring` dependency.
- Update `Vico` dependencies to use the version catalog.

## Verification Plan

### Automated Tests
- Run `./gradlew :app:assembleDebug` to verify the project builds successfully.

### Manual Verification
- None required for build-level changes, but I will monitor the build output for any new warnings.
