(ns fact.utility_facts
  (:use utility)
  (:import [org.joda.time DateMidnight])
  (:use midje.sweet))
  
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
   
(fact "gives the path to a given file, as a resource" 
   (resource "text.txt") => "C:\\dev\\EmailClojMatic_Dev\\resources\\text.txt")

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
    "2nd"  2
    "3rd"  3
    "4th"  4
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

(defrecord SomeRecord [name]) 
	
(fact "can tell if something is seq-able"    
  (sequable? []) => truthy
  (sequable? [[]]) => truthy
  (sequable? [1 2 3]) => truthy
  (sequable? '()) => truthy
  (sequable? '(1 2 3)) => truthy
  (sequable? #{}) => truthy
  (sequable? #{1 2 3}) => truthy
  (sequable? { :num 1 :count 3}) => truthy
  (sequable? (SomeRecord. "my record")) => truthy
  (sequable? (DateMidnight. 2000 1 1)) => falsey
  (sequable? 1) => falsey
  (sequable? "string") => falsey)
  
(fact "tells if something is a seq of seqs"
  (seq-of-seqs? [[]]) => truthy
  (seq-of-seqs? [[1 2 3] [1 2 3]]) => truthy
  (seq-of-seqs? (SomeRecord. "my receord")) => truthy
  (seq-of-seqs? (DateMidnight. 2000 1 1)) => falsey
  (seq-of-seqs? [1 2 3]) => falsey
  (seq-of-seqs? []) => falsey
  (seq-of-seqs? "") => falsey
  (seq-of-seqs? 1) => falsey)
   
(fact "do the body of the statement only if the binding evaluates to a seq"
   (seq-let [a [1 2 3]]
      "do something"
	  "something else") => "do something"
	  
   (seq-let [a []]
      "do something"
	  "something else") => "something else"
	  
   (seq-let [a nil]
      "do something"
	  "something else") => "something else"
	  
   (seq-let [num-seq [1 2 3]]
      num-seq
	  "something else") => [1 2 3]
	  
   (seq-let [num-seq [1 2 3]]
      num-seq) => [1 2 3])	  