(ns date-time
  (:use [clojure.contrib.seq-utils :only (find-first)])
  (:import [org.joda.time.format DateTimeFormat])
  (:import [org.joda.time DateMidnight]))
  
(defn today-num [] 
  (.. (DateMidnight.) dayOfWeek get))    
  
(defn for-display [date]
  (.toString date (DateTimeFormat/forPattern (str "yyyy/M/d"))))

(defn in-past? [date-time] 
  (.isBeforeNow date-time))

(defn first-today-or-beyond-of-group [seq-of-date-time-seqs] 
  (->> seq-of-date-time-seqs 
      (keep (partial find-first (comp not in-past?))) 
	   sort 
	   first))