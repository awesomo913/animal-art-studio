# Local testing (Android emulator + Ktor API)

## 1. Backend (Ktor + H2)

From a machine with **JDK 17+** and **Gradle** installed (or use the Gradle tool window in IntelliJ):

```bash
cd backend
gradle run
```

Defaults: **http://0.0.0.0:8080/**. On first start, `ContentSeed` inserts the penguin lesson when the DB is empty; `ContentMigrator.upgradePenguinLessonIfStale()` upgrades old installs to **Waddles v2** copy.

Optional: `gradle run --args="8081"` (or set `PORT`) to change the port.

## 2. Android app

1. Open the **`android`** folder in Android Studio (it generates/uses Gradle wrapper if missing).

2. In **`android/local.properties`**, point the emulator at the host loopback bridge:

```properties
animal_art_studio_url=http://10.0.2.2:8080/
```

(Adjust host/port if the API uses something else.)

3. Run the **`app`** configuration on an emulator or device. Cleartext to that URL should already be allowed for development builds.

## 3. Star show (Waddles) checklist

- **Home**: lesson cards show **`starring {buddy}`** and subtitle from API or `homeCardLine` from `StarShowCatalog`.
- **Lesson**: gradient backdrop, hero for penguin rounds, **`Tell Waddles!`** CTA, star ink palette, PNG export matches paper tint.
- **Celebrate**: titles/subcopy/SFX driven by **`starShowForLesson(lessonId, animalKey)`** from the scratchpad.

When you merge from GitHub, pull **`master`** in this repo, then reopen **`android`** so Gradle sync picks up changes.
