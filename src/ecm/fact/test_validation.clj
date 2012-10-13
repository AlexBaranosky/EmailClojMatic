(ns ecm.fact.test-validation
  (:use ecm.validation
        [ecm.config :only (valid-config?)]
        [ecm.email :only (disperse-reminders-file-missing-emails disperse-history-file-missing-emails)]
        [ecm.reminder-email-history :only (valid-history?)]
        [fs.core :only (exists?)]
        [clojure.java.io :only (resource)]
        midje.sweet))



(fact "if reminders.txt does not exist, send out an email"
  (validate-resources [...recipientA... ...recipientB...]) => falsey
  (provided
    (valid-config?) => true
    (exists? (resource "reminders.txt")) => false
    (disperse-reminders-file-missing-emails [...recipientA... ...recipientB...]) => nil :times 1))

(fact "if history file missing, don't send reminders, but disperse email notifying of that fact"
  (validate-resources [...recipientA... ...recipientB...]) => falsey
  (provided
    (valid-history?) => false
    (disperse-history-file-missing-emails [...recipientA... ...recipientB...]) => nil :times 1))

(fact "if config is not in valid state don't process reminders"
  (validate-resources [...recipient...]) => falsey
  (provided
    (valid-config?) => false))
