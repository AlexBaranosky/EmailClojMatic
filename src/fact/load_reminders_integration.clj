(ns fact.load-reminders-integration
  (:require core)
  (:import [reminder Reminder])
  (:import [org.joda.time DateMidnight])
  (:use [utility :only (resource)])
  (:require [joda-time-cop :as timecop])
  (:use clojure.test)
  (:use midje.sweet))
  
(fact "can load due reminders from a given file"
  (timecop/freeze (DateMidnight. 2011 6 1)
    #(= (core/load-due-reminders (resource "test_reminder_file.txt"))
	    [(Reminder. "message 1" [[(DateMidnight. 2011 6 4)]] 8)])) => true)