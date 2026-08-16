# Release checklist

## Required configuration

Release builds do not use the debug Supabase fallback. Provide the public
client configuration through Gradle properties or environment variables:

```powershell
$env:RELEASE_SUPABASE_URL = "https://<project>.supabase.co"
$env:RELEASE_SUPABASE_KEY = "sb_publishable_<key>"
.\gradlew.bat assembleRelease
```

The service-role key must never be provided to the Android build. It belongs
only in the CI secret used by `scripts/supabase_sync.py`.

## Signing

The repository intentionally does not contain a keystore or signing password.
Configure a private `signingConfigs.release` block or a CI signing plugin using
secret-backed values. Store the keystore outside GitHub and manage it through
the release process.

## Verification

Before publishing, run:

```powershell
.\gradlew.bat testDebugUnitTest lintDebug assembleDebug
.\gradlew.bat assembleRelease
```

Then install the release APK on a clean device, test authentication and deep
links, verify RLS with anonymous and authenticated users, and inspect the
generated artifact for unexpected secrets or debug-only dependencies.
