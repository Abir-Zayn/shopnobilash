# Property Database — Appwrite Schema

**Database ID:** `property_db`  
**Database Name:** Property Database  
**Platform:** Appwrite Tables DB  

---

## Tables Overview

| Table ID | Name | Purpose |
|---|---|---|
| `owners` | Owners | Property owner profiles |
| `properties` | Properties | Property listings |
| `property_owners` | Property Owners | Junction — links properties to owners |

---

## Table: `owners`

Stores property owner identity and contact information. An owner record is the
**listing/seller identity** of an app account — distinct from the renter
`profiles` record but bound to the same Appwrite Auth user via `user_id`.

| Column | Type | Required | Constraints | Notes |
|---|---|---|---|---|
| `$id` | string | auto | Primary Key | Appwrite auto-generated ID (kept random — `property_owners` relationships reference it) |
| `user_id` | string(36) | ❌ | **UNIQUE** | Appwrite Auth `$id` of the owning account. Nullable for legacy/admin-created rows. One owner record per account. |
| `name` | string(255) | ✅ | — | Full name of owner |
| `address_line1` | string(255) | ✅ | — | Primary address |
| `address_line2` | string(255) | ❌ | — | Secondary address (apt, floor, etc.) |
| `tin_certificate_no` | string(50) | ❌ | **UNIQUE** | Tax Identification Number |
| `profile_picture_url` | string(2048) | ❌ | — | URL to profile image |

> No `created_at` / `updated_at` columns on this table — rely on the Appwrite
> system fields `$createdAt` / `$updatedAt`. Do **not** send `created_at` /
> `updated_at` in the create payload; the collection has no such attributes and
> Appwrite rejects the write with `Invalid document structure: Unknown attribute`.

### Indexes

| Index Key | Type | Columns |
|---|---|---|
| `idx_tin_unique` | UNIQUE | `tin_certificate_no` |
| `idx_user_id_unique` | UNIQUE | `user_id` |

### Account ↔ Owner Binding

- **One account = one owner.** `user_id` mirrors the Auth `$id`, UNIQUE-indexed.
  An account can own many properties (via `property_owners`) but never more than
  one `owners` record. Duplicate insert throws `409` — surface as
  "You already have an owner account."
- **`user_id` is nullable**, not required — existing/admin-created owner rows
  predate the account link and stay null. Never make it required (breaks backfill).
- **`$id` stays random.** Do NOT set `owners.$id = Auth $id`: it is immutable in
  Appwrite and existing `property_owners` relationships already reference it.
- **Resolve ownership by session:**

```kotlin
val ownerRow = databases.listDocuments(
    databaseId = PROPERTY_DATABASE_ID,
    collectionId = TABLE_OWNERS,
    queries = listOf(Query.equal("user_id", account.get().id))
).documents.firstOrNull()
```

### "Become an Owner" Flow

1. Gate the action behind an **existing `profiles` row** — guarantees `user_id`
   always maps to a real profile (no orphan owners).
2. Pre-fill `name` / address from the user's `profiles` data; user supplies
   `tin_certificate_no`.
3. `databases.createDocument(TABLE_OWNERS, documentId = ID.unique(), data = mapOf("user_id" to account.get().id, ...))`.
4. Catch `409` on the UNIQUE index → user already an owner; route to owner dashboard.

> `profiles.identity_number` (renter NID/Passport) and `owners.tin_certificate_no`
> (business tax id) are separate identity fields by design — they serve different
> KYC purposes and are not duplicates.

---

## Table: `properties`

Stores property listing details, availability, and media.

| Column | Type | Required | Constraints | Notes |
|---|---|---|---|---|
| `$id` | string | auto | Primary Key | Appwrite auto-generated ID |
| `house_name` | string(255) | ✅ | — | Display name of property |
| `bed_no` | integer | ✅ | — | Number of bedrooms |
| `bath_no` | integer | ✅ | — | Number of bathrooms |
| `area_sqft` | integer | ✅ | — | Total area in square feet |
| `rent` | float (double) | ✅ | — | Monthly rent amount |
| `status` | enum | ❌ | Default: `Available` | See allowed values below |
| `contract_terms` | string(65535) | ❌ | — | Rental contract text |
| `description` | string(65535) | ❌ | — | Property description |
| `floor` | string(20) | ❌ | — | Floor/flat identifier e.g. `7A`, `8E` |
| `property_img` | string(2048)[] | ❌ | Array | List of property image URLs |
| `property_category` | enum | ✅ | — | Intended use of the property |
| `ads_created_on` | datetime | ❌ | — | When listing was published (ISO 8601) |
| `updated_on` | datetime | ❌ | — | Last update timestamp (ISO 8601) |

