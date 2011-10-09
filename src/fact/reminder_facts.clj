(ns fact.reminder-facts
  (:use [reminder :only (to-string due?)]
        [utility :only [do-at]]
        midje.sweet)
  (:import [org.joda.time DateMidnight]))

(defn- reminder [msg dates days-in-advance]
  { :message msg :dates dates :days-in-advance days-in-advance } )

(fact "reminders format in a specific way - first date after now that is in notification range then next line is the message"
  (to-string (reminder "message" [[(DateMidnight. 2011 1 18) (DateMidnight. 2013 1 19)]] 3)) => "Saturday 2013/1/19\nmessage"
  (to-string (reminder "message" [] 3)) => "this reminder is not scheduled\nmessage")

(tabular
  (fact "a reminder is due if the next date is within range to be notified"
    (do-at (DateMidnight. 2011 6 24)
      (due? (reminder "message" [[(DateMidnight. 2011 6 ?day)]] 3))) => ?is-due)

  ?day ?is-due
  23   falsey
  24   truthy
  25   truthy
  26   truthy
  27   falsey)

(fact "a reminder is not due if it has no dates"
  (due? (reminder "message" [] 3)) => falsey)