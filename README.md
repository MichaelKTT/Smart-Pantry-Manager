# Smart Pantry Manager

A small Android application for tracking leftover ingredients and finding recipes that use **only what is actually available, in enough quantity**.

Written in **Java with XML layouts**, using Android's `Activity`, `Intent`, `ListView`, custom `BaseAdapter`, `SQLiteOpenHelper` and `SharedPreferences`. The interface uses midnight purple, lavender and pale green.

## Open and run

1. Extract the entire ZIP. Open the **SmartPantryManager** folder in **Android Studio**: choose **Open**, not New Project. Select the folder containing `settings.gradle`.
2. Let Android Studio sync Gradle. Internet is needed for the first development setup, but the installed app works offline.
3. In SDK Manager, install **Android SDK Platform 35** and **Android SDK Build-Tools 34.0.0** if requested.
4. Select **JDK 17** as the Gradle JDK. Search Android Studio settings for **Gradle JDK**. Use or download a JDK 17 if necessary. The pinned project uses Android Gradle Plugin **8.7.3** and Gradle **8.9**.
5. Choose an emulator or phone running **Android 8.0 / API 26 or higher**. An API 35 emulator is a suitable first choice.
6. Select the **app** run configuration and press the green **Run** triangle.
7. On first launch, recipes are seeded automatically; the pantry starts empty. Add an ingredient, or open **Settings → Load sample pantry**, confirm, and then open **Recipes**. The sample has six complete matches.

There is no API key, Firebase project, web server, Google Maps or location permission to configure. You do not need to create an Empty Activity template or paste these files into another project.

### Install the supplied APK

`Installable_APK/SmartPantryManager-debug.apk` is next to this project folder in the download. You can drag it onto a running Android emulator, or copy it to an Android 8.0+ phone and open it. It is a debug build for local testing.

The supplied APK and a new Android Studio build use different debug signing keys. If an installation reports a signature conflict, uninstall the earlier build before installing the other one. **Uninstalling removes that installation's pantry data.**

### Build from a terminal

With JDK 17 and an Android SDK configured:

```powershell
# Windows, inside the project folder
.\gradlew.bat assembleDebug
```

```bash
# macOS / Linux
sh gradlew assembleDebug
```

Android Studio normally writes your SDK location to a local `local.properties`; that machine-specific file is deliberately excluded. If using only a terminal, set `ANDROID_HOME` to your Android SDK directory or create that local file yourself. The normal Gradle APK output is `app/build/outputs/apk/debug/app-debug.apk`.

## Screens

| Screen | Java class | XML layout |
| --- | --- | --- |
| Pantry list, edit/delete controls and summary | `ui/PantryActivity.java` | `activity_pantry.xml` |
| Add or edit a pantry batch | `ui/IngredientFormActivity.java` | `activity_ingredient_form.xml` |
| Complete recipe matches only | `ui/SuggestedRecipesActivity.java` | `activity_suggested_recipes.xml` |
| Ingredients, quantities and method | `ui/RecipeDetailActivity.java` | `activity_recipe_detail.xml` |
| In-app expiry highlights, optional sample, reset | `ui/SettingsActivity.java` | `activity_settings.xml` |

All Java classes are under `app/src/main/java/com/smartpantry/app/`. XML layouts are under `app/src/main/res/layout/`.

## Why SQLite?

The pantry is personal, small and useful offline. SQLite stores data on the device, needs no backend or credentials, and clearly demonstrates all four CRUD operations. The three tables separate pantry batches, recipes and recipe requirements. `SQLiteOpenHelper.onCreate()` creates the schema and seeds **20 recipes once**. Closing and reopening the app does not recreate the database. Uninstalling or clearing Android app storage does remove it.

The settings switch is stored separately in `SharedPreferences`; the actual pantry and recipes are in SQLite.

## The strict rule

`logic/RecipeMatcher.java` is the central business logic:

1. Exclude expired pantry batches and invalid quantities.
2. Normalize names using an explicit small alias dictionary, including tomato/tomatoes and egg/eggs.
3. Convert compatible units to a common base: kg → g; l, tsp, tbsp and cup → ml.
4. Add compatible batches of the same ingredient together.
5. Add repeated requirements together as well, so the same stock cannot be counted twice.
6. Reject the recipe immediately if any required ingredient is missing or short.

