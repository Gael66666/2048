# 2048 — Jardin de cristaux
ou aller dans Releases

Application Windows native du jeu 2048, sur le thème d'un jardin de
cristaux : les tuiles évoluent du vert pâle au doré puis à l'ember à mesure
qu'elles fusionnent.

Écrite en C avec l'API Win32 et GDI — une fenêtre native, sans navigateur,
sans framework, un seul fichier source.

## Compilation

**Depuis Windows (MinGW-w64 / MSYS2) :**
```bash
gcc -O2 -mwindows -o 2048-App.exe win32_2048.c -lgdi32 -luser32
```

**Depuis Linux (cross-compilation) :**
```bash
sudo apt-get install gcc-mingw-w64-x86-64
x86_64-w64-mingw32-gcc -O2 -mwindows -o 2048-App.exe win32_2048.c -lgdi32 -luser32
```

## Contrôles

- Flèches ou `WASD` : déplacer les tuiles
- `N` : nouvelle partie
- `Espace` : continuer après avoir atteint 2048

## Règles

Déplacez les tuiles dans une direction ; deux tuiles de même valeur qui se
percutent fusionnent en une seule tuile de valeur double. Une nouvelle tuile
(2 ou 4) apparaît après chaque coup. Le but est d'atteindre 2048 — vous
pouvez continuer à jouer ensuite pour un score plus élevé. La partie se
termine quand la grille est pleine et qu'aucune fusion n'est plus possible.

## Licence

MIT — voir [`LICENSE`](LICENSE).
