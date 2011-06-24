(ns fact.email-facts
  (:use email)
  (:import [org.joda.time DateMidnight])
  (:import [reminder Reminder])
  (:use midje.sweet))
  
(fact "formats email - one section for each reminder"
   (let [reminders [(Reminder. "message" [[(DateMidnight. 2022 1 1)]] 3)]
         recipient {:name "Jim" :email-address "jim@hotmail.com"}]
      (format-reminder-email reminders recipient)) => "From: \"EmailOMatic Reminder Service\"\nTo: Jim <jim@hotmail.com>\nSubject: The following reminders are coming up for tomorrow and the day after tomorrow: \n\n1. Saturday 2022/1/1\nmessage")
  
(fact "formats email nicely - even when there are no reminders"
   (let [reminders []
         recipient {:name "Jim" :email-address "jim@hotmail.com"}]
      (format-reminder-email reminders recipient)) => "From: \"EmailOMatic Reminder Service\"\nTo: Jim <jim@hotmail.com>\nSubject: The following reminders are coming up for tomorrow and the day after tomorrow: \n\n")
  
(fact "formats email nicely - even when there are no reminders"
   (let [reminders nil
         recipient {:name "Jim" :email-address "jim@hotmail.com"}]
      (format-reminder-email reminders recipient)) => "From: \"EmailOMatic Reminder Service\"\nTo: Jim <jim@hotmail.com>\nSubject: The following reminders are coming up for tomorrow and the day after tomorrow: \n\n")