# Movies For You and KMP-auth-flows

Repositorio de referencia: flujos de autenticación completos en **Kotlin Multiplatform**, con **Firebase Authentication real** — email/password, Google Sign-In y Apple Sign-In — sobre Clean Architecture y MVI.

Construido como ejercicio de portfolio para demostrar arquitectura, integración con SDKs nativos vía `expect`/`actual`, e inyección de dependencias con Koin en un contexto multiplatform real (no un ejemplo de juguete).

---

## Stack

- **Kotlin Multiplatform** — módulo `shared` (`commonMain`/`androidMain`/`iosMain`/`commonTest`)
- **Compose Multiplatform** — UI compartida entre Android e iOS
- **Navigation 3** — navegación tipada (`NavKey`, `NavDisplay`), con soporte multiplatform vía JetBrains
- **Clean Architecture** — `domain` / `data` / `presentation` / `ui`
- **MVI** — `State`/`Event`/`Effect` por pantalla
- **Koin** — inyección de dependencias, módulos separados por plataforma
- **Firebase Authentication** — SDKs nativos vía `expect`/`actual` (no la librería wrapper multiplatform de terceros)

---

## Arquitectura

```
shared/src/commonMain/kotlin/com/tiago/kmpauthflows/
├── domain/          → modelos, contratos, UseCases — sin ningún import de Firebase/Compose/Android
├── data/            → implementación real (Firebase), expect/actual por plataforma
├── presentation/     → State/Event/Effect + ViewModel, sin saber que existe Compose UI
├── ui/               → Composables "tontos" — pintan el State, emiten Event
├── platform/         → tipos opacos que domain/data reenvían sin conocer (PlatformActivity, PlatformTime)
└── di/               → módulos de Koin, comunes y por plataforma
```

La regla de dependencia es de afuera hacia adentro: `ui` → `presentation` → `domain` ← `data`. `domain` no importa nada de las otras tres capas — es Kotlin puro, testeable sin ningún framework.

---

## Decisiones de diseño documentadas

Cada una de estas fue una decisión consciente, no un accidente — se documentan acá porque son evidencia de por qué se resolvió así, no solo qué se resolvió.

### Por qué `loginWithGoogle()`/`loginWithApple()` no reciben un `idToken` desde afuera

En Android, Google (Credential Manager) sí expone un `idToken` suelto que se le pasa a Firebase por separado. Apple, en cambio, resuelve todo el flujo OAuth de punta a punta con un solo método de Firebase (`startActivityForSignInWithProvider`) — nunca hay un token intermedio que manipular. Forzar una firma simétrica (`idToken` para los dos) habría significado reconstruir a mano el intercambio OAuth de Apple en Android solo para simular una API que Firebase ya resuelve — trabajo real sin beneficio. La asimetría se absorbe adentro de `data`; `domain` solo pide `loginWithGoogle()`/`loginWithApple()`, sin parámetros.

### Por qué `PlatformActivity` es un tipo opaco que domain recibe, en vez de resolverse por DI

Se evaluaron tres approaches antes de este: un objeto global tipo `ActivityProvider` (Service Locator, con riesgo de memory leak y estado oculto — descartado), y un scope de Activity en Koin (`scope<MainActivity> { }` — descartado porque Koin prohíbe explícitamente que un `ViewModel` resuelva dependencias de un scope de Activity, para prevenir leaks). La solución final: un tipo `expect class PlatformActivity` opaco (domain nunca ve sus miembros), que viaja explícito desde la UI (que sí sabe cómo conseguirlo — `LocalActivity.current` en Android) hasta el `Provider` que lo necesita, a través del `Event` → `UseCase` → `Repository`.

### Por qué el mapeo de errores de Firebase vive en `data`, no en `domain`

`domain/model/AuthException.kt` es una jerarquía sellada sin ningún texto — ni hardcodeado ni referenciando un recurso. El texto real vive en `composeResources/strings.xml`; el mapeo `AuthException → StringResource` pasa en `presentation` (que sí puede depender de Compose Multiplatform). Cada `actual` de `FirebaseAuthService` mapea las excepciones nativas del SDK (`FirebaseAuthInvalidCredentialsException`, etc.) a `AuthException` — porque solo ahí se conocen los tipos reales de excepción de cada plataforma.

