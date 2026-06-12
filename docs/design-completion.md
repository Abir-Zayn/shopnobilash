# DORent → Shopnobilash: Design Completion Document

**Project:** Shopnobilash — property rental app for Chittagong, Bangladesh  
**Design source:** DORent handoff bundle (Claude Design → Claude Code)  
**Target platform:** Native Android, Kotlin, Jetpack Compose, Material Design 3  
**Date completed:** 2026-06-12

---

## 1. What Was Built

A pixel-faithful Jetpack Compose implementation of the DORent prototype — 11 screens, full navigation stack, MVVM architecture, and a complete design system. Every visual token (color, spacing, radius, typography) from the HTML/CSS prototype was translated to Kotlin/Compose primitives.

---

## 2. Screen Inventory

| # | Screen | Route | Entry Point |
|---|--------|--------|-------------|
| 1 | Splash / Onboarding | `splash` | App launch |
| 2 | Login | `login` | Splash CTA |
| 3 | Home | `home` | After login |
| 4 | Newly Added | `newly_added` | Home "See all" |
| 5 | Property Detail | `detail/{propertyId}` | Any card tap |
| 6 | Checkout & Payment | `checkout/{propertyId}` | Detail "Book Now" |
| 7 | Wishlist (Saved) | `wishlist` | Bottom nav |
| 8 | Chat List | `chat` | Bottom nav |
| 9 | Chat Thread | `chat_thread/{propertyId}` | Chat list row / Detail owner |
| 10 | Notifications | `notifications` | Home bell icon |
| 11 | Profile | `profile` | Bottom nav |

---

## 3. Design System

### 3.1 Color Tokens

All colors defined in `ui/theme/Color.kt` as top-level `Color` constants and bundled into `AppColors` data class. Accessed via `MaterialTheme.appColors` extension anywhere in the composition tree.

| Token | Hex | Usage |
|-------|-----|-------|
| `Accent` | `#1FAE84` | Primary brand, buttons, active states |
| `AccentDeep` | `#17916C` | Pressed states, dark text on AccentSoft |
| `AccentSoft` | `#E6F7F0` | Background tints on accent elements |
| `AccentSoft2` | `#D6F1E6` | Secondary container |
| `TagOrange` | `#E27A38` | Property type tags |
| `TagSoft` | `#FBEEE2` | Tag background |
| `Ink` | `#16191C` | Primary text, headings |
| `Ink2` | `#3B4045` | Secondary text |
| `Muted` | `#7C838B` | Placeholder, meta text |
| `Faint` | `#AAB0B6` | Inactive icons, timestamps |
| `LineLight` | `#EEF1F3` | Dividers |
| `Line2` | `#E3E7EA` | Field borders |
| `Bg` | `#F4F6F8` | Screen backgrounds |
| `CardWhite` | `#FFFFFF` | Card surfaces |
| `FieldBg` | `#F0F2F5` | Input field backgrounds |
| `StarYellow` | `#F4A92C` | Rating star |
| `Danger` | `#E5564B` | Error, logout, notification dot |
| `Blue` | `#2F6BE3` | Secondary accent, booking notifications |

**MD3 mapping:** `LightColorScheme` and `DarkColorScheme` map all tokens to Material3 slots. Dynamic color (`dynamicColorScheme`) is enabled on Android 12+ (API 31+) with static fallback on older devices.

### 3.2 Typography

Font: **Plus Jakarta Sans** loaded via `ui-text-google-fonts` (no bundled font assets needed). Weights used: 400, 500, 600, 700, 800.

```
headlineLarge  → 30sp / ExtraBold / −0.4sp tracking   (Splash heading)
headlineMedium → 24sp / Bold / −0.4sp tracking         (Screen titles: Saved, Messages, Profile)
headlineSmall  → 19sp / Bold / −0.3sp tracking         (Section heads: Categories, Recommend)
titleLarge     → 22sp / Bold                            (Property title on Detail)
titleMedium    → 16sp / Bold                            (Section labels, buttons)
titleSmall     → 14sp / SemiBold                        (Owner name, list labels)
bodyMedium     → 14sp / Normal                          (Descriptions, chat messages)
bodySmall      → 12sp / Normal                          (Address, meta, timestamps)
labelLarge     → 14sp / Bold                            (Category chips, filter buttons)
labelMedium    → 12sp / SemiBold                        (Tags, small CTAs)
labelSmall     → 11sp / SemiBold                        (Tab labels, unread badges)
```

