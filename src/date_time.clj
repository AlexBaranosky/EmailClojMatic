(ns date-time
  (:use [utility :only (seq-of-all-seqs?)])
  (:import [org.joda.time.format DateTimeFormat]
           [org.joda.time DateMidnight]))
  
(defn today-num [] 
  (.. (DateMidnight.) dayOfWeek get))    
  
(defn for-display [date]
  (.toString date (DateTimeFormat/forPattern (str "yyyy/M/d"))))

(defn first-not-in-past [date-times]
  (if (seq-of-all-seqs? date-times)
    (first (sort (keep first-not-in-past date-times)))
    (first (filter (comp not #(.isBeforeNow %)) date-times))))