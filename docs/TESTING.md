# Plan i wyniki testów — Warehouse Manager

## Testy jednostkowe

Testy znajdują się w `app/src/test/java/com/warehouse/manager/data/`.

| ID | Scenariusz | Oczekiwany rezultat |
|---|---|---|
| UT-01 | Przyjęcie 5 sztuk do stanu 15 | Nowy stan = 20 |
| UT-02 | Wydanie 3 sztuk ze stanu 15 | Nowy stan = 12 |
| UT-03 | Wydanie większej liczby sztuk niż stan | Odrzucenie operacji |
| UT-04 | Ilość operacji równa 0 | Odrzucenie operacji |
| UT-05 | Nieznany typ operacji | Odrzucenie operacji |
| UT-06 | Produkt bez nazwy lub SKU | Odrzucenie walidacji |
| UT-07 | Cena NaN | Odrzucenie walidacji |
| UT-08 | CSV zawiera przecinki, cudzysłowy i nowe linie | Poprawne cytowanie pól |

Uruchomienie: `./gradlew testDebugUnitTest`.

## Testy interfejsu

Testy Compose znajdują się w `app/src/androidTest/java/com/warehouse/manager/`.

| ID | Scenariusz | Oczekiwany rezultat |
|---|---|---|
| UI-01 | Zapis formularza z pustą nazwą i SKU | Widoczny komunikat walidacji |
| UI-02 | Zapis poprawnego produktu | Callback otrzymuje produkt z nazwą i SKU |

Uruchomienie: `./gradlew connectedDebugAndroidTest` na emulatorze/urządzeniu.

## Testy manualne aplikacji

| ID | Scenariusz | Oczekiwany rezultat |
|---|---|---|
| M-01 | Uruchomienie aplikacji | Widoczny Dashboard |
| M-02 | Dodanie produktu | Produkt widoczny na liście |
| M-03 | Edycja i usunięcie produktu | Lista odzwierciedla zmiany |
| M-04 | IN: 15 + 5 | Stan 20 |
| M-05 | OUT: 20 - 3 | Stan 17 |
| M-06 | Próba wydania ponad stan | Operacja odrzucona, stan bez zmian |
| M-07 | Przejście do Operations | Widoczna historia IN/OUT |
| M-08 | Przejście do Reports | Widoczne podsumowanie magazynu |
| M-09 | Eksport CSV | Systemowy selektor zapisuje plik CSV |
| M-10 | Ponowne uruchomienie aplikacji | Produkty i historia pozostają zapisane w Room |

## Rejestrowanie wyników

Przed oddaniem projektu należy uruchomić oba zestawy testów i wpisać rzeczywiste wyniki (PASS/FAIL), datę oraz wersję emulatora/Androida. Nie należy oznaczać testu jako zaliczonego, jeśli nie został uruchomiony.
