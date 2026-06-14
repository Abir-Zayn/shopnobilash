In this session we will working with connecting appwrite to our application .Starting with the Sign up . Can you complete this? or do I have to provide something?  Create a .env where you place the secret files. 

Authentication will have multiple parts
- Email password login
- Registration
- Forget password
- Google Sign up/Sign In
- Facebook Sign up/Sign In
- Access token, refresh token, id token.

Password Hashing & Salting: Never store plain-text passwords. Use strong, slow hashing algorithms like Argon2, bcrypt, or scrypt with a unique salt for every user.

2. Account Security & Verification
Security is paramount to preventing unauthorized access.

Email Verification: After registration, send a verification email. Configure your app to restrict access to certain features until the emailVerification flag is true.

State Management & Reactive UI
Auth State Observer: Use a Repository pattern or a ViewModel to expose an AuthState (e.g., Loading, Authenticated(User), Unauthenticated).

Token Refresh Handling: Implement an Interceptor (if using Retrofit) or a centralized handler to automatically catch 401 Unauthorized responses, attempt to refresh the token using the refresh token, and retry the original request.


Best Practices for Kotlin & Appwrite
Use DataStore: Don't store sensitive tokens in SharedPreferences. Use Android's DataStore with encryption if you are caching data locally.

Dependency Injection: Use Hilt or Koin to inject your Account service from Appwrite throughout your application. This makes testing easier and keeps your code modular.

Error Handling: Appwrite exceptions in Kotlin are specific. Use a try-catch block around your Auth calls and map them to domain-specific error types to show friendly messages to your users.

The Sign up screen is not scrollable . Fix this , like I cant add Confirm Password ,I have paste the password . Then I have to close the keyboard to see the Sign up button. 

Create a reusable snackbar component with success bg, failed bg, warn bg with the light mode text color and show snackbar for 1500ms(1.5sec) . Now whenever we get an error/success instead of showing the log in the app, show it on the snackbar.


Now I have 
Integrations,
Platforms >>
Localhost
Name
Choose any name that will help you distinguish between platforms.

Name
Localhost
Hostname
You can use * to allow wildcard hostnames or subdomains.

Hostname
