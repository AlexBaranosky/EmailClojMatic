(ns fact.email-facts
  (:use [email :only (format-reminder-email send-email send-email* disperse-parse-error-emails
                      disperse-unknown-error-emails disperse-history-file-missing-emails)]
        [utility :only (config)]
        midje.sweet)
  (:require [reminder :as so-can-use-Reminder-record])
  (:import [org.joda.time DateMidnight]
           [email EmailRecipient]
           [reminder Reminder]))

(fact "formats email - one section for each reminder"
  (let [reminders [(Reminder. "message" [(DateMidnight. 2022 1 1)] 3)]]
    (format-reminder-email reminders (EmailRecipient. "Jim" "jim@hotmail.com")) => "From: \"EmailOMatic Reminder Service\"\nTo: Jim <jim@hotmail.com>\nSubject: The following reminders are coming up: \n\n1. Saturday 2022/1/1\nmessage"))

(fact "formats email nicely - even when there are no reminders"
  (format-reminder-email [] (EmailRecipient. "Jim" "jim@hotmail.com")) => "From: \"EmailOMatic Reminder Service\"\nTo: Jim <jim@hotmail.com>\nSubject: The following reminders are coming up: \n\n")

(fact "formats email nicely - even when there are no reminders"
  (format-reminder-email nil (EmailRecipient. "Jim" "jim@hotmail.com")) => "From: \"EmailOMatic Reminder Service\"\nTo: Jim <jim@hotmail.com>\nSubject: The following reminders are coming up: \n\n")

(fact "send-email uses the configuration"
  (send-email "to@yahoo.com" "the message") => nil
  (provided
    (send-email* "to@yahoo.com" "the message" "from@gmail.com" "abc123") => nil :times 1
    (config) => { :gmail-address "from@gmail.com" :password "abc123" } :times 1))

;; TODO - Alex oct 10, 2011 - figure how to make these tests more thorough, they're weak
(fact "dispersing parse errors email sends message to each recipient"
  (disperse-parse-error-emails [(EmailRecipient. "bob" "bob@yahoo.com") (EmailRecipient. "john" "john@yahoo.com")] { :message "boom" }) => nil
  (provided
    (send-email "bob@yahoo.com" anything) => nil :times 1
    (send-email "john@yahoo.com" anything) => nil :times 1))

;; TODO - Alex oct 10, 2011 - figure how to make these tests more thorough, they're weak
(fact "dispersing unknown errors email sends message to each recipient"
  (disperse-unknown-error-emails [(EmailRecipient. "bob" "bob@yahoo.com") (EmailRecipient. "john" "john@yahoo.com")] (Exception. "boom")) => nil
  (provided
    (send-email "bob@yahoo.com" anything) => nil :times 1
    (send-email "john@yahoo.com" anything) => nil :times 1))

;; TODO - Alex oct 10, 2011 - figure how to make these tests more thorough, they're weak
(fact "dispersing history file missing error email sends message to each recipient"
  (disperse-history-file-missing-emails [(EmailRecipient. "bob" "bob@yahoo.com") (EmailRecipient. "john" "john@yahoo.com")]) => nil
  (provided
    (send-email "bob@yahoo.com" anything) => nil :times 1
    (send-email "john@yahoo.com" anything) => nil :times 1))