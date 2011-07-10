(ns fact.reminder-parsing-facts
  (:use reminder-parsing) 
  (:use impl.reminder-parsing-impl) 
  (:use utility)
  (:use [clojure.contrib.seq-utils :only (find-first)])
  (:import (org.joda.time DateMidnight))
  (:require [joda-time-cop :as timecop])
  (:use midje.sweet)) 
  
(fact "parses day-of-week-based strings"
  (timecop/freeze (DateMidnight. 2011 6 11)
    #(first (first (parse-schedule "Wednesdays ")))) => (DateMidnight. 2011 6 15))
  
(fact "can have more than one day-of-week to remind on"
  (timecop/freeze (DateMidnight. 2011 6 11)
    #(first (first (parse-schedule "Wednesdays & Fridays ")))) => (DateMidnight. 2011 6 15)
	
  (timecop/freeze (DateMidnight. 2011 6 11)
    #(first (second (parse-schedule "Wednesdays & Fridays ")))) => (DateMidnight. 2011 6 17))

(fact "parses day-of-month-based strings"
  (timecop/freeze (DateMidnight. 2011 6 11)
    #(first (first (parse-schedule "Every 21st and 25th of the month")))) => (DateMidnight. 2011 6 21)
	
  (timecop/freeze (DateMidnight. 2011 6 11)
    #(first (second (parse-schedule "Every 21st and 25th of the month")))) => (DateMidnight. 2011 6 25))

(fact "parses date-based string with one date into a vector with one schedule with one date time"
  (parse-schedule "2000 12 25") => [(DateMidnight. 2000 12 25)])

(fact "parses date-based string that have spaces"
  (parse-schedule "   2000 12 25   ") => [(DateMidnight. 2000 12 25)])

(fact "parses '&' separated strings into a schedule with two date times, sorted in ascending order"
  (parse-schedule "2000 12 25 & 1999 7 4") => [(DateMidnight. 1999 7 4) (DateMidnight. 2000 12 25)])

(tabular 
  (fact "parses everyday-based reminder lines"
    (timecop/freeze (DateMidnight. 2011 6 11)
      (fn [] (?nth (parse-schedule "  every day   " )))) => (DateMidnight. 2011 6 ?day)) 
	
	?nth   ?day
	first  11
	second 12
	third  13
	fourth 14)
  
(fact "parses every X weeks-based reminder lines"
  (first (parse-schedule "Every 2nd Sunday, starting 6/12/2011" )) => (DateMidnight. 2011 6 12)
  (second (parse-schedule "every   2nd   sunday, Starting   6/12/2011" )) => (DateMidnight. 2011 6 26))  
  
;(tabular
;  (fact "parses every X days-based reminder lines"
;    (?nth (parse-schedule "Every 4th day, starting 6/12/2011" )) => ?date)
  
;      ?nth     ?date
;      first    (DateMidnight. 2011 6 12)
;      second   (DateMidnight. 2011 6 16)
;      third    (DateMidnight. 2011 6 20)
;      fourth   (DateMidnight. 2011 6 24))
  
(fact "parses every X days-based reminder lines"
  (first (parse-schedule "Every 4th day, starting 6/8/2011" )) => (DateMidnight. 2011 6 8)
  (second (parse-schedule "Every 4th day, starting 6/8/2011" )) => (DateMidnight. 2011 6 12)
  (third (parse-schedule "Every 4th day, starting 6/8/2011" )) => (DateMidnight. 2011 6 16))
    
(fact "parses un-parsable strings into an empty schedule"
  (parse-schedule "uncomprehensible gibberish @#$$%") => [])

(fact "parses reminders from line"
  (parse-reminder-from-line "       2000 12 25       \"message\"      nOtIfY   5 dAYS in advance     ") 
  => { :message "message" 
       :schedule [(DateMidnight. 2000 12 25)] 
       :days-in-advance 5} )

(fact "defaults to notifying 3 days in advance if not specified"
  (parse-reminder-from-line "2000 12 25 \"message\"") 
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