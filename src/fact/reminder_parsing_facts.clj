(ns fact.reminder-parsing-facts
  (:use [reminder-parsing :only (parse-schedule parse-reminder-from-line parse-reminder)])
  (:use [impl.reminder-parsing-impl :only (reminder-line?)])
  (:use [clojure.contrib.seq-utils :only (find-first)])
  (:use [utility :only (do-at)])
  (:use midje.sweet)
  (:import (org.joda.time DateMidnight)))

(fact "parses day-of-week-based strings"
  (do-at (DateMidnight. 2011 6 11)
    (first (first (parse-schedule "Wednesdays ")))) => (DateMidnight. 2011 6 15))

(fact "can have more than one day-of-week to remind on"
  (do-at (DateMidnight. 2011 6 11)
    (first (first (parse-schedule "Wednesdays & Fridays ")))) => (DateMidnight. 2011 6 15)

  (do-at (DateMidnight. 2011 6 11)
    (first (second (parse-schedule "Wednesdays & Fridays ")))) => (DateMidnight. 2011 6 17))

(fact "parses day-of-month-based strings"
  (do-at (DateMidnight. 2011 6 11)
    (first (first (parse-schedule "Every 21st and 25th of the month")))) => (DateMidnight. 2011 6 21)

  (do-at (DateMidnight. 2011 6 11)
    (first (second (parse-schedule "Every 21st and 25th of the month")))) => (DateMidnight. 2011 6 25))

(fact "parses date-based string with one date into a vector with one schedule with one date time"
  (parse-schedule " ON 12/25/2000") => [(DateMidnight. 2000 12 25)])

(fact "parses date-based string that have spaces"
  (parse-schedule " on  12/25/2000  ") => [(DateMidnight. 2000 12 25)])

(fact "parses '&' separated strings into a schedule with two date times, sorted in ascending order"
  (parse-schedule "On 12/25/2000 & on 7/4/1999") => [(DateMidnight. 1999 7 4) (DateMidnight. 2000 12 25)])

(tabular
  (fact "parses everyday-based reminder lines"
    (do-at (DateMidnight. 2011 6 11)
      (take 4 (parse-schedule "  every day   "))) => [(DateMidnight. 2011 6 11) (DateMidnight. 2011 6 12)
                                                      (DateMidnight. 2011 6 13) (DateMidnight. 2011 6 14)]))

(fact "parses every X weeks-based reminder lines"
  (first (parse-schedule "Every 2nd Sunday, starting 6/12/2011" )) => (DateMidnight. 2011 6 12)
  (second (parse-schedule "every   2nd   sunday, Starting   6/12/2011" )) => (DateMidnight. 2011 6 26))

(fact "parses every X days-based reminder lines"
  (take 3 (parse-schedule "Every 4th day, starting 6/8/2011" ))
    => [(DateMidnight. 2011 6 8) (DateMidnight. 2011 6 12) (DateMidnight. 2011 6 16)])

(fact "parses every month/day of the year"
  (do-at (DateMidnight. 2011 6 1)
    (take 2 (first (parse-schedule "On 3/1 & on 9/1")))) => [(DateMidnight. 2012 3 1) (DateMidnight. 2013 3 1)]

  (do-at (DateMidnight. 2011 6 1)
    (take 2 (second (parse-schedule "On 3/1 & on 9/1")))) => [(DateMidnight. 2011 9 1) (DateMidnight. 2012 9 1)])

(fact "parses un-parsable strings into an empty schedule"
  (parse-schedule "@#$$%") => (throws RuntimeException "cannot parse: @#$$%"))

(fact "parses reminders from line"
  (parse-reminder-from-line "   On    12/25/2000      \"message\"      nOtIfY   5 dAYS in advance     ")
  => { :message "message"
       :schedule [(DateMidnight. 2000 12 25)]
       :days-in-advance 5} )

(fact "defaults to notifying 3 days in advance if not specified"
  (parse-reminder-from-line "on 12/25/2000 \"message\"")
  => { :message "message"
       :schedule [(DateMidnight. 2000 12 25)]
       :days-in-advance 3})

(fact "parses a reminder from line only when the line is a reminder line"
  (expect (parse-reminder ...line...) => nil
     (not-called parse-reminder-from-line))
  (provided (reminder-line? ...line...) => false)

  (parse-reminder ...line...) => ...reminder...
  (provided (reminder-line? ...line...) => true)
  (provided (parse-reminder-from-line ...line...) => ...reminder...))
