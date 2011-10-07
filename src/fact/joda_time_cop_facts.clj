(ns fact.joda-time-cop-facts
  (:use [joda-time-cop :only (do-at do-at*)])
  (:import (org.joda.time DateMidnight))
  (:use midje.sweet))

(fact "freezes time at given date then returns to normal afterward"
  (DateMidnight.) =not=> (DateMidnight. 2000 1 1)
  (do-at* (DateMidnight. 2000 1 1)
    (fn [] (DateMidnight.))) => (DateMidnight. 2000 1 1)
  (DateMidnight.) =not=> (DateMidnight. 2000 1 1))

(fact "there's a macro for running some code at a certain time"
  (DateMidnight.) =not=> (DateMidnight. 2000 1 1)
  (do-at (DateMidnight. 2000 1 1)
    (DateMidnight.)
    (DateMidnight.)) => (DateMidnight. 2000 1 1)
  (DateMidnight.) =not=> (DateMidnight. 2000 1 1))

(fact "when body throws exception, we always make sure to put the time back to normal"
  (DateMidnight.) =not=> (DateMidnight. 2000 1 1)
  (do-at (DateMidnight. 2000 1 1)
    (throw (RuntimeException. "boom"))) => (throws RuntimeException "boom")
  (DateMidnight.) =not=> (DateMidnight. 2000 1 1))