### 3.3 Shape Tokens

Defined in `ui/theme/Shape.kt` using Material3 `Shapes`:

| Shape | Radius | Usage |
|-------|--------|-------|
| `extraSmall` | 7dp | AppTag chip |
| `small` | 11–13dp | Filter chips, category pills, icon buttons |
| `medium` | 15–16dp | Input fields, payment method rows |
| `large` | 18–20dp | Cards (`PropertyCardV`, `PropertyCardH`) |
| `extraLarge` | 22–28dp | Bottom sheet, success modal |

### 3.4 Elevation / Shadow

No `tonalElevation` used in nav bar (set to 0dp to match design exactly). Card elevation via `Modifier.shadow(4.dp, shape)` rather than MD3 surface tonal elevation, preserving the white card appearance from the prototype.

---

## 4. Shared Components

Located in `ui/components/`.

### CommonComponents.kt

| Composable | Description |
|-----------|-------------|
| `AppTag` | Orange pill chip for property type labels |
| `PriceText` | Annotated string — accent price + muted period suffix |
| `PrimaryButton` | Full-width 56dp accent button, optional leading icon |
| `RoundIconButton` | 42dp FilledIconButton with 13dp rounded corners, active state |
| `StackHeader` | Back button + centered title + optional trailing slot |
| `InitialsAvatar` | Deterministic color from name hash, two-letter initials |
| `SaveToggleButton` | 34dp chip, white/accent toggle, accepts any icon pair |

### PropertyCards.kt

| Composable | Used on |
|-----------|---------|
| `PropertyCardVertical` | Home "Recommend" horizontal scroll row (200dp wide) |
| `PropertyCardHorizontal` | Newly Added, Wishlist, Home "Newly Added" section |

Both cards use `AsyncImage` (Coil) with `crossfade(true)` and a `FieldBg` placeholder background while loading.

### BottomNavBar.kt

Material3 `NavigationBar` with 4 tabs: Home, Saved, Chat, Profile. Uses outlined icons when inactive, filled icons when active. `indicatorColor = Transparent` removes the MD3 pill highlight (matches prototype behavior). Selected color = `Accent`.

---

## 5. Architecture

### MVVM Layer Map

```
Compose Screen
    ↕  collectAsStateWithLifecycle()
ViewModel  (StateFlow<UiState> + commands)
    ↕  suspend functions
PropertyRepository (interface)
    ↕
PropertyRepositoryImpl (mock data, in-memory saved set)
```

### ViewModel Patterns

Every feature ViewModel follows the same sealed UiState pattern:

```kotlin
sealed class XUiState {
    object Loading : XUiState()
    data class Success(val data: ...) : XUiState()
    data class Error(val message: String) : XUiState()
}
```

Save state (bookmarks) is shared via repository's `getSavedIds(): Flow<Set<String>>` — a single source of truth emitted as `StateFlow` into every screen that needs it. `toggleSave(id)` mutates the in-memory `MutableStateFlow<Set<String>>`.

### ViewModels

| ViewModel | Key State | Key Commands |
|-----------|-----------|--------------|
| `HomeViewModel` | `uiState`, `savedIds`, `selectedCategory` | `load()`, `setCategory()`, `toggleSave()` |
| `ListingViewModel` | `uiState`, `savedIds` | `load()`, `toggleSave()` |
| `DetailViewModel(propertyId)` | `uiState`, `savedIds` | `toggleSave()` |
| `CheckoutViewModel(propertyId)` | `uiState`, `selectedTerm`, `selectedPayment`, `bookingDone` | `selectTerm()`, `selectPayment()`, `confirmBooking()`, `dismissConfirmation()` |
| `WishlistViewModel` | `uiState` (savedProperties, selectedFilter) | `setFilter()`, `toggleSave()` |
| `ChatViewModel(propertyId?)` | `conversations`, `messages`, `inputText` | `setInputText()`, `sendMessage()` |
| `NotificationsViewModel` | `notifications` | `markAllRead()` |
| `ProfileViewModel` | `uiState` (listingCount, savedCount, rating) | — |

### DI (Koin)

Three modules:

