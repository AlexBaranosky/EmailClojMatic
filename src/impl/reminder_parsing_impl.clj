(ns impl.reminder-parsing-impl
  (:use utility)
  (:require [clojure.contrib.string :as str])
  (:use date-time-streams))

(defonce comment-line? (partial re-match? #"^\s*#.*$"))
(defonce blank-line? (partial re-match? #"^\s*$")) 

(defn reminder-line? [s]
  (and (not (comment-line? s)) 
  (not (blank-line? s))))

(defonce day-names ["Sunday" "Monday" "Tuesday" "Wednesday" "Thursday" "Friday" "Saturday"])
(defonce ordinals (map ordinalize (range 1 32)))
  
(defonce everyday-regex #"(?i)^\s*Every ?day\s*$")
(defonce every-x-weeks-regex (re-pattern (str "(?i)^\\s*Every\\s+(" (str/join "|" ordinals) ")\\s+(" (str/join "|" day-names) "),?\\s+starting\\s+(\\d{1,2})\\/(\\d{1,2})\\/(\\d{4})\\s*$")))
(defonce every-x-days-regex   (re-pattern (str "(?i)^\\s*Every\\s+(" (str/join "|" ordinals) ")\\s+day,?\\s+starting\\s+(\\d{1,2})\\/(\\d{1,2})\\/(\\d{4})\\s*$")))
(defonce date-regex #"(?i)^\s*on\s+(\d{1,2})/(\d{1,2})/(\d{4})\s*") 
(defonce day-of-week-regex #"(?i)(mondays|tuesdays|wednesdays|thursdays|fridays|saturdays|sundays)")  
(defonce day-of-month-identifier-regex #"(?i)^\s*Every (.+) of the month\s*$")  
(defonce ordinal-regex #"(?i)\d+(st|nd|rd|th)")  
  
(defonce ^{:private true} default-days-in-advance 3) 
  
(defn parse-days-in-advance [s]
  (if (or (= "" s) (nil? s))
     default-days-in-advance
     (->> s (re-captures #"(?i)^\s*notify\s+(\d+)\s+days?\s+in\s+advance\s*$") only parse-int)))
