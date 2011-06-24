(ns reminder-parsing
  (:use impl.reminder-parsing-impl)
  (:use utility)
  (:use date-time-streams)
  (:use date-time)
  (:use reminder)
  (:use [clojure.contrib.str-utils :only (re-split)])
  (:import [reminder Reminder])
  (:import [org.joda.time DateMidnight]))
  
(defn kind-of-schedule [s] 
  (cond (re-find day-of-month-identifier-regex s) :day-of-month-based
        (re-find every-x-weeks-regex s) :every-x-weeks-based  
        (re-find date-regex s)          :date-based    
        (re-find day-of-week-regex s)   :day-of-week-based		
        (re-find everyday-regex s)      :everyday-based    
		:else                           :unrecognized-format))
		
(defmulti parse-schedule kind-of-schedule)

(defmethod parse-schedule :day-of-month-based [s] 
   (let [[ordinals-part] (re-captures day-of-month-identifier-regex s)
         ordinals (map ordinal-to-int (re-match-seq ordinal-regex ordinals-part))]
     (map day-of-month-stream ordinals)))   
	
;; TODO: how shall we handle the fact that technically the day-of-week is unneeded, yet the reminder line phrasing is funny without it!?!?...	
(defmethod parse-schedule :every-x-weeks-based [s]
  (let [[ordinal day-of-week month day year] (re-captures every-x-weeks-regex s)
        start-date (DateMidnight. (Integer/parseInt year) (Integer/parseInt month) (Integer/parseInt day))
		x-weeks (ordinal-to-int ordinal)]
    (every-x-weeks-stream start-date x-weeks)))

(defmethod parse-schedule :date-based [s]
  (letfn [(parse-date [string]
            (let [[year month day] (->> string (re-captures date-regex) (map parse-int))]
               (DateMidnight. year month day)))]
          (->> (.split s "&") (map parse-date) sort)))
 
(defmethod parse-schedule :day-of-week-based [s]
  (->> s 
       (re-match-seq day-of-week-regex) 
       (map (comp day-of-week-stream day-nums lowercase-keyword))))
		
(defmethod parse-schedule :everyday-based [s]
  (today+all-future-dates))
	
(defmethod parse-schedule :unrecognized-format [s] 
   [])	   
	   
(defn parse-reminder-from-line [s]
  (let [[schedule-part message-part days-in-advance-part] (->> s trim (re-split #"\""))
        schedule (parse-schedule schedule-part)
        days-in-advance (parse-days-in-advance days-in-advance-part)]
    (Reminder. message-part schedule days-in-advance)))

(defn parse-reminder [s]
  (when (reminder-line? s)
    (parse-reminder-from-line s)))