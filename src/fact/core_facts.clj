(ns fact.core-facts
  (:use [reminder-email-history :only (num-reminders-sent-today record-num-reminders-sent-today)]
        [core :only (run-reminders load-due-reminders email-reminders-to)]
        [utility :only (config valid-config?)]
        [email :only (send-reminder-email disperse-parse-error-emails disperse-unknown-error-emails)]
        midje.sweet)
  (:require [reminder :as so-can-use-Reminder-record])
  (:import [reminder Reminder]))

(fact "won't email out any reminders if the history says N reminders were sent out, and we have <= N due"
  (expect (run-reminders [...recipients...]) => nil
    (not-called send-reminder-email))
  (provided
    (load-due-reminders anything)      => [(Reminder. nil nil nil)] :times 1
    (num-reminders-sent-today) => 1 :times 1) )

(fact "records the number of reminders sent -  when any were sent"
  (run-reminders [...recipients...]) => nil
  (provided (load-due-reminders anything) => [(Reminder. nil nil nil)]
            (send-reminder-email anything anything) => nil :times 1
	    (num-reminders-sent-today) => 0 :times 1)
	    (record-num-reminders-sent-today 1) => nil :times 1)

(fact "if there are no due reminders, never sends any emails out"
  (expect (run-reminders [...recipients...]) => nil
    (not-called send-reminder-email))
  (provided (load-due-reminders anything)  => [] :times 1))

(fact "if config is not in valid state don't process reminders"
  (expect (run-reminders [...recipient...]) => nil
    (not-called email-reminders-to))
  (provided
    (valid-config? (config)) => false))

(fact "if there is an unknown throwable, send an email out"
  (expect (run-reminders [...recipientA... ...recipientB...]) => nil
     (not-called send-reminder-email))
   (provided
     (load-due-reminders anything) => (throws Error "boom")
     (disperse-unknown-error-emails [...recipientA... ...recipientB...] anything) => nil :times 1))

;; TODO- Alex Oct8, 2011 - figure out how to test this slingshot stuff.
;(fact "if there's a problem parsing the reminders.txt, send an email out"
;  (expect (run-reminders [...recipientA... ...recipientB...]) => nil
;    (not-called send-reminder-email))
;  (provided
;    (load-due-reminders anything) => (throws CannotParseRemindersStone)
;    (disperse-parse-error-emails [...recipientA... ...recipientB...] anything) => nil :times 1))