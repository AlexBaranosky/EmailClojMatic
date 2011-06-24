(ns fact.core-facts
  (:require [reminder-email-history :as history])
  (:import [reminder Reminder])
  (:use core)
  (:use email)
  (:use midje.sweet))

(fact "won't email out any reminders if the history says N reminders were sent out, and we have <= N due" 
  (expect (email-reminders-to [...recipients...]) => nil
    (not-called send-reminder-email))
    (provided (load-due-reminders anything)      => [(Reminder. nil nil nil)] :times 1
	          (history/num-reminders-sent-today) => 1 :times 1) )

(fact "records the number of reminders sent -  when any were sent" 
  (email-reminders-to [...recipients...]) => nil
  (provided (load-due-reminders anything)      => [(Reminder. nil nil nil)]
            (send-reminder-email anything anything) => nil :times 1
	        (history/num-reminders-sent-today) => 0 :times 1) 
			(history/record-num-reminders-sent-today 1) => nil :times 1)