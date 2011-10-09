(ns fact.utility_facts
  (:use [utility :only (parse-int trim but-last re-match? re-match-seq re-captures only lowercase-keyword
                        third fourth fifth ordinal-to-int ordinalize seq-of-seqs? do-at* do-at
                        config-file-name config valid-config?)]
         midje.sweet)
  (:import [org.joda.time DateMidnight]))

(fact "parses an int"
   (parse-int "17") => 17)

(fact "trims a string"
   (trim " 17 ") => "17")

(tabular
  (fact "get all chars if a string but the last 'n'"
    (but-last "1234" ?amt) => ?result)

	?amt ?result
	0    "1234"
	1    "123"
	2    "12"
	3    "1"
	4    ""
	5    "")

(fact "knows if a string matches a given regex"
   (re-match? #"\d+" "1234") => truthy
   (re-match? #"\d+" "abcd") => falsey)

(fact "can get a seq of only the matches - not the captures"
  (re-match-seq #"(\d+)" "abc 123 xyz 789 rad") => ["123" "789"])

(fact "gives the captures from a given regex"
   (re-captures #"(\d) (\d) (\d)" "1 2 4") => ["1" "2" "4"]
   (re-captures #"(\d)" "a b c") => [])

(fact "throws when no items"
  (only []) => (throws RuntimeException "should have precisely one item, but had: 0"))

(fact "throws when more than one item"
  (only [1 2]) => (throws RuntimeException "should have precisely one item, but had: 2"))

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

(tabular
  (fact "convert ordinal strings to ints"
    (ordinal-to-int ?ord) => ?int)

	?ord  ?int
	"1st"  1
    "2nD"  2
    "3RD"  3
    "4Th"  4
    "5th"  5
    "6th"  6
    "7th"  7
    "8th"  8
    "9th"  9
    "10th" 10
    "11th" 11
    "12th" 12
    "13th" 13
    "14th" 14
    "15th" 15
    "16th" 16
    "17th" 17
    "18th" 18
    "19th" 19
    "20th" 20
    "21st" 21
    "22nd" 22
    "23rd" 23
    "24th" 24
    "25th" 25
    "26th" 26
    "27th" 27
    "28th" 28
    "29th" 29
    "30th" 30
    "31st" 31
    "331st" 331
    "33331st" 33331)

(tabular
  (fact "convert int to a ordinal"
    (ordinalize ?int) => ?ord)

	?int ?ord
    1  "1st"
    2  "2nd"
    3  "3rd"
    4  "4th"
    5  "5th"
    6  "6th"
    7  "7th"
    8  "8th"
    9  "9th"
    10 "10th"
    11 "11th"
    12 "12th"
    13 "13th"
    14 "14th"
    15 "15th"
    16 "16th"
    17 "17th"
    18 "18th"
    19 "19th"
    20 "20th"
    21 "21st"
    22 "22nd"
    23 "23rd"
    24 "24th"
    25 "25th"
    26 "26th"
    27 "27th"
    28 "28th"
    29 "29th"
    30 "30th"
    31 "31st"
    311 "311th"
    312 "312th"
    313 "313th"
    33331 "33331st")

(defrecord SampleRecord [name])

(fact "tells if something is a seq of seqs"
  (seq-of-seqs? [[]]) => truthy
  (seq-of-seqs? [[1 2 3] [1 2 3]]) => truthy
  (seq-of-seqs? (SampleRecord. "my receord")) => falsey
  (seq-of-seqs? (DateMidnight. 2000 1 1)) => falsey
  (seq-of-seqs? [1 2 3]) => falsey
  (seq-of-seqs? []) => falsey
  (seq-of-seqs? "") => falsey
  (seq-of-seqs? 1) => falsey)

(fact "freezes time at given date then returns to normal afterward"
  (DateMidnight.) =not=> (DateMidnight. 2000 1 1)
  (do-at* (DateMidnight. 2000 1 1)
    (fn [] (DateMidnight.))) => (DateMidnight. 2000 1 1)
  (DateMidnight.) =not=> (DateMidnight. 2000 1 1))

(fact "there's a macro for running some code at a certain time"
  (DateMidnight.) =not=> (DateMidnight. 2000 1 1)
  (do-at (DateMidnight. 2000 1 1)
    (DateMidnight.)
    (DateMidnight.)) => (DateMidnight. 2000 1 1)
  (DateMidnight.) =not=> (DateMidnight. 2000 1 1))

(fact "when body throws exception, we always make sure to put the time back to normal"
  (DateMidnight.) =not=> (DateMidnight. 2000 1 1)
  (do-at (DateMidnight. 2000 1 1)
    (throw (RuntimeException. "boom"))) => (throws RuntimeException "boom")
  (DateMidnight.) =not=> (DateMidnight. 2000 1 1))

(fact "if config file cannot be opened returns nil"
  (config) => nil
  (provided
    (config-file-name) => "nonexistent-file-aasdf"))

(tabular
  (fact "is invalid config if doesn't have gmail-address and password fields,
         or has too many fields or cant open the config file"
    (valid-config? (config)) => ?is-valid
    (provided (config) => ?config-contents))

  ?config-contents                                                  ?is-valid
  {:gmail-address "bob@corp.com" :password "abc123"}                truthy
  {:password "abc123"}                                              falsey
  {:gmail-address "abc123"}                                         falsey
  {:gmail-address "bob@corp.com" :password "abc123" :extra "field"} falsey
  {:gm41l-4ddr3ss "bob@corp.com" :pAssw0rd "abc123" }               falsey
   nil                                                              falsey)