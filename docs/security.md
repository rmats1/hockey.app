# Security checklist

## Deep links and intents

The app accepts only `VIEW` intents with the exact `hockeyapp://login-callback`
scheme and host before passing them to Supabase Auth. `onNewIntent` updates the
activity intent before applying the same validation. The callback uses a custom
scheme, so another installed app could theoretically claim the same scheme; a
future release should migrate to an HTTPS App Link with domain verification.

There are no exported services, receivers, providers, nested intent launches, or
`PendingIntent` usages in the current app code. External share and email intents
are created by the app and are not used to redirect into internal components.

## Supabase RLS checklist

RLS is enforced by Supabase, not by the Android client. Before release, verify
that RLS is enabled for every application table, especially `profiles`,
`favoritos`, `comentarios`, `predicciones`, `callups`, and training data.

- Anonymous users can read only intentionally public competition/news data.
- Authenticated users can read and update only their own profile (`auth.uid() = id`).
- Users can create or delete only rows owned by their authenticated user ID.
- Coach-only writes are protected by a server-side role/claim check, not a UI flag.
- Storage bucket policies restrict avatar paths to the authenticated owner.
- The service-role key is used only by `scripts/supabase_sync.py` through CI secrets;
  it must never be included in the APK or client `BuildConfig`.

Record the final SQL policies and verify them with authenticated and anonymous
Supabase tests before publishing a release.
