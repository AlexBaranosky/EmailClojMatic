(ns reminder-parsing
  (:use [date-time :only (first-not-in-past)]
        [date-time-streams :only (month+day-stream every-x-days-stream day-of-month-stream
                                  day-of-week-stream today+all-future-dates day-nums)]
        [utility :only (resource read-lines)]
        [utilize.seq :only (interleave++ only first-truthy-fn)]
        [utilize.regex :only (re-captures re-match-seq)]
        [utilize.string :only (ordinal-to-int ordinalize lowercase-keyword)]
        [clojure.string :only (join split)]
        slingshot.core)
  (:import [org.joda.time DateMidnight]))

(defrecord CannotParseRemindersStone [message])

(defn due? [reminder]
  (if-let [next (first-not-in-past (:dates reminder))]
    (let [start-reminding-on (.minusDays next (:days-in-advance reminder))]
      (not (.isAfterNow start-reminding-on)))))

(defn reminder-file []
  (resource "reminders.txt"))

(def comment-line? (partial re-matches #"^\s*#.*$"))
(def blank-line? (partial re-matches #"^\s*$"))

(defn reminder-line? [s]
  (not (first-truthy-fn [comment-line? blank-line?] s)))

(let [day-names ["Sunday" "Monday" "Tuesday" "Wednesday" "Thursday" "Friday" "Saturday"]
      ordinals (map ordinalize (range 1 32))]

  (def everyday-regex #"(?i)^\s*Every ?day\s*$")
  (def every-x-weeks-regex (re-pattern (str "(?i)^\\s*Every\\s+(" (join "|" ordinals) ")\\s+(" (join "|" day-names) "),?\\s+starting\\s+(\\d{1,2})\\/(\\d{1,2})\\/(\\d{4})\\s*$")))
  (def every-x-days-regex (re-pattern (str "(?i)^\\s*Every\\s+(" (join "|" ordinals) ")\\s+day,?\\s+starting\\s+(\\d{1,2})\\/(\\d{1,2})\\/(\\d{4})\\s*$")))
  (def date-regex #"(?i)^\s*on\s+(\d{1,2})/(\d{1,2})/(\d{4})\s+")
  (def month+day-regex #"(?i)^\s*on\s+(\d{1,2})/(\d{1,2})\s+")
  (def day-of-week-regex #"(?i)(mondays|tuesdays|wednesdays|thursdays|fridays|saturdays|sundays)")
  (def day-of-month-identifier-regex #"(?i)^\s*Every (.+) of the month\s*$")
  (def ordinal-regex #"(?i)\d+(st|nd|rd|th)")

  (def days-in-advance-regex #"(?i)^\s*notify\s+(\d+)\s+days?\s+in\s+advance\s*$"))

(def ^{:private true} default-days-in-advance 1)

(defn parse-days-in-advance [s]
  (cond
    (or (= "" s) (nil? s))
    default-days-in-advance

    (not (re-matches days-in-advance-regex s))
    (throw+ (CannotParseRemindersStone. (str "could not parse 'days in advance': " s)))

    :else
    (->> s (re-captures days-in-advance-regex) only (#(Integer/parseInt %)))))

(defn kind-of-schedule [s]
  (cond (re-find day-of-month-identifier-regex s) :day-of-month
        (re-find every-x-weeks-regex s) :every-x-weeks
        (re-find every-x-days-regex s) :every-x-days
        (re-find date-regex s) :date
        (re-find month+day-regex s) :month+day
        (re-find day-of-week-regex s) :day-of-week
        (re-find everyday-regex s) :everyday
	      :else :unrecognized-format))

(defmulti parse-reminder-dates kind-of-schedule)

(defmethod parse-reminder-dates :day-of-month [s]
   (let [[ordinals-part] (re-captures day-of-month-identifier-regex s)
         ordinals (map ordinal-to-int (re-match-seq ordinal-regex ordinals-part))]
     (apply interleave++ (map day-of-month-stream ordinals))))

;; TODO: Alex 7/11/2011 how shall we handle the fact that technically the day-of-week is unneeded, yet the reminder line phrasing is funny without it!?!?...
(defmethod parse-reminder-dates :every-x-weeks [s]
  (let [[ordinal day-of-week month day year] (re-captures every-x-weeks-regex s)
        start-date (DateMidnight. (Integer/parseInt year) (Integer/parseInt month) (Integer/parseInt day))
	      x-weeks (ordinal-to-int ordinal)]
    (every-x-days-stream start-date (* 7 x-weeks))))

(defmethod parse-reminder-dates :every-x-days [s]
  (let [[ordinal month day year] (re-captures every-x-days-regex s)
        start-date (DateMidnight. (Integer/parseInt year) (Integer/parseInt month) (Integer/parseInt day))
	      x-days (ordinal-to-int ordinal)]
    (every-x-days-stream start-date x-days)))

(defmethod parse-reminder-dates :date [s]
  (letfn [(parse-date [string]
            (let [[month day year] (->> string (re-captures date-regex) (map #(Integer/parseInt %)))]
               (DateMidnight. year month day)))]
          (->> (.split s "&") (map parse-date) sort)))

(defmethod parse-reminder-dates :day-of-week [s]
  (->> s
       (re-match-seq day-of-week-regex)
       (map (comp day-of-week-stream day-nums lowercase-keyword))
       (apply interleave++)))

(defmethod parse-reminder-dates :month+day [s]
  (letfn [(parse-month+day-date [string]
            (let [[month day] (->> string (re-captures month+day-regex) (map #(Integer/parseInt %)))]
              (month+day-stream month day)))]
    (apply interleave++ (map parse-month+day-date (.split s "&")))))

(defmethod parse-reminder-dates :everyday [s]
  (today+all-future-dates))

(defmethod parse-reminder-dates :unrecognized-format [s]
  (throw+ (CannotParseRemindersStone. (str "cannot parse: " s))))

(defn parse-reminder [line]
  (when (reminder-line? line)
    (let [[schedule-part message-part days-in-advance-part] (split line #"\"")]
      { :message message-part
        :dates (parse-reminder-dates schedule-part)
        :days-in-advance (parse-days-in-advance days-in-advance-part) } )))

(defn load-due-reminders [file]
  (->> file read-lines (keep parse-reminder) (filter due?)))
