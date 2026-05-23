# UP Sewa Hub — Android (Kotlin + Compose)

Native Android app that brings every Uttar Pradesh government service into one place. Built with Kotlin, Jetpack Compose, and Material 3.

**Key feature: you never leave the app.** Every govt service (Bhulekh, IGRSUP, e-District, Allahabad HC, eCourts, FCS, UPPCL, Parivahan, etc.) opens inside an in-app WebView with a native Material 3 toolbar. Per-domain CSS injection cleans up each govt site's cluttered chrome (headers, banners, sidebars, footers) and restyles the actual content (forms, tables, results) to match the app's look. The govt page is still doing the actual work — captchas, OTPs, submissions all stay official — but it *looks like your app rendered it*.

---

## What's in here

```
android/
├── settings.gradle.kts        Project settings
├── build.gradle.kts           Top-level build
├── gradle.properties          Build flags
├── gradle/wrapper/            Gradle wrapper config
└── app/
    ├── build.gradle.kts       App-module build (compose, deps)
    ├── proguard-rules.pro     Keep JS interface in release builds
    └── src/main/
        ├── AndroidManifest.xml
        ├── java/com/upsewa/hub/
        │   ├── UPSewaApp.kt              Application class
        │   ├── MainActivity.kt           Compose UI: Home, Category, Detail, Favorites, About
        │   ├── data/
        │   │   ├── Models.kt             Service + Category data + full catalog (26 services)
        │   │   └── Prefs.kt              DataStore for language + favorites
        │   ├── ui/theme/Theme.kt         Material 3 colour scheme
        │   └── webview/
        │       ├── ServiceWebViewActivity.kt   In-app browser with toolbar
        │       └── SiteInjections.kt           Per-site CSS to clean up govt UIs
        └── res/
            ├── values/        EN strings, colours, themes
            ├── values-hi/     Hindi strings (auto-switches with system locale or in-app toggle)
            ├── drawable/      Vector launcher icon
            ├── mipmap-anydpi-v26/  Adaptive icon
            └── xml/           Data extraction rules
```

---

## Build & install (Android Studio — recommended)

You need **Android Studio** (free, ~1 GB). Any version from **Hedgehog (2023.1)** onwards works.

1. **Install Android Studio** from <https://developer.android.com/studio>.
2. **Open the project**: `File → Open` and choose the `android/` folder.
3. Studio will say "Sync now" — let it. It downloads Gradle 8.7, the Android Gradle Plugin 8.5.2, Kotlin 2.0.0, and all dependencies (5–10 min the first time).
4. **Plug in your Android phone** with USB debugging enabled (Settings → About → tap Build Number 7× → Developer options → USB debugging).
5. Pick your device from the dropdown next to the green Run button, then click **Run** (or press `Shift+F10`).
6. App installs as **UP Sewa Hub** on your phone. Open it — done.

To produce a shareable APK instead:
- `Build → Build App Bundle(s) / APK(s) → Build APK(s)`
- APK lands in `android/app/build/outputs/apk/debug/app-debug.apk`
- Copy to phone, open with a file manager, allow "Install unknown apps", install.

To produce a Play-Store-ready signed AAB:
- `Build → Generate Signed App Bundle / APK…` → create a keystore once, then sign release builds.

---

## Build from the command line (optional)

If you have JDK 17 and the Android SDK installed:

```bash
cd android
# First run only: regenerate the gradle wrapper
gradle wrapper
# Then build a debug APK
./gradlew assembleDebug
# Output: app/build/outputs/apk/debug/app-debug.apk
```

Install on a connected phone:
```bash
./gradlew installDebug
```

---

## How "you never leave the app" works

Open `app/src/main/java/com/upsewa/hub/webview/ServiceWebViewActivity.kt`. The flow is:

1. User taps a service card.
2. `MainActivity` launches `ServiceWebViewActivity` with the official URL.
3. The activity hosts a single `WebView` (JavaScript enabled, DOM storage on).
4. On `onPageStarted` and `onPageFinished`, `SiteInjections.buildInjection(url)` returns a JS snippet that:
   - Injects a `<style id="upsh-injected-style">` with site-specific CSS rules.
   - Hides bloated chrome (headers, sidebars, footers, marquees, banner ads).
   - Restyles forms, tables, buttons, headings to match the app's green/gold brand.
5. The toolbar's "Clean UI" toggle removes the injected style so the user can see the original site if they want.
6. Hardware back navigates the WebView's history first; only finishes the activity when there's nothing left to go back to.

### Adding a new site's CSS rules

In `SiteInjections.kt`, find the `SITE_RULES` map. Add an entry keyed by the host (matched via `contains`):

```kotlin
"yourdomain.gov.in" to """
    header, footer, .navbar, .sidebar { display: none !important; }
    .form-container { background: #fff !important; border-radius: 12px !important; }
""".trimIndent(),
```

That's it — rebuild and the new site is styled too.

---

## Adding a new service

Open `app/src/main/java/com/upsewa/hub/data/Models.kt`, find the `services` list, and add an entry:

```kotlin
Service("my-svc", "misc", popular = false, icon = "🆕",
    nameEn = "New Service", nameHi = "नई सेवा",
    descEn = "Short description.", descHi = "छोटा विवरण।",
    url = "https://official-site.gov.in/",
    usesEn = listOf("Use 1", "Use 2"),
    usesHi = listOf("उपयोग 1", "उपयोग 2"),
    howEn = listOf("Step 1", "Step 2"),
    howHi = listOf("चरण 1", "चरण 2"),
    docs = listOf("Aadhaar", "Photo")
),
```

To add a category, append to the `categories` list in the same file.

---

## Roadmap — going from "clean WebView" to "fully native"

CSS injection is a fast path that works for every site, but you may want to render some services entirely natively. Three patterns:

1. **JS-bridge extraction** (no backend needed). Add a `@JavascriptInterface`-annotated class to the WebView, run a JS snippet after page-load that grabs the result table's text and posts it to Kotlin, then hide the WebView and render with Compose. Works well for read-only pages with stable HTML structure (eCourts result tables, Bhulekh khatauni view).
2. **Backend scraping API**. Stand up a small FastAPI/Express server that hits the govt site, parses HTML with BeautifulSoup/Cheerio, and returns clean JSON. Your app calls it with Retrofit. Survives CAPTCHA-free pages well; CAPTCHA pages still need WebView.
3. **Official APIs where they exist**:
   - eCourts NJDG public data — limited but real
   - mParivahan RC lookup
   - DigiLocker OAuth (for the user's own documents)
   - UMANG SDK for many state services

The current WebView+CSS approach is the realistic v1; pick a few high-traffic services and natively render them in v2.

---

## Tech notes

- **Kotlin 2.0.0** + Compose Compiler Gradle plugin (no more `composeOptions` block needed)
- **minSdk 24** (Android 7.0) — covers ~98% of Indian devices
- **targetSdk / compileSdk 34** (Android 14)
- Material 3 with light/dark colour schemes auto-following system theme
- DataStore Preferences for the two persisted bits: language and favourite-service IDs
- Single `WebView` instance per service open — destroyed on `onDestroy` to avoid the memory-leak pattern

---

## Privacy

- No analytics, no tracking, no ads.
- App stores only your language preference and your favourite-service IDs (local DataStore).
- Govt sites in the WebView use their own cookies — you're logging in to the real official site, the app never sees your credentials.

Made for citizens of Uttar Pradesh.
