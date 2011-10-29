(ns fact.email-cloj-matic-facts
  (:use [email-cloj-matic :only (-main)]
        [core :only (run-reminders)]
        [email :only (->EmailRecipient)]
        midje.sweet))

(fact "recipient info is supplied via the command line"
  (-main "Alex" "cool@dude.com" "Mr. Miyagi" "ninja@dude.com") => anything
  (provided
    (run-reminders [(->EmailRecipient "Alex" "cool@dude.com") (->EmailRecipient "Mr. Miyagi" "ninja@dude.com")]) => anything))