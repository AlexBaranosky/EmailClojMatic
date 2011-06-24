(ns date-time
  (:use [utility :only (seq-of-seqs?)])
  (:use [clojure.contrib.seq-utils :only (find-first)])
  (:import [org.joda.time.format DateTimeFormat])
  (:import [org.joda.time DateMidnight]))
  
(defn today-num [] 
  (.. (DateMidnight.) dayOfWeek get))    
  
(defn for-display [date]
  (.toString date (DateTimeFormat/forPattern (str "yyyy/M/d"))))

(defn in-past? [date-time] 
  (.isBeforeNow date-time))
  
(defmulti first-today-or-beyond-of-group (fn [xs] 
                                             (if (seq-of-seqs? xs) :seq-of-seqs :unnested-seq)))  
  
(defmethod first-today-or-beyond-of-group :seq-of-seqs [seq-of-date-time-seqs] 
  (->> seq-of-date-time-seqs 
      (keep (partial find-first (comp not in-past?))) 
	   sort 
	   first))   
  
(defmethod first-today-or-beyond-of-group :unnested-seq [date-times] 
  (find-first (comp not in-past?) date-times))