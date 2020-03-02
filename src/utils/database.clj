(ns utils.database
  (:require
   [environ.core :refer [env]]
   [next.jdbc :as jdbc]))

(def db-url
  (env :db-url))

(def db (if db-url
          {:connection-uri db-url
           :user           (env :db-user)
           :password       (env :db-password)}
          {:dbtype   "postgresql"
           :dbname   "comment"
           :user     "ozimos"
           :password ""}))

(def ds (jdbc/get-datasource db))

