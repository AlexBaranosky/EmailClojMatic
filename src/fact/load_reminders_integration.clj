(ns fact.load-reminders-integration
  (:require core)
  (:import [org.joda.time DateMidnight])
  (:use [utility :only (resource only)])
  (:use [joda-time-cop :only [do-at]])
  (:use clojure.test)
  (:use midje.sweet))

(fact "can load due reminders from a given file"
  (do-at (DateMidnight. 2011 6 1)
    (only (core/load-due-reminders (resource "test_reminder_file.txt")))) =>
      { :message "message 1" :schedule [(DateMidnight. 2011 6 4)] :days-in-advance 8 })
