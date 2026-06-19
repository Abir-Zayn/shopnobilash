# Shopnobilash — স্বপ্নবিলাস বাড়ি খোঁজা এখন সহজ

> A modern Android app for finding and renting properties in Bangladesh — built for renters who are tired of walking around looking for faded "To Let" signs.

---

## What is Shopnobilash?

Shopnobilash is a native Android rental marketplace for Bangladesh. It connects renters directly with verified landlords through a single app — no middlemen, no broker fees, no scam listings.

The name "স্বপ্নবিলাস" loosely translates to "find your abode" — exactly what the app helps you do.

---

## The Problem

Finding a rental in Bangladesh today looks like this:

- Walk or drive through neighborhoods hunting for hand-written "To Let" signs
- Call landlords from numbers scrawled on gates — half don't pick up
- Rely on brokers who charge 1–2 months' rent as commission
- Scroll through unverified Facebook groups full of scams and outdated posts
- No way to know if the landlord is real or if the property is still available

---

## How Shopnobilash Solves It

| Problem | Solution |
|---|---|
| No central listing source | Aggregated verified listings in one place |
| Scam listings | Admin-reviewed identity verification for landlords |
| Broker dependency | Direct renter-to-landlord chat, zero commission |
| No availability info | Real-time listing status and notifications |
| Can't save favorites | Wishlist to bookmark properties |
| Paper applications | In-app booking and checkout flow |

---

## Features

- **Browse listings** — filter by area, price, bedrooms
- **Property detail** — photos, description, owner info
- **Newly added** — latest listings first
- **Wishlist** — save properties for later
- **In-app chat** — message landlords per property without sharing your number
- **Booking & checkout** — submit rental requests in-app
- **Identity verification** — landlords upload documents; admin approves/rejects
- **Notifications** — real-time alerts for bookings, verification status, messages
- **Profile management** — avatar upload, personal info
- **Google sign-in** — one-tap OAuth login
- **OTP email verification** — verified accounts only

---

## Why Better Than Existing Rental Apps in Bangladesh

Most rental platforms in Bangladesh (Bikroy, Bproperty, Lamudi) are built for the web first — the mobile experience is an afterthought. Here is what Shopnobilash does differently:

| Feature | Shopnobilash | Typical BD Rental Sites |
|---|---|---|
| Native Android (Compose) | ✅ | ❌ Web-wrapped |
| Landlord identity verification | ✅ Admin-reviewed | ❌ Self-declared |
| In-app direct chat | ✅ Per-property threads | ❌ Exposes phone number |
| Broker-free | ✅ Zero commission | ❌ Broker-dependent |
| Real-time notifications | ✅ Push + in-app | ❌ Email only or none |
| OTP-verified accounts | ✅ | ❌ |
| Wishlist / saved properties | ✅ | Rarely |
| Offline saved listings | Planned | ❌ |

---

## Tech Stack

### Android
| Layer | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose + Material 3 |
| Architecture | MVVM + Clean Architecture |
| Dependency Injection | Koin |
| Navigation | Navigation Compose |
| Image Loading | Coil |
| Networking | Ktor |
| Serialization | Kotlinx Serialization |

### Backend (Appwrite)
| Service | Usage |
|---|---|
| Auth | Email/OTP + Google OAuth |
| Database | Properties, bookings, profiles, chat, notifications |
| Storage | Profile photos, property images, verification documents |
| Functions | Server-side admin actions (Node.js 18) |

### Architecture Pattern
```
presentation/   → Compose UI + ViewModels
domain/         → Use cases (business logic)
data/           → Repositories + Appwrite SDK calls
di/             → Koin modules
```

---

## 📥 Download

<div align="center">

### 🤖 Android APK — v1.2

[![⬇️ Download APK](https://img.shields.io/badge/⬇️_Download-APK_v1.2-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://drive.google.com/file/d/1SdK4BSFELU6kliUu5CVS9wC-QjP3eYbg/view?usp=sharing)

**[⬇️ Download Shopnobilash v1.2 (.apk)](https://drive.google.com/file/d/1SdK4BSFELU6kliUu5CVS9wC-QjP3eYbg/view?usp=sharing)**

</div>

> 📱 **Install steps:** Download the APK → open it → if prompted, allow **"Install from unknown sources"** for your browser/file manager → tap **Install**.
>
> Requires **Android 8.0 (API 26)** or newer.

---

## Screenshots

> Coming soon.

---

## Build & Run (for contributors)

1. Clone the repo
2. Open in Android Studio Hedgehog or newer
3. Create an `appwrite-config.properties` file or update `AppwriteConfig.kt` with your own Appwrite project details
4. Run on Android 8.0+ (API 26+)

```bash
git clone https://github.com/AbirZayn/shopnobilash.git
cd shopnobilash
# Open in Android Studio → Run
```

---

## License

MIT
