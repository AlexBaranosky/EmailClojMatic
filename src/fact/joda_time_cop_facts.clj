(ns fact.joda-time-cop-facts
  (:require [joda-time-cop :as timecop])
  (:import (org.joda.time DateMidnight))
  (:use midje.sweet))
  
(fact "freezes time at given date then returns to normal afterward"
  (DateMidnight.) =not=> (DateMidnight. 2000 1 1)
  (timecop/freeze (DateMidnight. 2000 1 1)
    #(DateMidnight.)) => (DateMidnight. 2000 1 1)			  
  (DateMidnight.) =not=> (DateMidnight. 2000 1 1))