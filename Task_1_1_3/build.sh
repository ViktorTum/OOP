#!/bin/bash

# --- Компиляция ---
# Создаем папку для скомпилированных файлов (если ее нет)
mkdir -p build/classes

# Компилируем все .java файлы из src/main/java в папку build/classes
echo "Компиляция проекта..."
javac -d build/classes -sourcepath src/main/java src/main/java/ru/nsu/tumilevich/*.java

# --- Запуск ---
# Запускаем главный класс, указывая, где лежат скомпилированные файлы (-cp)
echo "Запуск игры..."
java -cp build/classes ru.nsu.tumilevich.MainTask3
