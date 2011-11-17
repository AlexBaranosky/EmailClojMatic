(ns email
  (:use [config :only (config)]
        [clojure.string :only (join)]
        [date-time :only (first-not-in-past for-display)])
  (:import [org.apache.commons.mail SimpleEmail]))

(defn to-string [reminder]
  (if-let [date (first-not-in-past (:dates reminder))]
    (format "%s %s\n%s" (.. date dayOfWeek getAsText) (for-display date) (:message reminder))
    (format "%s\n%s"    "this reminder is not scheduled"                 (:message reminder))))

(defn format-reminder-email [reminders {:keys [name email-address]}]
  (let [from "\"EmailOMatic Reminder Service\""
        header (format "From: %s\nTo: %s <%s>\nSubject: " from name email-address)
        msg (str "The following reminders are coming up: \n\n")
		formatted-reminders (->> reminders (map to-string) (map-indexed #(format "%s. %s" (inc %1) %2)) (join "\n\n"))]
    (str header msg formatted-reminders)))

(defn send-email* [to-email-address message from-address authentication-password]
  (doto (SimpleEmail.)
    (.addTo to-email-address)
    (.setMsg message)
    (.setHostName "smtp.gmail.com")
    (.setSslSmtpPort "465")
    (.setSSL true)
    (.setFrom from-address "EmailOMatic Reminder Service")
    (.setSubject "EmailOMatic Reminder Service")
    (.setAuthentication from-address authentication-password)
    (.send)))

(defn send-email [to-email-address message]
  (let [cfg (config)]
    (send-email* to-email-address message (:gmail-address cfg) (:password cfg))))

(defn send-reminder-email [reminders recipient]
  (send-email (:email-address recipient)
              (format-reminder-email reminders recipient)))

(defn- disperse-emails [msg recipients]
  (doseq [r recipients]
    (send-email (:email-address r) msg)))

(defn- disperse-prefixed-emails [prefixing-message recipients error-msg]
  (disperse-emails (format "Could not send you your usual reminders. %s: \n%s" prefixing-message error-msg) recipients))

(def disperse-parse-error-emails
  (partial disperse-prefixed-emails "There was a problem parsing your reminders.txt"))

(def disperse-unknown-error-emails
  (partial disperse-prefixed-emails "There was an unknown error"))

(def disperse-reminders-file-missing-emails
  (partial disperse-emails "Could not send you your usual reminders. Could not load your reminders.txt file. It should be in the resources directory."))

(def disperse-history-file-missing-emails
  (partial disperse-emails "Could not send you your usual reminders. Could not load your reminder_email_history.cljdata file. It should be in the resources directory and have two fields: ':weekday-last-saved-on' and ':num-reminders-already-sent-today'"))