(ns email-cloj-matic
  (:gen-class)
  (:use core)) 
  
(defn -main [& names+email-addresses]
  {:pre [(even? (count names+email-addresses))]}
  
  (let [to-recipient (fn [[name email-address]] {:name name :email-address email-address})  
        recipients (map to-recipient (partition 2 names+email-addresses))] 
    (email-reminders-to recipients)))