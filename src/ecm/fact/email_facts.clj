(ns ecm.fact.email-facts
  (:use [ecm.email :only (format-reminder-email send-email send-email* disperse-parse-error-emails
                      disperse-unknown-error-emails disperse-history-file-missing-emails
                      disperse-reminders-file-missing-emails to-string)]
        [ecm.config :only (config)]
        midje.sweet)
  (:import [org.joda.time DateMidnight]))

(fact "reminders format in a specific way - first date after now that is in notification range then next line is the message"
  (to-string { :message "message" :dates [(DateMidnight. 2011 1 18) (DateMidnight. 2013 1 19)] :days-in-advance 3 }) => "Saturday 2013/1/19\nmessage"
  (to-string { :message "message" :dates [] :days-in-advance 3 }) => "this reminder is not scheduled\nmessage")

(fact "formats email - one section for each reminder"
  (let [reminders [{ :message "message" :dates [(DateMidnight. 2022 1 1)] :days-in-advance 3 }]]
    (format-reminder-email reminders { :name "Jim" :email-address "jim@hotmail.com" }) => "From: \"EmailOMatic Reminder Service\"\nTo: Jim <jim@hotmail.com>\nSubject: The following reminders are coming up: \n\n1. Saturday 2022/1/1\nmessage"))

(fact "formats email nicely - even when there are no reminders"
  (format-reminder-email [] { :name "Jim" :email-address "jim@hotmail.com" }) => "From: \"EmailOMatic Reminder Service\"\nTo: Jim <jim@hotmail.com>\nSubject: The following reminders are coming up: \n\n")

(fact "formats email nicely - even when there are no reminders"
  (format-reminder-email nil { :name "Jim" :email-address "jim@hotmail.com" }) => "From: \"EmailOMatic Reminder Service\"\nTo: Jim <jim@hotmail.com>\nSubject: The following reminders are coming up: \n\n")

(fact "send-email uses the configuration"
  (send-email "to@yahoo.com" "the message") => nil
  (provided
    (send-email* "to@yahoo.com" "the message" "from@gmail.com" "abc123") => nil :times 1
    (config) => { :gmail-address "from@gmail.com" :password "abc123" } :times 1))

;; TODO - Alex oct 10, 2011 - figure how to make these tests more thorough, they're weak
(fact "dispersing parse errors email sends message to each recipient"
  (disperse-parse-error-emails [{ :name "bob" :email-address "bob@yahoo.com" }
                                { :name "john" :email-address "john@yahoo.com" }] { :message "boom" }) => nil
  (provided
    (send-email "bob@yahoo.com" anything) => nil :times 1
    (send-email "john@yahoo.com" anything) => nil :times 1))

;; TODO - Alex oct 10, 2011 - figure how to make these tests more thorough, they're weak
(fact "dispersing unknown errors email sends message to each recipient"
  (disperse-unknown-error-emails [{ :name "bob" :email-address "bob@yahoo.com" }
                                  { :name "john" :email-address "john@yahoo.com" }] (Exception. "boom")) => nil
  (provided
    (send-email "bob@yahoo.com" anything) => nil :times 1
    (send-email "john@yahoo.com" anything) => nil :times 1))

;; TODO - Alex oct 10, 2011 - figure how to make these tests more thorough, they're weak
(fact "dispersing history file missing error email sends message to each recipient"
  (disperse-history-file-missing-emails [{ :name "bob" :email-address "bob@yahoo.com" }
                                         { :name "john" :email-address "john@yahoo.com" }]) => nil
  (provided
    (send-email "bob@yahoo.com" anything) => nil :times 1
    (send-email "john@yahoo.com" anything) => nil :times 1))

;; TODO - Alex oct 11, 2011 - figure how to make these tests more thorough, they're weak
(fact "dispersing history file missing error email sends message to each recipient"
  (disperse-reminders-file-missing-emails [{ :name "bob"  :email-address "bob@yahoo.com" }
                                           { :name "john" :email-address "john@yahoo.com" }]) => nil
  (provided
    (send-email "bob@yahoo.com" anything) => nil :times 1
    (send-email "john@yahoo.com" anything) => nil :times 1))