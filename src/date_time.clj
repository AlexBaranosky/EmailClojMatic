(ns date-time
  (:use [utility :only (seq-of-seqs?)]
        [clojure.contrib.seq-utils :only (find-first)])
  (:import [org.joda.time.format DateTimeFormat]
           [org.joda.time DateMidnight]))
  
(defn today-num [] 
  (.. (DateMidnight.) dayOfWeek get))    
  
(defn for-display [date]
  (.toString date (DateTimeFormat/forPattern (str "yyyy/M/d"))))

(defn first-not-in-past [date-times]
  (if (seq-of-seqs? date-times)
    (first (sort (keep first-not-in-past date-times)))
    (find-first (comp not #(.isBeforeNow %)) date-times)))