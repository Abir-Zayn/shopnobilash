# Authentication

Auth backed by **Appwrite** (Account API). Current scope: email/password registration + email verification via 6-digit OTP, email/password login, and Google OAuth2 sign-in.

## Stack

- **Backend:** Appwrite Cloud — endpoint `https://sgp.cloud.appwrite.io/v1`, project `shopnobilash` (`6a2bbc29001d7e1307a8`).
- **SDK:** `io.appwrite` Android SDK (`Account` service).
- **DI:** Koin. `appwriteModule` provides singleton `Client` + `Account`. See `di/AppwriteModule.kt`.
- **UI:** Jetpack Compose. State held in `AuthViewModel`, exposed as `StateFlow<AuthUiState>`.

## Flow

```
SignupScreen → (signUp) → OTP email sent → VerifyEmailScreen → (verifyOtp) → Home
```

1. User fills name, email, password, confirm password on `SignupScreen`.
2. `AuthViewModel.signUp()` validates input, creates the Appwrite user, then sends a 6-digit email OTP (`createEmailToken`).
3. On success → `OtpSent(userId, email)` → navigate to `VerifyEmailScreen`.
4. User enters the code. `verifyOtp()` calls `createSession(userId, secret = code)` — this both **creates a session** and **marks the email verified**.
5. On success → `EmailVerified` → navigate to Home.

## State machine — `AuthUiState`

| State | Meaning | UI reaction |
|-------|---------|-------------|
| `Idle` | initial / cleared | — |
| `Loading` | request in flight | spinners on buttons |
| `OtpSent(userId, email)` | signup ok, OTP sent | navigate to Verify screen |
| `OtpResent` | resend succeeded | success toast only |
| `EmailVerified` | OTP correct, session created | navigate to Home |
| `LoginSuccess` | email/password or Google sign-in ok | snackbar + navigate to Home |
| `Error(message)` | any failure | error snackbar |

## Registration (email + password)

`AuthViewModel.signUp(name, email, password, confirmPassword)`:

- Client validation:
  - all fields required
  - `password == confirmPassword`
  - password length ≥ 8
- Deletes any leftover `current` session first (Email OTP requires no active session).
- `account.create(userId = ID.unique(), email, password, name)`.
- `account.createEmailToken(userId, email)` → sends OTP.

## Login (email + password)

`AuthViewModel.login(email, password)`:

- Client validation: email + password both required.
- Deletes any leftover `current` session first (`createEmailPasswordSession` requires no active session).
- `account.createEmailPasswordSession(email, password)` → starts session.
- On success → `LoginSuccess` → navigate to Home.
- `LoginScreen` injects `AuthViewModel` via Koin, drives `LoginFormSection(isLoading, onSignIn)`, shows error snackbar.

## Google sign-in (OAuth2)

`AuthViewModel.loginWithGoogle(activity)`:

- Requires a `ComponentActivity` — `LoginScreen` resolves it from `LocalContext` via `Context.findActivity()` (tailrec unwrap of `ContextWrapper`).
- Deletes any leftover `current` session first.
- `account.createOAuth2Session(activity, provider = OAuthProvider.GOOGLE)` — opens the browser/custom tab for the Google consent flow.
- On success → `LoginSuccess` → snackbar + navigate to Home.
- Triggered from the Google circular button in `LoginFormSection` (`onGoogleSignIn`).
- `loginWithFacebook(activity)` exists (`OAuthProvider.FACEBOOK`) but the Facebook button is not yet wired (`onClick = {}`).

> **Setup required:** Google provider must be enabled in the Appwrite console (OAuth2 settings) with valid client ID/secret + redirect, else the flow fails.

## Email verification (OTP)

`VerifyEmailScreen` + `AuthViewModel`:

- `verifyOtp(userId, code)` → `account.createSession(userId, secret = code)`. Completing the email token verifies the email and starts the session.
- `resendOtp(userId, email)` → `account.createEmailToken(userId, email)` again → `OtpResent`.
- Code input constrained to 6 digits, numeric only. Verify button enabled only at length 6.
- "Skip for now" navigates straight to Home (verification not yet enforced as a gate).

## Navigation

`Screen.kt` routes:

- `Login` = `login`
- `Register` = `register`
- `VerifyEmail` = `verify_email/{userId}?email={email}` — `createRoute(userId, email)` URL-encodes email.

## Files

| File | Role |
|------|------|
| `ui/feature/auth/AuthViewModel.kt` | auth state + Appwrite calls |
| `ui/feature/auth/SignupScreen.kt` | registration UI, edge-swipe back to sign in |
| `ui/feature/auth/VerifyEmailScreen.kt` | OTP entry + resend |
| `ui/feature/auth/LoginScreen.kt` | login UI + email/password + Google sign-in; resolves `ComponentActivity` for OAuth |
| `ui/feature/auth/components/` | Signup/Login form + hero sections (`LoginFormSection` exposes `onGoogleSignIn`) |
| `di/AppwriteModule.kt` | Koin Client + Account singletons |
| `constants/AppwriteConfig.kt` | endpoint, project id |

## Done

- [x] Email/password registration (Appwrite `account.create`)
- [x] Email verification via 6-digit OTP (`createEmailToken` + `createSession`)
- [x] Resend OTP
- [x] Client-side validation + error/success snackbars
- [x] Signup ↔ Verify ↔ Home navigation
- [x] Login (email/password) via `account.createEmailPasswordSession`
- [x] Google OAuth2 sign-in via `account.createOAuth2Session`
- [x] Login success/failure snackbars (reusable `AppSnackbarHost`)

## Pending

- [ ] Session persistence / auto-login on launch
- [ ] Logout
- [ ] Password reset / forgot password
- [ ] Enforce email verification as a gate (currently "Skip for now" bypasses)
- [ ] Wire Facebook button to `loginWithFacebook` (VM method exists, button is no-op)
- [ ] Verify Google/Facebook providers enabled + configured in Appwrite console