- **`networkModule`** — `HttpClient` (Ktor Android) with `ContentNegotiation` (JSON) and `Logging`
- **`repositoryModule`** — `PropertyRepositoryImpl` as singleton bound to `PropertyRepository` interface
- **`appModule`** — all ViewModels; parameterized VMs (`DetailViewModel`, `CheckoutViewModel`, `ChatViewModel`) registered with `viewModel { (id: String) -> ... }` pattern and injected via `koinViewModel { parametersOf(id) }`

---

## 6. Navigation

`AppNavHost.kt` — single `NavHost`, start destination `splash`.

### Back Stack Behavior

| Action | Behavior |
|--------|----------|
| Login → Home | `popUpTo(splash) { inclusive = true }` — no back to splash |
| Logout | `popUpTo(0) { inclusive = true }` — clears entire stack |
| Bottom tab switch | `popUpTo(home) { inclusive = false }` — preserves home in stack |
| Checkout success → Home | `popUpTo(home) { inclusive = true }` — fresh home |
| Standard push (Detail, Chat, Notif) | Default push, back = `popBackStack()` |

### Argument passing

`propertyId: String` passed as path segment (`detail/{propertyId}`) for Detail, Checkout, ChatThread. Retrieved via `backStackEntry.arguments?.getString("propertyId")`.

---

## 7. Screen-by-Screen Design Notes

### 7.1 Splash Screen

- Full-screen `AsyncImage` hero behind gradient overlay (`radial` in prototype → `verticalGradient` in Compose, four color stops matching original opacity values)
- Brand mark top-left: 30dp rounded square Accent box + "DORent" ExtraBold
- 3 slides, animated dot indicators (`animateDpAsState` 6dp→26dp for active dot)
- "Next" advances slide; last slide becomes "Get Started" → navigates to Login
- "Skip for now" link bypasses onboarding directly to Login

### 7.2 Login Screen

- White card background, scrollable
- `OutlinedTextField` with `FieldBg` container, Accent focus border, transparent unfocused border
- Password toggle: `PasswordVisualTransformation` ↔ `VisualTransformation.None`
- Remember me: manual checkbox (Box + Icon) — not a Material Checkbox, matches the prototype's custom box
- Social buttons: Google + Apple (prototype icons replaced with `Icons.Filled.Language` + `Icons.Filled.PhoneAndroid` per user instruction to use Material Icons)

### 7.3 Home Screen

- `LazyColumn` wrapping everything including the horizontal `LazyRow` (recommended) for smooth scrolling
- Category chips in `horizontalScroll` Row (not LazyRow — fixed 5 items)
- Notification dot: 8dp danger-colored Box with `align(Alignment.TopEnd)` overlaying bell button
- "See all" buttons navigate to `NewlyAdded`
- `Scaffold` bottom bar = `BottomNavBar` with `currentRoute = "home"`

### 7.4 Property Detail Screen

- Hero image: `AsyncImage` 310dp height, no padding, fills full width
- Overlay controls (back + bookmark) absolutely positioned at top using Box + padding
- 3 thumbnail row below hero; second and third have semi-transparent overlay with `+n` count
- Expandable description: `buildAnnotatedString` with inline "read more / read less" accent-colored span; toggle via `expanded` state
- Meta pills: three equal-weight columns with icon + label, `Bg` background, 14dp rounded
- Owner card: `InitialsAvatar` + name/role + two `AccentSoft` icon chip buttons (phone + message → both navigate to ChatThread)
- Map: static placeholder Box with "📍 Here" label — ready for Google Maps or OSM integration
- Sticky bottom bar via `Scaffold.bottomBar`: price column + "Book Now" PrimaryButton

### 7.5 Checkout Screen

- `LazyColumn` body with `StackHeader` top bar and sticky bottom bar (Scaffold)
- Lease term selector: 3 chips (6 / 12 / 24 months), `AccentSoft` background + Accent border when selected
- Payment methods: three rows + dashed "Add new card" row. Radio indicator: 22dp Circle border, 11dp inner Circle fill when selected
- Price breakdown: white card with 1dp `LineLight` divider before total row
- Booking confirmed: `Dialog` with bottom-sheet-style content (`RoundedCornerShape(topStart = 28, topEnd = 28)`), `CheckCircle` icon, `AccentSoft` circle background

### 7.6 Wishlist Screen

- Filter chips in `horizontalScroll` Row: All / House / Apartment / Villa
- Empty state: bookmark icon in card, centered text block
- Populated state: `LazyColumn` of `PropertyCardHorizontal` with save-toggle wired to viewmodel

