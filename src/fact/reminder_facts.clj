(ns fact.reminder-facts
  (:use reminder)
  (:require [joda-time-cop :as timecop])
  (:use midje.sweet)
  (:import [reminder Reminder]
           [org.joda.time DateMidnight]))

(fact "reminders format in a specific way - first date after now that is in notification range then next line is the message"
  (to-string (Reminder. "message" [[(DateMidnight. 2011 1 18) (DateMidnight. 2013 1 19)]] 3)) => "Saturday 2013/1/19\nmessage"
  (to-string (Reminder. "message" [] 3)) => "this reminder is not scheduled\nmessage")
  
(tabular 
  (fact "a reminder is due if the next date in the schedule is within range to be notified"
    (timecop/freeze (DateMidnight. 2011 6 24)
      (fn [] (due? (Reminder. "message" [[(DateMidnight. 2011 6 ?day)]] 3)))) => ?is-due)

  ?day ?is-due
  23   falsey
  24   truthy
  25   truthy 
  26   truthy
  27   falsey)
  
(fact "a reminder is not due if it has no dates in the schedule"
  (due? (Reminder. "message" [] 3)) => falsey)