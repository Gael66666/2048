# 2048 — Jardin de cristaux

Application du jeu 2048 sur le thème d'un jardin de cristaux : les tuiles évoluent du vert pâle au doré puis à l'ember à mesure qu'elles fusionnent.

**Disponible en deux versions :**
- 🪟 **Windows** : Application native en C avec l'API Win32
- 📱 **Android** : Application mobile en Kotlin avec Jetpack Compose

---

## 📥 DÉMARRAGE RAPIDE

### ✅ **Installation facile (RECOMMANDÉ) - Pas de compilation !**

#### Pour Windows :
1. Allez dans [📦 Releases](https://github.com/Gael66666/2048/releases)
2. Téléchargez le fichier `2048-App.exe`
3. Lancez-le directement
4. ✅ C'est prêt ! Jouez immédiatement !

#### Pour Android :
1. Allez dans [📦 Releases](https://github.com/Gael66666/2048/releases)
2. Téléchargez le fichier `.apk`
3. Transférez-le sur votre téléphone Android
4. Ouvrez le fichier et installez l'application
5. ✅ C'est prêt ! Jouez immédiatement !

**Pas besoin de compiler, pas besoin d'outils spécialisés !** 🚀

---

## 🪟 Version Windows

### Installation sans compilation (RECOMMANDÉ)

Téléchargez simplement `2048-App.exe` depuis [Releases](https://github.com/Gael66666/2048/releases) et exécutez-le !

### Compilation manuelle (pour développeurs)

Si vous voulez compiler vous-même :

**Prérequis :**
- MinGW-w64 ou MSYS2

**Depuis Windows (MinGW-w64 / MSYS2) :**
```bash
gcc -O2 -mwindows -o 2048-App.exe win32_2048.c -lgdi32 -luser32
```

**Depuis Linux (cross-compilation) :**
```bash
sudo apt-get install gcc-mingw-w64-x86-64
x86_64-w64-mingw32-gcc -O2 -mwindows -o 2048-App.exe win32_2048.c -lgdi32 -luser32
```

### Contrôles Windows

- **Flèches** ou **WASD** : déplacer les tuiles
- **N** : nouvelle partie
- **Espace** : continuer après avoir atteint 2048

---

## 📱 Version Android

### Installation sans compilation (RECOMMANDÉ)

1. Allez dans [📦 Releases](https://github.com/Gael66666/2048/releases)
2. Téléchargez le fichier `.apk`
3. Transférez-le sur votre téléphone Android
4. Ouvrez le fichier et installez l'application
5. ✅ Profitez du jeu !

**Pas besoin de Android Studio, pas besoin de compiler !** 📦

---

### Compilation manuelle (pour développeurs)

Si vous voulez compiler vous-même :

**Préalable :**
- Android Studio (dernière version)
- SDK Android 21+ (minSdk)
- Target SDK 34
- Kotlin 1.9+

**Étapes de compilation :**

1. Clonez le repository
2. Ouvrez le dossier `android/` dans Android Studio
3. Attendez la synchronisation Gradle
4. Cliquez sur **Build > Build Bundle(s) / APK(s) > Build APK(s)**
5. L'APK sera généré dans `android/app/build/outputs/apk/release/`
6. Installez sur votre appareil

```bash
adb install app-release.apk
```

### Contrôles Android

- **Swipe** (glisser le doigt) : déplacer les tuiles (haut, bas, gauche, droite)
- **Boutons tactiles** : commandes directionnelles
- **Bouton "Nouvelle partie"** : réinitialiser le jeu
- **Bouton "Continuer"** : poursuivre après avoir atteint 2048

---

## 📋 Règles du jeu

Déplacez les tuiles dans une direction ; deux tuiles de même valeur qui se percutent fusionnent en une seule tuile de valeur double. Une nouvelle tuile (2 ou 4) apparaît après chaque coup. Le but est d'atteindre **2048** — vous pouvez continuer à jouer ensuite pour un score plus élevé. La partie se termine quand la grille est pleine et qu'aucune fusion n'est plus possible.

---

## 🎨 Thème

Les tuiles évoluent progressivement :
- **Vert pâle** → 2, 4, 8, 16, 32
- **Vert foncé** → 64, 128, 256
- **Bleu** → 512, 1024
- **Violet** → 1024
- **Doré** → 2048
- **Orange** → 4096+

---

## 📦 Téléchargements

👉 **Allez dans [RELEASES](https://github.com/Gael66666/2048/releases)** 👈

Vous trouverez :
- ✅ **2048-App.exe** (Windows - prêt à jouer)
- ✅ **app-release.apk** (Android - prêt à jouer)

**AUCUNE COMPILATION NÉCESSAIRE !**

---

## 🔧 Architecture

### Windows
- **win32_2048.c** : Code source complet (C + Win32 API + GDI)

### Android
- **android/build.gradle.kts** : Configuration Gradle
- **android/src/main/AndroidManifest.xml** : Configuration de l'application
- **android/src/main/kotlin/com/gael66666/game2048/MainActivity.kt** : Interface utilisateur (Jetpack Compose)
- **android/src/main/kotlin/com/gael66666/game2048/GameEngine.kt** : Logique du jeu

---

## 📄 Licence

MIT — voir [`LICENSE`](LICENSE).

---

## ❓ FAQ

**Q : Je suis sur Windows, que dois-je faire ?**  
A : Allez dans [Releases](https://github.com/Gael66666/2048/releases), téléchargez `2048-App.exe` et lancez-le. C'est tout !

**Q : Je suis sur Android, est-ce que je dois installer Android Studio ?**  
A : Non ! Allez dans [Releases](https://github.com/Gael66666/2048/releases), téléchargez l'APK et installez-le directement sur votre téléphone.

**Q : Puis-je jouer sans compiler ?**  
A : Oui ! Allez dans [Releases](https://github.com/Gael66666/2048/releases) et téléchargez la version précompilée pour votre plateforme (Windows EXE ou Android APK).

**Q : Comment compiler moi-même ?**  
A : Pour Windows, utilisez MinGW-w64 et la commande gcc. Pour Android, utilisez Android Studio. Voir les sections "Compilation manuelle" ci-dessus.

