# Fork-Notiz

Dies ist ein **Fork von [OWASP/wrongsecrets](https://github.com/OWASP/wrongsecrets)**, angelegt
als Prüfobjekt für das Projekt `c1-security-compliance`. Er ist **kein Beitrag an Upstream**
und soll nicht dorthin zurückfließen.

| | |
|---|---|
| Standardzweig | `mvp-basis` |
| Festgenagelt auf Tag | **`1.9.0`** (08.07.2024) |
| JDK | **22** — der Stand *am Tag*, nicht der von `main` |
| Grundlage | [ADR 0009 Fassung 2](https://github.com/hf-mg/c1-security-compliance/blob/main/docs/adr/0009-forks-mit-testsuite.md) |

**Warum ein alter Tag:** das Prüfobjekt soll veraltete Abhängigkeiten *haben*. Ein
tagesaktueller Stand hätte nichts zu melden.

**Warum das JDK aus dem Tag und nicht aus `main`:** auf `main` steht heute eine andere
Version. Wer von dort abschreibt, baut auf einem JDK, für das dieser Stand nie gedacht war —
und dann belegt weder ein grüner noch ein roter Lauf etwas.

---

## Änderungen gegenüber Upstream

Jede Änderung steht als Kommentar auch an ihrer Stelle im Quelltext. Diese Liste ist die
Übersicht.

### 1. Zweig `mvp-basis` auf Tag `1.9.0`, als Standardzweig gesetzt

Damit zeigt jeder Klon und jeder Workflow auf den festgenagelten Stand.

### 2. Geerbte Workflows nach `.github/workflows-upstream/` verschoben — **nicht gelöscht**

Sie bauen Container, veröffentlichen Releases und testen Cloud-Umgebungen. Für die Frage aus
`spec.md` §34 („baut dieses Projekt ohne unsere Zutaten?") tragen sie nichts bei und würden
nur Laufzeit und Rauschen kosten. Verschoben statt gelöscht, damit der Unterschied zu Upstream
sichtbar bleibt.

### 3. Ein eigener Workflow mit **zwei** Jobs

| Job | Kommando | Zweck |
|---|---|---|
| `nullbedingung` | `mvn -B verify`, unverändert | **die Messung.** `continue-on-error` — rot ist hier ein Ergebnis, kein Defekt |
| `bauen-und-testen` | zusätzlich `-Ddependency-check.skip=true` | der Job, den unsere Pipeline benutzt |

Zwei Jobs, weil `wrongsecrets` seinen eigenen Schwachstellen-Scanner fest in die
`verify`-Phase bindet. Der Schalter steht **nur im Workflow, nicht im POM** — ein lokales
`mvn verify` scheitert weiterhin, absichtlich: er soll auffallen.

### 4. CycloneDX-Plugin: Phase `install` → `package`

Unser Bau-Job fährt `mvn verify`, und `verify` liegt **vor** `install`. Mit der
Upstream-Bindung wäre die SBOM in unserem Lauf **nie entstanden** — lautlos, denn ein nicht
ausgeführtes Plugin meldet nichts.

### 5. `includeProvidedScope`: `true` → `false` *(01.09.2026)*

Gemessen trugen vorher **alle 373 Komponenten** `"scope": "required"` — auch
`org.projectlombok:lombok`, das im Manifest ausdrücklich als `provided` steht. Lombok ist ein
Annotationsprozessor und liegt im ausgelieferten Artefakt nicht; das Dokument behauptete das
Gegenteil.

Ursache ist die Abbildung, nicht die Auflösung: das Plugin bildet in CycloneDX 1.3 alles
Aufgenommene auf `required` ab. Also wird nur noch aufgenommen, was auch ausgeliefert wird.

**Preis:** Bauzeit-Abhängigkeiten sind in der SBOM nicht mehr sichtbar. Für eine Produkt-SBOM
nach CRA ist das richtig, für eine Lieferketten-Betrachtung ein Verlust — und steht deshalb in
der Abdeckungsaussage des Hauptprojekts.

### 6. NVD-API-Schlüssel aus `pom.xml` entfernt *(01.09.2026)*

In der Upstream-Fassung steht in `dependency-check-maven` ein **echter NVD-API-Schlüssel im
Klartext**, in einem öffentlichen Repository. Unser Fork trug ihn mit.

Er ist heraus, weil die Leitplanke des Hauptprojekts lautet: *„Never write real credentials
into files."* Der Schlüssel war nicht unserer.

**Am Verhalten ändert das nichts.** Er trug bereits nicht mehr; der Lauf scheiterte mit
`HTTP 403`. Genau dieses Scheitern **ist** das Ergebnis der Nullbedingungsmessung und soll
erhalten bleiben.

Wer einen eigenen Schlüssel braucht, setzt ihn **nicht** ins Manifest:

```bash
mvn verify -Dnvd.api.key=$NVD_API_KEY
```

---

## Das Ergebnis der Nullbedingungsmessung: **rot, mit benanntem Grund**

```
UpdateException: Error updating the NVD Data; the NVD returned a 403 or 404 error
BUILD FAILURE
```

Der Fehler liegt **hinter** Übersetzen und Testen. Im selben Stand laufen **183 Tests grün**,
sobald der Scanner übersprungen wird. Die Codebasis ist gesund; was scheitert, ist ein
Scanner, der fremde Zugangsdaten verlangt.

> Aus `docs/mvp.md` des Hauptprojekts: *„Ein ehrliches «rot, weil X» ist mehr wert als ein
> grün, das mit einer Sonderbehandlung erkauft wurde."*

Volltext der Messung:
[`docs/messungen/nullbedingung-wrongsecrets.md`](https://github.com/hf-mg/c1-security-compliance/blob/main/docs/messungen/nullbedingung-wrongsecrets.md)
