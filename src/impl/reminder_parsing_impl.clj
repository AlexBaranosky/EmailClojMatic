(ns impl.reminder-parsing-impl
  (:use [clojure.contrib.string :only (join)])
  (:use [utility :only (re-match? re-captures)]))

(def comment-line? (partial re-match? #"^\s*#.*$"))
(def blank-line? (partial re-match? #"^\s*$"))

(defn reminder-line? [s]
  (and (not (comment-line? s))
  (not (blank-line? s))))

(def day-names ["Sunday" "Monday" "Tuesday" "Wednesday" "Thursday" "Friday" "Saturday"])
(def ordinals (map ordinalize (range 1 32)))

(def everyday-regex #"(?i)^\s*Every ?day\s*$")
(def every-x-weeks-regex (re-pattern (str "(?i)^\\s*Every\\s+(" (join "|" ordinals) ")\\s+(" (join "|" day-names) "),?\\s+starting\\s+(\\d{1,2})\\/(\\d{1,2})\\/(\\d{4})\\s*$")))
(def every-x-days-regex   (re-pattern (str "(?i)^\\s*Every\\s+(" (join "|" ordinals) ")\\s+day,?\\s+starting\\s+(\\d{1,2})\\/(\\d{1,2})\\/(\\d{4})\\s*$")))
(def date-regex      #"(?i)^\s*on\s+(\d{1,2})/(\d{1,2})/(\d{4})\s*")
(def month+day-regex #"(?i)^\s*on\s+(\d{1,2})/(\d{1,2})\s*")
(def day-of-week-regex #"(?i)(mondays|tuesdays|wednesdays|thursdays|fridays|saturdays|sundays)")
(def day-of-month-identifier-regex #"(?i)^\s*Every (.+) of the month\s*$")
(def ordinal-regex #"(?i)\d+(st|nd|rd|th)")

(def ^{:private true} default-days-in-advance 3)

(def days-in-advance-regex #"(?i)^\s*notify\s+(\d+)\s+days?\s+in\s+advance\s*$")

(defn parse-days-in-advance [s]
  (cond (or (= "" s) (nil? s))
        default-days-in-advance

        (not (re-match? days-in-advance-regex s))
        (throw (RuntimeException. (str "could not parse 'days in advance': " s)))

        :else
        (->> s (re-captures days-in-advance-regex) only parse-int)))