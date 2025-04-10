# Optymalizacja Trasy

## Spis treści
* [O aplikacji](#o-aplikacji)
* [Technologie](#technologie)
* [Jak uruchomić?](#jak-uruchomić?)

## O aplikacji

*  Wybór punktów do odwiedzenia bezpośrednio na mapie,
*  Przesłanie wybranych lokalizacji do systemu backendowego,
*  Obliczenie optymalnej trasy (z użyciem rzeczywistych danych z OSRM),
*  Wizualizację wynikowej trasy na interaktywnej mapie (HTML + Folium),
*  Zapis i odczyt tras z bazy danych.

## Technologie

🧠 Spring Boot (Java) – logika backendu, API REST, komunikacja z bazą danych MySQL,
🌐 Python (Flask + Folium) – generowanie i renderowanie map z trasą,
🗺️ OSRM API – pobieranie rzeczywistych czasów i dystansów między punktami,
📦 Docker – konteneryzacja i uruchamianie aplikacji,
🌐 NGINX – reverse proxy kierujący ruch do odpowiednich serwisów,
🐬 MySQL – przechowywanie tras i punktów w bazie danych.

## Jak uruchomić?
