(ns reminder
  (:use date-time))

; each seq in schedule must be sorted in ascending order, or bad things happen!  

(defn to-string [reminder]
  (if-let [date (first-not-in-past (:schedule reminder))]
    (format "%s %s\n%s" (.. date dayOfWeek getAsText) (for-display date) (:message reminder))
    (format "%s\n%s"    "this reminder is not scheduled"                 (:message reminder))))

(defn due? [reminder]
  (if-let [next (first-not-in-past (:schedule reminder))]
    (let [start-reminding-on (.minusDays next (:days-in-advance reminder))]
      (in-past? start-reminding-on))))