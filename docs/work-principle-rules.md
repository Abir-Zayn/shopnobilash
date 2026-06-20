# Shopnobilash - Android Development Standards & Work Principles

Welcome to the development guidelines for **Shopnobilash**. Adhering to these principles ensures that the app remains performant, testable, crash-free, and highly maintainable.

---

## Table of Contents

- [1. Coroutines & Concurrency](#1-coroutines--concurrency)
  - [1.1 Never runBlocking on the Main Thread](#11-never-runblocking-on-the-main-thread)
  - [1.2 Avoid GlobalScope](#12-avoid-globalscope)
  - [1.3 Avoid Wrapping async/await in ViewModels](#13-avoid-wrapping-asyncawait-in-viewmodels)
- [2. State & Flow Management](#2-state--flow-management)
  - [2.1 Don't Collect Flows in init Blocks](#21-dont-collect-flows-in-init-blocks)
  - [2.2 Expose Read-Only StateFlow, Not MutableStateFlow](#22-expose-read-only-stateflow-not-mutablestateflow)
  - [2.3 Always Handle Flow Exceptions using catch](#23-always-handle-flow-exceptions-using-catch)
- [3. Kotlin & Language Best Practices](#3-kotlin--language-best-practices)
  - [3.1 Prefer lazy or Nullable over lateinit](#31-prefer-lazy-or-nullable-over-lateinit)
- [4. Jetpack Compose Optimization](#4-jetpack-compose-optimization)
  - [4.1 Avoid Unstable Parameters](#41-avoid-unstable-parameters)
  - [4.2 Image Loading with Coil Compose](#42-image-loading-with-coil-compose)
- [5. Networking & Data Layer (Retrofit)](#5-networking--data-layer-retrofit)
  - [5.1 URL Manipulation](#51-url-manipulation)
  - [5.2 Request Body & Form Data](#52-request-body--form-data)
  - [5.3 Header Manipulation](#53-header-manipulation)
  - [5.4 Kotlin Coroutine Support & Response Handling](#54-kotlin-coroutine-support--response-handling)
  - [5.5 Hilt & Serialization Configuration](#55-hilt--serialization-configuration)
  - [5.6 Error Handling in Repositories](#56-error-handling-in-repositories)

---

## 1. Coroutines & Concurrency

### 1.1 Never runBlocking on the Main Thread
* **Rule**: Never block the Main (UI) thread using `runBlocking`.
* **Why**: `runBlocking` blocks the calling thread until all coroutines within its block complete. Executing this on the Main thread freezes the UI, leading to lag, frame drops, and eventually ANR (Application Not Responding) dialogues.
* **Flutter Analogy**: Awaiting a synchronous, heavy blocking call inside the `build()` method of a widget.
* **Solution**: Use structured concurrency with lifecycle-aware scopes (like `viewModelScope.launch` or `lifecycleScope.launch`) to execute asynchronous workloads.

```kotlin
// ❌ Avoid - UI thread blocks until repository finishes
fun loadData() {
    runBlocking {
        repository.fetchData()
    }
}

//  Preferred - Async execution on lifecycle-managed scope
fun loadData() {
    viewModelScope.launch {
        repository.fetchData()
    }
}
```

---

### 1.2 Avoid GlobalScope
* **Rule**: Do not launch coroutines using `GlobalScope`.
* **Why**: Coroutines launched via `GlobalScope` operate on a global lifetime. They are not tied to any visual screen or ViewModel lifecycle. If the user navigates away and the host ViewModel is destroyed, a `GlobalScope` coroutine continues executing, causing memory leaks and null-pointer/lifecycle crashes (e.g. attempting to update UI elements that no longer exist).
* **Flutter Analogy**: Forgetting to cancel/dispose a `StreamSubscription` in a `StatefulWidget`'s `dispose()` lifecycle method.
* **Solution**: Use structured concurrency. Bind coroutines to the scope of their container (e.g., `viewModelScope`, `lifecycleScope`).

```kotlin
// ❌ Avoid - Keeps running after ViewModel is cleared
GlobalScope.launch {
    repository.saveData(data)
}

//  Preferred - Auto-cancelled when ViewModel is destroyed
viewModelScope.launch {
    repository.saveData(data)
}
```

---

### 1.3 Avoid Wrapping async/await in ViewModels
* **Rule**: Do not write raw async/await orchestration patterns directly within ViewModels.
* **Why**: ViewModels should organize and present UI state, not manage low-level async plumbing. Having a ViewModel launch a coroutine and immediately await multiple results leaks data/business layer concerns into the UI controller.
* **Flutter Analogy**: Implementing a raw `Future.microtask { }` directly inside a `ChangeNotifier` getter rather than delegating tasks to a domain service or repository.
* **Solution**: Extract concurrent business operations into a UseCase or Repository, returning unified results to the ViewModel.

```kotlin
// ❌ Avoid - Async orchestration and plumbing leaks into the ViewModel
class MainViewModel : ViewModel() {
    fun loadUserProfile() {
        viewModelScope.launch {
            val details = async(Dispatchers.IO) { api.getDetails() }
            val stats = async(Dispatchers.IO) { api.getStats() }
            _state.value = combine(details.await(), stats.await())
        }
    }
}

//  Preferred - Decoupled business logic inside UseCase / Repository
class MainViewModel @Inject constructor(
    private val getUserProfileUseCase: GetUserProfileUseCase
) : ViewModel() {
    fun loadUserProfile() {
        viewModelScope.launch {
            _state.value = getUserProfileUseCase()
        }
    }
}
```

---

## 2. State & Flow Management

### 2.1 Don't Collect Flows in init Blocks
* **Rule**: Avoid starting Flow collection within the `init { ... }` block of a ViewModel.
* **Why**: The ViewModel's `init` block is invoked immediately upon creation and stays active for the entire duration of the ViewModel's life. If you collect flows here, they execute continuously. If the UI is hidden or backgrounded, resources are wasted, and updating Composable states from background threads can trigger runtime crashes or overhead.
* **Solution**: Always collect flows in the UI using lifecycle-aware API collectors, such as `repeatOnLifecycle(Lifecycle.State.STARTED)` or `collectAsStateWithLifecycle()`. This ensures flow collection starts when the screen is visible and automatically pauses/resumes as needed.

```kotlin
// UI collection example (in Composable)
val uiState by viewModel.uiState.collectAsStateWithLifecycle()
```

---

### 2.2 Expose Read-Only StateFlow, Not MutableStateFlow
* **Rule**: ViewModels must never expose mutable state containers like `MutableStateFlow` directly to the UI.
* **Why**: Encapsulation and unidirectional data flow (UDF). Only the ViewModel should mutate its own internal state. Exposing a mutable state container allows Composable screens to modify the state directly, breaking the source-of-truth flow and making debugging difficult.
* **Flutter Analogy**: Exposing a `ValueNotifier` or a mutable controller directly, rather than exposing a read-only `ValueListenable` to views, or using Riverpod where consumers read provider states rather than mutating state notifier properties directly.

```kotlin
class HomeViewModel : ViewModel() {
    // 1. Private mutable state container
    private val _uiState = MutableStateFlow(HomeUiState())
    
    // 2. Public read-only exposure
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
}
```

---

### 2.3 Always Handle Flow Exceptions using catch
* **Rule**: Always append the `.catch { ... }` operator when setting up flow stream collection.
* **Why**: Uncaught exceptions thrown inside flow collectors/operators cancel the entire flow pipeline silently. This leads to broken UI states or application crashes.
* **Solution**: Use the `.catch` operator to handle exceptions gracefully, log errors, and emit fallback error states.

```kotlin
repository.getItems()
    .catch { exception ->
        // Handle error and update state
        _uiState.update { it.copy(error = exception.message) }
    }
    .collect { items ->
        _uiState.update { it.copy(items = items) }
    }
```

---

## 3. Kotlin & Language Best Practices

### 3.1 Prefer lazy or Nullable over lateinit
* **Rule**: Avoid using `lateinit var` for optional, nullable, or expensive variables. Use standard nullability (`?`) or Kotlin's `lazy` delegate instead.
* **Why**: A `lateinit` variable throws an `UninitializedPropertyAccessException` if accessed before initialization, and it cannot hold a `null` value. If a dependency is truly optional, represent it as nullable. If it is a heavy resource, use `lazy { ... }` to defer its instantiation until first access.

```kotlin
// ❌ Avoid - Risk of uninitialized crashes, cannot reset to null
private lateinit var analyticsTracker: AnalyticsTracker

//  Preferred - Explicitly represented as nullable
private var analyticsTracker: AnalyticsTracker? = null

//  Preferred - Loaded on first access
private val imageLoader by lazy { ImageLoader(context) }
```

---

## 4. Jetpack Compose Optimization

### 4.1 Avoid Unstable Parameters
* **Rule**: Do not pass unstable data models (e.g. mutable standard lists or objects without stable properties) to Composables.
* **Why**: Jetpack Compose checks parameters for stability to determine whether it can skip recomposing a function when its parent recomposes. Unstable parameters cause Compose to always rebuild the Composable, leading to UI lag and missed frame budgets.
* **Flutter Analogy**: Declaring widgets as `const` allows Flutter to skip rebuilding them during parent layout rebuilds.
* **Solution**: Wrap lists in immutable collections (`ImmutableList` from `kotlinx.collections.immutable`) or annotate custom domain/DTO classes with `@Stable` or `@Immutable`.

```kotlin
// ❌ Avoid - Standard List is unstable; UI recomposes unnecessarily
@Composable
fun ItemList(items: List<Item>) { ... }

//  Preferred - Using kotlinx immutable collection
@Composable
fun ItemList(items: ImmutableList<Item>) { ... }

//  Preferred - Annotating data classes
@Immutable
data class Item(val id: String, val name: String)
```

---

### 4.2 Image Loading with Coil Compose
When loading remote or local image resources in Jetpack Compose, use **Coil** (Coroutines Image Loader), which is the standard, most performant, and coroutines-integrated option.

#### 4.2.1 Primary Composable: `AsyncImage`
For standard image loading, use `AsyncImage`. It handles sizing, scaling, and placeholder states automatically.

```kotlin
AsyncImage(
    model = ImageRequest.Builder(LocalContext.current)
        .data("https://example.com/image.jpg")
        .crossfade(true)
        .build(),
    placeholder = painterResource(R.drawable.placeholder),
    error = painterResource(R.drawable.error),
    contentDescription = stringResource(R.string.description),
    contentScale = ContentScale.Crop,
    modifier = Modifier.clip(CircleShape)
)
```

#### 4.2.2 Low-Level Control: `rememberAsyncImagePainter`
Use `rememberAsyncImagePainter` only when a `Painter` is strictly required (e.g., inside a Compose `Canvas` or `Icon`), or when you need manual control over the image lifecycle.

> [!WARNING]
> `rememberAsyncImagePainter` does not automatically detect the Composable's size and defaults to loading the full, original dimension of the image. Use `AsyncImage` unless a painter is absolutely required.

```kotlin
val painter = rememberAsyncImagePainter(
    model = ImageRequest.Builder(LocalContext.current)
        .data("https://example.com/image.jpg")
        .size(Size.ORIGINAL) // Specify size when using painter manually
        .build()
)
```

#### 4.2.3 Slot API: `SubcomposeAsyncImage`
Use `SubcomposeAsyncImage` if you require custom layouts/slots for loading, success, or error states.

> [!CAUTION]
> Subcomposition is slower and resource-intensive. Avoid using `SubcomposeAsyncImage` in high-performance scrolls like `LazyColumn` or `LazyRow` lists.

```kotlin
SubcomposeAsyncImage(
    model = "https://example.com/image.jpg",
    contentDescription = null,
    loading = {
        CircularProgressIndicator()
    },
    error = {
        Icon(Icons.Default.Error, contentDescription = null)
    }
)
```

#### 4.2.4 Performance Best Practices
1. **Singleton ImageLoader**: Use a single `ImageLoader` instance globally to maximize caching behavior across disk and memory.
2. **Main-Safe**: Coil executes image processing pipelines off the main thread automatically.
3. **Crossfade**: Always enable `crossfade(true)` in `ImageRequest` for elegant, hardware-accelerated transitions.
4. **Sizing**: Configure `contentScale` and size limits properly to avoid decoding larger image assets than necessary.

---

## 5. Networking & Data Layer (Retrofit)

Follow these standards when building Retrofit API client structures.

#### 5.1 URL Manipulation
* **Dynamic Paths**: Annotate path variables with `@Path("name")` matching `{name}` blocks.
* **Query Parameters**: Annotate parameters with `@Query("key")`.
* **Complex Queries**: Use `@QueryMap Map<String, String>` to pass dynamic collections.

```kotlin
interface SearchService {
    @GET("group/{id}/users")
    suspend fun groupList(
        @Path("id") groupId: Int,
        @Query("sort") sort: String?,
        @QueryMap options: Map<String, String> = emptyMap()
    ): List<User>
}
```

#### 5.2 Request Body & Form Data
* **`@Body`**: Serializes a Kotlin data class directly into a JSON body via the configured converter.
* **`@FormUrlEncoded`**: Formats request as standard form url-encoded data using `@Field`.
* **`@Multipart`**: Sends files/binary media using multipart formatting via `@Part`.

```kotlin
interface UserService {
    @POST("users/new")
    suspend fun createUser(@Body user: User): User

    @FormUrlEncoded
    @POST("user/edit")
    suspend fun updateUser(
        @Field("first_name") first: String,
        @Field("last_name") last: String
    ): User

    @Multipart
    @PUT("user/photo")
    suspend fun uploadPhoto(
        @Part("description") description: RequestBody,
        @Part photo: MultipartBody.Part
    ): User
}
```

#### 5.3 Header Manipulation
* **Static Headers**: Define via `@Headers` annotation.
* **Dynamic Headers**: Define via `@Header`.
* **Dynamic Headers Map**: Define via `@HeaderMap`.
* **Global Headers**: Inject an OkHttp `Interceptor` to apply headers (like authorization or content-type tokens) globally.

```kotlin
interface WidgetService {
    @Headers("Cache-Control: max-age=640000")
    @GET("widget/list")
    suspend fun widgetList(): List<Widget>

    @GET("user")
    suspend fun getUser(@Header("Authorization") token: String): User
}
```

#### 5.4 Kotlin Coroutine Support & Response Handling
When writing `suspend` network operations, you have two choices for return types:
1. **Direct Body**: e.g., `suspend fun getUsers(): List<User>`. Returns deserialized data directly. Throws `HttpException` automatically on non-2xx status codes.
2. **Response Wrapper**: e.g., `suspend fun getUsers(): Response<List<User>>`. Provides access to the status code, response headers, and error body, and does **not** throw exceptions on non-2xx status codes.

```kotlin
@GET("users")
suspend fun getUsers(): List<User> // Throws HttpException on non-2xx responses

@GET("users")
suspend fun getUsersResponse(): Response<List<User>> // Requires manual status checks
```

#### 5.5 Hilt & Serialization Configuration
Configure Retrofit, OkHttpClient, and JSON serialization components as singletons in a central Network Module.

```kotlin
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor().apply { level = HttpLoggingInterceptor.Level.BODY })
        .connectTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, json: Json): Retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()
}
```

#### 5.6 Error Handling in Repositories
Always handle network layer failures (like server issues, timeouts, or network loss) inside the Repository implementation layer before exposing outputs to ViewModels.

```kotlin
class GitHubRepository @Inject constructor(
    private val service: GitHubService
) {
    suspend fun getRepos(username: String): Result<List<Repo>> = runCatching {
        // Direct body network call throws HttpException on 4xx/5xx responses
        service.listRepos(username)
    }.onFailure { exception ->
        // Handle/map specific network exceptions (e.g. UnknownHostException) here
    }
}
```