### 7.7 Chat List Screen

- Each row: `InitialsAvatar` + online dot (13dp Accent circle, border matches `Bg`) + name/time row + last-message row + unread badge
- Divider drawn as 1dp Box with `padding(start = 77.dp)` to align with text column
- Unread badge: `Accent` background `CircleShape` with white label

### 7.8 Chat Thread Screen

- Top bar in `Scaffold.topBar` with `padding(top = 52.dp)` for status bar clearance
- Property context chip at top of message list: thumbnail + title + price + chevron → taps navigate to Detail
- "Today" label centered between chip and messages
- Message bubbles: `RoundedCornerShape(18,18,6,18)` for sent, `(18,18,18,6)` for received — matches prototype exactly
- Send on keyboard IME action (`ImeAction.Send`) and send button both call `viewModel.sendMessage()`
- `LaunchedEffect(messages.size)` auto-scrolls to latest message

### 7.9 Notifications Screen

- Groups rendered via `forEach` over `LazyColumn` items (not nested lazy lists)
- Icon + tint derived from notification `type` field via `notificationIconAndTint()` helper
- Unread items: white card + shadow; read items: transparent background, no shadow
- CTA chip (`"Add a Review"`) drawn as inline AccentSoft→Accent Box below body text

### 7.10 Profile Screen

- Stats row: Listings / Saved / Rating — Saved count pulled live from `viewModel.uiState.savedCount` (derived from repository `getSavedIds()`)
- Host promo banner: dark `Ink` background, white text, Accent + icon chip right side
- Menu: single white card with internal `Box` dividers — `padding(start = 63.dp)` for icon-aligned separator
- Logout: `Danger` colored row, navigates back to Splash clearing full stack

---

## 8. Data Layer

### Mock Data (`data/model/Property.kt`)

5 properties seeded: `sherman`, `lara`, `minimal`, `earth`, `aspen`. All image URLs use `picsum.photos/seed/{id}/600/400` for stable deterministic placeholder images.

### Repository Interface

```kotlin
interface PropertyRepository {
    fun getSavedIds(): Flow<Set<String>>
    suspend fun toggleSave(id: String)
    suspend fun getListings(): Result<List<Property>>
    suspend fun getPropertyById(id: String): Result<Property>
}
```

### Current Implementation

`PropertyRepositoryImpl` — fully in-memory. Pre-seeds `earth` and `minimal` as saved. No network calls yet. Swap `PropertyRepositoryImpl` for a Ktor-backed implementation in `RepositoryModule.kt` without touching any ViewModel or UI code.

### Ktor Client (ready, not yet called)

`NetworkModule.kt` configures `HttpClient(Android)` with:
- `ContentNegotiation { json(Json { ignoreUnknownKeys = true }) }`
- `Logging { level = LogLevel.BODY }`

Base URL template: `https://api.dorent.app/v1` — replace with actual API when available.

---

## 9. What Needs Real Implementation Next

These are stubs or not-yet-connected features in the current build:

| Item | Current State | What's Needed |
|------|--------------|---------------|
| Map on Detail | Static placeholder Box | Integrate Google Maps Compose or OSMapCompose |
| Search bar | UI-only, no filtering | Wire to search ViewModel + filter repository |
| Filter button (Newly Added) | Tappable, no action | Bottom sheet with filter options |
| Social login (Google/Apple) | Navigates to Home directly | Firebase Auth or custom OAuth flow |
| Forgot password | TextButton, no action | Auth reset flow |
| Personal information menu item | No navigation | Profile edit screen |
| My properties menu item | No navigation | Host-side listing management screen |
| Payment methods menu item | No navigation | Saved cards management screen |
| Privacy & security menu item | No navigation | Settings screen |
| Help center menu item | No navigation | FAQ / support screen |
| Add new card (Checkout) | UI-only | Card entry form |
| Mark all read (Notifications) | `markAllRead()` clears unread flags in memory | Persist to backend |
| Booking confirmed | In-memory only | POST to bookings API |
| Profile stats | Hardcoded "2 listings", "4.9 rating" | Pull from user API |
| Offline caching | None | Room database or DataStore for listing cache |
| Real images | picsum.photos placeholders | User-uploaded or API-provided URLs |
| Authentication state | None (no session) | DataStore token persistence, auth interceptor |

