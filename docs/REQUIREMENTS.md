# Cel, zakres i wymagania

## Cel aplikacji

Celem Warehouse Manager jest umożliwienie użytkownikowi prowadzenia lokalnej ewidencji produktów magazynowych, kontrolowania ilości dostępnych jednostek oraz rejestrowania przyjęć i wydań.

## Zakres

Aplikacja obejmuje zarządzanie kartoteką produktów, wyszukiwanie, kontrolę niskiego stanu, operacje IN/OUT, historię ruchów magazynowych i eksport raportu produktów do CSV. Wersja nie obejmuje kont użytkowników, synchronizacji chmurowej ani skanowania kodów aparatem.

## Wymagania funkcjonalne

- WF-01: użytkownik może dodać produkt.
- WF-02: użytkownik może przeglądać listę produktów.
- WF-03: użytkownik może edytować produkt.
- WF-04: użytkownik może usunąć produkt.
- WF-05: użytkownik może wyszukiwać po nazwie, SKU i wartości kodu.
- WF-06: użytkownik może zarejestrować przyjęcie IN.
- WF-07: użytkownik może zarejestrować wydanie OUT.
- WF-08: system blokuje wydanie większe niż dostępny stan.
- WF-09: użytkownik może przeglądać historię operacji.
- WF-10: dashboard prezentuje liczbę produktów, stan łączny i niski stan.
- WF-11: raport produktów może zostać wyeksportowany do CSV.
- WF-12: dane są zachowane po ponownym uruchomieniu aplikacji dzięki Room.

## Wymagania niefunkcjonalne

- WNF-01: interfejs jest zbudowany w Jetpack Compose i Material 3.
- WNF-02: logika danych jest oddzielona od UI przez ViewModel i Repository.
- WNF-03: operacje bazodanowe są asynchroniczne i korzystają z Coroutines/Flow.
- WNF-04: walidowane są wymagane pola i wartości liczbowe.
- WNF-05: operacje aktualizacji stanu i zapisu historii są atomowe.
- WNF-06: aplikacja działa lokalnie bez wymagania połączenia sieciowego.
- WNF-07: automatyczne kopie zapasowe danych aplikacji są wyłączone.

## Ograniczenia i dalszy rozwój

Możliwymi rozszerzeniami są: skanowanie EAN/QR przez aparat, uwierzytelnianie, role użytkowników, synchronizacja REST/Firebase, kopie zapasowe oraz filtrowanie i eksport historii ruchów.
