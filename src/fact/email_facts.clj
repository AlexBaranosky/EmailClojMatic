(ns fact.email-facts
  (:use email)
  (:import [org.joda.time DateMidnight])
  (:use midje.sweet))

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
