(ns core
  (:use [reminder-parsing :only (parse-reminder)]
        [reminder :only (due?)]
        [email :only (send-reminder-email disperse-parse-error-emails disperse-unknown-error-emails)]
        [utility :only (resource config valid-config?)]
        [reminder-email-history :only (num-reminders-sent-today record-num-reminders-sent-today)]
        [clojure.contrib.duck-streams :only (read-lines)]
        slingshot.core)
  (:import (reminder-parsing CannotParseRemindersStone)))

(defn load-due-reminders [file]
  (->> file read-lines (keep parse-reminder) (filter due?)))

(defn email-reminders-to [recipients]
  (let [due-reminders (load-due-reminders (resource "reminders.txt"))]
    (when (> (count due-reminders) (num-reminders-sent-today))
      (do
        (doseq [r recipients]
          (send-reminder-email due-reminders r))
        (record-num-reminders-sent-today (count due-reminders))))))

(defn run-reminders [recipients]
  (when (valid-config? (config))
    (try+
      (email-reminders-to recipients)
      (catch CannotParseRemindersStone s
        (disperse-parse-error-emails recipients s))
      (catch Throwable e
        (disperse-unknown-error-emails recipients e)))))