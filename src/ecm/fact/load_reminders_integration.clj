(ns ecm.fact.load-reminders-integration
  (:use [ecm.reminder-parsing :only (load-due-reminders)]
        [clojure.java.io :only (resource)]
        [utilize.seq :only (only)]
        [utilize.testutils :only (do-at)]
        midje.sweet)
  (:import [org.joda.time DateMidnight]))

(do-at (DateMidnight. 2011 6 1)

  (fact "can load due reminders from a given file"
    (load-due-reminders (resource "test_reminder_file.txt"))
       => (one-of {:message "message 1"
                   :dates [(DateMidnight. 2011 6 4)]
                   :days-in-advance 8 })))