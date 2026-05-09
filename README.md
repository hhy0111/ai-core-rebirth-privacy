# AI Core Rebirth

AI Core Rebirth is a 2D idle hybrid casual mobile game prototype for Android and web testing.

## Project Structure

- `app/`: Android Kotlin project
- `web/`: browser-playable prototype and privacy policy page
- `docs/`: game design, monetization, QA, art prompt, and release documents
- `tools/`: asset generation and support scripts
- `image/`: local visual reference images

## Android Build

```powershell
.\gradlew.bat testDebugUnitTest
.\gradlew.bat assembleDebug
.\gradlew.bat bundleRelease
```

## Web Preview

Serve the `web/` directory with a local static server, then open:

```text
http://localhost:5174
```

Reset web test progress:

```text
http://localhost:5174/?reset=1
```

## Privacy Policy

GitHub Pages can serve:

```text
https://hhy0111.github.io/ai-core-rebirth-privacy/privacy-policy.html
```
