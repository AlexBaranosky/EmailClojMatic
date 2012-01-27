(ns ecm.fact.core-facts
  (:use [ecm.reminder-email-history :only (num-reminders-sent-today record-num-reminders-sent-today valid-history?)]
        [ecm.core :only (run-reminders email-reminders-to)]
        [ecm.reminder-parsing :only (reminder-file load-due-reminders)]
        [ecm.utility :only (fact-resource)]
        [ecm.validation :only (validate-resources)]
        [ecm.email :only (send-reminder-email disperse-parse-error-emails disperse-unknown-error-emails disperse-history-file-missing-emails disperse-reminders-file-missing-emails)]
        [utilize.testutils :only (do-at)]
        midje.sweet)
  (:import [org.joda.time DateMidnight]))



(against-background [(validate-resources) => true]

  (fact "won't email out any reminders if the history says N reminders were sent out, and we have <= N due"
    (run-reminders [...recipients...]) => nil
    (provided
      (send-reminder-email anything anything) => anything :times 0
      (load-due-reminders anything)      => [{}] :times 1
      (num-reminders-sent-today) => 1 :times 1) )

  (fact "records the number of reminders sent -  when any were sent"
    (run-reminders [...recipients...]) => nil
    (provided (load-due-reminders anything) => [{}]
              (send-reminder-email anything anything) => nil :times 1
        (num-reminders-sent-today) => 0 :times 1)
        (record-num-reminders-sent-today 1) => nil :times 1)

  (fact "if there are no due reminders, never sends any emails out"
    (run-reminders [...recipients...]) => nil
    (provided
      (send-reminder-email anything anything) => anything :times 0
      (load-due-reminders anything)  => [] :times 1))

  (fact "if there is an unknown throwable, send an email out"
    (run-reminders [...recipientA... ...recipientB...]) => nil
    (provided
      (send-reminder-email anything anything) => anything :times 0
      (load-due-reminders anything) => (throws Error "boom")
      (disperse-unknown-error-emails [...recipientA... ...recipientB...] anything) => nil :times 1)))



(fact "if resources do not validate, no reminder emails sent"
  (run-reminders [...recipientA... ...recipientB...]) => nil
  (provided
    (send-reminder-email anything anything) => anything :times 0
    (validate-resources [...recipientA... ...recipientB...]) => false))

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


(fact "if today is wednesday, and reminders are sent every wednesday, emails should be sent out today"
  (do-at (DateMidnight. 2011 10 19 ) ;; a Wednesday
    (run-reminders [...recipient...])) => nil
  (provided
    (reminder-file) => (fact-resource "remind-every-wednesday.txt")
    (num-reminders-sent-today) => 0
    (send-reminder-email anything anything) => nil :times 1))