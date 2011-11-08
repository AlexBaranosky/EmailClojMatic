(ns reminder-email-history
  (:use [date-time :only (today-num)]
        [utility :only (resource)])
  (:import [org.joda.time DateMidnight]
           [java.io File IOException]))

(defn history-file [] (resource "reminder_email_history.cljdata"))

(defn history []
  (try
    (-> (history-file) slurp read-string)
    (catch IOException e nil)))

(defn valid-history? []
  (let [hist (history)]
    (and (= 2 (count hist))
      (contains? hist :weekday-last-saved-on )
      (contains? hist :num-reminders-already-sent-today ))))

(defn num-reminders-sent-today []
  (let [hist (history)]
    (if (= (today-num) (:weekday-last-saved-on hist))
      (:num-reminders-already-sent-today hist)
      0)))

(defn record-num-reminders-sent-today [num-reminders]
  (spit (history-file)  { :weekday-last-saved-on (today-num)
                          :num-reminders-already-sent-today num-reminders}))
