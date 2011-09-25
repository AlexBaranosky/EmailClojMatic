(ns fact.date-time-streams-facts
  (:use date-time-streams)
  (:import [org.joda.time DateMidnight])
  (:use [joda-time-cop :only (do-at)])
  (:use [utility :only (third fourth)])
  (:use midje.sweet))

(fact "makes a stream of xth day of the month"
  (do-at (DateMidnight. 2011 6 15)
    (first (day-of-month-stream 1))) => (DateMidnight. 2011 7 1)

  (do-at (DateMidnight. 2011 6 15)
    (second (day-of-month-stream 1))) => (DateMidnight. 2011 8 1))

;(tabular
;  (fact "makes a stream of given day of the month"
;   (?nth (day-of-month-stream 1)) => ?date-time)

;	?nth   ?date-time
;	first  (DateMidnight. 2011 11 1)
;	second (DateMidnight. 2011 12 1)
;	third  (DateMidnight. 2012 1 1)
;	fourth (DateMidnight. 2012 2 1))

;(tabular
;  (fact "day-stream gives lazy infinite seqs of datetimes on the given day of the week"
;    (do-at (DateMidnight. 2011 6 1)
;     (?nth (day-of-week-stream (:tuesdays day-nums)))) => ?date-time)
;
;	?nth   ?date-time
;	first  (DateMidnight. 2011 12 27)
;	second (DateMidnight. 2012 1 3)
;	third  (DateMidnight. 2012 1 10)
;	fourth (DateMidnight. 2012 1 17))

(facts "day-stream gives lazy infinite seqs of datetimes on the given day of the week"
  (do-at (DateMidnight. 2011 6 1)
    (first (day-of-week-stream (:fridays day-nums)))) => (DateMidnight. 2011 6 3)

  (do-at (DateMidnight. 2011 6 1)
    (second (day-of-week-stream (:fridays day-nums)))) => (DateMidnight. 2011 6 10))
