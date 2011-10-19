(ns fact.load-reminders-integration
  (:use [reminder :only (load-due-reminders)]
        [utility :only (resource only do-at)]
        midje.sweet)
  (:require [reminder :as so-can-use-Reminder-record])
  (:import [org.joda.time DateMidnight]
           [reminder Reminder]))

(fact "can load due reminders from a given file"
  (do-at (DateMidnight. 2011 6 1)
    (only (load-due-reminders (resource "test_reminder_file.txt"))))
       => (Reminder."message 1" [(DateMidnight. 2011 6 4)] 8))