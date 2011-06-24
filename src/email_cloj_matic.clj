(ns email-cloj-matic
  (:gen-class)
  (:use core))
  
(defn -main [& _] 
  (email-reminders-to [{ :name "Alex" :email-address "dfasdfasfdsfaf" }]))