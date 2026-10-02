# PolyChat — Project Overview

> Orientation guide for developers (and AI assistants) working on this codebase.
> Paths are relative to the repository root. Shared Kotlin code lives under
> `composeApp/src/commonMain/kotlin/dev/goood/chat_client/` — abbreviated below as **`<common>/`**.

---

## 1. What this is

**PolyChat** is a multiplatform AI chat client built with **Kotlin Multiplatform + Compose Multiplatform**.
It is a *thin client*: all LLM work (OpenAI, Gemini, …) happens on a separate backend server; this app
authenticates against that server, manages chats, streams replies over SSE, and caches messages locally.

**Targets:** Android · iOS (arm64 + simulator) · Desktop JVM (macOS / Windows / Linux packages).

**Features**

| Feature | Description |
|---|---|
| Chats | List / create / delete chats; each chat is bound to a *source* (provider) and a *model* |
| Chat screen | Streamed replies (SSE), Markdown rendering, delete messages, select previous messages as context |
| System messages | CRUD for reusable system prompts that can be attached to a request |
| Models | Browse provider models and register new ones (`ModelPickerViewModel` / `AddModelDialog`) |
| Files | Upload files to a chat (with progress), list & delete them, drag-and-drop on desktop |
| Translate | Simple two-direction translation screen backed by `/api/translate` |
| Settings | Logout |

---

## 2. Tech stack

| Concern | Library |
|---|---|
| UI | Compose Multiplatform, Material 3, Material 3 Adaptive (`ListDetailPaneScaffold`), window-size-class |
| Navigation | `org.jetbrains.androidx.navigation:navigation-compose` with **type-safe `@Serializable` routes** |
| DI | **Koin** (`koin-compose-viewmodel-navigation`) |
| Networking | **Ktor client** + **Ktorfit** (Retrofit-style interfaces, KSP-generated), kotlinx.serialization, SSE plugin, Bearer auth |
| Local DB | **SQLDelight** (message cache only) |
| Key-value storage | `multiplatform-settings` (credentials + token) |
| Other | FileKit (file pickers), mikepenz Markdown renderer, LineAwesome icons, kotlinx-datetime |

Versions are centralized in [`gradle/libs.versions.toml`](../gradle/libs.versions.toml).
(Exception: `material3-window-size-class` is hard-coded in `composeApp/build.gradle.kts`.)

---

## 3. Repository layout

```
ChatClient/
├── build.gradle.kts            # root: plugin declarations only
├── settings.gradle.kts         # single module ":composeApp"
├── gradle/libs.versions.toml   # version catalog — edit versions here
├── composeApp/                 # THE module: all Kotlin code for every platform
│   ├── build.gradle.kts        # targets, deps, Android config, desktop packaging, SQLDelight
│   └── src/
│       ├── commonMain/         # ~95% of the code (UI, VMs, network, models, DB)
│       │   ├── kotlin/dev/goood/chat_client/...
│       │   ├── sqldelight/dev/goood/chat_client/cache/AppDatabase.sq
│       │   └── composeResources/
│       ├── commonTest/         # unit tests (kotlin.test)
│       ├── androidMain/        # MainActivity, Android actuals, @Preview files
│       ├── iosMain/            # MainViewController, iOS actuals
│       ├── appleMain/          # actuals shared by Apple targets (ShareManager, drag&drop)
│       └── desktopMain/        # main(), JVM actuals
├── iosApp/                     # Xcode project — thin SwiftUI wrapper around ComposeApp framework
└── Materials/                  # design assets (icons)
```

### Inside `<common>/`

