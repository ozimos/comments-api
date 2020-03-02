(ns user
  (:require
   [integrant.repl :refer [clear go halt prep init reset reset-all]]
   [system :refer [postgresql-config]]))

 
(integrant.repl/set-prep! (constantly postgresql-config))


(defn -main []
  (go))

(comment
  (go)
  (halt)
  (reset))