No ingredient is assumed free: even water, salt and oil are explicit where needed. There is no partial-match list mixed into suggestions.

### Deliberate matching boundaries

- A metric cup is **250 ml**, tablespoon **15 ml**, teaspoon **5 ml**. These volume units are for liquids; use g/kg for dry ingredients.
- Grams and millilitres are not interchangeable. Pieces and slices are separate. If a recipe asks for two tomatoes in pieces, entering only a weight does not qualify it. This avoids guessing food weights or densities.
- Dry chickpeas do not qualify for a cooked-chickpea recipe. Use **cooked chickpeas** or **canned chickpeas**, in grams. For garlic, use **garlic cloves**, in pieces. For tuna, use **canned tuna**, in grams.
- Aliases are intentionally limited. Fresh, dried and powdered foods are not broadly treated as equivalent.
- A batch dated today remains available; a date before today is excluded. This is a stock-tracking convention, not a food-safety assessment.
- Recipes qualify **independently**. Six suggestions mean six alternative choices; the pantry may not support cooking all six consecutively. After cooking, edit quantities yourself. The app does not deduct stock automatically.
- Expiry highlighting happens while using the app; no background notification service is included.

## Tests and verification

From this project folder, with Python 3 and JDK 17 on PATH:

```bash
python tests/run_tests.py
```

This compiles and executes the **actual production Java matching code**. It checks every recipe against exact quantities, each missing ingredient and each insufficient quantity, plus aliases, conversions, duplicate batches, repeated requirements, expiry and invalid data. See `docs/TESTING.md` for the device checklist.

The supplied debug APK was built from these Java/XML sources with Android SDK 35 and official Android build tools, then signature-verified. The sandbox could not give Gradle's JVM network access, so **a complete Gradle sync/build and lint run were not completed here**. Java compilation, resource linking, DEX generation, alignment and APK signing did succeed. No emulator or physical-device run was available here; complete the device checklist in Android Studio before treating this as submission evidence.

## Understand and complete the assignment

- `docs/LEARNING_GUIDE.md`: what each file does and which concepts to explain.
- `docs/TESTING.md`: reproducible checks, including SQLite persistence and strict matching.
- `docs/SUBMISSION_CHECKLIST.md`: GitHub, 5–7 minute narrated video, genuine screenshots, report, and the under-50 MB submission ZIP.
- `docs/REPORT_WORKBOOK.md`: report structure to complete with your own details and real app evidence.

This is an implementation to study, adapt and understand. Follow your institution's rules for acknowledging assistance and sources. Your own development history, narration, screenshots, reflections and originality declaration must be genuine. This download is a source-and-demo package, **not your completed Moodle submission**.

## Official documentation consulted

- Android Developers (2024) *Android Gradle Plugin 8.7 release notes*. Available at: https://developer.android.com/build/releases/agp-8-7-0-release-notes (Accessed: 2 November 2026).
- Android Developers (n.d.) *SQLiteOpenHelper*. Available at: https://developer.android.com/reference/android/database/sqlite/SQLiteOpenHelper (Accessed: 2 November 2026).
- Android Developers (n.d.) *ListView*. Available at: https://developer.android.com/reference/android/widget/ListView (Accessed: 2 November 2026).
- Android Developers (n.d.) *The activity lifecycle*. Available at: https://developer.android.com/guide/components/activities/activity-lifecycle (Accessed: 2 November 2026).
- Android Developers (n.d.) *Intents and intent filters*. Available at: https://developer.android.com/guide/components/intents-filters (Accessed: 2 November 2026).
- Android Developers (n.d.) *Behavior changes: Apps targeting Android 15 or higher*. Available at: https://developer.android.com/about/versions/15/behavior-changes-15 (Accessed: 2 November 2026).

The included Gradle wrapper files originate from Gradle v8.9.0 and retain their Apache 2.0 license notices. See `THIRD_PARTY_NOTICES.md`.
