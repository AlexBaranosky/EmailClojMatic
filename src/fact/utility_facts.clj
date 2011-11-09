(ns fact.utility_facts
  (:use [utility :only (config-file-name config valid-config?)]
         midje.sweet))

(fact "if config file cannot be opened returns nil"
  (config) => nil
  (provided
    (config-file-name) => "nonexistent-file-aasdf"))

(tabular
  (fact "is invalid config if doesn't have gmail-address and password fields,
         or has too many fields or cant open the config file"
    (valid-config?) => ?is-valid
    (provided (config) => ?config-contents))

  ?config-contents                                                  ?is-valid
  {:gmail-address "bob@corp.com" :password "abc123"}                truthy
  {:password "abc123"}                                              falsey
  {:gmail-address "abc123"}                                         falsey
  {:gmail-address "bob@corp.com" :password "abc123" :extra "field"} falsey
  {:gm41l-4ddr3ss "bob@corp.com" :pAssw0rd "abc123" }               falsey
   nil                                                              falsey)