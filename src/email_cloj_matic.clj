(ns email-cloj-matic
  (:gen-class)
  (:use [core :only (run-reminders)])
  (:import [email EmailRecipient]))
  
(defn -main [& names+email-addresses]
  {:pre [(even? (count names+email-addresses))]}
  
  (let [to-recipient (fn [[name email-address]] (EmailRecipient. name email-address))
        recipients (map to-recipient (partition 2 names+email-addresses))] 
    (run-reminders recipients)))