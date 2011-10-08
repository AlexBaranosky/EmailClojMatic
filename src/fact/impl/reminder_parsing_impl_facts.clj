(ns fact.impl.reminder-parsing-impl-facts
  (:use [impl.reminder-parsing-impl :only (comment-line? blank-line? reminder-line? parse-days-in-advance
                                           day-of-month-identifier-regex every-x-days-regex every-x-weeks-regex
                                           ordinal-regex month+day-regex day-of-week-regex date-regex)])
  (:use [utility :only (re-match-seq re-captures re-match?)])
  (:use midje.sweet)
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
     "abcdefg"                         (throws RuntimeException "could not parse 'days in advance': abcdefg"))

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

