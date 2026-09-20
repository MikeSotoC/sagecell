# Matrices Offline para Android

Proyecto Android nativo en Java. Calcula sin internet; no contiene SageMath ni SageCell.

## Funciones

- Dos matrices editables A y B, de 1×1 a 6×6. Cambia las dimensiones con **Aplicar**; se conservan los valores de las celdas que permanecen.
- Suma, resta y producto A × B. Transpuesta, determinante e inversa de A.
- Acepta enteros, negativos y decimales con punto o coma. Muestra hasta ocho cifras significativas. La aritmética usa `double`, no fracciones exactas ni álgebra simbólica.
- Indica las dimensiones incompatibles, los campos inválidos y las matrices singulares. El cálculo del determinante/inversa usa eliminación con pivoteo parcial.

## Abrir y compilar

Abre esta carpeta en Android Studio, instala SDK 35 si se solicita y sincroniza Gradle. Requiere JDK 17 y Android Gradle Plugin 8.7.3. Compila `app` y ejecuta en Android 7.0 o posterior (API 24). El proyecto no incluye Gradle Wrapper: Android Studio puede usar su instalación de Gradle; también puedes generar el wrapper con `gradle wrapper --gradle-version 8.9` si tienes Gradle instalado.

La compilación inicial descarga Gradle/Android Gradle Plugin. Una vez instalada, **la aplicación no solicita permiso de Internet y calcula completamente sin conexión**.

## Ejemplo

A = [ [1, 2], [3, 4] ]; B = [ [5, 6], [7, 8] ]. A × B = [ [19, 22], [43, 50] ]; det(A) = −2; inversa(A) = [ [−2, 1], [1.5, −0.5] ].

## Alcance

Este prototipo es un motor matricial numérico propio. Integrar SageMath completo en Android es otro trabajo: requiere portar sus dependencias nativas y su entorno Python. No se ha generado ni probado un APK en este entorno.
