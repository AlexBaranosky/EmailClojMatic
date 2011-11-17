(ns ecm.date-time
  (:import [org.joda.time.format DateTimeFormat]
           [org.joda.time DateMidnight]))
  
(defn today-num [] 
  (.. (DateMidnight.) dayOfWeek get))    
  
(defn for-display [date]
  (.toString date (DateTimeFormat/forPattern (str "yyyy/M/d"))))

(defn first-not-in-past [date-times]
  (first (remove #(.isBeforeNow %) date-times)))