```
App.kt                 Root composable: starts Koin, MaterialTheme, AppScreen
AppScreen.kt           Top-level NavHost: AuthGraph ⇄ MainGraph
AppViewModel.kt        Auth state (Authorized / Unauthorized), logout
NavigationRoute.kt     All @Serializable route objects
Const.kt               API base URL + token endpoint
Platform.kt            expect getPlatform()

di/                    Koin: appModule (+ appModulePreview), expect platformModule, createKoinConfiguration()
core/network/          Api.kt (HttpClient + Ktorfit setup) and API interfaces
core/other/            ShareManager (expect), FileReader, ShareFileModel
services/              AuthService(+Impl), LocalStorage, SystemMessagesService
cache/                 Database.kt (SQLDelight wrapper), DatabaseDriverFactory (interface)
model/                 @Serializable DTOs: Chat, Message, Chunk, MFile, SystemMessage, User, ...
viewModels/            Abstract VM + Impl + Preview triplets (see §5)
ui/                    Screens & containers
  ├── AuthContainer.kt / LoginScreen.kt
  ├── MainContainer.kt          Scaffold, TopAppBar, BottomNav / NavigationRail, nested NavHost
  ├── ChatListScreen.kt         ListDetailPaneScaffold: chat list + ChatScreen detail
  ├── chatScreen/               ChatScreen, MessageInput, SettingsElement
  ├── systemMessages/           list, detail, AddModelDialog
  ├── filesDialog/              FilesDialog
  ├── TranslateScreen.kt, SettingsScreen.kt, Snackbar.kt
  ├── composable/               reusable widgets (dialogs, dropdowns, nav bar, progress, ...)
  ├── platformComposable/       expect composables (drag&drop, context menu, window size)
  └── theme/                    Colors.kt, Sizes.kt
```

---

## 4. Entry points — where to start reading

Read in this order to understand the app end to end:

1. **Platform launchers** (all just call `App()`):
   - Desktop: `composeApp/src/desktopMain/.../main.kt` (also `FileKit.init`)
   - Android: `composeApp/src/androidMain/.../MainActivity.kt`
   - iOS: `composeApp/src/iosMain/.../MainViewController.kt` → `iosApp/iosApp/ContentView.swift`
2. **`<common>/App.kt`** — `KoinApplication(createKoinConfiguration())` → `AppScreen()`.
3. **`<common>/di/AppModule.kt`** — every singleton and ViewModel binding in one place.
4. **`<common>/AppScreen.kt`** — chooses `AuthGraph` or `MainGraph` based on `AppViewModel.authState`.
5. **`<common>/ui/MainContainer.kt`** — the main shell and nested nav graph (`addMainNavigationGraph`).
6. **`<common>/ui/ChatListScreen.kt`** → **`ui/chatScreen/ChatScreen.kt`** — the core chat experience.
7. **`<common>/viewModels/ChatViewModelImpl.kt`** — message loading, caching, and streaming.
8. **`<common>/core/network/Api.kt`** + **`StreamApi.kt`** — how the app talks to the backend.

---

## 5. Architecture

Pragmatic **MVVM**, single module, no separate domain/repository layer.

```
Compose screen ──koinViewModel()──▶ ViewModel (StateFlow<State>) ──▶ Api (Ktorfit / StreamApi)
                                        │                         └─▶ Services (Auth, SystemMessages)
                                        └──▶ Database (SQLDelight cache)        └─▶ LocalStorage (Settings)
```

### Conventions

- **ViewModel triplets.** Screens that need IDE previews use an abstract base + real impl + fake:
  `ChatViewModel` / `ChatViewModelImpl` / `ChatViewModelPreview` (same for `MainViewModel`,
  `SystemMessagesViewModel`, `SMDetailViewModel`, `FileDialogViewModel`).
  The Koin module binds the impl (`appModule`) or the preview (`appModulePreview`).
  Simpler VMs (`LoginViewModel`, `AddChatViewModel`, `ModelPickerViewModel`, `TranslateViewModel`) are concrete classes.
- **State shape.** Each VM exposes `StateFlow<State>` where `State` is a nested `sealed interface`
  (`Loading` / `Success` / `Error(message)` plus screen-specific variants), and separate `StateFlow`s for data.
