(ns email-cloj-matic
  (:gen-class)
  (:use [core :only (run-reminders)]
        [email :only (->EmailRecipient)]))
  
(defn -main [& names+email-addresses]
  {:pre [(even? (count names+email-addresses))]}

  (->> names+email-addresses
       (partition 2)
       (map (partial apply ->EmailRecipient))
       run-reminders))