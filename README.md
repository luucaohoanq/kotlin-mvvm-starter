# Kotlin MVVM Sample App

- This is a sample Android application built using Kotlin, MVVM architecture, and Jetpack Compose. It demonstrates best practices in Android development, including dependency injection with Hilt, state management with ViewModel, and UI design with Jetpack Compose.

# 🖼 Screens

<div align="center">
  <table>
    <tr><th colspan="3">📱 Screen Previews</th></tr>
    <tr>
      <td align="center"><img src="Sources/assets/home_3.png" alt="Home 3" width="240"/><br/>Home 3</td>
      <td align="center"><img src="Sources/assets/home_1.png" alt="Home 1" width="240"/><br/>Home 1</td>
      <td align="center"><img src="Sources/assets/home_2.png" alt="Home 2" width="240"/><br/>Home 2</td>
    </tr>
    <tr>
      <td align="center"><img src="Sources/assets/favorite_item.png" alt="Favorite Item" width="240"/><br/>Favorite Item</td>
      <td align="center"><img src="Sources/assets/camera_detail.png" alt="Camera Detail" width="240"/><br/>Camera Detail</td>
      <td align="center"><img src="Sources/assets/camera_detail_with_like.png" alt="Camera Detail With Like" width="240"/><br/>Camera Detail With Like</td>
    </tr>
    <tr>
      <td align="center"><img src="Sources/assets/account_screen_under_development.png" alt="Account Under Development" width="240"/><br/>Account Under Development</td>
      <td align="center"><img src="Sources/assets/setting_screen.png" alt="Settings" width="240"/><br/>Settings</td>
      <td align="center"><img src="Sources/assets/splash_screen.png" alt="Splash Screen" width="240"/><br/>Splash Screen</td>
    </tr>
  </table>
</div>


# Splash Screen

- Generate own icon at https://icon.kitchen/

<img src="Sources/capture/icon_kitchen.png" alt="Icon"/>

- Copy all inside `Sources/IconKitchen-Output/android/res/` to `app/src/main/res/` (overwrite existing files)
- Change `app/src/main/AndroidManifest.xml` to use the new icon, name is `hoang_ic_launcher`, default is `ic_launcher` and `ic_launcher_round`

```xml
<application
        android:icon="@mipmap/hoang_ic_launcher"
        android:roundIcon="@mipmap/hoang_ic_launcher">
</application>
```

# Environment variables config

- local.properties

```
my.api.key=YOUR_API_KEY
```

- build.gradle.kts: update `defaultConfig` to read from `local.properties` and enable `buildConfig`
  feature

```kotlin
defaultConfig {
    /// Existing configurations...

    // Read from local.properties
    val properties = Properties()
    if (rootProject.file("local.properties").exists()) {
        properties.load(project.rootProject.file("local.properties").inputStream())
    } else {
        throw RuntimeException("local.properties file not found")
    }

    val error = "variable not found in local.properties"

    // Define BuildConfig fields without revealing fallback values
    buildConfigField(
        "String", "YOUR_API_KEY",
        "\"${properties.getProperty("my.api.key") ?: throw RuntimeException(error)}\""
    )
}

buildFeatures {

    // Existing features...

    // Environment variable configuration
    buildConfig = true
}

```
