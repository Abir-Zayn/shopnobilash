# Authentication

Auth backed by **Appwrite** (Account API). Scope: email/password registration + email OTP verification, email/password login, Google OAuth2, Facebook OAuth2, session-based auto-login, profile setup gate, and logout.

## Stack

- **Backend:** Appwrite Cloud — endpoint `https://sgp.cloud.appwrite.io/v1`, project `shopnobilash` (`6a2bbc29001d7e1307a8`).
- **SDK:** `io.appwrite` Android SDK (`Account`, `Databases` services).
- **DI:** Koin. `appwriteModule` provides singleton `Client`, `Account`, `Databases`. See `di/AppwriteModule.kt`.
- **UI:** Jetpack Compose. Auth state in `AuthViewModel` exposed as `StateFlow<AuthUiState>`.

---

## Full Flow

```
App Launch
    │
    ▼
SplashScreen → checkSession()
    ├── SessionValid (session + profile exists) ──────────────────────► Home
    ├── SessionValidNoProfile (session, no profile) ──────────────────► ProfileSetup ──► Home
    └── SessionInvalid (no session) → Onboarding Carousel → Login/Register
                                                                │
                                               ┌───────────────┴───────────────┐
                                          Register                           Login / Google OAuth
                                               │                                │
                                        OTP → VerifyEmail              LoginSuccess
                                               │                                │
                                               └───────────────┬───────────────┘
                                                               ▼
                                                         ProfileSetup
                                                      (checks if profile exists)
                                                    ├── Profile exists → Home
                                                    └── No profile → Show form → Save → Home
```

---

## State Machine — `AuthUiState`

| State | Meaning | UI reaction |
|-------|---------|-------------|
| `Idle` | initial / cleared | — |
| `Loading` | request in flight | spinner on button |
| `SessionChecking` | `account.get()` + profile lookup in flight | branded loading screen |
| `SessionValid` | session active + profile exists | navigate to Home |
| `SessionValidNoProfile` | session active, profile missing | navigate to ProfileSetup |
| `SessionInvalid` | no active session | show onboarding carousel |
| `OtpSent(userId, email)` | signup ok, OTP sent | navigate to VerifyEmail |
| `OtpResent` | resend succeeded | success toast only |
| `EmailVerified` | OTP correct, session created | navigate to ProfileSetup |
| `LoginSuccess` | email/password or OAuth ok | navigate to ProfileSetup |
| `LoggedOut` | session deleted | navigate to Login |
| `Error(message)` | any failure | error snackbar |

---

## Auto-Login (Session Persistence)

Appwrite sessions last **1 year** by default. The SDK stores the session cookie on-device automatically — no manual token management needed.

On every app launch, `SplashScreen` calls `viewModel.checkSession()`:

```kotlin
fun checkSession() = viewModelScope.launch {
    _uiState.value = AuthUiState.SessionChecking
    try {
        val user = account.get()
        val hasProfile = profileRepo.getProfile(user.id).getOrNull() != null
        _uiState.value = if (hasProfile) AuthUiState.SessionValid
                         else AuthUiState.SessionValidNoProfile
    } catch (_: AppwriteException) {
        _uiState.value = AuthUiState.SessionInvalid
    }
}
```

- `SessionValid` → straight to Home (no UI flicker)
- `SessionValidNoProfile` → ProfileSetup (completes onboarding)
- `SessionInvalid` → shows onboarding carousel

---

## Registration (Email + Password)

`AuthViewModel.signUp(name, email, password, confirmPassword)`:

- Validation: all fields required, `password == confirmPassword`, length ≥ 8.
- Deletes any leftover `current` session first (OTP flow requires none).
- `account.create(userId = ID.unique(), email, password, name)`.
- `account.createEmailToken(userId, email)` → sends 6-digit OTP.
- On success → `OtpSent` → navigate to `VerifyEmailScreen`.

---

## Login (Email + Password)

`AuthViewModel.login(email, password)`:

- Validation: both fields required.
- Deletes any leftover `current` session first.
- `account.createEmailPasswordSession(email, password)`.
- On success → `LoginSuccess` → navigate to `ProfileSetup`.

---

## Google / Facebook OAuth2

`AuthViewModel.loginWithGoogle(activity)` / `loginWithFacebook(activity)`:

- Requires a `ComponentActivity` — resolved from `LocalContext` in `LoginScreen`.
- Deletes any leftover session first.
- `account.createOAuth2Session(activity, provider)` — opens browser consent flow.
- On success → `LoginSuccess` → navigate to `ProfileSetup`.
- Facebook button exists in VM but is not yet wired in UI (`onClick = {}`).

> **Setup required:** Google/Facebook providers must be enabled in Appwrite console with valid client ID/secret + redirect URI.

---

## Email OTP Verification

`AuthViewModel.verifyOtp(userId, code)`:

- `account.createSession(userId, secret = code)` — creates session + marks email verified.
- On success → `EmailVerified` → navigate to `ProfileSetup`.

`AuthViewModel.resendOtp(userId, email)`:

- `account.createEmailToken(userId, email)` again → `OtpResent`.

---

## Profile Setup Gate

After any successful auth event (`LoginSuccess` or `EmailVerified`), nav goes to `ProfileSetup`.

