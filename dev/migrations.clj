(ns migrations
 (:require 
   [utils.database :refer [db]]
  [migratus.core :as migratus]))

(def db-config
  {:store                :database
   :migration-dir        "migrations/"
   :init-in-transaction? false
   :db                   db})

(migratus/init db-config)

(comment
  (migratus/create db-config "create-user")
  (migratus/migrate db-config)

;rollback the last migration applied
  (migratus/rollback db-config)

;bring up migrations matching the ids
  (migratus/up db-config 20111206154000)

;bring down migrations matching the ids
  (migratus/down db-config 20111206154000))
  