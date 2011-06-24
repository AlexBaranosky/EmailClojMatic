(ns reminder-email-history
  (:require [org.danlarkin.json :as json])
  (:use [date-time :only (today-num)])
  (:import [org.joda.time DateMidnight])
  (:use [utility :only (resource)]))

(defn history-file [] (resource "reminder_email_history.json")) 
  
(defn num-reminders-sent-today [] 
  (let [json (json/decode (slurp (history-file)))]
    (if (= (today-num) (:weekday-last-saved-on json))
	  (:num-reminders-already-sent-today json)
	  0)))

(defn record-num-reminders-sent-today [num-reminders] 
  (let [json (json/encode { :weekday-last-saved-on (today-num)
                            :num-reminders-already-sent-today num-reminders} )]
    (spit (history-file) json)))