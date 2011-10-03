Next Steps
=============================================

* use specific type of Exception such as CannotParseRemindersException, or perhaps error-kit

* put all config stuff into a config file that's accessed a function from Coojure code -- this config file will be in
  clojure data format, or property file format, and will not be committed to the repo.

* add sensible and clear error message, in some form, perhaps email, if you use the EmailClojMatic with
    a) no config file, or with
    b) an illformed one.

* add link to the uberjar on wiki

* clean up 'setup' section of README