### Por qué la verificación de email es "lazy" (client-side), no con Cloud Functions

El requisito era "una cuenta sin verificar se elimina a las 24hs". La solución completa y correcta requiere una **Cloud Function programada** (backend Node.js/TypeScript, fuera del alcance de Kotlin/KMP) corriendo periódicamente. Se implementó la versión client-side conscientemente: el chequeo (¿pasaron 24hs sin verificar?) corre en el próximo intento de login con esa cuenta, no en un job de servidor. **Limitación real, documentada a propósito:** si nadie vuelve a intentar loguearse con una cuenta sin verificar, esa cuenta queda huérfana en Firebase para siempre — no hay ningún proceso que la borre proactivamente. Se decidió no implementar la Cloud Function para no extender el alcance de este ejercicio más allá de KMP.

### Por qué no hay forma de reenviar el mail de verificación

Si el mail de verificación no llega (spam, email mal tipeado), el usuario no tiene forma de pedirlo de nuevo antes de que la cuenta se elimine sola a las 24hs — queda bloqueado hasta que expire y pueda registrarse de cero. Limitación conocida y documentada; la solución (un botón de "reenviar" en `HomeScreen`, o reenvío automático en cada intento de login dentro de la ventana) quedó fuera de alcance a propósito.

---

## Estado por plataforma

| Feature | Android | iOS |
|---|---|---|
| Email/password (login + registro) | ✅ Funcional, probado | ⏳ Código completo, placeholder — requiere Mac |
| Google Sign-In | ✅ Funcional, probado (Credential Manager) | ⏳ Placeholder documentado |
| Apple Sign-In | ✅ Funcional (flujo OAuth web de Firebase — sin SDK nativo) | ⏳ Placeholder documentado (requiere `ASAuthorizationAppleIDProvider`) |
| Verificación de email (24hs) | ✅ Funcional, probado | ⏳ Depende de FirebaseAuthService de iOS |

Cada archivo `.ios.kt` sin implementar tiene un `TODO` con los pasos exactos pendientes (agregar plugin CocoaPods, declarar el pod, `pod install`, implementar con `suspendCancellableCoroutine`).

---

## Setup para clonar y correr

Ningún dato sensible vive en este repo por fuera de configuración estándar — `google-services.json`/`GoogleService-Info.plist` **no son secretos** (Google los diseña para ir en el cliente; la seguridad real vive en las Firebase Security Rules del servidor).

1. Creá un proyecto propio en [Firebase Console](https://console.firebase.google.com).
2. Agregá una app Android con el namespace `com.tiago.kmpauthflows` (o el que uses si forkeaste el repo) — necesitás el **SHA-1** de tu keystore de debug (`./gradlew signingReport`) para que Google Sign-In funcione.
3. Descargá `google-services.json` y colocalo en `androidApp/`.
4. En Authentication → Sign-in method, habilitá **Email/Password** y **Google**.
5. Apple Sign-In requiere Apple Developer Program (cuenta paga, u$s99/año) — se puede omitir para correr Android.
6. Sincronizá Gradle y corré `androidApp` en un dispositivo/emulador **con Google Play Services** (no todas las imágenes de AVD lo incluyen).

---

## Tests

29 tests, 100% en `commonTest` (corren sobre el target Android, vía `withHostTestBuilder`):

- **domain** — unitarios puros, con `FakeAuthRepository` escrito a mano, sin ningún SDK real.
- **presentation** — `ViewModel` con Turbine, verificando `State`/`Effect` con el mismo `FakeAuthRepository`.

No hay tests de integración contra Firestore/Firebase real — decisión consciente: los SDKs nativos de Firebase son difíciles de instanciar "en memoria" para un test, a diferencia de una base local tipo SQLDelight/Room. El testing de esa integración puntual se apoya en pruebas manuales, documentadas en cada feature.