(ns fact.email-facts
  (:use [email :only (format-reminder-email send-email send-email* disperse-parse-error-email disperse-unknown-error-email)])
  (:use [utility :only (config)])
  (:use midje.sweet)
  (:import [org.joda.time DateMidnight]))

(fact "formats email - one section for each reminder"
   (let [reminders [{ :message "message" :schedule [(DateMidnight. 2022 1 1)] :days-in-advance 3}]
         recipient {:name "Jim" :email-address "jim@hotmail.com"}]
      (format-reminder-email reminders recipient)) => "From: \"EmailOMatic Reminder Service\"\nTo: Jim <jim@hotmail.com>\nSubject: The following reminders are coming up: \n\n1. Saturday 2022/1/1\nmessage")

(fact "formats email nicely - even when there are no reminders"
   (let [reminders []
         recipient {:name "Jim" :email-address "jim@hotmail.com"}]
      (format-reminder-email reminders recipient)) => "From: \"EmailOMatic Reminder Service\"\nTo: Jim <jim@hotmail.com>\nSubject: The following reminders are coming up: \n\n")

(fact "formats email nicely - even when there are no reminders"
   (let [reminders nil
         recipient {:name "Jim" :email-address "jim@hotmail.com"}]
      (format-reminder-email reminders recipient)) => "From: \"EmailOMatic Reminder Service\"\nTo: Jim <jim@hotmail.com>\nSubject: The following reminders are coming up: \n\n")

(fact "send-email uses the configuration"
  (send-email "to@yahoo.com" "the message") => nil
  (provided
    (send-email* "to@yahoo.com" "the message" "from@gmail.com" "abc123") => nil :times 1
    (config) => { :gmail-address "from@gmail.com" :password "abc123" } :times 1))

(fact "dispersing parse errors sends message to each recipient"
  (disperse-parse-error-email [{:email-address "bob@yahoo.com" :name "bob"} {:email-address "john@yahoo.com" :name "john"}] (Exception. "boom")) => nil
  (provided
    (send-email "bob@yahoo.com" anything) => nil :times 1
    (send-email "john@yahoo.com" anything) => nil :times 1))

(fact "dispersing parse errors sends message to each recipient"
  (disperse-unknown-error-email [{:email-address "bob@yahoo.com" :name "bob"} {:email-address "john@yahoo.com" :name "john"}] (Exception. "boom")) => nil
  (provided
    (send-email "bob@yahoo.com" anything) => nil :times 1
    (send-email "john@yahoo.com" anything) => nil :times 1))