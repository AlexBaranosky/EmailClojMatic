(ns fact.reminder-facts
  (:use [reminder :only (to-string due?)]
        [utility :only [do-at]]
        midje.sweet)
  (:import [org.joda.time DateMidnight]
           [reminder Reminder]))

(fact "reminders format in a specific way - first date after now that is in notification range then next line is the message"
  (to-string (Reminder. "message" [[(DateMidnight. 2011 1 18) (DateMidnight. 2013 1 19)]] 3)) => "Saturday 2013/1/19\nmessage"
  (to-string (Reminder. "message" [] 3)) => "this reminder is not scheduled\nmessage")

(tabular
  (fact "a reminder is due if the next date is within range to be notified"
    (do-at (DateMidnight. 2011 6 24)
      (due? (Reminder. "message" [(DateMidnight. 2011 6 ?day)] 3))) => ?is-due)

  ?day ?is-due
  23   falsey
  24   truthy
  25   truthy
  26   truthy
  27   truthy
  28   falsey)

(fact "a reminder is not due if it has no dates"
  (due? (Reminder. "message" [] 3)) => falsey)

(tabular
  (fact "when: reminding 0 days in advance, then: a reminder is only due on its 'date'"
    (do-at (DateMidnight. 2000 1 2)
      (due? (Reminder. "msg" [?date] 0))) => ?due)

  :where
  ?date                        ?due
  (DateMidnight. 2000 1 1)     falsey
  (DateMidnight. 2000 1 2)     truthy      )