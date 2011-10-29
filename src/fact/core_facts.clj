(ns fact.core-facts
  (:use [reminder-email-history :only (num-reminders-sent-today record-num-reminders-sent-today valid-history?)]
        [core :only (run-reminders email-reminders-to)]
        [reminder-parsing :only (reminder-file load-due-reminders)]
        [utility :only (valid-config? fact-resource do-at)]
        [email :only (send-reminder-email disperse-parse-error-emails disperse-unknown-error-emails
                      disperse-history-file-missing-emails disperse-reminders-file-missing-emails)]
        [fs :only (exists?)]
        midje.sweet)
  (:require [reminder-parsing :as so-can-use-Reminder-record])
  (:import [reminder-parsing Reminder]
           [org.joda.time DateMidnight]))

(against-background [(valid-history?) => true]

  (fact "won't email out any reminders if the history says N reminders were sent out, and we have <= N due"
    (run-reminders [...recipients...]) => nil
    (provided
      (send-reminder-email anything anything) => anything :times 0
      (load-due-reminders anything)      => [(Reminder. nil nil nil)] :times 1
      (num-reminders-sent-today) => 1 :times 1) )

  (fact "records the number of reminders sent -  when any were sent"
    (run-reminders [...recipients...]) => nil
    (provided (load-due-reminders anything) => [(Reminder. nil nil nil)]
              (send-reminder-email anything anything) => nil :times 1
        (num-reminders-sent-today) => 0 :times 1)
        (record-num-reminders-sent-today 1) => nil :times 1)

  (fact "if there are no due reminders, never sends any emails out"
    (run-reminders [...recipients...]) => nil
    (provided
      (send-reminder-email anything anything) => anything :times 0
      (load-due-reminders anything)  => [] :times 1))

  (fact "if config is not in valid state don't process reminders"
    (run-reminders [...recipient...]) => nil
    (provided
      (email-reminders-to anything) => anything :times 0
      (valid-config?) => false))

  (fact "if there is an unknown throwable, send an email out"
    (run-reminders [...recipientA... ...recipientB...]) => nil
    (provided
      (send-reminder-email anything anything) => anything :times 0
      (load-due-reminders anything) => (throws Error "boom")
      (disperse-unknown-error-emails [...recipientA... ...recipientB...] anything) => nil :times 1)))

(fact "if reminders.txt does not exist, send out an email"
  (run-reminders [...recipientA... ...recipientB...]) => nil
  (provided
    (email-reminders-to anything) => anything :times 0
    (exists? anything) => false
    (disperse-reminders-file-missing-emails [...recipientA... ...recipientB...]) => nil :times 1))

(fact "if history file missing, don't send reminders, but disperse email notifying of that fact"
  (run-reminders [...recipientA... ...recipientB...]) => nil
  (provided
    (email-reminders-to anything) => anything :times 0
    (valid-history?) => false
    (disperse-history-file-missing-emails [...recipientA... ...recipientB...]) => nil :times 1))

;; TODO- Alex Oct8, 2011 - figure out how to test this slingshot stuff.
;(fact "if there's a problem parsing the reminders.txt, send an email out"
;  (run-reminders [...recipientA... ...recipientB...]) => nil
;  (provided
;    (email-reminders-to anything) => anything :times 0
;    (load-due-reminders anything) => (throws CannotParseRemindersStone)
;    (disperse-parse-error-emails [...recipientA... ...recipientB...] anything) => nil :times 1))

(fact "regression test Oct 18, 2011 - program hanging when trying to parse the below ill-formatted line in a reminders.txt -
       should email out reminder emails in cases of badly formatted reminders.txt"
  (run-reminders [...recipientA... ...recipientB...]) => nil
  (provided
    (reminder-file) => (fact-resource "bad-day-and-month-format-reminders.txt")
    (disperse-parse-error-emails [...recipientA... ...recipientB...] anything) => nil :times 1
    (send-reminder-email anything anything) => nil :times 0))


(fact "if today is wendesday, and reminders are sent every wednesday, emails should be sent out today"
  (do-at (DateMidnight. 2011 10 19 ) ;; a Wednesday
    (run-reminders [...recipient...])) => nil
  (provided
    (reminder-file) => (fact-resource "remind-every-wednesday.txt")
    (num-reminders-sent-today) => 0
    (send-reminder-email anything anything) => nil :times 1))