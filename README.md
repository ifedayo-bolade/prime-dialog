## PrimeDialog

A modern and highly customizable Android dialog library with animations, custom views, adaptive theming, and a fluent builder-style API. PrimeDialog gives developers complete control over dialog appearance and behavior while keeping implementation simple and clean.


**FEATURES:**
- Custom dialog layouts and views
- Smooth window and component animations
- Adaptive day/night mode support
- Accent color customization
- Rounded corners and dynamic sizing
- Header backgrounds, overlays, and icon support
- Typography customization
- "Don't show again" support
- Java and Kotlin friendly
- Lightweight and easy to integrate


<img width="250" height="482" alt="1" src="https://github.com/user-attachments/assets/da41def2-8e2f-4124-b9fb-9ff254d5b4b8" />
<img width="250" height="482" alt="2" src="https://github.com/user-attachments/assets/0a9f7d15-6baf-4616-8eec-7d78d3fd0f12" />
<img width="250" height="482" alt="3" src="https://github.com/user-attachments/assets/5cf0dd72-c9aa-426b-9824-39a03ade6fce" />
<img width="250" height="482" alt="4" src="https://github.com/user-attachments/assets/c99ba32f-e43d-4de5-9375-1ccf9633a2a2" />
<img width="250" height="482" alt="5" src="https://github.com/user-attachments/assets/1326f8cb-c271-4891-8a41-2d171406f8a9" />
<img width="250" height="482" alt="6" src="https://github.com/user-attachments/assets/77d6c5c4-47c9-4d72-a72f-c76867a47793" />


## 📦 INTEGRATION

### Step 1: Add JitPack repository

Add this to your `settings.gradle`:

```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

---

### Step 2: Add dependency

```kotlin
dependencies {
    implementation("com.github.ifedayo-bolade:prime-dialog:1.0.1")
}
```



## 🚀 Quick Start

#### Kotlin

```kotlin
PrimeDialog(this)
    .setIcon(R.drawable.ic_info)
    .setTitle("PrimeDialog")
    .setMessage("A beautiful customizable Android dialog library.")
    .setPositiveButton("OK")
    .setNegativeButton("Cancel")
    .show()
```

---

#### Java

```java
new PrimeDialog(this)
        .setIcon(R.drawable.ic_info)
        .setTitle("PrimeDialog")
        .setMessage("A beautiful customizable Android dialog library.")
        .setPositiveButton("OK")
        .setNegativeButton("Cancel")
        .show();
```
