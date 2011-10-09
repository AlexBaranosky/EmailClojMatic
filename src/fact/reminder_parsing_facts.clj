(ns fact.reminder-parsing-facts
  (:use [reminder-parsing :only (parse-schedule parse-reminder-from-line parse-reminder
                                 comment-line? blank-line? reminder-line? parse-days-in-advance
                                 day-of-month-identifier-regex every-x-days-regex every-x-weeks-regex
                                 ordinal-regex month+day-regex day-of-week-regex date-regex)])
  (:use [clojure.contrib.seq-utils :only (find-first)])
  (:use [utility :only (do-at re-match-seq re-captures re-match?)])
  (:use midje.sweet)
  (:import (slingshot Stone))
  (:import (org.joda.time DateMidnight)))

(fact "lines with '#' as the first non-whitespace char are comment lines"
  (comment-line? "    # asdf") => true
  (comment-line? "something else") => false)

(fact "a line is blank when it is only whitespace"
   (blank-line? "  \t\n\r  ") => true
   (blank-line? "abcdef") => false)

(fact "is a reminder line when not a comment line or a blank line"
  (against-background (comment-line? ...line...) => false,
  	              (blank-line? ...line...) => false)

  (reminder-line? ...line...) => true

  (reminder-line? ...line...) => false
  (provided (comment-line? ...line...) => true)

  (reminder-line? ...line...) => false
  (provided (blank-line? ...line...) => true))

(tabular
  (fact "parses the number of days in advance"
    (parse-days-in-advance ?string) => ?days-in-advance)

     ?string                            ?days-in-advance
     "   notify 14 days in advance   "  14
     ""                                 3
     nil                                3
     "abcdefg"                         (throws slingshot.Stone))

(fact "day-of-week regex works"
  (re-match-seq day-of-week-regex " Wednesdays ") => ["Wednesdays"])

(fact "date-based regex works"
  (re-captures date-regex " oN 2/3/2009  ") => ["2" "3" "2009"])

(fact "day+month-based regex works"
  (re-captures month+day-regex " oN 2/3  ") => ["2" "3"])

(fact "every x weeks-based regex works"
  (re-captures every-x-weeks-regex " Every 2nd Sunday starting 2/3/2009  ") => ["2nd" "Sunday" "2" "3" "2009"])

(fact "every x days-based regex works"
  (re-captures every-x-days-regex "  EvEry    4Th    dAy,    stArting    2/3/2009  ") => ["4Th" "2" "3" "2009"])

(fact "day-of-month-identifier regex works"
  (re-match? day-of-month-identifier-regex "Every blah blah blah of the month") => truthy)

(fact "ordinal regex works"
  (re-match-seq ordinal-regex "  abc 1st xyz 7th 8th 9th  ") => ["1st" "7th" "8th" "9th"])

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
  (parse-schedule "@#$$%") => (throws slingshot.Stone))

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