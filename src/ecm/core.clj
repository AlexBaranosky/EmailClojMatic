(ns ecm.core
  (:use [ecm.reminder-parsing :only (load-due-reminders reminder-file)]
        [ecm.email :only (send-reminder-email disperse-parse-error-emails disperse-unknown-error-emails)]
        [ecm.validation :only (validate-resources)]
        [ecm.reminder-email-history :only (num-reminders-sent-today record-num-reminders-sent-today)]
        [slingshot.slingshot :only (try+)]))

(defn email-reminders-to [recipients]
  (let [due-reminders (load-due-reminders (reminder-file))]
    (when (> (count due-reminders) (num-reminders-sent-today))
      (doseq [r recipients]
        (send-reminder-email due-reminders r))
      (record-num-reminders-sent-today (count due-reminders)))))

(defn run-reminders [recipients]
  (when (validate-resources recipients)
    (try+
      (email-reminders-to recipients)
      (catch [:type :ecm.reminder-parsing/cannot-parse-reminder ] {:keys [text]}
        (disperse-parse-error-emails recipients text))
      (catch Throwable e
        (disperse-unknown-error-emails recipients (.getMessage e))))))