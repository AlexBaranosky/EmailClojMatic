(ns fact.utility_facts
  (:use [utility :only (parse-int trim re-match-seq re-captures lowercase-keyword
                        third fourth fifth config-file-name config valid-config? )]
         midje.sweet)
  (:import [org.joda.time DateMidnight]))

(fact "parses an int"
   (parse-int "17") => 17)

(fact "trims a string"
   (trim " 17 ") => "17")

(fact "can get a seq of only the matches - not the captures"
  (re-match-seq #"(\d+)" "abc 123 xyz 789 rad") => ["123" "789"])

(fact "gives the captures from a given regex"
   (re-captures #"(\d) (\d) (\d)" "1 2 4") => ["1" "2" "4"]
   (re-captures #"(\d)" "a b c") => [])

(fact "can generate lower-case keywords from a string"
  (lowercase-keyword "BobCratchet") => :bobcratchet
  (lowercase-keyword "Bob Cratchet") => :bob-cratchet)

(tabular
  (fact "extra english ways of getting nth element of a seq"
    (?nth [1 2 3 4 5 6]) => ?element)

  ?nth    ?element
  third   3
  fourth  4
  fifth   5)

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