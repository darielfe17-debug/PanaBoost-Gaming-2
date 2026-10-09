# PanaBoost Gaming 2.0
Proyecto nativo Java, sin bibliotecas de terceros, para Android 8+ y HyperOS.

## Funciones reales
- Panel RAM, espacio libre, nivel y temperatura de batería y estado térmico (Android 10+).
- Modo sesión gaming con refresco cada 3 segundos.
- Selección de apps instaladas y lanzamiento.
- Control No molestar **solo con autorización del usuario** y si HyperOS lo permite.
- Limpieza de la caché **de la propia app**.

## Límites técnicos
Sin root ni permisos privilegiados no se puede modificar CPU/GPU, eliminar cachés de otras apps, liberar RAM arbitrariamente ni subir los FPS máximos. No se inventan porcentajes de optimización.

## GitHub Actions
Extrae y sube **el contenido** de este ZIP a la raíz de tu repositorio. Conserva `.github/workflows/android-apk.yml`. Abre Actions, espera el resultado y descarga `PanaBoost-Gaming-APK` si la compilación termina en verde.

## Nota
El código se preparó pero no se ha compilado ni probado en un teléfono en este entorno. La selección de apps puede estar limitada por visibilidad de paquetes en algunas versiones de Android.
