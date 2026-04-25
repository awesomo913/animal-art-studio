# Animal Art Studio (kids)

A two-part project: a **Ktor + H2** coaching API and a **Jetpack Compose** Android app. The coach uses simple, explainable line “coverage” checks (not a harsh grade) and unlocks a playful “wiggle to life” moment after enough gentle **nudges** and a successful last step.

## Layout

- `backend/` — Kotlin/JVM API (port **8080** by default)
- `android/` — Android app “Paws & Doodles”

## Run the backend

Requirements: **JDK 17+** and **Gradle** (or open `backend` in IntelliJ and use its Gradle).

```bash
cd backend
gradle run
```

Data is stored under `backend/data/` as an **H2** file database. Environment overrides:

- `PORT` — listen port
- `DATABASE_URL` — full JDBC URL (if you move to PostgreSQL, add the Postgres driver and URL)
- `NUDGES_FOR_MAGIC` — practice nudges required before the magic unlock (default **5**)

### Try the API

- `GET http://localhost:8080/healthz`
- `GET http://localhost:8080/v1/lessons`
- `GET http://localhost:8080/v1/lessons/penguin-happy`

## Run the Android app

1. Open the `android/` folder in **Android Studio** (Giraffe+).
2. Create `android/local.properties` if missing (Android Studio usually does), and add your API base URL for the emulator:

```properties
animal_art_studio_url=http://10.0.2.2:8080/
```

Use your computer’s LAN IP instead of `10.0.2.2` when testing on a physical device.

3. Start the backend on the host machine, then run the app.

The app allows **cleartext HTTP** for local development only — switch to HTTPS for production.

## What we were critical about

- **“Mistakes” are nudges:** the product copy and server fields talk about **practice nudges**, not blame.
- **Magic scope:** the app animates your bitmap with a **preset** motion (no promise of full skeletal rigging from every stroke).
- **Crash logs:** the app POSTs a short buffer to `POST /v1/client-logs` (and also logs to the in-memory ring buffer on the device). Review that against your family/privacy rules before shipping broadly.

## Next improvements (not yet in code)

- Replace H2 with PostgreSQL for multi-instance hosting
- Richer vision + LLM coaching with strict child-safety prompts and fallbacks
- Real `res/raw/*.ogg` animal sounds and optional Lottie/Rive packs per `animalKey`
- Parental gate and account model if you add cloud accounts
