import json
import sys

# Lee el archivo JSON
with open("data.json", "r") as f:
    data = json.load(f)

# Imprime los elementos para la matrix
# Cada elemento de "secrets" será una entrada de matrix
matrix_entries = data["secrets"]

# GitHub Actions necesita JSON en una sola línea
print(json.dumps({"include": matrix_entries}))
