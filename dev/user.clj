(ns user
 (:require 
  [integrant.repl :refer [clear go halt prep init reset reset-all]]
  [ragtime.jdbc :as jdbc]
  [ragtime.repl :as repl]
  [utils.database :refer [db]]
  [comment.system :refer [config]]))

(integrant.repl/set-prep! (constantly config))

(def db-config
  {:datastore  (jdbc/sql-database db)
   :migrations (jdbc/load-resources "migrations")})

(defn -main []
 (go))
(comment
 (go)
 (repl/migrate db-config)
 (repl/rollback db-config)
 (halt)
 (reset))
