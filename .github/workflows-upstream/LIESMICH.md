# Die geerbten Upstream-Workflows

Hierher verschoben, nicht gelöscht — `spec.md` §20.4: annotieren, nie umschreiben.

**Warum sie nicht laufen:** dieser Fork existiert für **eine** Frage, die Nullbedingung aus
`spec.md` §34: *baut und testet das Projekt in der CI grün, ohne fremde Zugangsdaten und ohne
private Registry-Zugänge?*

Die Upstream-Workflows bauen Container, deployen nach Heroku, fahren ZAP-Scans und melden an
CodeClimate. Sie beantworten die Frage nicht, sie verstellen sie: ein roter Heroku-Job sagt
nichts darüber, ob `mvn verify` durchläuft — und **mit** Zugangsdaten grün zu werden wäre
genau das, was §34 ausschließt.

Wer sie zurückholen will: `git mv .github/workflows-upstream/*.yml .github/workflows/`
