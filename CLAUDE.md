# CLAUDE.md

PolyChat — Kotlin Multiplatform + Compose Multiplatform AI chat client (Android, iOS, Desktop JVM).
Thin client for a separate backend that proxies LLM providers.

**Full project guide: [docs/PROJECT_OVERVIEW.md](docs/PROJECT_OVERVIEW.md)** — read it before non-trivial changes.

## Layout
- Single Gradle module `:composeApp`; nearly all code is in
  `composeApp/src/commonMain/kotlin/dev/goood/chat_client/`.
- Platform source sets: `androidMain`, `iosMain`, `appleMain` (shared Apple), `desktopMain`.
- `iosApp/` is only a SwiftUI wrapper; don't put app logic there.
- Library versions: `gradle/libs.versions.toml`.

## Commands
```bash
./gradlew :composeApp:run              # run desktop app
./gradlew :composeApp:desktopTest      # fastest test run (commonTest on JVM)
./gradlew :composeApp:assembleDebug    # Android build
./gradlew generateCommonMainAppDatabaseInterface   # regenerate SQLDelight code
```

## Conventions
- MVVM: Compose screen → `koinViewModel()` → ViewModel exposing `StateFlow` + nested `sealed interface State`.
- Screens with previews use VM triplets: abstract `XViewModel`, `XViewModelImpl`, `XViewModelPreview`.
  Register new VMs in **both** `appModule` and `appModulePreview` (`di/AppModule.kt`).
- Routes are `@Serializable` objects in `NavigationRoute.kt`; main nav graph is `addMainNavigationGraph` in `ui/MainContainer.kt`.
  Chat detail is shown via `ListDetailPaneScaffold` in `ChatListScreen.kt`, not via a nav route.
- Network: Ktorfit interfaces in `core/network/` returning `Flow<T>`; new interfaces get `ktorfit.createXApi()` in `Api.kt` (KSP-generated).
  SSE streaming and multipart upload live in `StreamApi.kt`.
- DTOs in `model/` use kotlinx.serialization with `@SerialName` for snake_case fields.
- Platform-specific code uses `expect`/`actual`; provide actuals for android, desktop, and ios/apple.
- Prefer constructor injection for new classes (existing code also uses `KoinComponent` + `by inject()`).

## Gotchas
- API base URL is hard-coded in `Const.kt`.
- SQLDelight caches only messages (`AppDatabase.sq`); no migrations exist yet.
- Unused leftovers: `AppScreen_old.kt`, `Greeting.kt`, `TestApi`, `ChatDetailRoute`, `RegisterRoute`.
