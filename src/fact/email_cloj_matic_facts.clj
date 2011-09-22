(ns fact.email-cloj-matic-facts
  (:use email-cloj-matic)
  (:use core)
  (:use midje.sweet))

(fact "recipient info is supplied via the command line"
  (-main "Alex" "cool@dude.com" "Mr. Miyagi" "ninja@dude.com") => anything
  (provided (email-reminders-to [{:name "Alex" :email-address "cool@dude.com" }
                                 {:name "Mr. Miyagi" :email-address "ninja@dude.com" }]) => anything))
