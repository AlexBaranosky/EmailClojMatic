(ns ecm.validation
  (:use [ecm.email :only (disperse-history-file-missing-emails disperse-reminders-file-missing-emails)]
        [ecm.config :only (valid-config?)]
        [clojure.java.io :only (resource)]
        [ecm.reminder-email-history :only (valid-history?)]))

(defn validate-resources [recipients]
  (when (valid-config?)
    (cond
      (not (resource "reminders.txt"))
      (disperse-reminders-file-missing-emails recipients)

      (not (valid-history?))
      (disperse-history-file-missing-emails recipients)

      :else
      true)))