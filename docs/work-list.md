
In this session we will completely organize our application codebase . We are following MVVM 
architecture . Here is the folder based MVVM layout example

app/
└── src/main/
    ├── data/
    │   ├── auth/
    │   │   ├── model/
    │   │   │   ├── LoginRequest.kt
    │   │   │   └── LoginResponse.kt
    │   │   ├── repository/
    │   │   │   ├── AuthRepository.kt
    │   │   │   └── AuthRepositoryImpl.kt
    │   │   └── source/
    │   │       ├── remote/
    │   │       │   └── AuthApiService.kt
    │   │       └── local/
    │   │           └── AuthLocalDataSource.kt
    │   │
    │   └── profile/
    │       ├── model/
    │       │   └── UserProfile.kt
    │       ├── repository/
    │       │   ├── ProfileRepository.kt
    │       │   └── ProfileRepositoryImpl.kt
    │       └── source/
    │           ├── remote/
    │           │   └── ProfileApiService.kt
    │           └── local/
    │               └── ProfileLocalDataSource.kt
    │
    ├── domain/
    │   ├── auth/
    │   │   └── usecase/
    │   │       ├── LoginUseCase.kt
    │   │       └── LogoutUseCase.kt
    │   └── profile/
    │       └── usecase/
    │           ├── GetProfileUseCase.kt
    │           └── UpdateProfileUseCase.kt
    │
    └── presentation/
        ├── auth/
        │   ├── viewmodel/
        │   │   └── AuthViewModel.kt
        │   └── ui/
        │       ├── LoginScreen.kt
        │       └── RegisterScreen.kt
        │
        └── profile/
            ├── viewmodel/
            │   └── ProfileViewModel.kt
            └── ui/
                └── ProfileScreen.kt


3 main layers:

data/ — Models, repositories, remote/local data sources. Each feature owns its own slice.
domain/ — UseCases that contain business logic. Sits between data and presentation. Pure Kotlin, no Android dependency.
presentation/ — ViewModel + Composable UI screens per feature.