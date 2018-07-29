(ns palara.reddit
  (:require [creddit.core :as creddit]))

(eval (read-string (slurp "config.edn")))

(def credentials {:user-client (:app appcreds)
                  :user-secret (:secret appcreds)})

(def creddit-client (creddit/init credentials))