### `property_category` Allowed Values

| Value | Meaning |
|---|---|
| `House` | Residential rental |
| `Coaching` | Coaching centre / tuition space |
| `Office` | Corporate / professional workspace |
| `Shop` | Retail shop |
| `Showroom` | Display / showroom space |

### `status` Allowed Values

| Value | Meaning |
|---|---|
| `Available` | Property open for rent (default) |
| `Rented` | Currently occupied |
| `Maintenance` | Temporarily unavailable |

---

## Table: `property_owners`

Junction table — maps properties to their owners. Supports multiple owners per property and multiple properties per owner.

| Column | Type | Required | Constraints | Notes |
|---|---|---|---|---|
| `$id` | string | auto | Primary Key | Appwrite auto-generated ID |
| `property_id` | relationship | ❌ | → `properties.$id` ON DELETE CASCADE | Many-to-one |
| `owner_id` | relationship | ❌ | → `owners.$id` ON DELETE CASCADE | Many-to-one |

### Relationship Details

| Key | Related Table | Relation Type | On Delete | Two-Way |
|---|---|---|---|---|
| `property_id` | `properties` | `manyToOne` | CASCADE | No |
| `owner_id` | `owners` | `manyToOne` | CASCADE | No |

> Deleting a property or owner automatically removes all related `property_owners` rows.

---

## Table: `bookings`

Tracks rental contracts between tenants and property owners.

| Column | Type | Required | Constraints | Notes |
|---|---|---|---|---|
| `$id` | string | auto | Primary Key | Appwrite auto-generated ID |
| `property_id` | relationship | ❌ | → `properties.$id` ON DELETE CASCADE | Many-to-one |
| `tenant_id` | string(36) | ✅ | — | Appwrite Auth `$id` of the renter |
| `start_date` | datetime | ✅ | — | Lease start date (ISO 8601) |
| `advance_payment` | float (double) | ✅ | — | Security deposit / down payment |
| `is_paid` | boolean | ❌ | Default: `false` | Advance payment cleared? |
| `monthly_rent` | float (double) | ✅ | — | Rent locked in for this contract |
| `contract_duration` | integer | ✅ | — | Duration in **months** e.g. `18` |
| `total_amount` | float (double) | ✅ | — | `contract_duration × monthly_rent` — calculated and written by app |
| `booking_status` | enum | ❌ | Default: `Pending` | See allowed values below |

### `booking_status` Allowed Values

| Value | Meaning | App Side-Effect |
|---|---|---|
| `Pending` | Tenant applied, awaiting owner review | Property `status` stays `Available` |
| `Approved` | Owner accepted, awaiting advance payment | Property `status` stays `Available` |
| `Active` | Advance paid, tenant moved in | Set property `status` → `Rented` |
| `Terminate` | Lease completed or broken | Set property `status` → `Available` |

### Relationship Details

| Key | Related Table | Relation Type | On Delete |
|---|---|---|---|
| `property_id` | `properties` | `manyToOne` | CASCADE |

> `total_amount` is computed by the Kotlin app (`contract_duration * monthly_rent`) before writing — stored as a float for historical record keeping.

---

## Table: `reviews`

Holds community accountability ratings — tenants review properties, owners review tenants.

