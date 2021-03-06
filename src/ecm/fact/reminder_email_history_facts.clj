(ns ecm.fact.reminder-email-history-facts
  (:use [ecm.reminder-email-history :only (num-reminders-sent-today history-file record-num-reminders-sent-today history valid-history?)]
        [clojure.java.io :only (resource)]
        [utilize.testutils :only (do-at)]
        midje.sweet)
  (:import [org.joda.time DateMidnight]))

(def fake-history-file (resource "fake_email_history.cljdata"))

(def today (DateMidnight.))
(def yesterday (.minusDays today 1))

(defn- given-three-reminders-sent-on [date-time]
  (let [history-data (str "{ :num-reminders-already-sent-today 3, :weekday-last-saved-on " (.. date-time dayOfWeek get) " }")]
    (spit fake-history-file history-data)))

(do-at today
  (facts "about number of reminders already sent today"
    (fact "can tell you them"
      (num-reminders-sent-today) => 3
      (provided
        (history) => {:num-reminders-already-sent-today 3
                      :weekday-last-saved-on (.getDayOfWeek today)}))

    (fact "resets them to 0 on each new day"
      (num-reminders-sent-today) => 0
      (provided
        (history) => {:num-reminders-already-sent-today 3
                      :weekday-last-saved-on (.getDayOfWeek yesterday)}))))

(fact "stores and retrieves a reminder's count and day of persisting"
  (do-at today
    (given-three-reminders-sent-on yesterday)
    (record-num-reminders-sent-today 5)
    (num-reminders-sent-today)) => 5
  (provided
    (history-file) => fake-history-file))

(fact "if history file cannot be opened returns nil"
  (history) => nil
  (provided
    (history-file) => "nonexistent-file-aasdf"))

(fact "history is invalid if it comes back nil"
  (valid-history?) => false
  (provided
    (history) => nil))

(tabular
  (fact "is invalid history if doesn't have required fields,
         or has too many fields or cant open the history file"
    (valid-history?) => ?is-valid
    (provided (history) => ?history-contents))

  ?history-contents                                                   ?is-valid
  {:weekday-last-saved-on "1" :num-reminders-already-sent-today "5"}  truthy
  {:weekday-last-saved-on "1"}                                        falsey
  {:num-reminders-already-sent-today "5"}                             falsey
  {:weekday-last-saved-on "1" :num-reminders-already-sent-today "5" :extra "field"}  falsey
  {:w33kday-last-saved-on "1" :num-r3m1nders-already-sent-today "5" } falsey
   nil                                                                falsey)