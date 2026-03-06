# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**Home Launcher** is an Android launcher app that creates an "AI Phone" experience. The app declares itself as a HOME application and centers around the AI assistant (DouBao/豆包) as the primary interface. When set as default launcher, it auto-launches the AI assistant on device wakeup.

## Build Commands

```bash
# Build debug APK
./gradlew assembleDebug

# Build release APK (requires keystore configuration)
./gradlew assembleRelease

# Clean build artifacts
./gradlew clean

# Install debug build to connected device
./gradlew installDebug

# Run lint checks
./gradlew lint

# Run unit tests
./gradlew test
```

**Output location**: `app/build/outputs/apk/`

## Release Signing

Release builds require `app/keystore.properties` with:
- `keystoreFilePath`: Path to release keystore
- `keystorePassword`: Keystore password
- `keyAlias`: Key alias
- `keyPassword`: Key password

Without this file, release builds will fail. Debug builds use the default Android debug keystore.

## Architecture

### Core Components

1. **MainActivity** (`app/src/main/java/com/launcher/home/MainActivity.java`)
   - Primary launcher activity with GridView for app display
   - Manages AI assistant auto-launch via multiple methods (Deep Link, schemes, Intent)
   - Implements app whitelist filtering system
   - Handles unlock mechanisms (click count, volume keys)

2. **AccessibilityLockService** (`app/src/main/java/com/launcher/home/AccessibilityLockService.java`)
   - Optional accessibility service for enhanced locking
   - Intercepts back key to prevent exiting AI assistant
   - Must be manually enabled by user in system accessibility settings

### Key Configuration Locations

| Configuration | Location |
|---------------|----------|
| App whitelist | `MainActivity.java` lines 54-66 (`APP_WHITELIST` array) |
| AI assistant package | `MainActivity.java` lines 47-49 (`AI_ASSISTANT_PACKAGE`) |
| Accessibility config | `app/src/main/res/xml/accessibility_service_config.xml` |
| Whitelist behavior | Empty array = show all apps; populated = show only whitelisted |

### Build Configuration

- **Compile SDK**: 35 (Android 14)
- **Min SDK**: 26 (Android 8.0)
- **Target SDK**: 35
- **Package**: `com.launcher.home`
- **Gradle Plugin**: 8.7.3
- **ProGuard**: Enabled for release builds

## Unlock Mechanisms

The app implements multiple ways to unlock/exit the AI assistant:

1. **Click Unlock**: Tap screen 3 times consecutively (detected in `MainActivity.java`)
2. **Volume Key Unlock**: Simultaneous volume up/down or quick alternate presses
3. **Accessibility Service**: Enhanced back key interception (optional, user-enabled)

## Important Permissions

- `QUERY_ALL_PACKAGES`: Required to query all installed apps for the launcher
- `BIND_ACCESSIBILITY_SERVICE`: Optional, for enhanced lock service only

## Project-Specific Patterns

- **App Whitelist**: Uses a static array `APP_WHITELIST` in MainActivity. Modify this to control which apps appear in the launcher.
- **AI Launch Strategy**: Tries multiple methods in order: Deep Link (`doubao://voice`), URL schemes, then package launch.
- **Dark Theme**: Hardcoded color `#1a1a2e` for background, blue accents for UI elements.

## Documentation

The project includes Chinese documentation:
- `USER_GUIDE.md` - Comprehensive user guide
- `RELEASE_NOTES.md` - Feature overview and roadmap
- `PRIVACY_POLICY.md` - Privacy policy (no data collection)
- `SIGNING_GUIDE.md` - Keystore generation guide
