(ns fact.date-time-facts
  (:use date-time)
  (:require [joda-time-cop :as timecop])
  (:import [org.joda.time DateMidnight])
  (:use midje.sweet))

(fact "displays a date-time in a display format"
   (for-display (DateMidnight. 2000 5 25)) => "2000/5/25")

(tabular 
  (fact "date-time is in the future when past today"
    (timecop/freeze (DateMidnight. 2001 1 2)
      (fn [] (in-past? (DateMidnight. 2001 1 ?day)))) => ?past)
	  
	?day ?past
	1    truthy
	2    falsey
	3    falsey)

(fact "gives the earliest date time that is today or after now in a sequence of date-time sequences (assuming each subsequence is in order)" 
  (timecop/freeze (DateMidnight. 2011 11 10)
    #(first-not-in-past [[(DateMidnight. 1999 1 1) (DateMidnight. 2011 1 19) (DateMidnight. 2011 11 10) (DateMidnight. 2011 11 11)]
                         [(DateMidnight. 2011 1 18) (DateMidnight. 2011 1 20) (DateMidnight. 2011 1 21)]
                         [(DateMidnight. 2011 1 17) (DateMidnight. 2011 1 18) (DateMidnight. 2011 1 20)]])) 
  => (DateMidnight. 2011 11 10))
  
(fact "if no dates are today or in the future gives nil" 
  (first-not-in-past [[(DateMidnight. 1999 1 1) (DateMidnight. 1999 1 19) (DateMidnight. 1999 11 11)]
                                   [(DateMidnight. 1999 1 18) (DateMidnight. 1999 1 20) (DateMidnight. 1999 1 21)]
                                   [(DateMidnight. 1999 1 17) (DateMidnight. 1999 1 18) (DateMidnight. 1999 1 20)]]) 
  => nil)

(fact "works for un-nested seqs as well" 
  (timecop/freeze (DateMidnight. 2011 11 10)
    #(first-not-in-past [(DateMidnight. 1999 1 1) (DateMidnight. 2011 1 19) (DateMidnight. 2011 11 10) (DateMidnight. 2011 11 11)])) 
  => (DateMidnight. 2011 11 10))
  
(fact "if no dates are today or in the future gives nil" 
  (first-not-in-past [(DateMidnight. 1999 1 1) (DateMidnight. 1999 1 19) (DateMidnight. 1999 11 11)]) 
  => nil)
 
(tabular 
  (fact (timecop/freeze (DateMidnight. 2011 6 ?date) 
    (fn [] (today-num))) => ?day-num)
  
  ?date ?day-num
   6    1
   7    2
   8    3
   9    4
  10    5
  11    6
  12    7 )