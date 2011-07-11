(ns reminder-parsing
  (:use impl.reminder-parsing-impl)
  (:use utility)
  (:use date-time-streams)
  (:use date-time)
  (:use reminder)
  (:use [clojure.contrib.str-utils :only (re-split)])
  (:import [org.joda.time DateMidnight]))
  
(defn kind-of-schedule [s] 
  (cond (re-find day-of-month-identifier-regex s) :day-of-month
        (re-find every-x-weeks-regex s) :every-x-weeks  
        (re-find every-x-days-regex s) :every-x-days 
        (re-find date-regex s)          :date   
        (re-find day-of-week-regex s)   :day-of-week		
        (re-find everyday-regex s)      :everyday    
	:else                           :unrecognized-format))
		
(defmulti parse-schedule kind-of-schedule)

(defmethod parse-schedule :day-of-month [s] 
   (let [[ordinals-part] (re-captures day-of-month-identifier-regex s)
         ordinals (map ordinal-to-int (re-match-seq ordinal-regex ordinals-part))]
     (map day-of-month-stream ordinals)))   
	
;; TODO: how shall we handle the fact that technically the day-of-week is unneeded, yet the reminder line phrasing is funny without it!?!?...	
(defmethod parse-schedule :every-x-weeks [s]
  (let [[ordinal day-of-week month day year] (re-captures every-x-weeks-regex s)
        start-date (DateMidnight. (Integer/parseInt year) (Integer/parseInt month) (Integer/parseInt day))
	x-weeks (ordinal-to-int ordinal)]
    (every-x-weeks-stream start-date x-weeks)))
		
(defmethod parse-schedule :every-x-days [s]
  (let [[ordinal month day year] (re-captures every-x-days-regex s)
        start-date (DateMidnight. (Integer/parseInt year) (Integer/parseInt month) (Integer/parseInt day))
	x-days (ordinal-to-int ordinal)]
    (every-x-days-stream start-date x-days)))

(defmethod parse-schedule :date [s]
  (letfn [(parse-date [string]
            (let [[month day year] (->> string (re-captures date-regex) (map parse-int))]
               (DateMidnight. year month day)))]
          (->> (.split s "&") (map parse-date) sort)))
 
(defmethod parse-schedule :day-of-week [s]
  (->> s 
       (re-match-seq day-of-week-regex) 
       (map (comp day-of-week-stream day-nums lowercase-keyword))))
		
(defmethod parse-schedule :everyday [s]
  (today+all-future-dates))
	
(defmethod parse-schedule :unrecognized-format [s] 
   [])	   
	   
(defn parse-reminder-from-line [s]
  (let [[schedule-part message-part days-in-advance-part] (->> s trim (re-split #"\""))]
     {:message message-part 
      :schedule (parse-schedule schedule-part)
      :days-in-advance (parse-days-in-advance days-in-advance-part) }))

(defn parse-reminder [s]
  (when (reminder-line? s)
    (parse-reminder-from-line s)))
