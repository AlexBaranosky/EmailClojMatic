(ns fact.load-reminders-integration
  (:use [core :only (load-due-reminders)]
        [utility :only (resource only do-at)]
        midje.sweet)
  (:import [org.joda.time DateMidnight]))

(fact "can load due reminders from a given file"
  (do-at (DateMidnight. 2011 6 1)
    (only (load-due-reminders (resource "test_reminder_file.txt")))) =>
      { :message "message 1" :dates [(DateMidnight. 2011 6 4)] :days-in-advance 8 })