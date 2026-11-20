# Indicador de confianza (respaldo documental)

Se calcula en el backend (`ConfidenceIndicator.java`), no lo inventa el modelo.

## Fórmula

Solo se usan las fuentes con similitud >= umbral (`RAG_SCORE_THRESHOLD`, 0,55).

```
parte_similitud = (promedio - umbral) / (1 - umbral)
parte_fuentes   = min(fuentes, 3) / 3
indice          = round(100 * (0,7 * parte_similitud + 0,3 * parte_fuentes))
```

- Alto: 70 a 100
- Medio: 40 a 69
- Bajo: 0 a 39
- Sin fuentes: índice 0 y "respaldo insuficiente".

Ejemplo: fuentes 0,91, 0,88 y 0,85 dan un promedio de 0,88 → (0,33 / 0,45) = 0,733 → 100 · (0,513 + 0,3) = **81 (Alto)**.

Es una medida heurística, no la probabilidad de que la solución sea correcta.

## Commits

1. `feat: :sparkles: add heuristic confidence indicator to RAG responses`: cálculo del índice en el backend y se devuelve en el historial.
2. `test: :white_check_mark: add confidence indicator tests`: pruebas con 3 fuentes altas, 1 fuente cerca del umbral, cálculo matemático y sin fuentes.
3. `feat: :lipstick: show confidence indicator in the assistant`: se muestra junto a la respuesta y este README.
