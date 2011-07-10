(ns fact.impl.reminder-parsing-impl-facts
  (:use impl.reminder-parsing-impl) 
  (:use utility)
  (:use date-time)
  (:use date-time-streams)
  (:require [joda-time-cop :as timecop])
  (:import (org.joda.time DateMidnight))
  (:import [reminder Reminder])
  (:use midje.sweet))
  
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
   
(fact "parses the number of days in advance"
  (parse-days-in-advance "   notify 14 days in advance   ") => 14
  (parse-days-in-advance "") => 3
  (parse-days-in-advance nil) => 3)   
   
(fact "day-of-week regex works"
  (re-match-seq day-of-week-regex " Wednesdays ") => ["Wednesdays"])
   
(fact "date-based regex works"
  (re-captures date-regex "  2009  2  3  ") => ["2009" "2" "3"])
   
(fact "every x weeks-based regex works"
  (re-captures every-x-weeks-regex " Every 2nd Sunday starting 2/3/2009  ") => ["2nd" "Sunday" "2" "3" "2009"])
   
(fact "every x days-based regex works"
  (re-captures every-x-days-regex "  EvEry    4Th    dAy,    stArting    2/3/2009  ") => ["4Th" "2" "3" "2009"])
   
(fact "day-of-month-identifier regex works"
  (re-match? day-of-month-identifier-regex "Every blah blah blah of the month") => truthy)
   
(fact "ordinal regex works"
  (re-match-seq ordinal-regex "  abc 1st xyz 7th 8th 9th  ") => ["1st" "7th" "8th" "9th"])
  