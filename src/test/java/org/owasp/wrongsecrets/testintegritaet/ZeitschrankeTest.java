package org.owasp.wrongsecrets.testintegritaet;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

/**
 * MECHANISMUS 5 aus spec.md 24.3, absichtlich eingebaut zur Vorfuehrung von Pruefung 12 (Issue
 * #25).
 *
 * <p>Ein Test, der drei Sekunden haengt. Er existiert auf beiden Zweigen mit derselben
 * Identitaet. Nur die Zeitschranke unterscheidet sich.
 *
 * <p>Hier ist sie WEIT: derselbe haengende Test meldet GRUEN.
 *
 * <p><b>Die Identitaetsmenge aendert sich zwischen den beiden Laeufen nicht.</b> Ein
 * fehlgeschlagener Test ist ein AUSGEFUEHRTER (spec.md 24.4). Also kann der Inventurvergleich
 * diesen Mechanismus nicht sehen - und genau das ist das Ergebnis, das Issue #25 fuer Nr. 5
 * erwartet.
 */
class ZeitschrankeTest {

  @Test
  @Timeout(value = 60)
  void haengtDreiSekunden() throws InterruptedException {
    Thread.sleep(Duration.ofSeconds(3).toMillis());
  }
}
