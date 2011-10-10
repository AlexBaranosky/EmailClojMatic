(ns date-time
  (:use [utility :only (seq-of-seqs?)]
        [clojure.contrib.seq-utils :only (find-first)])
  (:import [org.joda.time.format DateTimeFormat]
           [org.joda.time DateMidnight]))
  
(defn today-num [] 
  (.. (DateMidnight.) dayOfWeek get))    
  
(defn for-display [date]
  (.toString date (DateTimeFormat/forPattern (str "yyyy/M/d"))))

(defmulti first-not-in-past #(if (seq-of-seqs? %) :seq-of-seqs :unnested-seq))
  
(defmethod first-not-in-past :seq-of-seqs [seq-of-date-time-seqs] 
  (->> seq-of-date-time-seqs 
      (keep (partial find-first (comp not #(.isBeforeNow %))))
	   sort 
	   first))   
  
(defmethod first-not-in-past :unnested-seq [date-times] 
  (find-first (comp not #(.isBeforeNow %)) date-times))