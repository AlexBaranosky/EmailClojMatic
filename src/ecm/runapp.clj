(ns ecm.runapp
  (:gen-class)
  (:use [ecm.core :only (run-reminders)]))


(defn -main [& names+email-addresses]
  {:pre [(even? (count names+email-addresses))]}
  (let [recipients (for [[name email] (partition 2 names+email-addresses)]
                     {:name name
                      :email-address email})]
    (run-reminders recipients)))
