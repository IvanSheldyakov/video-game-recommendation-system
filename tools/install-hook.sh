#!/bin/bash

# Копирование файла pre-commit из папки tools в папку .git/hooks/
cp tools/pre-commit .git/hooks/
chmod +x .git/hooks/pre-commit

echo "Hook installed successfully!"