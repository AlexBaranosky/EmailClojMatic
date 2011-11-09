(ns fact.date-time-streams-facts
  (:use [date-time-streams :only (day-of-week-stream month+day-stream day-of-month-stream day-nums)]
        [utilize.testutils :only (do-at)]
        midje.sweet)
  (:import [org.joda.time DateMidnight]))

(fact "makes a stream of xth day of the month"
  (do-at (DateMidnight. 2011 10 15)
    (take 4 (day-of-month-stream 1))) => [(DateMidnight. 2011 11 1)
                                          (DateMidnight. 2011 12 1)
                                          (DateMidnight. 2012 1 1)
                                          (DateMidnight. 2012 2 1)])

(fact "makes stream of month+day, for every year into infinite - using every 4th year as leap year"
  (do-at (DateMidnight. 2011 6 15)
    (take 5 (month+day-stream 9 1))) => [(DateMidnight. 2011 9 1)
                                         (DateMidnight. 2012 9 1)
                                         (DateMidnight. 2013 9 1)
                                         (DateMidnight. 2014 9 1)
                                         (DateMidnight. 2015 9 1)])

(facts "day-stream gives lazy infinite seqs of datetimes on the given day of the week"
  (do-at (DateMidnight. 2011 12 25)
    (take 4 (day-of-week-stream (:tuesdays day-nums)))) => [(DateMidnight. 2011 12 27)
                                                            (DateMidnight. 2012 1 3)
                                                            (DateMidnight. 2012 1 10)
                                                            (DateMidnight. 2012 1 17)])