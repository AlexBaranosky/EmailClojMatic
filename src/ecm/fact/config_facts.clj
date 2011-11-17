(ns ecm.fact.config-facts
  (:use [config :only (config-file-name config valid-config?)]
         midje.sweet))

(fact "if config file cannot be opened returns nil"
  (config) => nil
  (provided
    (config-file-name) => "nonexistent-file-aasdf"))

(tabular
  (fact "is invalid config if doesn't have a valid gmail-address and password fields,
         or has too many fields or can't open the config file"
    (valid-config?) => ?is-valid
    (provided (config) => ?config-contents))

  ?config-contents                                                  ?is-valid
  {:gmail-address "bob@gmail.com"  :password "abc123"}               truthy
  {:gmail-address "bob@hotmail.com" :password "abc123"}              falsey
  {:gmail-address "gmail.com" :password "abc123"}                    falsey
  {:password "abc123"}                                              falsey
  {:gmail-address "abc123"}                                         falsey
  {:gmail-address "bob@gmail.com" :password "abc123" :extra "field"} falsey
  {:gm41l-4ddr3ss "bob@gmail.com" :pAssw0rd "abc123" }               falsey
   nil                                                              falsey)