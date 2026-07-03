# 2048 - Jardin de Cristaux (Android)

Version Android Kotlin du jeu 2048 avec le thème du jardin de cristaux.

## Installation

### Prérequis
- Android Studio (dernière version)
- SDK Android 21+ (minSdk)
- Target SDK 34
- Kotlin 1.9+

### Compilation

1. Clonez le repository
2. Ouvrez le dossier `android/` dans Android Studio
3. Attendez la synchronisation Gradle
4. Cliquez sur **Build > Build APK**
5. L'APK sera généré dans `android/app/build/outputs/apk/`

### Installation sur un appareil

```bash
adb install app-release.apk
```

Ou utilisez l'émulateur Android Studio.

## Commandes

- **Flèches / Swipe** : Déplacer les tuiles (haut, bas, gauche, droite)
- **Boutons tactiles** : Commandes directionnelles
- **Nouvelle partie** : Réinitialiser le jeu
- **Continuer** : Poursuivre après avoir atteint 2048

## Règles

Déplacez les tuiles dans une direction ; deux tuiles de même valeur qui se percutent fusionnent en une seule tuile de valeur double.
Une nouvelle tuile (2 ou 4) apparaît après chaque coup.
Le but est d'atteindre 2048 — vous pouvez continuer à jouer ensuite pour un score plus élevé.
La partie se termine quand la grille est pleine et qu'aucune fusion n'est plus possible.

## Thème

Les tuiles évoluent du vert pâle au doré puis à l'orange à mesure qu'elles fusionnent, suivant le thème du jardin de cristaux.

## Architecture

- **MainActivity.kt** : Interface utilisateur avec Jetpack Compose
- **GameEngine.kt** : Logique du jeu (mouvements, fusions, score)

## Licence

MIT - voir `LICENSE`
