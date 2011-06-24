(ns core
  (:use [reminder-parsing :only (parse-reminder)])
  (:use [reminder :only (due?)])
  (:use [email :only (send-reminder-email)])
  (:use [utility :only (resource seq-let)])
  (:require [reminder-email-history :as history])
  (:use	[clojure.contrib.duck-streams :only (read-lines)]))

(defn load-due-reminders [file]
  (->> file read-lines (keep parse-reminder) (filter due?)))

(defn email-reminders-to [recipients]
  (seq-let [due-reminders (load-due-reminders (resource "reminders.txt"))]
    (when (> (count due-reminders) (history/num-reminders-sent-today))
      (do
	    (doseq [r recipients] (send-reminder-email due-reminders r))
		(history/record-num-reminders-sent-today (count due-reminders))))))