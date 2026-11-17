					 Warehouse Manager


Aplikacja Android do lokalnego zarządzania stanem magazynowym. Projekt wykorzystuje Kotlin, Jetpack Compose Material 3, MVVM, Room/SQLite oraz Kotlin Coroutines i Flow.
 

					 Funkcjonalności


- Dashboard: liczba produktów, łączny stan jednostek, produkty z niskim stanem i ostatnie operacje.

- Produkty: dodawanie, odczyt, edycja, usuwanie oraz wyszukiwanie po nazwie, SKU i wartości kodu kreskowego.

- Operacje magazynowe: przyjęcie (IN) i wydanie (OUT); operacja aktualizacji stanu i zapis historii są wykonywane w jednej transakcji Room.

- Historia operacji magazynowych.

- Raport CSV eksportowany przez systemowy selektor miejsca zapisu Androida.

- Walidacja danych, komunikaty Snackbar/Toast oraz testowalne reguły biznesowe.


					

						Technologie


Obszar - Rozwiązanie

Język - Kotlin 

IDE/SDK - Android Studio, Android SDK 36 

UI - Jetpack Compose, Material 3 

Architektura - MVVM + Repository 

Baza danych - Room / SQLite 

Asynchroniczność - Coroutines, Flow, viewModelScope 

Nawigacja - Dolna nawigacja Compose 





					ARchitektura




  mermaid
flowchart TD
   
 UI[Jetpack Compose UI] --> VM[WarehouseViewModel]
  
 VM --> REP[WarehouseRepository]
   
 REP --> RULES[StockRules / CsvFormatter]
  
 REP --> DB[WarehouseDatabase - Room]
   
 DB --> DAO[WarehouseDao]
   
 DAO --> P[(products)]
   
 DAO --> M[(movements)]
   
 DAO -->|Flow| REP
   
 REP -->|Flow| VM
  
 VM --> UI

Warstwa UI prezentuje dane i przekazuje intencje użytkownika. ViewModel zarządza stanem ekranu i zakresem korutyn. Repository realizuje przypadki użycia i transakcje. DAO udostępnia zapytania Room, a SQLite zapewnia trwałość danych.



 							Przypadki użycia


mermaid

flowchart LR

 U([Użytkownik]) --> A[Przegląd dashboardu]
   
 U --> B[Zarządzanie produktami: CRUD]
  
 U --> C[Wyszukiwanie produktu]
   
 U --> D[Przyjęcie IN]
  
 U --> E[Wydanie OUT]
  
 U --> F[Przegląd historii]
 
 U --> G[Przegląd raportów]
  
 U --> H[Eksport CSV]


 						Wymagania i ograniczenia



- Aplikacja wymaga Androida w wersji API 26 lub nowszej.
- Dane są przechowywane lokalnie w bazie Room na urządzeniu.
- Pole kodu kreskowego służy do ręcznego zapisu i wyszukiwania wartości; skanowanie aparatem nie jest zaimplementowane.
- Synchronizacja Firebase/REST i konta użytkowników nie są częścią tej wersji. Aplikacja nie wymaga sieci do podstawowej pracy.
- Automatyczna kopia zapasowa Androida jest wyłączona w manifeście (`allowBackup=false`).


						 Uruchomienie



1. Rozpakuj repozytorium do ścieżki bez znaków narodowych, np. `C:\AndroidProjects\WarehouseManager`.
2. Otwórz folder projektu w Android Studio.
3. Zainstaluj Android SDK Platform 36 oraz skonfiguruj JDK 17.
4. Poczekaj na Gradle Sync.
5. Uruchom aplikację na emulatorze lub urządzeniu z API 26+.




 							Testy


Testy jednostkowe reguł magazynowych i formatu CSV:

./gradlew testDebugUnitTest

Testy interfejsu Compose wymagają uruchomionego emulatora lub urządzenia:

./gradlew connectedDebugAndroidTest
