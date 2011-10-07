(ns fact.core-facts
  (:require [reminder-email-history :as history])
  (:use core)
  (:use [utility :only (config valid-config?)])
  (:use email)
  (:use midje.sweet))

(fact "won't email out any reminders if the history says N reminders were sent out, and we have <= N due"
  (expect (run-reminders [...recipients...]) => nil
    (not-called send-reminder-email))
  (provided
    (load-due-reminders anything)      => [{ :message nil :schedule nil :days-in-advance nil}] :times 1
    (history/num-reminders-sent-today) => 1 :times 1) )

(fact "records the number of reminders sent -  when any were sent"
  (run-reminders [...recipients...]) => nil
  (provided (load-due-reminders anything) => [{ :message nil :schedule nil :days-in-advance nil}]
            (send-reminder-email anything anything) => nil :times 1
	    (history/num-reminders-sent-today) => 0 :times 1)
	    (history/record-num-reminders-sent-today 1) => nil :times 1)

(fact "if there are no due reminders, never sends any emails out"
  (expect (run-reminders [...recipients...]) => nil
    (not-called send-reminder-email))
  (provided (load-due-reminders anything)  => [] :times 1))

(comment TODO Alex Oct 3, 2011 - verify on send-email is really weak - see
  if Midje can manage something more specific)
(fact "if there's a problem parsing the reminders.txt, send an email out"
  (expect (run-reminders [...recipient...]) => nil
    (not-called send-reminder-email))
  (provided
    (load-due-reminders anything) => (throws RuntimeException "boom")
    (send-email anything anything) => nil :times 1))

(fact "if config is missing a required field don't process reminders"
  (expect (run-reminders [...recipient...]) => nil
    (not-called email-reminders-to*))
  (provided
    (valid-config? (config)) => false))