| Column | Type | Required | Constraints | Notes |
|---|---|---|---|---|
| `$id` | string | auto | Primary Key | Appwrite auto-generated ID |
| `property_id` | relationship | ❌ | → `properties.$id` ON DELETE CASCADE | Nullable. Property being reviewed |
| `reviewee_id` | string(36) | ❌ | — | Nullable. Appwrite Auth `$id` of target tenant |
| `reviewer_id` | string(36) | ✅ | — | Appwrite Auth `$id` of person leaving feedback |
| `ratings` | integer | ✅ | min: 1, max: 5 | Star rating scale |
| `comment` | string(5000) | ❌ | — | Free-text feedback |
| `images_url` | string(2048)[] | ❌ | Array | Photo proof attachments |
| `total_like` | integer | ❌ | Default: `0`, min: 0 | Helpfulness upvotes |
| `total_dislike` | integer | ❌ | Default: `0`, min: 0 | Downvotes |
| `is_visible` | boolean | ❌ | Default: `true` | Admin kill-switch — set `false` to hide |
| `created_at` | datetime | ❌ | — | ISO 8601 |
| `updated_at` | datetime | ❌ | — | ISO 8601 |

### Review Type Logic

| `property_id` | `reviewee_id` | Review Type |
|---|---|---|
| set | null | Tenant → Property review |
| null | set | Owner → Tenant review |
| set | set | Invalid — avoid both on same document |

### Relationship Details

| Key | Related Table | Relation Type | On Delete |
|---|---|---|---|
| `property_id` | `properties` | `manyToOne` | CASCADE |

> `total_like` / `total_dislike` are counters incremented by app logic — not derived from a separate votes table. Use Appwrite atomic increment via `Query` update when users vote.

---

## Table: `profiles`

Extended user data for renters/app users. `$id` **must** be set manually to match Appwrite Auth `$id`.

| Column | Type | Required | Constraints | Notes |
|---|---|---|---|---|
| `$id` | string | manual | Primary Key | Set to Appwrite Auth `$id` — never use `ID.unique()` |
| `full_name` | string(255) | ✅ | — | Legal full name |
| `phone_number` | string(20) | ✅ | — | Primary contact number |
| `gmail` | string(255) | ✅ | — | Primary email address |
| `profile_picture_url` | string(2048) | ❌ | — | Avatar image URL |
| `permanent_address` | string(500) | ✅ | — | Home district / permanent address |
| `blood_group` | enum | ❌ | — | `A+` `A-` `B+` `B-` `AB+` `AB-` `O+` `O-` |
| `occupation` | string(100) | ❌ | — | e.g. `Student`, `Day Laborer`, `Garments Worker` |
| `emergency_contact` | string(20) | ✅ | — | Backup phone number |
| `emergency_contact_recipient` | string(100) | ✅ | — | Relation/name e.g. `Father`, `Spouse` |
| `identity_type` | enum | ✅ | — | `NID`, `Passport`, `Birth Certificate` |
| `identity_number` | string(50) | ✅ | **UNIQUE** | Card/document number |
| `identity_image_url` | string(2048) | ❌ | — | Storage bucket URL for uploaded document photo |
| `is_verified` | boolean | ❌ | Default: `false` | Admin manually flips to `true` after document check |
| `total_listings` | integer | ❌ | Default: `0`, min: 0 | Count of properties this user owns/listed |
| `total_reviews` | integer | ❌ | Default: `0`, min: 0 | Count of reviews involving this user |
| `ratings` | integer | ❌ | Default: `0`, min: 0 | Aggregate rating — placeholder, scoring logic TBD |
| `created_at` | datetime | ❌ | — | ISO 8601 |
| `updated_at` | datetime | ❌ | — | ISO 8601 |

> `total_listings` / `total_reviews` / `ratings` are denormalized counters, all
> default `0`. Wired to live updates + cron reconciliation later — see council
> notes. For now: stored, default 0, untouched.

### Indexes

| Index Key | Type | Columns |
|---|---|---|
| `idx_identity_number_unique` | UNIQUE | `identity_number` |

### Kotlin — Manual ID Match Pattern

```kotlin
val userAuthId = account.get().id

databases.createDocument(
    databaseId = PROPERTY_DATABASE_ID,
    collectionId = TABLE_PROFILES,
    documentId = userAuthId, // matches Auth $id exactly
    data = mapOf(
        "full_name" to "Mehedi Hasan",
        "phone_number" to "+88018XXXXXXXX",
        // ...
    )
)
```

> Never call `ID.unique()` for profiles — the `$id` must mirror the Auth user ID so a single lookup by Auth session resolves the profile without a query.

---

## Table: `wishlist`

Tracks properties bookmarked by users.

