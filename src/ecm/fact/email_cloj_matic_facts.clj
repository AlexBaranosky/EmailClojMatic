(ns ecm.fact.email-cloj-matic-facts
  (:use [ecm.email-cloj-matic :only (-main)]
        [ecm.core :only (run-reminders)]
        midje.sweet))

(fact "recipient info is supplied via the command line"
  (-main "Alex" "cool@dude.com" "Mr. Miyagi" "ninja@dude.com") => anything
  (provided
    (run-reminders [{ :name "Alex" :email-address "cool@dude.com"}
                    { :name "Mr. Miyagi" :email-address "ninja@dude.com" }]) => anything))