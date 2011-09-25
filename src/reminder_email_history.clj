(ns reminder-email-history
  (:use [date-time :only (today-num)])
  (:import [org.joda.time DateMidnight])
  (:use [utility :only (resource)]))

(defn history-file [] (resource "reminder_email_history.json"))

(defn num-reminders-sent-today []
  (let [history (with-in-str (slurp (history-file)) (read))]
    (if (= (today-num) (:weekday-last-saved-on history))
	  (:num-reminders-already-sent-today history)
	  0)))

(defn record-num-reminders-sent-today [num-reminders]
  (let [history (pr-str { :weekday-last-saved-on (today-num)
                          :num-reminders-already-sent-today num-reminders} )]
    (spit (history-file) history)))
