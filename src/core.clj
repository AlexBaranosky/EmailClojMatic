(ns core
  (:use [reminder-parsing :only (load-due-reminders reminder-file)]
        [email :only (send-reminder-email disperse-parse-error-emails disperse-unknown-error-emails
                      disperse-history-file-missing-emails disperse-reminders-file-missing-emails)]
        [utility :only (resource config valid-config?)]
        [reminder-email-history :only (num-reminders-sent-today record-num-reminders-sent-today valid-history?)]
        [fs :only (exists?)]
        slingshot.core)
  (:import (reminder-parsing CannotParseRemindersStone)))

(defn email-reminders-to [recipients]
  (let [due-reminders (load-due-reminders (reminder-file))]
    (when (> (count due-reminders) (num-reminders-sent-today))
      (doseq [r recipients]
        (send-reminder-email due-reminders r))
      (record-num-reminders-sent-today (count due-reminders)))))

(defn run-reminders [recipients]
  (if (valid-config?)
    (cond
      (not (exists? (resource "reminders.txt")))
      (disperse-reminders-file-missing-emails recipients)

      (not (valid-history?))
      (disperse-history-file-missing-emails recipients)

      :else (try+
              (email-reminders-to recipients)
              (catch CannotParseRemindersStone s
                (disperse-parse-error-emails recipients (:message s)))
              (catch Throwable e
                (disperse-unknown-error-emails recipients (.getMessage e)))))))