---

## 10. Shopnobilash Localization Notes

The current codebase uses `"New York, USA"` sample data. For Chittagong targeting:

- Replace `MOCK_PROPERTIES` with Chittagong neighborhoods: GEC Circle, Agrabad, Nasirabad, Panchlaish, Khulshi, Halishahar, Bayazid
- Property types relevant locally: `Flat`, `Bachelor`, `Family`, `Office`, `Shop`, `Sublet`
- Price in BDT (Taka) — update `formatPrice()` from `$` prefix to `৳` prefix
- Period options: `mo` (month), `yr` (year) — common in BD rental market
- Contact methods: WhatsApp deep link (`wa.me/+880...`) more common than in-app call
- Map: Bangladesh uses Google Maps well; OSM also viable and free
- Language: add Bengali (`bn`) string resources for `res/values-bn/strings.xml`
- Fonts: Plus Jakarta Sans has no Bengali glyphs — add **Hind Siliguri** or **Noto Sans Bengali** as fallback font family

---

## 11. File Structure Reference

```
app/src/main/java/com/dorent/app/
├── di/
│   ├── AppModule.kt          — ViewModels
│   ├── NetworkModule.kt      — Ktor HttpClient
│   └── RepositoryModule.kt   — Repository binding
├── navigation/
│   ├── AppNavHost.kt         — NavHost, all composable() destinations
│   └── Screen.kt             — Sealed class with route strings
├── ui/
│   ├── theme/
│   │   ├── Color.kt          — Brand palette + AppColors + MD3 schemes
│   │   ├── Type.kt           — Plus Jakarta Sans typography scale
│   │   ├── Shape.kt          — Corner radius constants
│   │   └── Theme.kt          — DORentTheme + appColors extension
│   ├── components/
│   │   ├── CommonComponents.kt  — AppTag, PriceText, PrimaryButton, RoundIconButton,
│   │   │                          StackHeader, InitialsAvatar, SaveToggleButton
│   │   ├── PropertyCards.kt     — PropertyCardVertical, PropertyCardHorizontal
│   │   └── BottomNavBar.kt      — NavigationBar with 4 tabs
│   └── feature/
│       ├── onboarding/       — SplashScreen.kt, LoginScreen.kt
│       ├── home/             — HomeScreen.kt, HomeViewModel.kt, HomeUiState.kt
│       ├── listing/          — NewlyAddedScreen.kt, ListingViewModel.kt, ListingUiState.kt
│       ├── detail/           — DetailScreen.kt, DetailViewModel.kt, DetailUiState.kt
│       ├── checkout/         — CheckoutScreen.kt, CheckoutViewModel.kt, CheckoutUiState.kt
│       ├── wishlist/         — WishlistScreen.kt, WishlistViewModel.kt, WishlistUiState.kt
│       ├── chat/             — ChatListScreen.kt, ChatThreadScreen.kt, ChatViewModel.kt
│       ├── notifications/    — NotificationsScreen.kt, NotificationsViewModel.kt
│       └── profile/          — ProfileScreen.kt, ProfileViewModel.kt
├── data/
│   ├── model/
│   │   └── Property.kt       — All data classes + MOCK_PROPERTIES, MOCK_CONVERSATIONS,
│   │                            MOCK_NOTIFICATIONS, helper functions
│   ├── remote/
│   │   ├── ApiService.kt     — Ktor client functions (template, not yet called)
│   │   └── dto/              — @Serializable response DTOs
│   └── repository/
│       ├── PropertyRepository.kt     — Interface
│       └── PropertyRepositoryImpl.kt — Mock in-memory implementation
├── DoRentApp.kt              — Application class, Koin startKoin
└── MainActivity.kt           — enableEdgeToEdge, DORentTheme, AppNavHost
```

---

## 12. Gradle Dependencies

```toml
compose-bom             = "2024.09.00"
navigation-compose      = "2.8.0"
lifecycle               = "2.8.5"
koin                    = "3.5.6"
ktor                    = "2.3.12"
kotlinx-serialization   = "1.7.1"
coil                    = "2.7.0"
googleFonts             = "1.7.0"
materialIconsExtended   = "1.7.0"
```

All dependencies in `gradle/libs.versions.toml` (version catalog). Build is `minSdk 26`, `targetSdk 35`, `compileSdk 35`.
