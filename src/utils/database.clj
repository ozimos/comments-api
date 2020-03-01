(ns utils.database
 (:require
            [environ.core :refer [env]]
  [next.jdbc :as jdbc]))

(def db (or (env "DB_URL") {:dbtype   "postgresql" 
                            :dbname   "comment"
                            :user     "ozimos"
                            :password ""}))

(def ds (jdbc/get-datasource db))

(defn migrate []
 (jdbc/execute! ds [(slurp "resources/migrations/up.sql")]))
 
(defn migrate-drop []
 (jdbc/execute! ds [(slurp "resources/migrations/down.sql")]))
 
(comment
 (migrate)
 (migrate-drop))
