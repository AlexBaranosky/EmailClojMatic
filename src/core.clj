(ns core
  (:use [reminder-parsing :only (parse-reminder)])
  (:use [reminder :only (due?)])
  (:use [email :only (send-reminder-email send-email)])
  (:use [utility :only (resource config valid-config?)])
  (:require [reminder-email-history :as history])
  (:use	[clojure.contrib.duck-streams :only (read-lines)]))

(defn load-due-reminders [file]
  (->> file read-lines (keep parse-reminder) (filter due?)))

(defn email-reminders-to* [recipients]
  (let [due-reminders (load-due-reminders (resource "reminders.txt"))]
    (when (> (count due-reminders) (history/num-reminders-sent-today))
      (do
        (doseq [r recipients]
          (send-reminder-email due-reminders r))
        (history/record-num-reminders-sent-today (count due-reminders))))))

(defn- disperse-error-email [recipients ex]
  (doseq [r recipients]
    (send-email (:email-address r)
      (str "Could not send you your usual reminder update. There was a problem reading your reminders.txt: " (.getMessage ex)))))

(defn run-reminders [recipients]
  (when (valid-config? (config))
    (try
      (email-reminders-to* recipients)
      (catch RuntimeException e
        (disperse-error-email recipients e)))))