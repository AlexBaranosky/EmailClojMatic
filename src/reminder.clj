(ns reminder
  (:use date-time))

; each seq in schedule must be sorted in scending order, or bad things happen!  
(defrecord Reminder [message schedule days-in-advance])

(defn to-string [reminder]
  (if-let [date (first-today-or-beyond-of-group (:schedule reminder))]
    (format "%s %s\n%s" (.. date dayOfWeek getAsText) (for-display date) (:message reminder))
    (format "%s\n%s"    "this reminder is not scheduled"                  (:message reminder))))

(defn due? [reminder]
  (if-let [next (first-today-or-beyond-of-group (:schedule reminder))]
     (let [start-reminding-on (.minusDays next (:days-in-advance reminder))]
		(in-past? start-reminding-on))))