| Column | Type | Required | Constraints | Notes |
|---|---|---|---|---|
| `$id` | string | auto | Primary Key | Appwrite auto-generated ID |
| `property_id` | relationship | ❌ | → `properties.$id` ON DELETE CASCADE | Property being saved |
| `user_id` | string(36) | ✅ | — | Appwrite Auth `$id` of the user |
| `created_at` | datetime | ❌ | — | ISO 8601 — when bookmarked |

### Duplicate Prevention — App-Layer Guard

Appwrite cannot index relationship columns, so the composite unique constraint is enforced in Kotlin before insert:

```kotlin
// Check before saving to wishlist
val existing = databases.listDocuments(
    databaseId = PROPERTY_DATABASE_ID,
    collectionId = TABLE_WISHLIST,
    queries = listOf(
        Query.equal("user_id", currentUserId),
        Query.equal("property_id", propertyId)
    )
)
if (existing.total == 0L) {
    // Safe to insert
    databases.createDocument(...)
}
```

> Cascade delete is handled by the relationship — when a property is deleted, all wishlist rows referencing it are wiped automatically.

### Relationship Details

| Key | Related Table | Relation Type | On Delete |
|---|---|---|---|
| `property_id` | `properties` | `manyToOne` | CASCADE |

---

## Table: `notifications`

User-specific alerts, system reminders, and transactional status updates.

| Column | Type | Required | Constraints | Notes |
|---|---|---|---|---|
| `$id` | string | auto | Primary Key | Appwrite auto-generated ID |
| `user_id` | string(36) | ✅ | — | Appwrite Auth `$id` of recipient |
| `title` | string(255) | ✅ | — | Bold header text |
| `message` | string(1000) | ✅ | — | Body text of the alert |
| `notification_type` | enum | ✅ | — | `System`, `Booking`, `Payment`, `Reminder` |
| `action_route` | string(255) | ❌ | — | Android nav destination e.g. `review_submission_page` |
| `action_target_id` | string(255) | ❌ | — | ID needed for nav e.g. property or booking `$id` |
| `is_read` | boolean | ❌ | Default: `false` | Flipped to `true` when user opens notification |
| `created_at` | datetime | ❌ | — | ISO 8601 |

### `notification_type` Allowed Values

| Value | Trigger |
|---|---|
| `System` | Platform-wide announcements |
| `Booking` | Booking status changes (Approved / Active / Terminate) |
| `Payment` | Advance payment cleared |
| `Reminder` | Cron-triggered prompts (e.g. 30-day review nudge) |

### Deep-Link Navigation Pattern

`action_route` + `action_target_id` together tell the Kotlin app which screen to open and with what data:

| `notification_type` | `action_route` | `action_target_id` |
|---|---|---|
| `Reminder` | `review_submission_page` | `property_$id` |
| `Booking` | `booking_details_page` | `booking_$id` |
| `Payment` | `payment_details_page` | `booking_$id` |

### Use Cases

**30-Day Review Prompt** — Appwrite Function cron job checks active bookings where `start_date` was 30 days ago. If no review exists → inserts notification with `type=Reminder`, `action_route=review_submission_page`.

**Booking Confirmation** — Immediately after `booking_status` flips to `Approved` or `Active`, Kotlin inserts notification with `type=Booking`, `action_route=booking_details_page`, `action_target_id=booking.$id`.

---

## Kotlin Constants

Defined in `constants/AppwriteConfig.kt`:

```kotlin
const val PROPERTY_DATABASE_ID   = "property_db"
const val TABLE_OWNERS           = "owners"
const val TABLE_PROPERTIES       = "properties"
const val TABLE_PROPERTY_OWNERS  = "property_owners"
const val TABLE_BOOKINGS         = "bookings"
```

---

## Notes

- Appwrite does not support `required = true` with a `default` value on the same column. `bed_no` and `bath_no` are required — app code must always supply a value (use `0` for studio/open-plan).
- `property_img` is stored as a string array; each entry is a URL (max 2048 chars). Order is preserved as inserted.
- `tin_certificate_no` uniqueness is enforced via the `idx_tin_unique` index, not a DB constraint — handle duplicate errors (`409`) in app code.
- `$createdAt` / `$updatedAt` are Appwrite system fields on every document and can supplement `created_at` / `updated_on` if needed.
