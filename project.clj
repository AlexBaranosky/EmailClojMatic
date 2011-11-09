(defproject emailclojmatic "0.9.0-alpha"
  :description "Periodic email reminders"
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [joda-time "2.0"]
                 [utilize "0.1.2"]
                 [slingshot "0.4.0"]
                 [org.apache.commons/commons-email "1.2"]
                 [fs "0.9.0"]]
  :dev-dependencies [[midje "1.2-alpha4"]]
  :main email-cloj-matic)
