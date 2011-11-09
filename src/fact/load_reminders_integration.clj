(ns fact.load-reminders-integration
  (:use [reminder-parsing :only (load-due-reminders)]
        [utility :only (resource)]
        [utilize.seq :only (only)]
        [utilize.testutils :only (do-at)]
        midje.sweet)
  (:require [reminder-parsing :as so-can-use-Reminder-record])
  (:import [org.joda.time DateMidnight]
           [reminder-parsing Reminder]))

(fact "can load due reminders from a given file"
  (do-at (DateMidnight. 2011 6 1)
    (only (load-due-reminders (resource "test_reminder_file.txt"))))
       => (Reminder."message 1" [(DateMidnight. 2011 6 4)] 8))