`ProfileSetupViewModel` checks on init:
1. `account.get()` → gets `userId` and `email`.
2. `profileRepo.getProfile(userId)` → if profile exists → emit `ProfileExists` → auto-forward to Home.
3. If no profile → emit `ShowForm` → display form.

Required fields on form:
- Full Name, Phone Number, Gmail (pre-filled / read-only), Permanent Address
- Emergency Contact, Emergency Contact Recipient
- Identity Type (NID / Passport / Birth Certificate), Identity Number

On save → `databases.createDocument(documentId = userId, ...)` — ID matches Auth user ID.

---

## Logout

`ProfileViewModel.logout()`:

```kotlin
fun logout() = viewModelScope.launch {
    runCatching { account.deleteSession("current") }
    _loggedOut.value = true
}
```

- `AppNavHost` observes `loggedOut` state → navigates to `Login`, clears entire back stack.
- Session is deleted server-side; subsequent `account.get()` calls return 401.

---

## Navigation Routes

| Screen | Route | Trigger |
|--------|-------|---------|
| `Splash` | `splash` | start destination |
| `Login` | `login` | SessionInvalid / logout |
| `Register` | `register` | "Sign up" tap |
| `VerifyEmail` | `verify_email/{userId}?email={email}` | after signup OTP sent |
| `ProfileSetup` | `profile_setup` | LoginSuccess / EmailVerified / SessionValidNoProfile |
| `Home` | `home` | SessionValid / profile saved / profile exists |

---

## Files

| File | Role |
|------|------|
| `ui/feature/auth/AuthViewModel.kt` | auth state + session check + logout |
| `ui/feature/auth/SignupScreen.kt` | registration UI |
| `ui/feature/auth/VerifyEmailScreen.kt` | OTP entry + resend |
| `ui/feature/auth/LoginScreen.kt` | login UI + Google OAuth |
| `ui/feature/onboarding/SplashScreen.kt` | session check gate + onboarding carousel |
| `ui/feature/profile_setup/ProfileSetupViewModel.kt` | profile existence check + save |
| `ui/feature/profile_setup/ProfileSetupScreen.kt` | profile form UI |
| `ui/feature/profile/ProfileViewModel.kt` | profile stats + logout |
| `data/model/Profile.kt` | Profile data class + IdentityType enum |
| `data/repository/ProfileRepository.kt` | interface |
| `data/repository/ProfileRepositoryImpl.kt` | Appwrite Databases impl |
| `di/AppwriteModule.kt` | Koin: Client, Account, Databases singletons |
| `di/RepositoryModule.kt` | Koin: PropertyRepository, ProfileRepository |
| `di/AppModule.kt` | Koin: all ViewModels |
| `navigation/Screen.kt` | route definitions incl. ProfileSetup |
| `navigation/AppNavHost.kt` | full nav graph |
| `constants/AppwriteConfig.kt` | endpoint, project id, DB + table constants |

---

## Done

- [x] Email/password registration (`account.create`)
- [x] Email verification via 6-digit OTP (`createEmailToken` + `createSession`)
- [x] Resend OTP
- [x] Client-side validation + error/success snackbars
- [x] Login (email/password) via `createEmailPasswordSession`
- [x] Google OAuth2 via `createOAuth2Session`
- [x] Session persistence — auto-login on launch via `account.get()`
- [x] Profile existence check on session restore
- [x] ProfileSetup screen — form + Appwrite write with `documentId = userId`
- [x] ProfileSetup auto-skip to Home when profile already exists
- [x] Logout — `deleteSession("current")` + nav to Login
- [x] Logout wired through `ProfileViewModel` → `AppNavHost` observer

## Pending

- [ ] Wire Facebook button to `loginWithFacebook` (VM method exists, button is no-op)
- [ ] Enforce email verification as a gate (currently "Skip for now" bypasses it)
- [ ] Password reset / forgot password flow
- [ ] Profile image upload (Storage bucket)
- [ ] Verify Google/Facebook providers enabled + configured in Appwrite console

---

## Changelog

### 2026-06-16
- Added `checkSession()` to `AuthViewModel` — checks `account.get()` + profile existence on launch
- Added `SessionChecking`, `SessionValid`, `SessionValidNoProfile`, `SessionInvalid`, `LoggedOut` states to `AuthUiState`
- Added `logout()` to `AuthViewModel` and `ProfileViewModel`; wired in `AppNavHost` via `loggedOut` StateFlow observer
- `SplashScreen` now checks session on first composition; shows branded loading screen while checking, carousel only on `SessionInvalid`
- Created `ProfileRepository` interface + `ProfileRepositoryImpl` (Appwrite Databases)
- Created `ProfileSetupViewModel` — checks existing profile on init, exposes `prefillEmail` from `account.get()`
- Created `ProfileSetupScreen` — scrollable form with section labels, read-only email, multiline address, identity type dropdown
- Added `Databases` singleton to `appwriteModule`
- Added `ProfileRepository` to `repositoryModule`
- Added `ProfileSetupViewModel` to `appModule`; `ProfileViewModel` now also receives `Account` for logout
- Added `Screen.ProfileSetup` route; updated `AppNavHost` — `LoginSuccess`/`EmailVerified` now route to `ProfileSetup` instead of `Home`
- `ProfileSetupScreen` auto-navigates to `Home` when profile already exists (returning users skip the form)
