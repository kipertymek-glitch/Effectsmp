# SmpEffects

Plugin do Paper/Spigot 1.20.x dla serwerów SMP: losowe, stałe efekty dla graczy z systemem
ulepszania i rerollowania przez crafting oparty o głowy graczy zdobywane za zabójstwa.

## Funkcje

- `/smp start` (op / uprawnienie `smp.admin`) — losuje każdemu graczowi online jeden stały
  efekt z zbalansowanej puli (Speed, Strength, Jump Boost, Regeneration, Haste, Resistance,
  Saturation, Luck — konfigurowalne w `config.yml`).
- **Ulepszacz Efektu** — craftowalny item (4x głowa gracza + 4x złote jabłko + 1x diament w
  kształcie X), użycie (PPM) podnosi efekt gracza o poziom, maksymalnie do poziomu II.
- **Losowanie Efektu** — craftowalny item (2x głowa gracza + perła Endermana + sztabka złota,
  bezkształtny), użycie losuje nowy efekt i resetuje poziom do I.
- Głowy graczy zdobywa się zabijając innych graczy (dropią się automatycznie zabójcy).
- Jeśli zabijesz gracza, który ma ulepszony efekt (poziom > I), jego efekt **znika
  całkowicie** — ryzyko dla mocniejszych graczy.
- Efekty są zapisywane w `plugins/SmpEffects/data.yml` i wracają po restarcie serwera.

## Komendy

| Komenda | Opis |
|---|---|
| `/smp start` | Losuje efekty wszystkim graczom online |
| `/smp effect <gracz>` | Pokazuje efekt danego gracza |
| `/smp reset <gracz>` | Usuwa efekt danego gracza |

## Uprawnienia

- `smp.admin` (domyślnie: op)

## Budowanie lokalnie

Wymaga Java 17+ i Maven:

```bash
mvn package
```

Gotowy plik `.jar` pojawi się w `target/SmpEffects-1.0.0.jar`.

## Budowanie przez GitHub (automatycznie)

W repozytorium jest gotowy workflow `.github/workflows/build.yml`. Wystarczy:

1. Utworzyć nowe repozytorium na GitHubie.
2. Wrzucić do niego całą zawartość tego folderu:
   ```bash
   git init
   git add .
   git commit -m "SmpEffects plugin"
   git branch -M main
   git remote add origin https://github.com/TWOJA_NAZWA/TWOJE_REPO.git
   git push -u origin main
   ```
3. Po pushu w zakładce **Actions** na GitHubie uruchomi się build. Po jego zakończeniu gotowy
   plik `.jar` znajdziesz jako artefakt w tym samym uruchomieniu (zakładka Actions → wybrany
   run → sekcja Artifacts → `SmpEffects-jar`).

## Instalacja na serwerze

1. Pobierz `.jar` (lokalnie z `target/` lub z artefaktu GitHub Actions).
2. Wrzuć do folderu `plugins/` na serwerze Paper/Spigot 1.20.x.
3. Zrestartuj serwer — wygeneruje się `plugins/SmpEffects/config.yml`.
4. Dostosuj pulę efektów w `config.yml`, jeśli chcesz.
5. Wpisz `/smp start`, aby rozdać losowe efekty graczom online.
