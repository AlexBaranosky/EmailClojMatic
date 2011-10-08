(ns fact.load-reminders-integration
  (:use [core :only (load-due-reminders)])
  (:use [utility :only (resource only do-at)])
  (:import [org.joda.time DateMidnight])
  (:use midje.sweet))

(fact "can load due reminders from a given file"
  (do-at (DateMidnight. 2011 6 1)
    (only (load-due-reminders (resource "test_reminder_file.txt")))) =>
      { :message "message 1" :schedule [(DateMidnight. 2011 6 4)] :days-in-advance 8 })