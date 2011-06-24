(defproject emailclojmatic "1.0.0-SNAPSHOT"
  :description "Clojure implimentation of the EmailOMatic"
  :dev-dependencies [[midje "1.2-alpha3"]]
  :dependencies [[org.clojure/clojure "1.2.0"]
                 [org.clojure/clojure-contrib "1.2.0"]			 
                 [joda-time "1.6"]      
				 [org.apache.commons/commons-email "1.2"]
                 [fs "0.4.0"]
				 [org.danlarkin/clojure-json "1.2-SNAPSHOT"]]
  :main email-cloj-matic)