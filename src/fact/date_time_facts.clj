(ns fact.date-time-facts
  (:use [date-time :only (for-display first-not-in-past today-num)]
        [utilize.testutils :only (do-at)]
        midje.sweet)
  (:import [org.joda.time DateMidnight]))

(fact "displays a date-time in a display format"
   (for-display (DateMidnight. 2000 5 25)) => "2000/5/25")

(fact "works for un-nested seqs as well"
  (do-at (DateMidnight. 2011 11 10)
    (first-not-in-past [(DateMidnight. 2011 11 9) (DateMidnight. 2011 11 10) (DateMidnight. 2011 11 11)]))
  => (DateMidnight. 2011 11 10))

(fact "if no dates are today or in the future gives nil"
  (first-not-in-past [(DateMidnight. 1999 1 1) (DateMidnight. 1999 1 19) (DateMidnight. 1999 11 11)])
  => nil)

(tabular
  (fact
    (do-at (DateMidnight. 2011 6 ?date)
      (today-num)) => ?day-num)

  ?date ?day-num
   6    1
   7    2
   8    3
   9    4
  10    5
  11    6
  12    7 )