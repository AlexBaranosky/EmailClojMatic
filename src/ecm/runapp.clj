(ns ecm.runapp
  (:gen-class)
  (:use [ecm.core :only (run-reminders)]))
  
(defn -main [& names+email-addresses]
  {:pre [(even? (count names+email-addresses))]}

  (->> names+email-addresses
       (partition 2)
       (map (comp (partial apply hash-map)
                  (partial interleave [:name :email-address])))
       run-reminders))