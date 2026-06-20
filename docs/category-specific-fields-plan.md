# Plan — Category-Specific Property Fields

**Status:** Phases 0–4 done (2026-06-21). Indexes deferred (open question). Live.
**Goal:** Let owners list Shop / Office / Coaching without residential-only fields (bedrooms) breaking the form, and capture commercial-specific data — cleanly, on live Appwrite data, with zero risky migration.

> Source of truth schema: [`docs/property-database.md`](./property-database.md). Update it once this lands.

---

## Problem

The `properties` table is residential-shaped:

- `bed_no` (int, **required**) and `bath_no` (int, **required**) force a "bedrooms" concept onto commercial listings, where it makes no sense.
- No place to store commercial fields: electricity meter type, office room sharing, listing advance amount.
- Listing form has no per-category branching — every category sees the same residential inputs.

Commercial room counts owners expect:

| Category | Typical rooms |
|---|---|
| Coaching | 1–4 |
| Office | 1–4 |
| Shop | 1–2 |

---

## Decision (council verdict)

**Additive nullable columns only. No JSON blob. No separate detail tables. No `required → optional` migration.**

Rationale:

- **No JSON** — Appwrite cannot index/query inside a JSON column. Filters like "shops with commercial meter under X rent" would break (client-side only). Display-only data could live in JSON, but our fields must stay filterable.
- **No detail tables** — Appwrite joins are painful (extra round-trips); ~90% of fields overlap across categories. Not worth 5x migration.
- **No required→optional flip** — flipping live `bed_no`/`bath_no` to optional risks re-validating/locking prod rows. Avoid entirely by **reframing** `bed_no` as a generic room count instead of nulling it.

---

## Core reframe

`bed_no` stops meaning "bedrooms" and becomes a **generic room count**. Every category has rooms (a 1-room shop = `bed_no` 1). `bath_no` is already universal.

- **Do NOT rename the columns** — breaks existing Kotlin code and queries. Keep `bed_no` / `bath_no` as IDs; relabel in UI only.
- **Keep both required** — no migration. Commercial always supplies a real count (≥1).
- **Kill the `0` studio hack** — `bed_no = 0` was the old studio/open-plan sentinel. Switch studios to `bed_no = 1` so `0` never carries hidden meaning. (Audit existing rows for `bed_no = 0` first — see Phase 0.)

---

## Schema changes — `properties`

All new columns **optional / nullable** → existing rows backfill `null`, zero downtime, no script.

| Column | Type | Required | Applies to | Notes |
|---|---|---|---|---|
| `bed_no` *(existing)* | int | ✅ keep | all | **Generic room count.** UI label: "Bedrooms" (House) / "Rooms" (Shop, Office, Coaching). No rename. |
| `bath_no` *(existing)* | int | ✅ keep | all | Already universal. |
| `meter_type` | enum (`Commercial`, `SubMeter`) | ❌ nullable | Shop / commercial | Null for residential. |
| `office_room_type` | enum (`Private`, `Shared`) | ❌ nullable | Office | Co-working vs private office. |
| `advance_amount` | float (double) | ❌ nullable (schema) / ✅ required at form for commercial | Shop / Office | **Listing-level asked advance.** Shop + Office share one column. Nullable in schema (residential = null) but **form requires it** for Shop / Office. |

### `advance_amount` vs `bookings.advance_payment` — NOT duplicates

- `properties.advance_amount` = the advance the **owner advertises/asks** on the listing (sticker number).
- `bookings.advance_payment` = the advance **actually agreed/paid** for a specific contract.

Different lifecycle, different table. Document both so nobody collapses them.

---

## Kotlin form branching

One `when(property_category)` drives **labels + visible fields + range validation**. Validation lives in the form, not the schema.

```
when (category) {
    House     -> label bed_no = "Bedrooms"; hide meter/office/advance
    Coaching  -> label bed_no = "Rooms" (1..4); hide meter/office/advance
    Office    -> label bed_no = "Rooms" (1..4); show office_room_type + advance_amount (advance_amount REQUIRED)
    Shop      -> label bed_no = "Rooms" (1..2); show meter_type + advance_amount (advance_amount REQUIRED)
}
```

- Hidden fields emit `null` in the create/update payload.
- `advance_amount` is **required at form level** for Shop / Office — block submit if empty (schema stays nullable for residential rows).
- Range caps (1–4 / 1–2) are **soft hints**, not hard rejects — an owner with 5 rooms must not be blocked. Warn, don't block.

---

## UX — plain labels (no jargon)

Owners/renters never see the DB. They see labels. Rewrite before storage matters:

| Raw field | User-facing label |
|---|---|
| `meter_type` | "Electricity: Own dedicated meter / Shared meter (billed by landlord)" |
| `office_room_type` | "Private office or Shared / co-working space?" |
| `advance_amount` | "Advance / deposit required (amount)" |

Drop "sub-meter" from user text — meaningless to a normal user. Tooltip optional.

---

## Phased rollout

### Phase 0 — Audit ✅ (2026-06-21)
- Existing `properties`: 1 row total, `bed_no`/`bath_no` both populated.
- Rows with `bed_no = 0`: **0** → no studio-sentinel backfill needed.
- Commercial rows: **0**.
- Read-path label sites confirmed: `PropertyRepositoryImpl.kt`, `wishlistCard.kt`, `PropertyCards.kt`, `DetailScreen.kt`, `PropertyCreatedScreen.kt`.
- Index count before change: **0** → ample headroom.

### Phase 1 — Schema ✅ (additive, zero downtime)
- Added `meter_type` (enum `Commercial`/`SubMeter`), `office_room_type` (enum `Private`/`Shared`), `advance_amount` (double, min 0) — all nullable, `available`. Existing row backfilled `null`.
- Indexes: **none added** — search-filter need is an open question. Add when a filter/sort path actually needs them.

### Phase 2 — App write path ✅
- `AddPropertyScreen.kt`: `when(category)` drives room label, conditional commercial fields, validation. `advance_amount` form-required for Shop/Office; room range is a soft warning, not a block.
- Studios write `bed_no = 1` (min 1; `0` sentinel removed).
- Model: `MeterType`, `OfficeRoomType` enums + `PropertyDraft` extended. Repo writes the 3 fields (omit/null for residential).

### Phase 3 — App read path ✅
- `Property.roomCountLabel` helper (category-aware) replaces hardcoded "Bedrooms" in `PropertyCards.kt`, `wishlistCard.kt`, `DetailScreen.kt`, `PropertyCreatedScreen.kt`.
- `DetailScreen.kt` surfaces commercial fields (meter / office space / advance) when present.

### Phase 4 — Docs ✅
- `docs/property-database.md` updated: new columns, `bed_no` reframe, `advance_amount` vs `advance_payment` distinction.

---

## Open questions
- Keep room caps as soft warnings or drop them entirely?
- Any need to filter by `meter_type` / `office_room_type` in search? If no → could skip their indexes.

---

## Rejected approaches
- **JSON `attributes` column** — unqueryable in Appwrite; breaks commercial filters.
- **Per-category detail tables** — join pain, 5x migration, fields overlap ~90%.
- **Flip `bed_no`/`bath_no` to optional** — risky migration on live required columns; unnecessary once `bed_no` is reframed as generic rooms.
- **Dynamic data-driven form schema stored in Appwrite** — over-engineered for 5 categories; solves a problem we don't have.
- **Duplicate advance money into shop/office columns** — collapsed into one `advance_amount`.