- **Dependency access.** Mix of constructor injection (`ChatViewModelImpl(handle, api, driverFactory)`) and
  `KoinComponent` + `by inject()`. Either is accepted; constructor injection is preferred for new code
  (it's what `viewModelOf(::X)` resolves).
- **Network calls** return `Flow<T>` (Ktorfit `FlowConverterFactory`); consumers use `.catch { }.collect { }`
  or `.first()` inside `viewModelScope.launch`.
- **Platform code** uses `expect`/`actual`: `platformModule` (DB driver), `getPlatform()`, `ShareManager`,
  `PlatformDragAndDropArea`, `PlatformContextMenu`, `PlatformWindowSize`.

### Navigation

Two levels of `NavHost`, all routes in `NavigationRoute.kt`:

```
AppScreen NavHost
├── AuthGraph  → AuthContainer → LoginScreen
└── MainGraph  → MainContainer (nested NavHost)
    ├── MainGraph/ChatListRoute         ChatListScreen (list + ChatScreen as detail pane)
    ├── SystemMessagesGraph
    │   ├── SystemMessagesRoute
    │   └── SystemMessageDetailRoute(id)   (id = -1 → create new)
    ├── TranslateRoute
    └── SettingsRoute                   (logout → back to AuthGraph)
```

- `ChatDetailRoute` exists but is **unused** — chat detail is shown via `ListDetailPaneScaffold`, not navigation.
- Width ≥ `Medium` shows a `NavigationRail`; otherwise a bottom bar (`BottomNavBar`).
- `MainContainer` owns the top-bar title and back button; the list/detail pane reports its state back
  via `updateListDetailPaneStatus`.

### Networking & auth

- Base URL: `Const.Network.API_ENDPOINT` (`<common>/Const.kt`).
- `Api` builds one `HttpClient` (logging, JSON lenient/ignoreUnknownKeys, Bearer auth, SSE) and exposes:
  `authApi`, `chatApi`, `filesApi`, `translateApi`, `testApi`, `streamApi`.
- **Auth flow:** `LoginViewModel` → `AuthService.login(User)` → `POST api/get-auth-token` → token stored in
  `LocalStorage`. On 401 Ktor's `refreshTokens` re-posts the stored user credentials to the same endpoint.
  `AuthService.isAuthorized()` = "stored user exists".
- **Streaming (`StreamApi.streamRequestWithType`)**: `POST /api/streamMessage` as SSE. Event types:
  - `message` → user's request as saved by server → `ReplyVariants.SavedRequest`
  - `chunk` → partial text → `ReplyVariants.Chunks` (appended to `ChatViewModel.newReply`)
  - `finalMessage` → full assistant message → `ReplyVariants.FinalReply`
- File upload: `StreamApi.uploadFile` (multipart `/api/file`, emits `ProgressUpdate`).

Main endpoints (see `ChatApi.kt`, `FilesApi.kt`, `TranslateApi.kt`):
`/api/GetChats`, `/api/NewChat`, `/api/DeleteChat`, `/api/Model/{GetSources,GetAvailableModels,GetProviderModels,AddModel}`,
`/api/Messages?chat_id&time_stamp`, `/api/Message` (DELETE), `/api/system_message(s)`, `/api/files`, `/api/file`, `/api/translate`.

### Local persistence

- **SQLDelight** schema: `composeApp/src/commonMain/sqldelight/dev/goood/chat_client/cache/AppDatabase.sq`
  — a single `Message` table (cache only; chats and system messages are not cached).
- `cache/Database.kt` wraps queries. Driver per platform via `DatabaseDriverFactory` bound in `platformModule`
  (Desktop DB file: `~/messages.db`).
- **Incremental sync** in `ChatViewModelImpl.getMessages(chatId)`:
  1. emit cached messages from DB;
  2. fetch messages newer than `MAX(updatedAt)` from API, upsert them (server rows with `deleted_at` are removed);
  3. reload from DB as the source of truth.
- Files on a message are stored as a comma-joined string of names.
- Regenerate DB interfaces: `./gradlew generateCommonMainAppDatabaseInterface`.

### Shared state services

- `SystemMessagesService` (singleton) holds the system-message list as a `StateFlow`, shared by the
  System Messages screens and the chat screen's system-message picker.

---

## 6. Build, run, test

```bash
./gradlew :composeApp:run                    # run desktop app
./gradlew :composeApp:assembleDebug          # Android APK
./gradlew :composeApp:desktopTest            # run commonTest on JVM (fastest)
./gradlew :composeApp:allTests               # all test targets
./gradlew packageDmg                         # macOS installer → composeApp/build/compose/binaries/main/
```

- iOS: open `iosApp/iosApp.xcodeproj` in Xcode and run (it builds the `ComposeApp` framework via Gradle).
- Android previews live in `composeApp/src/androidMain/.../previews/` and use the preview VMs.
- Tests: `composeApp/src/commonTest/` — currently model serialization tests and `isSavedModel` logic.
  Pure functions (e.g. `internal fun isSavedModel` in `ModelPickerViewModel.kt`) are the easiest thing to test.
- App name/version: top of `composeApp/build.gradle.kts` (`appName`, `appVersionName`, `appVersionCode`).

---

## 7. How-to recipes

**Add a new backend endpoint**
1. Add/extend a DTO in `<common>/model/` (`@Serializable`, use `@SerialName` for snake_case).
2. Add a method to the relevant Ktorfit interface in `core/network/` returning `Flow<T>`.
3. For a new interface, add `val xApi = ktorfit.createXApi()` in `Api.kt` (the `create…` extension is KSP-generated — build once).

**Add a new screen**
1. Add a `@Serializable` route to `NavigationRoute.kt`.
2. Create the composable in `ui/` and the VM in `viewModels/`.
3. Register the VM in **both** `appModule` and `appModulePreview` in `di/AppModule.kt`.
4. Add a `composable<Route>` entry in `addMainNavigationGraph` (`ui/MainContainer.kt`), and optionally a
   `BottomRouteElement` + title mapping in `MainContainer`.

**Add platform-specific behavior**
Declare `expect` in `commonMain`, then provide `actual` in `androidMain`, `desktopMain`, and `iosMain`
(or `appleMain` when shared across Apple targets).

**Change the DB schema**
Edit `AppDatabase.sq`, regenerate, and update `cache/Database.kt`. There are no migrations yet — a schema
change on an existing install will need a migration (`.sqm`) or a DB reset.

---

## 8. Known quirks & tech debt

Useful to know before changing things:

- `Const.API_ENDPOINT` is a hard-coded plain-HTTP IP; there is no build-flavor/env config.
- `LocalStorage` stores the user's **password in plain text** (multiplatform-settings) to support token refresh.
- `ChatViewModelImpl` creates its own `Database` instance (not provided via DI).
- `SystemMessagesService` owns a never-cancelled `CoroutineScope` on `Dispatchers.Main`.
- Leftover/unused code: `AppScreen_old.kt`, `Greeting.kt`, `TestApi`, `StreamApi.myOtherDataStream`,
  `StreamApi.streamRequest`, `NavigationRoute.ChatDetailRoute` / `RegisterRoute`.
- Errors are often logged with `println`; there is no logging abstraction.
- `composeApp/messages.db` is committed to the repo (stray local DB file).
- Android `platformModule` resolves `Context` via Koin `get()`; `MainApplication` does not start Koin itself —
  verify Android context wiring if Android DB access fails.

---

## 9. Quick lookup

| I want to… | Look at |
|---|---|
| Change API base URL | `<common>/Const.kt` |
| See all DI bindings | `<common>/di/AppModule.kt` |
| See all routes | `<common>/NavigationRoute.kt`, `<common>/ui/MainContainer.kt` |
| Change how messages stream | `core/network/StreamApi.kt`, `viewModels/ChatViewModelImpl.sendMessage` |
| Change message caching | `cache/Database.kt`, `AppDatabase.sq`, `ChatViewModelImpl.getMessages` |
| Change login / token handling | `services/AuthServiceImpl.kt`, `services/LocalStorage.kt`, `Api.kt` (Auth plugin) |
| Edit the message input bar | `ui/chatScreen/MessageInput.kt` |
| Edit per-chat settings (sys message, context msgs) | `ui/chatScreen/SettingsElement.kt` |
| Add/register provider models | `ui/systemMessages/AddModelDialog.kt`, `viewModels/ModelPickerViewModel.kt` |
| Create a new chat | `ui/composable/AddChatDialog.kt`, `viewModels/AddChatViewModel.kt` |
| Theme colors / sizes | `ui/theme/Colors.kt`, `ui/theme/Sizes.kt` |
| Bump a library version | `gradle/libs.versions.toml` |
| Desktop window / packaging | `desktopMain/.../main.kt`, `compose.desktop {}` in `composeApp/build.gradle.kts` |
